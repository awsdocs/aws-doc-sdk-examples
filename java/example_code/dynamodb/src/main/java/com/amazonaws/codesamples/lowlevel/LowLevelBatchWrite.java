// snippet-sourcedescription:[LowLevelBatchWrite.java demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.Java.CodeExample.LowLevelBatchWrite] 

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult;
import com.amazonaws.services.dynamodbv2.model.ConsumedCapacity;
import com.amazonaws.services.dynamodbv2.model.DeleteRequest;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

public class LowLevelBatchWrite {

    static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
    static String table1Name = "Forum";
    static String table2Name = "Thread";

    public static void main(String[] args) throws IOException {

        writeMultipleItemsBatchWrite();

    }

    private static void writeMultipleItemsBatchWrite() {
        try {

            // Create a map for the requests in the batch
            Map<String, List<WriteRequest>> requestItems = new HashMap<String, List<WriteRequest>>();

            // Create a PutRequest for a new Forum item
            Map<String, AttributeValue> forumItem = new HashMap<String, AttributeValue>();
            forumItem.put("Name", new AttributeValue().withS("Amazon RDS"));
            forumItem.put("Threads", new AttributeValue().withN("0"));

            List<WriteRequest> forumList = new ArrayList<WriteRequest>();
            forumList.add(new WriteRequest().withPutRequest(new PutRequest().withItem(forumItem)));
            requestItems.put(table1Name, forumList);

            // Create a PutRequest for a new Thread item
            Map<String, AttributeValue> threadItem = new HashMap<String, AttributeValue>();
            threadItem.put("ForumName", new AttributeValue().withS("Amazon RDS"));
            threadItem.put("Subject", new AttributeValue().withS("Amazon RDS Thread 1"));
            threadItem.put("Message", new AttributeValue().withS("ElastiCache Thread 1 message"));
            threadItem.put("KeywordTags", new AttributeValue().withSS(Arrays.asList("cache", "in-memory")));

            List<WriteRequest> threadList = new ArrayList<WriteRequest>();
            threadList.add(new WriteRequest().withPutRequest(new PutRequest().withItem(threadItem)));

            // Create a DeleteRequest for a Thread item
            Map<String, AttributeValue> threadDeleteKey = new HashMap<String, AttributeValue>();
            threadDeleteKey.put("ForumName", new AttributeValue().withS("Amazon S3"));
            threadDeleteKey.put("Subject", new AttributeValue().withS("S3 Thread 100"));

            threadList.add(new WriteRequest().withDeleteRequest(new DeleteRequest().withKey(threadDeleteKey)));
            requestItems.put(table2Name, threadList);

            BatchWriteItemResult result;
            BatchWriteItemRequest batchWriteItemRequest = new BatchWriteItemRequest()
                .withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);

            do {
                System.out.println("Making the request.");

                batchWriteItemRequest.withRequestItems(requestItems);
                result = client.batchWriteItem(batchWriteItemRequest);

                // Print consumed capacity units
                for (ConsumedCapacity consumedCapacity : result.getConsumedCapacity()) {
                    String tableName = consumedCapacity.getTableName();
                    Double consumedCapacityUnits = consumedCapacity.getCapacityUnits();
                    System.out.println("Consumed capacity units for table " + tableName + ": " + consumedCapacityUnits);
                }

                // Check for unprocessed keys which could happen if you exceed
                // provisioned throughput
                System.out.println("Unprocessed Put and Delete requests: \n" + result.getUnprocessedItems());
                requestItems = result.getUnprocessedItems();
            } while (result.getUnprocessedItems().size() > 0);

        }
        catch (AmazonServiceException ase) {
            System.err.println("Failed to retrieve items: ");
            ase.printStackTrace(System.err);
        }

    }

}
// snippet-end:[dynamodb.Java.CodeExample.LowLevelBatchWrite]