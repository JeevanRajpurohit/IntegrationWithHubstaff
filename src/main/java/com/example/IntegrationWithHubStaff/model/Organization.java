package com.example.IntegrationWithHubStaff.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.example.IntegrationWithHubStaff.util.DateConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@DynamoDBTable(tableName = "Organizations")
public class Organization {
    @DynamoDBHashKey
    @DynamoDBAttribute(attributeName = "organizationId")
    private String organizationId;

    @DynamoDBAttribute
    private String organizationName;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "createdAt_organizationId_index")
    @DynamoDBTypeConverted(converter = DateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = DateConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifiedAt;
}