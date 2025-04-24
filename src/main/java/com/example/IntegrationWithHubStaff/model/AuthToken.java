package com.example.IntegrationWithHubStaff.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.example.IntegrationWithHubStaff.util.DateConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@DynamoDBTable(tableName = "Integrations")
public class AuthToken {

    @DynamoDBHashKey
    @DynamoDBAttribute(attributeName = "clientId")
    private String clientId;

    @DynamoDBAttribute(attributeName = "clientSecret")
    private String clientSecret;

    @DynamoDBAttribute(attributeName = "accessToken")
    private String accessToken;

    @DynamoDBAttribute(attributeName = "refreshToken")
    private String refreshToken;

    @DynamoDBAttribute(attributeName = "redirectUri")
    private String redirectUrl;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "expiresAt_index")
    @DynamoDBAttribute(attributeName = "expiresAt")
    private Long expiresAt;

    @DynamoDBAttribute(attributeName = "createdAt")
    @DynamoDBTypeConverted(converter = DateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @DynamoDBAttribute(attributeName = "updatedAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DynamoDBTypeConverted(converter = DateConverter.class)
    private Date modifiedAt;
}
