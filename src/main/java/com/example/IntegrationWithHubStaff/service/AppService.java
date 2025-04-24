package com.example.IntegrationWithHubStaff.service;

import com.example.IntegrationWithHubStaff.dtos.AppDto;
import com.example.IntegrationWithHubStaff.model.UserApp;

import java.util.List;
import java.util.Set;

public interface AppService {
    List<AppDto> getUniqueAppDtosByOrganization(String organizationId);

    List<AppDto> getAppsByUserId(String userId);
}
