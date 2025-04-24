package com.example.IntegrationWithHubStaff.dtos;

import com.example.IntegrationWithHubStaff.validations.Groups;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class AppDto {
        @NotBlank(message = "AppName cannot be blank", groups = Groups.GetAppNames.class)
        private String appName;

        private String userId;
        private String organizationId;
        private Date createdAt;
}
