package com.example.IntegrationWithHubStaff.dtos.appTimeSpentDto;

import com.example.IntegrationWithHubStaff.validations.Groups;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppTimeActivityDto {
    @NotBlank(message = "User ID cannot be blank", groups = Groups.FetchAppTimeByUser.class)
    private String userId;

    @NotBlank(message = "Project ID cannot be blank", groups = Groups.FetchAppTimeByProject.class)
    private String projectId;

    @NotBlank(message = "Organization ID cannot be blank", groups = Groups.FetchAppTimeByOrganization.class)
    private String organizationId;

    @NotBlank(message = "App name cannot be blank", groups = {
            Groups.FetchAppTimeByUser.class,
            Groups.FetchAppTimeByProject.class,
            Groups.FetchAppTimeByOrganization.class
    })
    private String appName;
}
