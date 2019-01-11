// snippet-sourcedescription:[DynamoDBDynamicFaultInjection.java demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.Java.CodeExample.73f7c57e-91fe-41c4-8fbf-b3bece71119b] 

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


package com.amazonaws.services.dynamodbv2.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;

/**
 * This sample demonstrates how to inject failures, latencies in your DynamoDB
 * client for testing
 */
public class DynamoDBDynamicFaultInjection {

    private static final Logger logger = LoggerFactory.getLogger(DynamoDBDynamicFaultInjection.class);

    public static String TABLENAME = "my-favorite-movies-table";

    /*
     * Important: Be sure to fill in your AWS access credentials in the
     * AwsCredentials.properties file before you try to run this sample and
     * configure your log4j.properties
     *
     * http://aws.amazon.com/security-credentials
     */
    static AmazonDynamoDBClient dynamoDBClient;

    /**
     * The only information needed to create a client are security credentials
     * consisting of the AWS Access Key ID and Secret Access Key. All other
     * configuration, such as the service endpoints, are performed
     * automatically. Client parameters, such as proxies, can be specified in an
     * optional ClientConfiguration object when constructing a client.
     *
     * @see com.amazonaws.auth.BasicAWSCredentials
     * @see com.amazonaws.auth.PropertiesCredentials
     * @see com.amazonaws.ClientConfiguration
     */
    private static void init() throws Exception {

        dynamoDBClient = new AmazonDynamoDBClient(new DefaultAWSCredentialsProviderChain());

        // pass in the client for access to the cached metadata.
        RequestHandler2 requestHandler = new FaultInjectionRequestHandler(dynamoDBClient);

        dynamoDBClient.addRequestHandler(requestHandler);
    }

    public static void main(String[] args) throws Exception {

        init();

        try {

            // Create a table with a primary key named 'name', which holds a
            // string
            createTable();

            // Describe our new table
            describeTable();

            // Add some items
            putItem(newItem("Bill & Ted's Excellent Adventure", 1989, "****", "James", "Sara"));
            putItem(newItem("Airplane", 1980, "*****", "James", "Billy Bob"));

            // Get some items
            getItem("Airplane");
            getItem("Bill & Ted's Excellent Adventure");

            // Scan items for movies with a year attribute greater than 1985
            Map<String, Condition> scanFilter = new HashMap<String, Condition>();
            Condition condition = new Condition().withComparisonOperator(ComparisonOperator.GT.toString())
                .withAttributeValueList(new AttributeValue().withN("1985"));
            scanFilter.put("year", condition);
            ScanRequest scanRequest = new ScanRequest(TABLENAME).withScanFilter(scanFilter);
            ScanResult scanResult = dynamoDBClient.scan(scanRequest);
            logger.info("Result: " + scanResult);

        }
        catch (AmazonServiceException ase) {

            logger.error("Service Exception: " + ase);

        }
        catch (AmazonClientException ace) {

            logger.error("Client Exception: " + ace);
        }
    }

    /*
     * Get an item from the table
     */
    private static void getItem(String keyVal) {

        Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        key.put("name", new AttributeValue(keyVal));

        GetItemRequest getItemRequest = new GetItemRequest().withTableName(TABLENAME).withKey(key);

        GetItemResult item = dynamoDBClient.getItem(getItemRequest);

        logger.info("Get Result: " + item);
    }

    /*
     * Describe the table
     */
    private static void describeTable() {
        DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(TABLENAME);
        TableDescription tableDescription = dynamoDBClient.describeTable(describeTableRequest).getTable();
        logger.info("Table Description: " + tableDescription);
    }

    /*
     * Put given item into the table
     *
     * @param item
     */
    private static void putItem(Map<String, AttributeValue> item) {

        try {

            PutItemRequest putItemRequest = new PutItemRequest(TABLENAME, item);
            PutItemResult putItemResult = dynamoDBClient.putItem(putItemRequest);
            logger.info("Result: " + putItemResult);
        }
        catch (Exception e) {
            // TODO: handle exception
        }
    }

    /*
     * Create the table if it already does not exist
     */
    private static void createTable() {
        List<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("name").withAttributeType("S"));

        List<KeySchemaElement> ks = new ArrayList<KeySchemaElement>();
        ks.add(new KeySchemaElement().withAttributeName("name").withKeyType(KeyType.HASH)); // Partition
                                                                                            // key

        ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput().withReadCapacityUnits(10L)
            .withWriteCapacityUnits(10L);

        CreateTableRequest request = new CreateTableRequest().withTableName(TABLENAME)
            .withAttributeDefinitions(attributeDefinitions).withKeySchema(ks)
            .withProvisionedThroughput(provisionedThroughput);

        try {
            CreateTableResult createdTableDescription = dynamoDBClient.createTable(request);
            logger.info("Created Table: " + createdTableDescription);
            // Wait for it to become active
            waitForTableToBecomeAvailable(TABLENAME);
        }
        catch (ResourceInUseException e) {
            logger.warn("Table already existed", e);
        }
    }

    /*
     * Create new item helper
     */
    private static Map<String, AttributeValue> newItem(String name, int year, String rating, String... fans) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("name", new AttributeValue(name));
        item.put("year", new AttributeValue().withN(Integer.toString(year)));
        item.put("rating", new AttributeValue(rating));
        item.put("fans", new AttributeValue().withSS(fans));

        return item;
    }

    /*
     * Waits for the table to become ACTIVE Times out after 10 minutes
     */
    private static void waitForTableToBecomeAvailable(String tableName) {
        logger.info("Waiting for " + tableName + " to become ACTIVE...");

        long startTime = System.currentTimeMillis();
        long endTime = startTime + (10 * 60 * 1000);
        while (System.currentTimeMillis() < endTime) {
            try {
                Thread.sleep(1000 * 20);
            }
            catch (Exception e) {
            }
            try {
                DescribeTableRequest request = new DescribeTableRequest().withTableName(tableName);
                TableDescription tableDescription = dynamoDBClient.describeTable(request).getTable();
                String tableStatus = tableDescription.getTableStatus();
                logger.info("  - current state: " + tableStatus);
                if (tableStatus.equals(TableStatus.ACTIVE.toString()))
                    return;
            }
            catch (AmazonServiceException ase) {
                if (ase.getErrorCode().equalsIgnoreCase("ResourceNotFoundException") == false)
                    throw ase;
            }
        }

        throw new RuntimeException("Table " + tableName + " never went active");
    }

}
// snippet-end:[dynamodb.Java.CodeExample.73f7c57e-91fe-41c4-8fbf-b3bece71119b]