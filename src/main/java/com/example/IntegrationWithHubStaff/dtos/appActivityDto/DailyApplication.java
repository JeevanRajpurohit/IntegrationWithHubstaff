package com.example.IntegrationWithHubStaff.dtos.appActivityDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class DailyApplication {
    private Long id;
    private String appName;
    private String date;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("task_id")
    private Long taskId;
    private Integer tracked;
    @JsonProperty("created_at")
    private Date createdAt;
    @JsonProperty("updated_at")
    private Date updatedAt;
}
