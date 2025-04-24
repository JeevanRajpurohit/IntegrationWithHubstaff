package com.example.IntegrationWithHubStaff.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.IntegrationWithHubStaff.model.Organization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class OrganizationRepository {
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public Organization save(Organization organization) {
        dynamoDBMapper.save(organization);
        return organization;
    }

    public Organization findByOrganizationId(String organizationId) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":organizationId", new AttributeValue().withS(organizationId));

        DynamoDBQueryExpression<Organization> queryExpression = new DynamoDBQueryExpression<Organization>()
                .withKeyConditionExpression("organizationId = :organizationId")
                .withExpressionAttributeValues(eav)
                .withConsistentRead(false);

        List<Organization> results = dynamoDBMapper.query(Organization.class, queryExpression);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<DynamoDBMapper.FailedBatch> saveAll(List<Organization> organizations) {
        return dynamoDBMapper.batchSave(organizations);
    }
    public List<Organization> getAllOrganizations(){
        return dynamoDBMapper.scan(Organization.class, new DynamoDBScanExpression());
    }

}