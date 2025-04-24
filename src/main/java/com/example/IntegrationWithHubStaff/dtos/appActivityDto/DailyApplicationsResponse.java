package com.example.IntegrationWithHubStaff.dtos.appActivityDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DailyApplicationsResponse {
    @JsonProperty("daily_applications")
    private List<DailyApplication> dailyApplications;
}

