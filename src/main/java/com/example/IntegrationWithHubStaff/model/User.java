package com.example.IntegrationWithHubStaff.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@DynamoDBTable(tableName = "Users")
public class User {

    @DynamoDBHashKey(attributeName = "email")
    private String email;

    @DynamoDBAttribute(attributeName = "userId")
    private String userId;

    @DynamoDBAttribute(attributeName = "name")
    private String name;

    @DynamoDBAttribute(attributeName = "status")
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "OrganizationDateIndex")
    private String status;

    @DynamoDBAttribute(attributeName = "createdAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "OrganizationDateIndex")
    private Date createdAt;

    @DynamoDBAttribute(attributeName = "updatedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    @DynamoDBAttribute(attributeName = "organizationId")
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "organizationIdIndex")
    private String organizationId;
}
