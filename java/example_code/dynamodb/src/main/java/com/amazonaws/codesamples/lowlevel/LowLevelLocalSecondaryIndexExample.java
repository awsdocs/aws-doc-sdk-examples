// snippet-sourcedescription:[LowLevelLocalSecondaryIndexExample.java demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.Java.CodeExample.LowLevelLocalSecondaryIndexExample] 

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
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity;
import com.amazonaws.services.dynamodbv2.model.ReturnItemCollectionMetrics;
import com.amazonaws.services.dynamodbv2.model.Select;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;

public class LowLevelLocalSecondaryIndexExample {

    static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());

    public static String tableName = "CustomerOrders";

    public static void main(String[] args) throws Exception {

        createTable();
        loadData();

        query(null);
        query("IsOpenIndex");
        query("OrderCreationDateIndex");

        deleteTable(tableName);

    }

    public static void createTable() {

        CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
            .withProvisionedThroughput(
                new ProvisionedThroughput().withReadCapacityUnits((long) 1).withWriteCapacityUnits((long) 1));

        // Attribute definitions for table partition key and sort key
        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("CustomerId").withAttributeType("S"));
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("OrderId").withAttributeType("N"));

        // Attribute definition for index sort key attributes
        attributeDefinitions
            .add(new AttributeDefinition().withAttributeName("OrderCreationDate").withAttributeType("N"));
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("IsOpen").withAttributeType("N"));

        createTableRequest.setAttributeDefinitions(attributeDefinitions);

        // Key schema for table
        ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<KeySchemaElement>();
        tableKeySchema.add(new KeySchemaElement().withAttributeName("CustomerId").withKeyType(KeyType.HASH)); // Partition
                                                                                                              // key
        tableKeySchema.add(new KeySchemaElement().withAttributeName("OrderId").withKeyType(KeyType.RANGE)); // Sort
                                                                                                            // key

        createTableRequest.setKeySchema(tableKeySchema);

        ArrayList<LocalSecondaryIndex> localSecondaryIndexes = new ArrayList<LocalSecondaryIndex>();

        // OrderCreationDateIndex
        LocalSecondaryIndex orderCreationDateIndex = new LocalSecondaryIndex().withIndexName("OrderCreationDateIndex");

        // Key schema for OrderCreationDateIndex
        ArrayList<KeySchemaElement> indexKeySchema = new ArrayList<KeySchemaElement>();
        indexKeySchema.add(new KeySchemaElement().withAttributeName("CustomerId").withKeyType(KeyType.HASH)); // Partition
                                                                                                              // key
        indexKeySchema.add(new KeySchemaElement().withAttributeName("OrderCreationDate").withKeyType(KeyType.RANGE)); // Sort
                                                                                                                      // key

        orderCreationDateIndex.setKeySchema(indexKeySchema);

        // Projection (with list of projected attributes) for
        // OrderCreationDateIndex
        Projection projection = new Projection().withProjectionType(ProjectionType.INCLUDE);
        ArrayList<String> nonKeyAttributes = new ArrayList<String>();
        nonKeyAttributes.add("ProductCategory");
        nonKeyAttributes.add("ProductName");
        projection.setNonKeyAttributes(nonKeyAttributes);

        orderCreationDateIndex.setProjection(projection);

        localSecondaryIndexes.add(orderCreationDateIndex);

        // IsOpenIndex
        LocalSecondaryIndex isOpenIndex = new LocalSecondaryIndex().withIndexName("IsOpenIndex");

        // Key schema for IsOpenIndex
        indexKeySchema = new ArrayList<KeySchemaElement>();
        indexKeySchema.add(new KeySchemaElement().withAttributeName("CustomerId").withKeyType(KeyType.HASH)); // Partition
                                                                                                              // key
        indexKeySchema.add(new KeySchemaElement().withAttributeName("IsOpen").withKeyType(KeyType.RANGE)); // Sort
                                                                                                           // key

        // Projection (all attributes) for IsOpenIndex
        projection = new Projection().withProjectionType(ProjectionType.ALL);

        isOpenIndex.setKeySchema(indexKeySchema);
        isOpenIndex.setProjection(projection);

        localSecondaryIndexes.add(isOpenIndex);

        // Add index definitions to CreateTable request
        createTableRequest.setLocalSecondaryIndexes(localSecondaryIndexes);

        System.out.println("Creating table " + tableName + "...");
        System.out.println(client.createTable(createTableRequest));
        waitForTableToBecomeAvailable(tableName);
    }

    public static void query(String indexName) {

        System.out.println("\n***********************************************************\n");
        System.out.println("Querying table " + tableName + "...");

        QueryRequest queryRequest = new QueryRequest().withTableName(tableName).withConsistentRead(true)
            .withScanIndexForward(true).withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);

        HashMap<String, Condition> keyConditions = new HashMap<String, Condition>();

        keyConditions.put("CustomerId", new Condition().withComparisonOperator(ComparisonOperator.EQ)
            .withAttributeValueList(new AttributeValue().withS("bob@example.com")));

        if (indexName == "IsOpenIndex") {
            System.out.println("\nUsing index: '" + indexName + "': Bob's orders that are open.");
            System.out.println("Only a user-specified list of attributes are returned\n");
            queryRequest.setIndexName(indexName);

            keyConditions.put("IsOpen", new Condition().withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withN("1")));

            // ProjectionExpression
            queryRequest.setProjectionExpression("OrderCreationDate, ProductCategory, ProductName, OrderStatus");

        }
        else if (indexName == "OrderCreationDateIndex") {
            System.out.println("\nUsing index: '" + indexName + "': Bob's orders that were placed after 01/31/2013.");
            System.out.println("Only the projected attributes are returned\n");
            queryRequest.setIndexName(indexName);

            keyConditions.put("OrderCreationDate", new Condition().withComparisonOperator(ComparisonOperator.GT)
                .withAttributeValueList(new AttributeValue().withN("20130131")));

            // Select
            queryRequest.setSelect(Select.ALL_PROJECTED_ATTRIBUTES);

        }
        else {
            System.out.println("\nNo index: All of Bob's orders, by OrderId:\n");
        }

        queryRequest.setKeyConditions(keyConditions);

        QueryResult result = client.query(queryRequest);
        List<Map<String, AttributeValue>> items = result.getItems();
        Iterator<Map<String, AttributeValue>> itemsIter = items.iterator();
        while (itemsIter.hasNext()) {
            Map<String, AttributeValue> currentItem = itemsIter.next();

            Iterator<String> currentItemIter = currentItem.keySet().iterator();
            while (currentItemIter.hasNext()) {
                String attr = (String) currentItemIter.next();
                if (attr == "OrderId" || attr == "IsOpen" || attr == "OrderCreationDate") {
                    System.out.println(attr + "---> " + currentItem.get(attr).getN());
                }
                else {
                    System.out.println(attr + "---> " + currentItem.get(attr).getS());
                }
            }
            System.out.println();
        }
        System.out.println("\nConsumed capacity: " + result.getConsumedCapacity() + "\n");

    }

    public static void deleteTable(String tableName) {
        System.out.println("Deleting table " + tableName + "...");
        client.deleteTable(new DeleteTableRequest().withTableName(tableName));
        waitForTableToBeDeleted(tableName);
    }

    public static void loadData() {
        System.out.println("Loading data into table " + tableName + "...");

        HashMap<String, AttributeValue> item = new HashMap<String, AttributeValue>();

        item.put("CustomerId", new AttributeValue().withS("alice@example.com"));
        item.put("OrderId", new AttributeValue().withN("1"));
        item.put("IsOpen", new AttributeValue().withN("1"));
        item.put("OrderCreationDate", new AttributeValue().withN("20130101"));
        item.put("ProductCategory", new AttributeValue().withS(" Book"));
        item.put("ProductName", new AttributeValue().withS(" The Great Outdoors"));
        item.put("OrderStatus", new AttributeValue().withS("PACKING ITEMS"));
        /* no ShipmentTrackingId attribute */
        PutItemRequest putItemRequest = new PutItemRequest().withTableName(tableName).withItem(item)
            .withReturnItemCollectionMetrics(ReturnItemCollectionMetrics.SIZE);
        PutItemResult result = client.putItem(putItemRequest);
        System.out.println("Item collection metrics: " + result.getItemCollectionMetrics());

        item = new HashMap<String, AttributeValue>();
        item.put("CustomerId", new AttributeValue().withS("alice@example.com"));
        item.put("OrderId", new AttributeValue().withN("2"));
        item.put("IsOpen", new AttributeValue().withN("1"));
        item.put("OrderCreationDate", new AttributeValue().withN("20130221"));
        item.put("ProductCategory", new AttributeValue().withS("Bike"));
        item.put("ProductName", new AttributeValue().withS("Super Mountain"));
        item.put("OrderStatus", new AttributeValue().withS("ORDER RECEIVED"));
        /* no ShipmentTrackingId attribute */
        putItemRequest = new PutItemRequest().withTableName(tableName).withItem(item)
            .withReturnItemCollectionMetrics(ReturnItemCollectionMetrics.SIZE);
        result = client.putItem(putItemRequest);
        System.out.println("Item collection metrics: " + result.getItemCollectionMetrics());

        item = new HashMap<String, AttributeValue>();
        item.put("CustomerId", new AttributeValue().withS("alice@example.com"));
        item.put("OrderId", new AttributeValue().withN("3"));
        /* no IsOpen attribute */
        item.put("OrderCreationDate", new AttributeValue().withN("20130304"));
        item.put("ProductCategory", new AttributeValue().withS("Music"));
        item.put("ProductName", new AttributeValue().withS("A Quiet Interlude"));
        item.put("OrderStatus", new AttributeValue().withS("IN TRANSIT"));
        item.put("ShipmentTrackingId", new AttributeValue().withS("176493"));
        putItemRequest = new PutItemRequest().withTableName(tableName).withItem(item)
            .withReturnItemCollectionMetrics(ReturnItemCollectionMetrics.SIZE);
        result = client.putItem(putItemRequest);
        System.out.println("Item collection metrics: " + result.getItemCollectionMetrics());

        item = new HashMap<String, AttributeValue>();
        item.put("CustomerId", new AttributeValue().withS("bob@example.com"));
        item.put("OrderId", new AttributeValue().withN("1"));
        /* no IsOpen attribute */
        item.put("OrderCreationDate", new AttributeValue().withN("20130111"));
        item.put("ProductCategory", new AttributeValue().withS("Movie"));
        item.put("ProductName", new AttributeValue().withS("Calm Before The Storm"));
        item.put("OrderStatus", new AttributeValue().withS("SHIPPING DELAY"));
        item.put("ShipmentTrackingId", new AttributeValue().withS("859323"));
        putItemRequest = new PutItemRequest().withTableName(tableName).withItem(item)
            .withReturnItemCollectionMetrics(ReturnItemCollectionMetrics.SIZE);
        result = client.putItem(putItemRequest);
        System.out.println("Item collection metrics: " + result.getItemCollectionMetrics());

        item = new HashMap<String, AttributeValue>();
        item.put("CustomerId", new AttributeValue().withS("bob@example.com"));
        item.put("OrderId", new AttributeValue().withN("2"));
        /* no IsOpen attribute */
        item.put("OrderCreationDate", new AttributeValue().withN("20130124"));
        item.put("ProductCategory", new AttributeValue().withS("Music"));
        item.put("ProductName", new AttributeValue().withS("E-Z Listening"));
        item.put("OrderStatus", new AttributeValue().withS("DELIVERED"));
        item.put("ShipmentTrackingId", new AttributeValue().withS("756943"));
        putItemRequest = new PutItemRequest().withTableName(tableName).withItem(item)
            .withReturnItemCollectionMetrics(ReturnItemCollectionMetrics.SIZE);
        result = client.putItem(putItemRequest);
        System.out.println("Item collection metrics: " + result.getItemCollectionMetrics());

        item = new HashMap<String, AttributeValue>();
        item.put("CustomerId", new AttributeValue().withS("bob@example.com"));
        item.put("OrderId", new AttributeValue().withN("3"));
        /* no IsOpen attribute */
        item.put("OrderCreationDate", new AttributeValue().withN("20130221"));
        item.put("ProductCategory", new AttributeValue().withS("Music"));
        item.put("ProductName", new AttributeValue().withS("Symphony 9"));
        item.put("OrderStatus", new AttributeValue().withS("DELIVERED"));
        item.put("ShipmentTrackingId", new AttributeValue().withS("645193"));
        putItemRequest = new PutItemRequest().withTableName(tableName).withItem(item)
            .withReturnItemCollectionMetrics(ReturnItemCollectionMetrics.SIZE);
        result = client.putItem(putItemRequest);
        System.out.println("Item collection metrics: " + result.getItemCollectionMetrics());

        item = new HashMap<String, AttributeValue>();
        item.put("CustomerId", new AttributeValue().withS("bob@example.com"));
        item.put("OrderId", new AttributeValue().withN("4"));
        item.put("IsOpen", new AttributeValue().withN("1"));
        item.put("OrderCreationDate", new AttributeValue().withN("20130222"));
        item.put("ProductCategory", new AttributeValue().withS("Hardware"));
        item.put("ProductName", new AttributeValue().withS("Extra Heavy Hammer"));
        item.put("OrderStatus", new AttributeValue().withS("PACKING ITEMS"));
        /* no ShipmentTrackingId attribute */
        putItemRequest = new PutItemRequest().withTableName(tableName).withItem(item)
            .withReturnItemCollectionMetrics(ReturnItemCollectionMetrics.SIZE);
        result = client.putItem(putItemRequest);
        System.out.println("Item collection metrics: " + result.getItemCollectionMetrics());

        item = new HashMap<String, AttributeValue>();
        item.put("CustomerId", new AttributeValue().withS("bob@example.com"));
        item.put("OrderId", new AttributeValue().withN("5"));
        /* no IsOpen attribute */
        item.put("OrderCreationDate", new AttributeValue().withN("20130309"));
        item.put("ProductCategory", new AttributeValue().withS("Book"));
        item.put("ProductName", new AttributeValue().withS("How To Cook"));
        item.put("OrderStatus", new AttributeValue().withS("IN TRANSIT"));
        item.put("ShipmentTrackingId", new AttributeValue().withS("440185"));
        putItemRequest = new PutItemRequest().withTableName(tableName).withItem(item)
            .withReturnItemCollectionMetrics(ReturnItemCollectionMetrics.SIZE);
        result = client.putItem(putItemRequest);
        System.out.println("Item collection metrics: " + result.getItemCollectionMetrics());

        item = new HashMap<String, AttributeValue>();
        item.put("CustomerId", new AttributeValue().withS("bob@example.com"));
        item.put("OrderId", new AttributeValue().withN("6"));
        /* no IsOpen attribute */
        item.put("OrderCreationDate", new AttributeValue().withN("20130318"));
        item.put("ProductCategory", new AttributeValue().withS("Luggage"));
        item.put("ProductName", new AttributeValue().withS("Really Big Suitcase"));
        item.put("OrderStatus", new AttributeValue().withS("DELIVERED"));
        item.put("ShipmentTrackingId", new AttributeValue().withS("893927"));
        putItemRequest = new PutItemRequest().withTableName(tableName).withItem(item)
            .withReturnItemCollectionMetrics(ReturnItemCollectionMetrics.SIZE);
        result = client.putItem(putItemRequest);
        System.out.println("Item collection metrics: " + result.getItemCollectionMetrics());

        item = new HashMap<String, AttributeValue>();
        item.put("CustomerId", new AttributeValue().withS("bob@example.com"));
        item.put("OrderId", new AttributeValue().withN("7"));
        /* no IsOpen attribute */
        item.put("OrderCreationDate", new AttributeValue().withN("20130324"));
        item.put("ProductCategory", new AttributeValue().withS("Golf"));
        item.put("ProductName", new AttributeValue().withS("PGA Pro II"));
        item.put("OrderStatus", new AttributeValue().withS("OUT FOR DELIVERY"));
        item.put("ShipmentTrackingId", new AttributeValue().withS("383283"));
        putItemRequest = new PutItemRequest().withTableName(tableName).withItem(item)
            .withReturnItemCollectionMetrics(ReturnItemCollectionMetrics.SIZE);
        result = client.putItem(putItemRequest);
        System.out.println("Item collection metrics: " + result.getItemCollectionMetrics());

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
// snippet-end:[dynamodb.Java.CodeExample.LowLevelLocalSecondaryIndexExample]