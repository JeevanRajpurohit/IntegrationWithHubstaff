package com.example.IntegrationWithHubStaff.service.ServiceImplementation;

import com.example.IntegrationWithHubStaff.model.Organization;
import com.example.IntegrationWithHubStaff.model.User;
import com.example.IntegrationWithHubStaff.model.UserAppUsage;
import com.example.IntegrationWithHubStaff.repository.OrganizationRepository;
import com.example.IntegrationWithHubStaff.service.DataSyncScheduler;
import com.example.IntegrationWithHubStaff.service.HubstaffAPIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class DataSyncSchedulerImpl implements DataSyncScheduler {

    @Autowired private HubstaffAPIService hubstaffAPIService;
    @Autowired private OrganizationRepository organizationRepository;

  //  @Scheduled(cron = "* * * * * *")
    @Override
    public void syncOrganizations() {
        log.info("Starting sync of organizations");
        try {
            List<Organization> organizations = hubstaffAPIService.fetchAllOrganizations();
            log.info("Synced {} organizations", organizations.size());
        } catch (Exception e) {
            log.error("Error syncing organizations: {}", e.getMessage(), e);
        }
        log.info("Completed sync of organizations");
    }

 //   @Scheduled(cron = "* * * * * *")
    @Override
    public void syncOrganizationMembers() {
        log.info("Starting sync of organization members");
        try {
            List<Organization> organizations = hubstaffAPIService.fetchAllOrganizations();
            for (Organization org : organizations) {
                try {
                    log.info("Fetching users for organization: {}", org.getOrganizationName());
                    List<User> users = hubstaffAPIService.fetchOrganizationMembers(
                            org.getOrganizationName(), org.getOrganizationId());
                    log.info("Synced {} users for organization: {}", users.size(), org.getOrganizationName());
                } catch (Exception e) {
                    log.error("Error syncing users for organization {}: {}", org.getOrganizationName(), e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("Error retrieving organizations: {}", e.getMessage(), e);
        }
        log.info("Completed sync of organization members");
    }

 //   @Scheduled(cron = "* * * * * *")
    @Override
    public void syncApplicationActivities() {
        log.info("Starting sync of application activities");
        try {
            List<Organization> organizations = hubstaffAPIService.fetchAllOrganizations();
            for (Organization org : organizations) {
                try {
                    Date today = Date.from(Instant.now().minus(1, ChronoUnit.DAYS));
                    List<UserAppUsage> activities = hubstaffAPIService.fetchApplicationActivities(
                            org.getOrganizationName(), org.getOrganizationId(), today);
                    log.info("Synced {} app activities for organization: {}", activities.size(), org.getOrganizationName());
                } catch (Exception e) {
                    log.error("Error syncing activities for organization {}: {}", org.getOrganizationName(), e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("Error retrieving organizations: {}", e.getMessage(), e);
        }
        log.info("Completed sync of application activities");
    }
}