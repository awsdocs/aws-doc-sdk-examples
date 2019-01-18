// snippet-sourcedescription:[LowLevelParallelScan.java demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.Java.CodeExample.LowLevelParallelScan] 

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;

public class LowLevelParallelScan {

    // total number of sample items
    static int scanItemCount = 300;

    // number of items each scan request should return
    static int scanItemLimit = 10;

    // number of logical segments for parallel scan
    static int parallelScanThreads = 16;

    // table that will be used for scanning
    static String productCatalogTableName = "ProductCatalog";

    static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());

    public static void main(String[] args) throws Exception {
        try {

            // Clean up the table
            deleteTable(productCatalogTableName);
            waitForTableToBeDeleted(productCatalogTableName);
            createTable(productCatalogTableName, 10L, 5L, "Id", "N");
            waitForTableToBecomeAvailable(productCatalogTableName);

            // Upload sample data for scan
            uploadSampleProducts(productCatalogTableName, scanItemCount);

            // Scan the table using multiple threads
            parallelScan(productCatalogTableName, scanItemLimit, parallelScanThreads);
        }
        catch (AmazonServiceException ase) {
            System.err.println(ase.getMessage());
        }
    }

    private static void parallelScan(String tableName, int itemLimit, int numberOfThreads) {
        System.out.println(
            "Scanning " + tableName + " using " + numberOfThreads + " threads " + itemLimit + " items at a time");
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        // Divide DynamoDB table into logical segments
        // Create one task for scanning each segment
        // Each thread will be scanning one segment
        int totalSegments = numberOfThreads;
        for (int segment = 0; segment < totalSegments; segment++) {
            // Runnable task that will only scan one segment
            ScanSegmentTask task = new ScanSegmentTask(tableName, itemLimit, totalSegments, segment);

            // Execute the task
            executor.execute(task);
        }

        shutDownExecutorService(executor);
    }

    // Runnable task for scanning a single segment of a DynamoDB table
    private static class ScanSegmentTask implements Runnable {

        // DynamoDB table to scan
        private String tableName;

        // number of items each scan request should return
        private int itemLimit;

        // Total number of segments
        // Equals to total number of threads scanning the table in parallel
        private int totalSegments;

        // Segment that will be scanned with by this task
        private int segment;

        public ScanSegmentTask(String tableName, int itemLimit, int totalSegments, int segment) {
            this.tableName = tableName;
            this.itemLimit = itemLimit;
            this.totalSegments = totalSegments;
            this.segment = segment;
        }

        @Override
        public void run() {
            System.out.println("Scanning " + tableName + " segment " + segment + " out of " + totalSegments
                + " segments " + itemLimit + " items at a time...");
            Map<String, AttributeValue> exclusiveStartKey = null;
            int totalScannedItemCount = 0;
            int totalScanRequestCount = 0;
            try {
                while (true) {
                    ScanRequest scanRequest = new ScanRequest().withTableName(tableName).withLimit(itemLimit)
                        .withExclusiveStartKey(exclusiveStartKey).withTotalSegments(totalSegments).withSegment(segment);

                    ScanResult result = client.scan(scanRequest);

                    totalScanRequestCount++;
                    totalScannedItemCount += result.getScannedCount();

                    // print items returned from scan request
                    processScanResult(segment, result);

                    exclusiveStartKey = result.getLastEvaluatedKey();
                    if (exclusiveStartKey == null) {
                        break;
                    }
                }
            }
            catch (AmazonServiceException ase) {
                System.err.println(ase.getMessage());
            }
            finally {
                System.out.println("Scanned " + totalScannedItemCount + " items from segment " + segment + " out of "
                    + totalSegments + " of " + tableName + " with " + totalScanRequestCount + " scan requests");
            }
        }
    }

    private static void processScanResult(int segment, ScanResult result) {
        for (Map<String, AttributeValue> item : result.getItems()) {
            printItem(segment, item);
        }
    }

    private static void uploadSampleProducts(String tableName, int itemCount) {
        System.out.println("Uploading " + itemCount + " sample items to " + tableName);
        for (int productIndex = 0; productIndex < itemCount; productIndex++) {
            uploadProduct(tableName, String.valueOf(productIndex));
        }
    }

    private static void uploadProduct(String tableName, String productIndex) {
        try {
            // Add a book.
            Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
            item.put("Id", new AttributeValue().withN(productIndex));
            item.put("Title", new AttributeValue().withS("Book " + productIndex + " Title"));
            item.put("ISBN", new AttributeValue().withS("111-1111111111"));
            item.put("Authors", new AttributeValue().withSS(Arrays.asList("Author1")));
            item.put("Price", new AttributeValue().withN("2"));
            item.put("Dimensions", new AttributeValue().withS("8.5 x 11.0 x 0.5"));
            item.put("PageCount", new AttributeValue().withN("500"));
            item.put("InPublication", new AttributeValue().withBOOL(true));
            item.put("ProductCategory", new AttributeValue().withS("Book"));

            PutItemRequest itemRequest = new PutItemRequest().withTableName(tableName).withItem(item);
            client.putItem(itemRequest);
            item.clear();

        }
        catch (AmazonServiceException ase) {
            System.err.println("Failed to create item " + productIndex + " in " + tableName);
        }
    }

    private static void deleteTable(String tableName) {
        try {

            DeleteTableRequest request = new DeleteTableRequest().withTableName(tableName);

            client.deleteTable(request);

        }
        catch (AmazonServiceException ase) {
            System.err.println("Failed to delete table " + tableName + " " + ase);
        }
    }

    private static void createTable(String tableName, long readCapacityUnits, long writeCapacityUnits,
        String partitionKeyName, String partitionKeyType) {

        createTable(tableName, readCapacityUnits, writeCapacityUnits, partitionKeyName, partitionKeyType, null, null);
    }

    private static void createTable(String tableName, long readCapacityUnits, long writeCapacityUnits,
        String partitionKeyName, String partitionKeyType, String sortKeyName, String sortKeyType) {

        try {
            System.out.println("Creating table " + tableName);
            ArrayList<KeySchemaElement> ks = new ArrayList<KeySchemaElement>();
            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();

            ks.add(new KeySchemaElement().withAttributeName(partitionKeyName).withKeyType(KeyType.HASH)); // Partition
                                                                                                          // key
            attributeDefinitions
                .add(new AttributeDefinition().withAttributeName(partitionKeyName).withAttributeType(partitionKeyType));

            if (sortKeyName != null) {
                ks.add(new KeySchemaElement().withAttributeName(sortKeyName).withKeyType(KeyType.RANGE)); // Sort
                                                                                                          // key
                attributeDefinitions
                    .add(new AttributeDefinition().withAttributeName(sortKeyName).withAttributeType(sortKeyType));
            }

            // Provide initial provisioned throughput values as Java long data
            // types
            ProvisionedThroughput provisionedthroughput = new ProvisionedThroughput()
                .withReadCapacityUnits(readCapacityUnits).withWriteCapacityUnits(writeCapacityUnits);

            CreateTableRequest request = new CreateTableRequest().withTableName(tableName).withKeySchema(ks)
                .withProvisionedThroughput(provisionedthroughput).withAttributeDefinitions(attributeDefinitions);

            client.createTable(request);

        }
        catch (AmazonServiceException ase) {
            System.err.println("Failed to create table " + tableName + " " + ase);
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

    private static void printItem(int segment, Map<String, AttributeValue> attributeList) {
        System.out.print("Segment " + segment + ", ");
        for (Map.Entry<String, AttributeValue> item : attributeList.entrySet()) {
            String attributeName = item.getKey();
            AttributeValue value = item.getValue();
            System.out.print(attributeName + " " + (value.getS() == null ? "" : "S=[" + value.getS() + "]")
                + (value.getN() == null ? "" : "N=[" + value.getN() + "]")
                + (value.getB() == null ? "" : "B=[" + value.getB() + "]")
                + (value.getSS() == null ? "" : "SS=[" + value.getSS() + "]")
                + (value.getNS() == null ? "" : "NS=[" + value.getNS() + "]")
                + (value.getBS() == null ? "" : "BS=[" + value.getBS() + "]") + ", ");
        }
        // Move to next line
        System.out.println();
    }

    private static void shutDownExecutorService(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }
        catch (InterruptedException e) {
            executor.shutdownNow();

            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
// snippet-end:[dynamodb.Java.CodeExample.LowLevelParallelScan]