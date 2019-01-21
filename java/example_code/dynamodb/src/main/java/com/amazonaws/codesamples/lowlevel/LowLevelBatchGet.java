// snippet-sourcedescription:[LowLevelBatchGet.java demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.Java.CodeExample.LowLevelBatchGet] 

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BatchGetItemRequest;
import com.amazonaws.services.dynamodbv2.model.BatchGetItemResult;
import com.amazonaws.services.dynamodbv2.model.KeysAndAttributes;

public class LowLevelBatchGet {

    static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
    static String table1Name = "Forum";
    static String table2Name = "Thread";

    public static void main(String[] args) throws IOException {

        retrieveMultipleItemsBatchGet();

    }

    private static void retrieveMultipleItemsBatchGet() {
        try {

            Map<String, KeysAndAttributes> requestItems = new HashMap<String, KeysAndAttributes>();

            List<Map<String, AttributeValue>> tableKeys = new ArrayList<Map<String, AttributeValue>>();
            Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
            key.put("Name", new AttributeValue().withS("Amazon S3"));
            tableKeys.add(key);

            key = new HashMap<String, AttributeValue>();
            key.put("Name", new AttributeValue().withS("Amazon DynamoDB"));
            tableKeys.add(key);

            requestItems.put(table1Name, new KeysAndAttributes().withKeys(tableKeys));

            tableKeys = new ArrayList<Map<String, AttributeValue>>();

            key = new HashMap<String, AttributeValue>();
            key.put("ForumName", new AttributeValue().withS("Amazon DynamoDB"));
            key.put("Subject", new AttributeValue().withS("DynamoDB Thread 1"));
            tableKeys.add(key);

            key = new HashMap<String, AttributeValue>();
            key.put("ForumName", new AttributeValue().withS("Amazon DynamoDB"));
            key.put("Subject", new AttributeValue().withS("DynamoDB Thread 2"));
            tableKeys.add(key);

            key = new HashMap<String, AttributeValue>();
            key.put("ForumName", new AttributeValue().withS("Amazon S3"));
            key.put("Subject", new AttributeValue().withS("S3 Thread 1"));
            tableKeys.add(key);

            requestItems.put(table2Name, new KeysAndAttributes().withKeys(tableKeys));

            BatchGetItemResult result;
            BatchGetItemRequest batchGetItemRequest = new BatchGetItemRequest();
            do {
                System.out.println("Making the request.");

                batchGetItemRequest.withRequestItems(requestItems);
                result = client.batchGetItem(batchGetItemRequest);

                List<Map<String, AttributeValue>> table1Results = result.getResponses().get(table1Name);
                if (table1Results != null) {
                    System.out.println("Items in table " + table1Name);
                    for (Map<String, AttributeValue> item : table1Results) {
                        printItem(item);
                    }
                }

                List<Map<String, AttributeValue>> table2Results = result.getResponses().get(table2Name);
                if (table2Results != null) {
                    System.out.println("\nItems in table " + table2Name);
                    for (Map<String, AttributeValue> item : table2Results) {
                        printItem(item);
                    }
                }

                // Check for unprocessed keys which could happen if you exceed
                // provisioned
                // throughput or reach the limit on response size.
                for (Map.Entry<String, KeysAndAttributes> pair : result.getUnprocessedKeys().entrySet()) {
                    System.out.println("Unprocessed key pair: " + pair.getKey() + ", " + pair.getValue());
                }
                requestItems = result.getUnprocessedKeys();
            } while (result.getUnprocessedKeys().size() > 0);

        }
        catch (AmazonServiceException ase) {
            System.err.println("Failed to retrieve items.");
        }

    }

    private static void printItem(Map<String, AttributeValue> attributeList) {
        for (Map.Entry<String, AttributeValue> item : attributeList.entrySet()) {
            String attributeName = item.getKey();
            AttributeValue value = item.getValue();
            System.out.println(attributeName + " " + (value.getS() == null ? "" : "S=[" + value.getS() + "]")
                + (value.getN() == null ? "" : "N=[" + value.getN() + "]")
                + (value.getB() == null ? "" : "B=[" + value.getB() + "]")
                + (value.getSS() == null ? "" : "SS=[" + value.getSS() + "]")
                + (value.getNS() == null ? "" : "NS=[" + value.getNS() + "]")
                + (value.getBS() == null ? "" : "BS=[" + value.getBS() + "] \n"));
        }
    }
}// snippet-end:[dynamodb.Java.CodeExample.LowLevelBatchGet]