// snippet-sourcedescription:[LowLevelTableExample.java demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.Java.CodeExample.LowLevelTableExample] 

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

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ListTablesRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;
import com.amazonaws.services.dynamodbv2.model.UpdateTableRequest;

public class LowLevelTableExample {

    static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
    static String tableName = "ExampleTable";

    public static void main(String[] args) throws Exception {

        createExampleTable();
        listMyTables();
        getTableInformation();
        updateExampleTable();

        deleteExampleTable();
    }

    static void createExampleTable() {

        // Provide the initial provisioned throughput values as Java long data
        // types
        ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput().withReadCapacityUnits(5L)
            .withWriteCapacityUnits(6L);
        CreateTableRequest request = new CreateTableRequest().withTableName(tableName)
            .withProvisionedThroughput(provisionedThroughput);

        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("Id").withAttributeType("N"));
        request.setAttributeDefinitions(attributeDefinitions);

        ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<KeySchemaElement>();
        tableKeySchema.add(new KeySchemaElement().withAttributeName("Id").withKeyType(KeyType.HASH)); // Partition
                                                                                                      // key
        request.setKeySchema(tableKeySchema);

        client.createTable(request);

        waitForTableToBecomeAvailable(tableName);

        getTableInformation();

    }

    static void listMyTables() {
        String lastEvaluatedTableName = null;
        do {

            ListTablesRequest listTablesRequest = new ListTablesRequest().withLimit(10)
                .withExclusiveStartTableName(lastEvaluatedTableName);

            ListTablesResult result = client.listTables(listTablesRequest);
            lastEvaluatedTableName = result.getLastEvaluatedTableName();

            for (String name : result.getTableNames()) {
                System.out.println(name);
            }

        } while (lastEvaluatedTableName != null);
    }

    static void getTableInformation() {

        TableDescription tableDescription = client.describeTable(new DescribeTableRequest().withTableName(tableName))
            .getTable();
        System.out.format(
            "Name: %s:\n" + "Status: %s \n" + "Provisioned Throughput (read capacity units/sec): %d \n"
                + "Provisioned Throughput (write capacity units/sec): %d \n",
            tableDescription.getTableName(), tableDescription.getTableStatus(),
            tableDescription.getProvisionedThroughput().getReadCapacityUnits(),
            tableDescription.getProvisionedThroughput().getWriteCapacityUnits());
    }

    static void updateExampleTable() {

        ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput().withReadCapacityUnits(6L)
            .withWriteCapacityUnits(7L);

        UpdateTableRequest updateTableRequest = new UpdateTableRequest().withTableName(tableName)
            .withProvisionedThroughput(provisionedThroughput);

        client.updateTable(updateTableRequest);
        waitForTableToBecomeAvailable(tableName);
    }

    static void deleteExampleTable() {
        DeleteTableRequest deleteTableRequest = new DeleteTableRequest().withTableName(tableName);
        client.deleteTable(deleteTableRequest);
        waitForTableToBeDeleted(tableName);
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
            }
        }
        throw new RuntimeException("Table " + tableName + " was never deleted");
    }

}
// snippet-end:[dynamodb.Java.CodeExample.LowLevelTableExample]