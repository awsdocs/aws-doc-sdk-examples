// snippet-sourcedescription:[LowLevelGlobalSecondaryIndexExample.java demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.Java.CodeExample.ca489b9b-6254-4df4-b738-f7a21cc1ceb2] 

/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/


package com.amazonaws.codesamples.lowlevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.Select;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;

public class LowLevelGlobalSecondaryIndexExample {

    static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());

    public static String tableName = "Issues";

    public static void main(String[] args) throws Exception {

        createTable();
        loadData();

        queryIndex("CreateDateIndex");
        queryIndex("TitleIndex");
        queryIndex("DueDateIndex");

        deleteTable(tableName);

    }

    public static void createTable() {

        // Attribute definitions
        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();

        attributeDefinitions.add(new AttributeDefinition().withAttributeName("IssueId").withAttributeType("S"));
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("Title").withAttributeType("S"));
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("CreateDate").withAttributeType("S"));
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("DueDate").withAttributeType("S"));

        // Key schema for table
        ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<KeySchemaElement>();
        tableKeySchema.add(new KeySchemaElement().withAttributeName("IssueId").withKeyType(KeyType.HASH)); // Partition
                                                                                                           // key
        tableKeySchema.add(new KeySchemaElement().withAttributeName("Title").withKeyType(KeyType.RANGE)); // Sort
                                                                                                          // key

        // Initial provisioned throughput settings for the indexes
        ProvisionedThroughput ptIndex = new ProvisionedThroughput().withReadCapacityUnits(1L)
            .withWriteCapacityUnits(1L);

        // CreateDateIndex
        GlobalSecondaryIndex createDateIndex = new GlobalSecondaryIndex().withIndexName("CreateDateIndex")
            .withProvisionedThroughput(ptIndex)
            .withKeySchema(new KeySchemaElement().withAttributeName("CreateDate").withKeyType(KeyType.HASH), // Partition
                                                                                                             // key
                new KeySchemaElement().withAttributeName("IssueId").withKeyType(KeyType.RANGE)) // Sort
                                                                                                // key
            .withProjection(
                new Projection().withProjectionType("INCLUDE").withNonKeyAttributes("Description", "Status"));

        // TitleIndex
        GlobalSecondaryIndex titleIndex = new GlobalSecondaryIndex().withIndexName("TitleIndex")
            .withProvisionedThroughput(ptIndex)
            .withKeySchema(new KeySchemaElement().withAttributeName("Title").withKeyType(KeyType.HASH), // Partition
                                                                                                        // key
                new KeySchemaElement().withAttributeName("IssueId").withKeyType(KeyType.RANGE)) // Sort
                                                                                                // key
            .withProjection(new Projection().withProjectionType("KEYS_ONLY"));

        // DueDateIndex
        GlobalSecondaryIndex dueDateIndex = new GlobalSecondaryIndex().withIndexName("DueDateIndex")
            .withProvisionedThroughput(ptIndex)
            .withKeySchema(new KeySchemaElement().withAttributeName("DueDate").withKeyType(KeyType.HASH)) // Partition
                                                                                                          // key
            .withProjection(new Projection().withProjectionType("ALL"));

        CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
            .withProvisionedThroughput(
                new ProvisionedThroughput().withReadCapacityUnits((long) 1).withWriteCapacityUnits((long) 1))
            .withAttributeDefinitions(attributeDefinitions).withKeySchema(tableKeySchema)
            .withGlobalSecondaryIndexes(createDateIndex, titleIndex, dueDateIndex);

        System.out.println("Creating table " + tableName + "...");
        System.out.println(client.createTable(createTableRequest));
        waitForTableToBecomeAvailable(tableName);
    }

    public static void queryIndex(String indexName) {

        System.out.println("\n***********************************************************\n");
        System.out.print("Querying index " + indexName + "...");

        QueryRequest queryRequest = new QueryRequest().withTableName(tableName).withIndexName(indexName)
            .withScanIndexForward(true);

        HashMap<String, Condition> keyConditions = new HashMap<String, Condition>();

        if (indexName == "CreateDateIndex") {
            System.out.println("Issues filed on 2013-11-01");
            keyConditions.put("CreateDate", new Condition().withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS("2013-11-01")));
            keyConditions.put("IssueId", new Condition().withComparisonOperator(ComparisonOperator.BEGINS_WITH)
                .withAttributeValueList(new AttributeValue().withS("A-")));

        }
        else if (indexName == "TitleIndex") {
            System.out.println("Compilation errors");

            keyConditions.put("Title", new Condition().withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS("Compilation error")));
            keyConditions.put("IssueId", new Condition().withComparisonOperator(ComparisonOperator.BEGINS_WITH)
                .withAttributeValueList(new AttributeValue().withS("A-")));

            // Select
            queryRequest.setSelect(Select.ALL_PROJECTED_ATTRIBUTES);

        }
        else if (indexName == "DueDateIndex") {
            System.out.println("Items that are due on 2013-11-30");

            keyConditions.put("DueDate", new Condition().withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS("2013-11-30")));

            // Select
            queryRequest.setSelect(Select.ALL_PROJECTED_ATTRIBUTES);

        }
        else {
            System.out.println("\nNo valid index name provided");
            return;
        }

        queryRequest.setKeyConditions(keyConditions);

        QueryResult result = client.query(queryRequest);

        List<Map<String, AttributeValue>> items = result.getItems();
        Iterator<Map<String, AttributeValue>> itemsIter = items.iterator();

        System.out.println();

        while (itemsIter.hasNext()) {
            Map<String, AttributeValue> currentItem = itemsIter.next();

            Iterator<String> currentItemIter = currentItem.keySet().iterator();
            while (currentItemIter.hasNext()) {
                String attr = (String) currentItemIter.next();
                if (attr == "Priority") {
                    System.out.println(attr + "---> " + currentItem.get(attr).getN());
                }
                else {
                    System.out.println(attr + "---> " + currentItem.get(attr).getS());
                }
            }
            System.out.println();
        }

    }

    public static void deleteTable(String tableName) {
        System.out.println("Deleting table " + tableName + "...");
        client.deleteTable(new DeleteTableRequest().withTableName(tableName));
        waitForTableToBeDeleted(tableName);
    }

    public static void loadData() {

        System.out.println("Loading data into table " + tableName + "...");

        // IssueId, Title,
        // Description,
        // CreateDate, LastUpdateDate, DueDate,
        // Priority, Status

        putItem("A-101", "Compilation error", "Can't compile Project X - bad version number. What does this mean?",
            "2013-11-01", "2013-11-02", "2013-11-10", 1, "Assigned");

        putItem("A-102", "Can't read data file", "The main data file is missing, or the permissions are incorrect",
            "2013-11-01", "2013-11-04", "2013-11-30", 2, "In progress");

        putItem("A-103", "Test failure", "Functional test of Project X produces errors", "2013-11-01", "2013-11-02",
            "2013-11-10", 1, "In progress");

        putItem("A-104", "Compilation error", "Variable 'messageCount' was not initialized.", "2013-11-15",
            "2013-11-16", "2013-11-30", 3, "Assigned");

        putItem("A-105", "Network issue", "Can't ping IP address 127.0.0.1. Please fix this.", "2013-11-15",
            "2013-11-16", "2013-11-19", 5, "Assigned");

    }

    public static void putItem(String issueId, String title, String description, String createDate,
        String lastUpdateDate, String dueDate, Integer priority, String status) {

        HashMap<String, AttributeValue> item = new HashMap<String, AttributeValue>();

        item.put("IssueId", new AttributeValue().withS(issueId));
        item.put("Title", new AttributeValue().withS(title));
        item.put("Description", new AttributeValue().withS(description));
        item.put("CreateDate", new AttributeValue().withS(createDate));
        item.put("LastUpdateDate", new AttributeValue().withS(lastUpdateDate));
        item.put("DueDate", new AttributeValue().withS(dueDate));
        item.put("Priority", new AttributeValue().withN(priority.toString()));
        item.put("Status", new AttributeValue().withS(status));

        try {
            client.putItem(new PutItemRequest().withTableName(tableName).withItem(item));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void waitForTableToBecomeAvailable(String tableName) {
        System.out.println("Waiting for " + tableName + " to become ACTIVE...");

        long startTime = System.currentTimeMillis();
        long endTime = startTime + (10 * 60 * 1000);

        while (System.currentTimeMillis() < endTime) {
            DescribeTableRequest request = new DescribeTableRequest().withTableName(tableName);
            TableDescription tableDescription = client.describeTable(request).getTable();
            String tableStatus = tableDescription.getTableStatus();

            System.out.println("  - current state: " + tableStatus);

            if (tableStatus.equals(TableStatus.ACTIVE.toString()))
                return;
            try {
                Thread.sleep(1000 * 20);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Table " + tableName + " never went active");
    }

    private static void waitForTableToBeDeleted(String tableName) {

        System.out.println("Waiting for " + tableName + " while status DELETING...");

        long startTime = System.currentTimeMillis();
        long endTime = startTime + (10 * 60 * 1000);

        while (System.currentTimeMillis() < endTime) {
            try {
                DescribeTableRequest request = new DescribeTableRequest().withTableName(tableName);
                TableDescription tableDescription = client.describeTable(request).getTable();
                String tableStatus = tableDescription.getTableStatus();
                System.out.println("  - current state: " + tableStatus);

                if (tableStatus.equals(TableStatus.ACTIVE.toString()))
                    return;
            }
            catch (ResourceNotFoundException e) {
                System.out.println("Table " + tableName + " is not found. It was deleted.");
                return;
            }
            try {
                Thread.sleep(1000 * 20);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Table " + tableName + " was never deleted");
    }

}
// snippet-end:[dynamodb.Java.CodeExample.ca489b9b-6254-4df4-b738-f7a21cc1ceb2]