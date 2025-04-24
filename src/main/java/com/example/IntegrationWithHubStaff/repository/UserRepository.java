package com.example.IntegrationWithHubStaff.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.IntegrationWithHubStaff.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public User save(User user) {
        dynamoDBMapper.save(user);
        return user;
    }

    public List<User> saveAll(List<User> users) {
        dynamoDBMapper.batchSave(users);
        return users;
    }

    public User findByEmail(String email) {
        return dynamoDBMapper.load(User.class, email);
    }

    public List<User> findByOrganizationId(String organizationId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":organizationId", new AttributeValue().withS(organizationId));

        DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<User>()
                .withIndexName("organizationIdIndex")
                .withKeyConditionExpression("organizationId = :organizationId")
                .withExpressionAttributeValues(eav)
                .withConsistentRead(false);

        return dynamoDBMapper.query(User.class, queryExpression);
    }

}