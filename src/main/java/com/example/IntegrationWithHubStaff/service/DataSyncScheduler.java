package com.example.IntegrationWithHubStaff.service;

import com.example.IntegrationWithHubStaff.model.Organization;
import com.example.IntegrationWithHubStaff.model.User;
import com.example.IntegrationWithHubStaff.model.UserAppUsage;
import com.example.IntegrationWithHubStaff.repository.OrganizationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public interface DataSyncScheduler {


  //  @Scheduled(cron = "* * * * * *")
    void syncOrganizations();

   // @Scheduled(cron = "* * * * * *")
    void syncOrganizationMembers();

  //  @Scheduled(cron = "* * * * * *")
    void syncApplicationActivities();
}
