package com.example.IntegrationWithHubStaff.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.example.IntegrationWithHubStaff.util.DateConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@DynamoDBTable(tableName = "UserAppUsage")
public class UserAppUsage {

    @DynamoDBHashKey
    private String userId;

    @DynamoDBRangeKey
    private String appActivityId;

    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "OrganizationAppIndex")
    private String appName;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "OrganizationAppIndex")
    private String organizationId;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "ProjectAppIndex")
    private String projectId;

    @DynamoDBAttribute
    private String taskId;

    @DynamoDBAttribute
    private Integer trackedTime;

    @DynamoDBIndexRangeKey(globalSecondaryIndexNames = "ProjectAppIndex")
    @DynamoDBTypeConverted(converter = DateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = DateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifiedAt;
}