package com.example.IntegrationWithHubStaff.dtos.organizationResponseDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
public class HubstaffOrganizationResponse {
    private List<OrganizationResponse> organizations;
}