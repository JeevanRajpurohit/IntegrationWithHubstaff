package com.example.IntegrationWithHubStaff.dtos.userResponseDto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class UserResponseWrapper {
    private List<UserResponse> users;

}