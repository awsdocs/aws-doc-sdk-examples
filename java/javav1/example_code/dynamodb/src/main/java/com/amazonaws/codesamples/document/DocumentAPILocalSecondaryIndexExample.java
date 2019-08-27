// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.java.codeexample.DocumentAPILocalSecondaryIndexExample] 
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
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity;
import com.amazonaws.services.dynamodbv2.model.Select;

public class DocumentAPILocalSecondaryIndexExample {

    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    static DynamoDB dynamoDB = new DynamoDB(client);

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

        // Attribute definitions for table partition and sort keys
        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("CustomerId").withAttributeType("S"));
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("OrderId").withAttributeType("N"));

        // Attribute definition for index primary key attributes
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
        System.out.println(dynamoDB.createTable(createTableRequest));

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

    public static void query(String indexName) {

        Table table = dynamoDB.getTable(tableName);

        System.out.println("\n***********************************************************\n");
        System.out.println("Querying table " + tableName + "...");

        QuerySpec querySpec = new QuerySpec().withConsistentRead(true).withScanIndexForward(true)
            .withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);

        if (indexName == "IsOpenIndex") {

            System.out.println("\nUsing index: '" + indexName + "': Bob's orders that are open.");
            System.out.println("Only a user-specified list of attributes are returned\n");
            Index index = table.getIndex(indexName);

            querySpec.withKeyConditionExpression("CustomerId = :v_custid and IsOpen = :v_isopen")
                .withValueMap(new ValueMap().withString(":v_custid", "bob@example.com").withNumber(":v_isopen", 1));

            querySpec.withProjectionExpression("OrderCreationDate, ProductCategory, ProductName, OrderStatus");

            ItemCollection<QueryOutcome> items = index.query(querySpec);
            Iterator<Item> iterator = items.iterator();

            System.out.println("Query: printing results...");

            while (iterator.hasNext()) {
                System.out.println(iterator.next().toJSONPretty());
            }

        }
        else if (indexName == "OrderCreationDateIndex") {
            System.out.println("\nUsing index: '" + indexName + "': Bob's orders that were placed after 01/31/2015.");
            System.out.println("Only the projected attributes are returned\n");
            Index index = table.getIndex(indexName);

            querySpec.withKeyConditionExpression("CustomerId = :v_custid and OrderCreationDate >= :v_orddate")
                .withValueMap(
                    new ValueMap().withString(":v_custid", "bob@example.com").withNumber(":v_orddate", 20150131));

            querySpec.withSelect(Select.ALL_PROJECTED_ATTRIBUTES);

            ItemCollection<QueryOutcome> items = index.query(querySpec);
            Iterator<Item> iterator = items.iterator();

            System.out.println("Query: printing results...");

            while (iterator.hasNext()) {
                System.out.println(iterator.next().toJSONPretty());
            }

        }
        else {
            System.out.println("\nNo index: All of Bob's orders, by OrderId:\n");

            querySpec.withKeyConditionExpression("CustomerId = :v_custid")
                .withValueMap(new ValueMap().withString(":v_custid", "bob@example.com"));

            ItemCollection<QueryOutcome> items = table.query(querySpec);
            Iterator<Item> iterator = items.iterator();

            System.out.println("Query: printing results...");

            while (iterator.hasNext()) {
                System.out.println(iterator.next().toJSONPretty());
            }

        }

    }

    public static void deleteTable(String tableName) {

        Table table = dynamoDB.getTable(tableName);
        System.out.println("Deleting table " + tableName + "...");
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

        Table table = dynamoDB.getTable(tableName);

        System.out.println("Loading data into table " + tableName + "...");

        Item item = new Item().withPrimaryKey("CustomerId", "alice@example.com").withNumber("OrderId", 1)
            .withNumber("IsOpen", 1).withNumber("OrderCreationDate", 20150101).withString("ProductCategory", "Book")
            .withString("ProductName", "The Great Outdoors").withString("OrderStatus", "PACKING ITEMS");
        // no ShipmentTrackingId attribute

        PutItemOutcome putItemOutcome = table.putItem(item);

        item = new Item().withPrimaryKey("CustomerId", "alice@example.com").withNumber("OrderId", 2)
            .withNumber("IsOpen", 1).withNumber("OrderCreationDate", 20150221).withString("ProductCategory", "Bike")
            .withString("ProductName", "Super Mountain").withString("OrderStatus", "ORDER RECEIVED");
        // no ShipmentTrackingId attribute

        putItemOutcome = table.putItem(item);

        item = new Item().withPrimaryKey("CustomerId", "alice@example.com").withNumber("OrderId", 3)
            // no IsOpen attribute
            .withNumber("OrderCreationDate", 20150304).withString("ProductCategory", "Music")
            .withString("ProductName", "A Quiet Interlude").withString("OrderStatus", "IN TRANSIT")
            .withString("ShipmentTrackingId", "176493");

        putItemOutcome = table.putItem(item);

        item = new Item().withPrimaryKey("CustomerId", "bob@example.com").withNumber("OrderId", 1)
            // no IsOpen attribute
            .withNumber("OrderCreationDate", 20150111).withString("ProductCategory", "Movie")
            .withString("ProductName", "Calm Before The Storm").withString("OrderStatus", "SHIPPING DELAY")
            .withString("ShipmentTrackingId", "859323");

        putItemOutcome = table.putItem(item);

        item = new Item().withPrimaryKey("CustomerId", "bob@example.com").withNumber("OrderId", 2)
            // no IsOpen attribute
            .withNumber("OrderCreationDate", 20150124).withString("ProductCategory", "Music")
            .withString("ProductName", "E-Z Listening").withString("OrderStatus", "DELIVERED")
            .withString("ShipmentTrackingId", "756943");

        putItemOutcome = table.putItem(item);

        item = new Item().withPrimaryKey("CustomerId", "bob@example.com").withNumber("OrderId", 3)
            // no IsOpen attribute
            .withNumber("OrderCreationDate", 20150221).withString("ProductCategory", "Music")
            .withString("ProductName", "Symphony 9").withString("OrderStatus", "DELIVERED")
            .withString("ShipmentTrackingId", "645193");

        putItemOutcome = table.putItem(item);

        item = new Item().withPrimaryKey("CustomerId", "bob@example.com").withNumber("OrderId", 4)
            .withNumber("IsOpen", 1).withNumber("OrderCreationDate", 20150222).withString("ProductCategory", "Hardware")
            .withString("ProductName", "Extra Heavy Hammer").withString("OrderStatus", "PACKING ITEMS");
        // no ShipmentTrackingId attribute

        putItemOutcome = table.putItem(item);

        item = new Item().withPrimaryKey("CustomerId", "bob@example.com").withNumber("OrderId", 5)
            /* no IsOpen attribute */
            .withNumber("OrderCreationDate", 20150309).withString("ProductCategory", "Book")
            .withString("ProductName", "How To Cook").withString("OrderStatus", "IN TRANSIT")
            .withString("ShipmentTrackingId", "440185");

        putItemOutcome = table.putItem(item);

        item = new Item().withPrimaryKey("CustomerId", "bob@example.com").withNumber("OrderId", 6)
            // no IsOpen attribute
            .withNumber("OrderCreationDate", 20150318).withString("ProductCategory", "Luggage")
            .withString("ProductName", "Really Big Suitcase").withString("OrderStatus", "DELIVERED")
            .withString("ShipmentTrackingId", "893927");

        putItemOutcome = table.putItem(item);

        item = new Item().withPrimaryKey("CustomerId", "bob@example.com").withNumber("OrderId", 7)
            /* no IsOpen attribute */
            .withNumber("OrderCreationDate", 20150324).withString("ProductCategory", "Golf")
            .withString("ProductName", "PGA Pro II").withString("OrderStatus", "OUT FOR DELIVERY")
            .withString("ShipmentTrackingId", "383283");

        putItemOutcome = table.putItem(item);
        assert putItemOutcome != null;
    }

}

// snippet-end:[dynamodb.java.codeexample.DocumentAPILocalSecondaryIndexExample] 