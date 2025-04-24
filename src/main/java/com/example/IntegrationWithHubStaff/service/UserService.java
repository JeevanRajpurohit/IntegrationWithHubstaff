package com.example.IntegrationWithHubStaff.service;

import com.example.IntegrationWithHubStaff.dtos.userResponseDto.UserResponse;
import com.example.IntegrationWithHubStaff.model.User;

import java.util.List;

public interface UserService {
    List<UserResponse> getUsersByOrganizationId(String organizationId);
}
