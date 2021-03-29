//snippet-sourcedescription:[EnhancedScanRecordsWithExpression.java demonstrates how to scan and query an Amazon DynamoDB table by using the enhanced client and a software.amazon.awssdk.enhanced.dynamodb.Expression object.]
//snippet-keyword:[SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/29/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.dynamodb;

// snippet-start:[dynamodb.java2.mapping.scanEx.import]
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.Select;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
// snippet-end:[dynamodb.java2.mapping.scanEx.import]

public class EnhancedScanRecordsWithExpression {

    public static void main(String[] args) {

        String tableName = "Issues";
        System.out.format(
                "Creating table \"%s\" with a simple primary key: \"Name\".\n",
                tableName);

        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .build();

        createTable(ddb, tableName);
        loadData(ddb, tableName);
        scanIndex(ddb, tableName, "CreateDateIndex");
        scanUsingContains(ddb, tableName, "CreateDateIndex");
        queryIndex(ddb, tableName, "CreateDateIndex");
        ddb.close();
    }

    // Query the table using a secondary index.
    public static void queryIndex(DynamoDbClient ddb, String tableName, String indexName) {

        try {
            // Create a DynamoDbEnhancedClient and use the DynamoDbClient object.
            DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(ddb)
                    .build();

            // Create a DynamoDbTable object based on Issues.
            DynamoDbTable<Issues> table = enhancedClient.table("Issues", TableSchema.fromBean(Issues.class));
            String dateVal = "2013-11-19";
            DynamoDbIndex<Issues> secIndex =
                    enhancedClient.table("Issues",
                            TableSchema.fromBean(Issues.class))
                           .index("dueDateIndex");

            AttributeValue attVal = AttributeValue.builder()
                    .s(dateVal)
                    .build();

             // Create a QueryConditional object that's used in the query operation.
            QueryConditional queryConditional = QueryConditional
                    .keyEqualTo(Key.builder().partitionValue(attVal)
                            .build());

            // Get items in the Issues table.
            SdkIterable<Page<Issues>> results =  secIndex.query(
                    QueryEnhancedRequest.builder()
                            .queryConditional(queryConditional)
                            .build());

            AtomicInteger atomicInteger = new AtomicInteger();
            atomicInteger.set(0);
            results.forEach(page -> {

                Issues issue = (Issues) page.items().get(atomicInteger.get());
                System.out.println("The issue title is "+issue.getTitle());
                atomicInteger.incrementAndGet();
            });

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    // snippet-start:[dynamodb.java2.mapping.scanEx.main]
    // Scan the table and retrieve only items where createDate is 2013-11-15.
    public static void scanIndex(DynamoDbClient ddb, String tableName, String indexName) {

        System.out.println("\n***********************************************************\n");
        System.out.print("Select items for "+tableName +" where createDate is 2013-11-15!");

        try {
            // Create a DynamoDbEnhancedClient and use the DynamoDbClient object.
            DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(ddb)
                    .build();

            // Create a DynamoDbTable object based on Issues.
            DynamoDbTable<Issues> table = enhancedClient.table("Issues", TableSchema.fromBean(Issues.class));

            // Setup the scan based on the index.
            if (indexName == "CreateDateIndex") {
                System.out.println("Issues filed on 2013-11-15");

                AttributeValue attVal = AttributeValue.builder()
                        .s("2013-11-15")
                        .build();

                // Get only items in the Issues table for 2013-11-15.
                Map<String, AttributeValue> myMap = new HashMap<>();
                myMap.put(":val1", attVal);

                Map<String, String> myExMap = new HashMap<>();
                myExMap.put("#createDate", "createDate");

                Expression expression = Expression.builder()
                        .expressionValues(myMap)
                        .expressionNames(myExMap)
                        .expression("#createDate = :val1")
                        .build();

                ScanEnhancedRequest enhancedRequest = ScanEnhancedRequest.builder()
                        .filterExpression(expression)
                        .limit(15)
                        .build();

                // Get items in the Issues table.
                Iterator<Issues> results = table.scan(enhancedRequest).items().iterator();

                while (results.hasNext()) {
                    Issues issue = results.next();
                    System.out.println("The record description is " + issue.getDescription());
                    System.out.println("The record title is " + issue.getTitle());
                }
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[dynamodb.java2.mapping.scanEx.main]


   // Scan table for records where title contains the word issues using the contains function
    public static void scanUsingContains(DynamoDbClient ddb, String tableName, String indexName) {

        System.out.println("\n***********************************************************\n");
        System.out.print("Select items for "+tableName +" where title contains the word issues");

        try {
            // Create a DynamoDbEnhancedClient and use the DynamoDbClient object.
            DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(ddb)
                    .build();

            // Create a DynamoDbTable object based on Issues.
            DynamoDbTable<Issues> table = enhancedClient.table("Issues", TableSchema.fromBean(Issues.class));

            AttributeValue attVal = AttributeValue.builder()
                    .s("issue")
                    .build();

            Map<String, AttributeValue> myMap = new HashMap<>();
            myMap.put(":val1", attVal);

            Map<String, String> myExMap = new HashMap<>();
            myExMap.put("#title", "title");

            Expression expression = Expression.builder()
                    .expressionValues(myMap)
                    .expressionNames(myExMap)
                    .expression("contains(#title, :val1)")
                    .build();

            ScanEnhancedRequest enhancedRequest = ScanEnhancedRequest.builder()
                    .filterExpression(expression)
                    .limit(15)
                    .build();

            // Get items in the Issues table.
            Iterator<Issues> results = table.scan(enhancedRequest).items().iterator();

            while (results.hasNext()) {
                Issues issue = results.next();
                System.out.println("The record description is " + issue.getDescription());
                System.out.println("The record title is " + issue.getTitle());
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    // Load data into the table.
    public static void loadData(DynamoDbClient ddb, String tableName) {
        System.out.println("Loading data into table " + tableName + "...");

        putItem(ddb, tableName, "A-101", "Compilation error", "Can't compile Project X - bad version number. What does this mean?",
                "2013-11-01", "2013-11-02", "2013-11-10", 1, "Assigned");

        putItem(ddb, tableName,"A-102", "Can't read data file", "The main data file is missing, or the permissions are incorrect",
                "2013-11-01", "2013-11-04", "2013-11-30", 2, "In progress");

        putItem(ddb, tableName,"A-103", "Test failure", "Functional test of Project X produces errors", "2013-11-01", "2013-11-02",
                "2013-11-10", 1, "In progress");

        putItem(ddb, tableName,"A-104", "Compilation error", "Variable 'messageCount' was not initialized.", "2013-11-15",
                "2013-11-16", "2013-11-30", 3, "Assigned");

        putItem(ddb, tableName,"A-105", "Network issue", "Can't ping IP address 127.0.0.1. Please fix this.", "2013-11-15",
                "2013-11-16", "2013-11-19", 5, "Assigned");
    }

    // Populate the table with data.
    public static void putItem(DynamoDbClient ddb,
                               String tableName,
                               String issueId,
                               String title,
                               String description,
                               String createDate,
                               String lastUpdateDate,
                               String dueDate,
                               Integer priority,
                               String status) {

        HashMap<String, AttributeValue> item = new HashMap<String, AttributeValue>();

        item.put("issueId", AttributeValue.builder().s(issueId).build());
        item.put("title", AttributeValue.builder().s(title).build());
        item.put("description",  AttributeValue.builder().s(description).build());
        item.put("createDate", AttributeValue.builder().s(createDate).build());
        item.put("lastUpdateDate", AttributeValue.builder().s(lastUpdateDate).build());
        item.put("dueDate", AttributeValue.builder().s(dueDate).build());
        item.put("priority", AttributeValue.builder().n(priority.toString()).build());
        item.put("status", AttributeValue.builder().s(status).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();

        try {
            ddb.putItem(request);
            System.out.println(tableName +" was successfully updated");

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    // Create a table with indexes.
    public static void createTable(DynamoDbClient ddb, String tableName) {

        try {
            // Attribute definitions
            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();

            // Define attributes
            attributeDefinitions.add(AttributeDefinition.builder()
                    .attributeName("issueId")
                    .attributeType("S")
                    .build());

            attributeDefinitions.add(AttributeDefinition.builder()
                    .attributeName("title")
                    .attributeType("S")
                    .build());

            attributeDefinitions.add(AttributeDefinition.builder()
                    .attributeName("createDate")
                    .attributeType("S")
                    .build());

            attributeDefinitions.add(AttributeDefinition.builder()
                    .attributeName("dueDate")
                    .attributeType("S")
                    .build());

            ArrayList<KeySchemaElement> tableKey = new ArrayList<KeySchemaElement>();
            KeySchemaElement key = KeySchemaElement.builder()
                    .attributeName("issueId")
                    .keyType(KeyType.HASH)
                    .build();

            KeySchemaElement key2 = KeySchemaElement.builder()
                    .attributeName("title")
                    .keyType(KeyType.RANGE) // Sort
                    .build();

            // Add KeySchemaElement objects to the list.
            tableKey.add(key);
            tableKey.add(key2);

            // Create a ProvisionedThroughput object.
            ProvisionedThroughput ptIndex = ProvisionedThroughput.builder()
                    .readCapacityUnits(1L)
                    .writeCapacityUnits(1L)
                    .build();

            KeySchemaElement keyDate = KeySchemaElement.builder()
                    .attributeName("createDate")
                    .keyType(KeyType.HASH)
                    .build();

            KeySchemaElement keyIssues = KeySchemaElement.builder()
                    .attributeName("issueId")
                    .keyType(KeyType.RANGE)
                    .build();

            List<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
            keySchema.add(keyDate);
            keySchema.add(keyIssues);

            Projection projection =  Projection.builder()
                    .projectionType("INCLUDE")
                    .nonKeyAttributes("description", "status")
                    .projectionType("INCLUDE")
                    .build();

            GlobalSecondaryIndex createDateIndex = GlobalSecondaryIndex.builder()
                    .indexName("createDateIndex")
                    .provisionedThroughput(ptIndex)
                    .keySchema(keySchema)
                    .projection(projection)
                    .build();

            KeySchemaElement keySchemaTitle = KeySchemaElement.builder()
                    .attributeName("title")
                    .keyType(KeyType.HASH)
                    .build();

            KeySchemaElement keySchemaIssueId = KeySchemaElement.builder()
                    .attributeName("issueId")
                    .keyType(KeyType.RANGE)
                    .build();

            List<KeySchemaElement> keySchemaCol2 = new ArrayList<KeySchemaElement>();
            keySchemaCol2.add(keySchemaTitle);
            keySchemaCol2.add(keySchemaIssueId);


            GlobalSecondaryIndex titleIndex = GlobalSecondaryIndex.builder()
                    .indexName("titleIndex")
                    .provisionedThroughput(ptIndex)
                    .keySchema(keySchemaCol2)
                    .projection(Projection.builder().projectionType("KEYS_ONLY").build())
                    .build();

            KeySchemaElement keySchemaDueDate = KeySchemaElement.builder()
                    .attributeName("dueDate")
                    .keyType(KeyType.HASH)
                    .build();

            List<KeySchemaElement> keySchemaCol3 = new ArrayList<KeySchemaElement>();
            keySchemaCol3.add(keySchemaDueDate);

            GlobalSecondaryIndex dueDateIndex = GlobalSecondaryIndex.builder()
                    .indexName("dueDateIndex")
                    .provisionedThroughput(ptIndex)
                    .keySchema(keySchemaCol3)
                    .projection(projection)
                    .build();

            List<GlobalSecondaryIndex> globalIndex = new ArrayList<>();
            globalIndex.add(createDateIndex);
            globalIndex.add(dueDateIndex);
            globalIndex.add(titleIndex);

            CreateTableRequest request = CreateTableRequest.builder()
                    .keySchema(tableKey)
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(new Long(10))
                            .writeCapacityUnits(new Long(10))
                            .build())
                    .globalSecondaryIndexes(globalIndex)
                    .attributeDefinitions(attributeDefinitions)
                    .tableName(tableName)
                    .build();

            System.out.println("Creating table " + tableName + "...");

            DynamoDbWaiter dbWaiter = ddb.waiter();
            CreateTableResponse response =  ddb.createTable(request);

            // Wait until the table is created.
            WaiterResponse<DescribeTableResponse> waiterResponse =  dbWaiter.waitUntilTableExists(r -> r.tableName(tableName));
            waiterResponse.matched().response().ifPresent(System.out::println);

            String newTable = response.tableDescription().tableName();
            System.out.println("Table " + tableName + " is created");

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
