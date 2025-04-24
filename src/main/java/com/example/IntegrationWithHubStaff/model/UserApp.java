package com.example.IntegrationWithHubStaff.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.example.IntegrationWithHubStaff.util.DateConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@DynamoDBTable(tableName = "UserApps")
public class UserApp {

    @DynamoDBHashKey
    private String userId;

    @DynamoDBRangeKey
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "organizationId_appName_index")
    private String appName;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "organizationId_appName_index")
    private String organizationId;

    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = DateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
}