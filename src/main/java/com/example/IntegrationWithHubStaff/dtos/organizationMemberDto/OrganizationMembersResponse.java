package com.example.IntegrationWithHubStaff.dtos.organizationMemberDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationMembersResponse {
    private List<Membership> members;
    private List<UserDetail> users;
}