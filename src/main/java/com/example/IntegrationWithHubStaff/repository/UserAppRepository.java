package com.example.IntegrationWithHubStaff.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.IntegrationWithHubStaff.model.UserApp;
import com.example.IntegrationWithHubStaff.model.UserAppUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserAppRepository {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public UserApp save(UserApp userApp) {
        dynamoDBMapper.save(userApp);
        return userApp;
    }
    public List<UserApp> saveAll(List<UserApp> userAppList) {
        dynamoDBMapper.batchSave(userAppList);
        return userAppList;
    }

    public List<UserApp> findByUserId(String userId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":userId", new AttributeValue().withS(userId));

        DynamoDBQueryExpression<UserApp> queryExpression = new DynamoDBQueryExpression<UserApp>()
                .withKeyConditionExpression("userId = :userId")
                .withExpressionAttributeValues(eav);

        return dynamoDBMapper.query(UserApp.class, queryExpression);
    }
    public List<UserApp> findByOrganizationId(String organizationId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":organizationId", new AttributeValue().withS(organizationId));

        DynamoDBQueryExpression<UserApp> queryExpression = new DynamoDBQueryExpression<UserApp>()
                .withIndexName("organizationId_appName_index")
                .withKeyConditionExpression("organizationId = :organizationId")
                .withExpressionAttributeValues(eav)
                .withConsistentRead(false);

        return dynamoDBMapper.query(UserApp.class, queryExpression);
    }
}
