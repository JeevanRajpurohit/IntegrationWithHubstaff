package com.example.IntegrationWithHubStaff.service;

import com.example.IntegrationWithHubStaff.dtos.organizationResponseDto.OrganizationResponse;

import java.util.List;

public interface OrganizationService {
    List<OrganizationResponse> getAllOrganizations();
}
