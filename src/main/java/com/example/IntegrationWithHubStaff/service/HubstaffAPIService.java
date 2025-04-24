package com.example.IntegrationWithHubStaff.service;

import com.example.IntegrationWithHubStaff.dtos.appActivityDto.DailyApplication;
import com.example.IntegrationWithHubStaff.dtos.organizationResponseDto.OrganizationResponse;
import com.example.IntegrationWithHubStaff.model.*;
import org.springframework.http.*;
import java.util.*;


public interface HubstaffAPIService {

    List<Organization> fetchAllOrganizations();

    List<Organization> fetchOrganizations();

    List<User> fetchOrganizationMembers(String organizationName, String organizationId);

    List<UserAppUsage> fetchApplicationActivities(String organizationName, String organizationId, Date date);

    UserAppUsage convertToUserAppUsage(DailyApplication dailyApp, String organizationId);

    Map<String, Integer> getTimeSpentByUserOnApp(String userId, String appName);

    Map<String, Integer> getTimeSpentByOrganizationOnApp(String organizationId, String appName);

    Map<String, Integer> getTimeSpentByProjectOnApp(String projectId, String appName);

    HttpHeaders createHeadersWithToken(String accessToken);

    Organization convertToOrganizationEntity(OrganizationResponse orgResp);

}