package com.example.IntegrationWithHubStaff.service.ServiceImplementation;

import com.example.IntegrationWithHubStaff.config.AppConfig;
import com.example.IntegrationWithHubStaff.dtos.appActivityDto.DailyApplication;
import com.example.IntegrationWithHubStaff.dtos.appActivityDto.DailyApplicationsResponse;
import com.example.IntegrationWithHubStaff.dtos.organizationMemberDto.Membership;
import com.example.IntegrationWithHubStaff.dtos.organizationMemberDto.OrganizationMembersResponse;
import com.example.IntegrationWithHubStaff.dtos.organizationResponseDto.OrganizationResponse;
import com.example.IntegrationWithHubStaff.dtos.organizationMemberDto.UserDetail;
import com.example.IntegrationWithHubStaff.dtos.organizationResponseDto.HubstaffOrganizationResponse;
import com.example.IntegrationWithHubStaff.exception.BadRequestException;
import com.example.IntegrationWithHubStaff.exception.OAuthException;
import com.example.IntegrationWithHubStaff.model.*;
import com.example.IntegrationWithHubStaff.repository.*;
import com.example.IntegrationWithHubStaff.service.AuthService;
import com.example.IntegrationWithHubStaff.service.HubstaffAPIService;
import com.example.IntegrationWithHubStaff.util.DateFilterUtil;
import com.example.IntegrationWithHubStaff.util.HubstaffApiConstants;
import com.example.IntegrationWithHubStaff.util.RequestValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HubstaffAPIServiceImpl implements HubstaffAPIService {

    @Autowired private AuthService authService;
    @Autowired private RestTemplate restTemplate;
    @Autowired private AppConfig hubstaffConfig;
    @Autowired private OrganizationRepository organizationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserAppRepository userAppRepository;
    @Autowired private UserAppUsageRepository userAppUsageRepository;
    @Autowired private AuthTokenRepository authTokenRepository;
    @Autowired private ModelMapper modelMapper;

    @Override
    public List<Organization> fetchAllOrganizations() {
        try {
          AuthToken clientId = authTokenRepository.findByClientId(hubstaffConfig.getClientId());

            if (RequestValidationUtil.isNullOrEmpty(clientId)) {
                log.warn("No organization names found in auth tokens");
                return Collections.emptyList();
            }

            List<Organization> allOrganizations = new ArrayList<>();
                try {
                    allOrganizations.addAll(fetchOrganizations());
                } catch (Exception e) {
                    log.error("Failed to fetch organizations for {}: {}", clientId, e.getMessage());
                }
            return allOrganizations;
        } catch (Exception e) {
            log.error("Failed to fetch all organizations", e);
            throw new RuntimeException("Failed to fetch all organizations: " + e.getMessage());
        }
    }

    @Override
    public List<Organization> fetchOrganizations() {
        try {
            String accessToken = authService.getAccessToken(hubstaffConfig.getClientId());
            HttpHeaders headers = createHeadersWithToken(accessToken);

            ResponseEntity<HubstaffOrganizationResponse> response = restTemplate.exchange(
                    HubstaffApiConstants.ORGANIZATIONS_ENDPOINT,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    HubstaffOrganizationResponse.class);

            if (RequestValidationUtil.isNullOrEmpty(response.getBody()) || RequestValidationUtil.isNullOrEmpty(response.getBody().getOrganizations())) {
                log.warn("Received empty response for organizations");
                return Collections.emptyList();
            }

            List<Organization> organizations = response.getBody().getOrganizations().stream()
                    .map(this::convertToOrganizationEntity)
                    .collect(Collectors.toList());

            List<Organization> filteredOrganizations = DateFilterUtil.filterFromPreviousDay(
                    organizations,
                    u -> u.getCreatedAt() != null
                            ? u.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                            : null
            );

            organizationRepository.saveAll(filteredOrganizations);
            log.info("Saved {} organizations for {}", organizations.size(), hubstaffConfig.getClientId());

            return organizations;
        } catch (HttpClientErrorException e) {
            log.error("Hubstaff API Error - Status: {}, Response: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new OAuthException("Organization sync failed: " + e.getMessage());
        } catch (Exception e) {
            log.error("Organization sync failed for {}", hubstaffConfig.getClientId(), e);
            throw new RuntimeException("Organization sync failed: " + e.getMessage());
        }
    }

    @Override
    public List<User> fetchOrganizationMembers(String organizationName, String organizationId) {
        try {
            String accessToken = authService.getAccessToken(hubstaffConfig.getClientId());
            HttpHeaders headers = createHeadersWithToken(accessToken);

            String apiUrl = String.format(HubstaffApiConstants.ORGANIZATION_MEMBERS_TEMPLATE, organizationId);

            ResponseEntity<OrganizationMembersResponse> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    OrganizationMembersResponse.class);

            if (RequestValidationUtil.isNullOrEmpty(response.getBody()) || RequestValidationUtil.isNullOrEmpty(response.getBody().getMembers()) || RequestValidationUtil.isNullOrEmpty(response.getBody().getUsers())) {
                log.warn("Received incomplete response for organization members");
                return Collections.emptyList();
            }

            Map<Long, UserDetail> userDetailsMap = response.getBody().getUsers().stream()
                    .collect(Collectors.toMap(UserDetail::getId, Function.identity()));

            List<User> users = new ArrayList<>();
            for (Membership membership : response.getBody().getMembers()) {
                UserDetail userDetail = userDetailsMap.get(membership.getUser_id());
                if (userDetail != null) {
                    User user = new User();
                    user.setUserId(membership.getUser_id().toString());
                    user.setOrganizationId(organizationId);
                    user.setName(userDetail.getName());
                    user.setEmail(userDetail.getEmail());
                    user.setStatus(userDetail.getStatus());
                    user.setCreatedAt(userDetail.getCreatedAt());
                    user.setUpdatedAt(userDetail.getUpdatedAt());

                    users.add(user);
                }
            }
            List<User> filteredUsers = DateFilterUtil.filterFromPreviousDay(
                    users,
                    u -> u.getCreatedAt() != null
                            ? u.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                            : null
            );

            userRepository.saveAll(filteredUsers);
            log.info("Saved {} users for organization: {}", users.size(), organizationName);

            return users;
        } catch (Exception e) {
            log.error("Failed to fetch members for org {} ({})", organizationName, organizationId, e);
            throw new RuntimeException("Failed to fetch organization members: " + e.getMessage());
        }
    }

    @Override
    public List<UserAppUsage> fetchApplicationActivities(String organizationName, String organizationId, Date date) {
        try {
            String accessToken = authService.getAccessToken(hubstaffConfig.getClientId());
            HttpHeaders headers = createHeadersWithToken(accessToken);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = dateFormat.format(date);

            String url = String.format(
                    HubstaffApiConstants.APP_ACTIVITIES_DAILY_TEMPLATE,
                    organizationId,
                    URLEncoder.encode(dateStr, StandardCharsets.UTF_8),
                    URLEncoder.encode(dateStr, StandardCharsets.UTF_8));

            log.info("Fetching daily app activities for org {} on date {}", organizationId, dateStr);

            ResponseEntity<String> rawResponse = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class);

            log.debug("Raw API response: {}", rawResponse.getBody());

            ResponseEntity<DailyApplicationsResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    DailyApplicationsResponse.class);

            if (RequestValidationUtil.isNullOrEmpty(response.getBody())) {
                log.warn("Received null response body from API");
                return Collections.emptyList();
            }

            List<DailyApplication> dailyApplications = response.getBody().getDailyApplications();
            if (dailyApplications == null || dailyApplications.isEmpty()) {
                log.info("No daily application activities found for org {} on {}", organizationId, dateStr);
                return Collections.emptyList();
            }

            List<UserAppUsage> appUsages = dailyApplications.stream()
                    .map(dailyApp -> convertToUserAppUsage(dailyApp, organizationId))
                    .collect(Collectors.toList());

            userAppUsageRepository.saveAll(appUsages);
            log.info("Saved {} daily app activities for org {}", appUsages.size(), organizationId);

            List<UserApp> userApps = dailyApplications.stream()
                    .map(dailyApp -> {
                        UserApp userApp = new UserApp();
                        userApp.setUserId(dailyApp.getUserId().toString());
                        userApp.setAppName(dailyApp.getAppName());
                        userApp.setOrganizationId(organizationId);
                        userApp.setCreatedAt(new Date());
                        return userApp;
                    })
                    .distinct()
                    .collect(Collectors.toList());

            userAppRepository.saveAll(userApps);
            log.info("Saved {} UserApp records for org {}", userApps.size(), organizationId);

            return appUsages;
        } catch (HttpClientErrorException e) {
            log.error("API Error - Status: {}, Response: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new OAuthException("Failed to fetch daily application activities: " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to fetch daily app activities for org {} on {}", organizationId, date, e);
            throw new RuntimeException("Failed to fetch daily application activities: " + e.getMessage());
        }
    }

    @Override
    public UserAppUsage convertToUserAppUsage(DailyApplication dailyApp, String organizationId) {
        UserAppUsage usage = new UserAppUsage();
        usage.setUserId(dailyApp.getUserId().toString());
        usage.setAppActivityId(dailyApp.getId().toString());
        usage.setAppName(dailyApp.getAppName());
        usage.setOrganizationId(organizationId);
        usage.setProjectId(dailyApp.getProjectId() != null ? dailyApp.getProjectId().toString() : null);
        usage.setTaskId(dailyApp.getTaskId() != null ? dailyApp.getTaskId().toString() : null);
        usage.setTrackedTime(dailyApp.getTracked());
        usage.setCreatedAt(dailyApp.getCreatedAt());
        usage.setModifiedAt(dailyApp.getUpdatedAt());
        return usage;
    }

    @Override
    public Map<String, Integer> getTimeSpentByUserOnApp(String userId, String appName) {
        try {
            int totalTime = userAppUsageRepository.findByUserId(userId).stream()
                    .filter(usage -> usage.getAppName().equals(appName))
                    .mapToInt(UserAppUsage::getTrackedTime)
                    .sum();

            return Map.of(
                    "userId", Integer.valueOf(userId),
                    "totalTime", totalTime
            );
        } catch(Exception e) {
            log.error("Failed to get app time for user {} and app {}", userId, appName, e);
            throw new BadRequestException("Failed to get app usage time: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Integer> getTimeSpentByOrganizationOnApp(String organizationId, String appName) {
        try {
            int totalTime = userAppUsageRepository.findByOrganizationIdAndAppName(organizationId, appName)
                    .stream()
                    .mapToInt(UserAppUsage::getTrackedTime)
                    .sum();

            return Map.of(
                    "organizationId", Integer.valueOf(organizationId),
                    "totalTime", totalTime
            );
        } catch (Exception e) {
            log.error("Failed to get app time for org {} and app {}", organizationId, appName, e);
            throw new BadRequestException("Failed to get organization app usage time: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Integer> getTimeSpentByProjectOnApp(String projectId, String appName) {
        try {
            int totalTime = userAppUsageRepository.findByProjectIdAndAppName(projectId, appName)
                    .stream()
                    .mapToInt(UserAppUsage::getTrackedTime)
                    .sum();

            return Map.of(
                    "projectId", Integer.valueOf(projectId),
                    "totalTime", totalTime
            );
        } catch (Exception e) {
            log.error("Failed to get app time for org {} and app {}", projectId, appName, e);
            throw new BadRequestException("Failed to get organization app usage time: " + e.getMessage());
        }
    }

    @Override
    public HttpHeaders createHeadersWithToken(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        return headers;
    }

    @Override
    public Organization convertToOrganizationEntity(OrganizationResponse orgResp) {
        Organization org = new Organization();
        org.setOrganizationId(orgResp.getId().toString());
        org.setOrganizationName(orgResp.getName());
        org.setCreatedAt(orgResp.getCreatedAt() != null ? orgResp.getCreatedAt() : new Date());
        org.setModifiedAt(orgResp.getModifiedAt() != null ? orgResp.getModifiedAt() : new Date());
        return org;
    }
}