package com.example.IntegrationWithHubStaff.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.IntegrationWithHubStaff.config.AppConfig;
import com.example.IntegrationWithHubStaff.model.AuthToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class AuthTokenRepository {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AppConfig hubstaffConfig;

    public AuthToken save(AuthToken authToken) {
        dynamoDBMapper.save(authToken);
        return authToken;
    }

    public List<AuthToken> findAll() {
        return dynamoDBMapper.scan(AuthToken.class, new DynamoDBScanExpression());
    }
    public AuthToken findByClientId(String clientId) {
        return dynamoDBMapper.load(AuthToken.class,clientId);
    }

}