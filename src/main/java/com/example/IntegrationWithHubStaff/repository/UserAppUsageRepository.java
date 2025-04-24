package com.example.IntegrationWithHubStaff.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.IntegrationWithHubStaff.model.Organization;
import com.example.IntegrationWithHubStaff.model.UserAppUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserAppUsageRepository {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public UserAppUsage save(UserAppUsage userAppUsage) {
        dynamoDBMapper.save(userAppUsage);
        return userAppUsage;
    }
    public List<UserAppUsage> saveAll(List<UserAppUsage> userAppUsageList) {
        dynamoDBMapper.batchSave(userAppUsageList);
        return userAppUsageList;
    }

    public List<UserAppUsage> findByUserId(String userId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":userId", new AttributeValue().withS(userId));

        DynamoDBQueryExpression<UserAppUsage> queryExpression = new DynamoDBQueryExpression<UserAppUsage>()
                .withKeyConditionExpression("userId = :userId")
                .withExpressionAttributeValues(eav);


        return dynamoDBMapper.query(UserAppUsage.class, queryExpression);
    }


    public List<UserAppUsage> findByOrganizationIdAndAppName(String organizationId, String appName) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":organizationId", new AttributeValue().withS(organizationId));
        eav.put(":appName", new AttributeValue().withS(appName));

        DynamoDBQueryExpression<UserAppUsage> queryExpression = new DynamoDBQueryExpression<UserAppUsage>()
                .withIndexName("OrganizationAppIndex")
                .withKeyConditionExpression("organizationId = :organizationId and appName = :appName")
                .withExpressionAttributeValues(eav)
                .withConsistentRead(false);

        return dynamoDBMapper.query(UserAppUsage.class, queryExpression);
    }
    public List<UserAppUsage> findByProjectIdAndAppName(String projectId, String appName){
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":projectId", new AttributeValue().withS(projectId));
        eav.put(":appName",new AttributeValue().withS(appName));

        DynamoDBQueryExpression<UserAppUsage> queryExpression= new DynamoDBQueryExpression<UserAppUsage>()
                .withIndexName("ProjectAppIndex")
                .withKeyConditionExpression("projectId = :projectId")
                .withFilterExpression("appName = :appName")
                .withExpressionAttributeValues(eav)
                .withConsistentRead(false);
        return dynamoDBMapper.query(UserAppUsage.class, queryExpression);
    }

}