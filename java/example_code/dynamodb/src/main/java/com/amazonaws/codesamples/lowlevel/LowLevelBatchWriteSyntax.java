// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.java.codeexample.LowLevelBatchWriteSyntax] 
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteRequest;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

// **********************************************************************
// This sample gives syntax for Java Low Level Batch Write
// It is not included with xi:include but is given here for references
// **********************************************************************

public class LowLevelBatchWriteSyntax {

    static AmazonDynamoDBClient client;
    static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static void main(String[] args) throws IOException {

        createClient();

        writeMultipleItemsBatchWrite();

    }

    private static void createClient() throws IOException {
        AWSCredentials credentials = new PropertiesCredentials(
            LowLevelBatchWriteSyntax.class.getResourceAsStream("AwsCredentials.properties"));

        client = new AmazonDynamoDBClient(credentials);
    }

    private static void writeMultipleItemsBatchWrite() {
        try {

            // Begin syntax extract

            // Create a map for the requests in the batch
            Map<String, List<WriteRequest>> requestItems = new HashMap<String, List<WriteRequest>>();

            // Create a PutRequest for a new Forum item
            Map<String, AttributeValue> forumItem = new HashMap<String, AttributeValue>();
            forumItem.put("Name", new AttributeValue().withS("Amazon RDS"));
            forumItem.put("Threads", new AttributeValue().withN("0"));

            List<WriteRequest> forumList = new ArrayList<WriteRequest>();
            forumList.add(new WriteRequest().withPutRequest(new PutRequest().withItem(forumItem)));
            requestItems.put("Forum", forumList);

            // Create a PutRequest for a new Thread item
            Map<String, AttributeValue> threadItem = new HashMap<String, AttributeValue>();
            threadItem.put("ForumName", new AttributeValue().withS("Amazon RDS"));
            threadItem.put("Subject", new AttributeValue().withS("Amazon RDS Thread 1"));

            List<WriteRequest> threadList = new ArrayList<WriteRequest>();
            threadList.add(new WriteRequest().withPutRequest(new PutRequest().withItem(threadItem)));

            // Create a DeleteRequest for a Thread item
            Map<String, AttributeValue> threadDeleteKey = new HashMap<String, AttributeValue>();
            threadDeleteKey.put("ForumName", new AttributeValue().withS("Some partition key value"));
            threadDeleteKey.put("Subject", new AttributeValue().withS("Some sort key value"));

            threadList.add(new WriteRequest().withDeleteRequest(new DeleteRequest().withKey(threadDeleteKey)));
            requestItems.put("Thread", threadList);

            BatchWriteItemRequest batchWriteItemRequest = new BatchWriteItemRequest();

            System.out.println("Making the request.");

            batchWriteItemRequest.withRequestItems(requestItems);
            client.batchWriteItem(batchWriteItemRequest);

            // End syntax extract

        }
        catch (AmazonServiceException ase) {
            System.err.println("Failed to retrieve items: ");
            ase.printStackTrace(System.err);
        }

    }

}

// snippet-end:[dynamodb.java.codeexample.LowLevelBatchWriteSyntax] 