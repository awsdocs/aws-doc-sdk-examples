// snippet-sourcedescription:[DocumentAPIGlobalSecondaryIndexExample.java demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.Java.CodeExample.DocumentAPIGlobalSecondaryIndexExample] 

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


package com.amazonaws.codesamples.document;

import java.util.ArrayList;
import java.util.Iterator;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

public class DocumentAPIGlobalSecondaryIndexExample {

    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    static DynamoDB dynamoDB = new DynamoDB(client);

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
        dynamoDB.createTable(createTableRequest);

        // Wait for table to become active
        System.out.println("Waiting for " + tableName + " to become ACTIVE...");
        try {
            Table table = dynamoDB.getTable(tableName);
            table.waitForActive();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void queryIndex(String indexName) {

        Table table = dynamoDB.getTable(tableName);

        System.out.println("\n***********************************************************\n");
        System.out.print("Querying index " + indexName + "...");

        Index index = table.getIndex(indexName);

        ItemCollection<QueryOutcome> items = null;

        QuerySpec querySpec = new QuerySpec();

        if (indexName == "CreateDateIndex") {
            System.out.println("Issues filed on 2013-11-01");
            querySpec.withKeyConditionExpression("CreateDate = :v_date and begins_with(IssueId, :v_issue)")
                .withValueMap(new ValueMap().withString(":v_date", "2013-11-01").withString(":v_issue", "A-"));
            items = index.query(querySpec);
        }
        else if (indexName == "TitleIndex") {
            System.out.println("Compilation errors");
            querySpec.withKeyConditionExpression("Title = :v_title and begins_with(IssueId, :v_issue)")
                .withValueMap(new ValueMap().withString(":v_title", "Compilation error").withString(":v_issue", "A-"));
            items = index.query(querySpec);
        }
        else if (indexName == "DueDateIndex") {
            System.out.println("Items that are due on 2013-11-30");
            querySpec.withKeyConditionExpression("DueDate = :v_date")
                .withValueMap(new ValueMap().withString(":v_date", "2013-11-30"));
            items = index.query(querySpec);
        }
        else {
            System.out.println("\nNo valid index name provided");
            return;
        }

        Iterator<Item> iterator = items.iterator();

        System.out.println("Query: printing results...");

        while (iterator.hasNext()) {
            System.out.println(iterator.next().toJSONPretty());
        }

    }

    public static void deleteTable(String tableName) {

        System.out.println("Deleting table " + tableName + "...");

        Table table = dynamoDB.getTable(tableName);
        table.delete();

        // Wait for table to be deleted
        System.out.println("Waiting for " + tableName + " to be deleted...");
        try {
            table.waitForDelete();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    public static void putItem(

        String issueId, String title, String description, String createDate, String lastUpdateDate, String dueDate,
        Integer priority, String status) {

        Table table = dynamoDB.getTable(tableName);

        Item item = new Item().withPrimaryKey("IssueId", issueId).withString("Title", title)
            .withString("Description", description).withString("CreateDate", createDate)
            .withString("LastUpdateDate", lastUpdateDate).withString("DueDate", dueDate)
            .withNumber("Priority", priority).withString("Status", status);

        table.putItem(item);
    }

}
// snippet-end:[dynamodb.Java.CodeExample.DocumentAPIGlobalSecondaryIndexExample]