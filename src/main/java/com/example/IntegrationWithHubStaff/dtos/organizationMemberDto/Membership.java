package com.example.IntegrationWithHubStaff.dtos.organizationMemberDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Membership {

    @JsonProperty("user_id")
    private Long user_id;

    @JsonProperty("membership_role")
    private String membership_Role;

    @JsonProperty("membership_status")
    private String membership_Status;

    @JsonProperty("created_at")
    private Date Created_at;

    @JsonProperty("updated_at")
    private Date Updated_at;
}
