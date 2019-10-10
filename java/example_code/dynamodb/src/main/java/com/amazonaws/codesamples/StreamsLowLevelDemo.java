// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-sourcesyntax:[java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.java.codeexample.StreamsLowLevelDemo] 
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


package com.amazon.codesamples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBStreams;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBStreamsClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeStreamRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeStreamResult;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.GetRecordsRequest;
import com.amazonaws.services.dynamodbv2.model.GetRecordsResult;
import com.amazonaws.services.dynamodbv2.model.GetShardIteratorRequest;
import com.amazonaws.services.dynamodbv2.model.GetShardIteratorResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.Record;
import com.amazonaws.services.dynamodbv2.model.Shard;
import com.amazonaws.services.dynamodbv2.model.ShardIteratorType;
import com.amazonaws.services.dynamodbv2.model.StreamSpecification;
import com.amazonaws.services.dynamodbv2.model.StreamViewType;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

public class StreamsLowLevelDemo {

    public static void main(String args[]) throws InterruptedException {

        AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion(Regions.US_EAST_2)
            .withCredentials(new DefaultAWSCredentialsProviderChain())
            .build();

        AmazonDynamoDBStreams streamsClient =
            AmazonDynamoDBStreamsClientBuilder
                .standard()
                .withRegion(Regions.US_EAST_2)
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();

        // Create a table, with a stream enabled
        String tableName = "TestTableForStreams";

        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>(
            Arrays.asList(new AttributeDefinition()
                .withAttributeName("Id")
                .withAttributeType("N")));

        ArrayList<KeySchemaElement> keySchema = new ArrayList<>(
            Arrays.asList(new KeySchemaElement()
                .withAttributeName("Id")
                .withKeyType(KeyType.HASH))); // Partition key

        StreamSpecification streamSpecification = new StreamSpecification()
            .withStreamEnabled(true)
            .withStreamViewType(StreamViewType.NEW_AND_OLD_IMAGES);

        CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
            .withKeySchema(keySchema).withAttributeDefinitions(attributeDefinitions)
            .withProvisionedThroughput(new ProvisionedThroughput()
                .withReadCapacityUnits(10L)
                .withWriteCapacityUnits(10L))
            .withStreamSpecification(streamSpecification);

        System.out.println("Issuing CreateTable request for " + tableName);
        dynamoDBClient.createTable(createTableRequest);
        System.out.println("Waiting for " + tableName + " to be created...");

        try {
            TableUtils.waitUntilActive(dynamoDBClient, tableName);
        } catch (AmazonClientException e) {
            e.printStackTrace();
        }

        // Print the stream settings for the table
        DescribeTableResult describeTableResult = dynamoDBClient.describeTable(tableName);
        String streamArn = describeTableResult.getTable().getLatestStreamArn();
        System.out.println("Current stream ARN for " + tableName + ": " +
            describeTableResult.getTable().getLatestStreamArn());
        StreamSpecification streamSpec = describeTableResult.getTable().getStreamSpecification();
        System.out.println("Stream enabled: " + streamSpec.getStreamEnabled());
        System.out.println("Update view type: " + streamSpec.getStreamViewType());
        System.out.println();

        // Generate write activity in the table

        System.out.println("Performing write activities on " + tableName);
        int maxItemCount = 100;
        for (Integer i = 1; i <= maxItemCount; i++) {
            System.out.println("Processing item " + i + " of " + maxItemCount);

            // Write a new item
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("Id", new AttributeValue().withN(i.toString()));
            item.put("Message", new AttributeValue().withS("New item!"));
            dynamoDBClient.putItem(tableName, item);


            // Update the item
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("Id", new AttributeValue().withN(i.toString()));
            Map<String, AttributeValueUpdate> attributeUpdates = new HashMap<>();
            attributeUpdates.put("Message", new AttributeValueUpdate()
                .withAction(AttributeAction.PUT)
                .withValue(new AttributeValue()
                    .withS("This item has changed")));
            dynamoDBClient.updateItem(tableName, key, attributeUpdates);

            // Delete the item
            dynamoDBClient.deleteItem(tableName, key);
        }

        // Get all the shard IDs from the stream.  Note that DescribeStream returns
        // the shard IDs one page at a time.
        String lastEvaluatedShardId = null;

        do {
            DescribeStreamResult describeStreamResult = streamsClient.describeStream(
                new DescribeStreamRequest()
                    .withStreamArn(streamArn)
                    .withExclusiveStartShardId(lastEvaluatedShardId));
            List<Shard> shards = describeStreamResult.getStreamDescription().getShards();

            // Process each shard on this page

            for (Shard shard : shards) {
                String shardId = shard.getShardId();
                System.out.println("Shard: " + shard);

                // Get an iterator for the current shard

                GetShardIteratorRequest getShardIteratorRequest = new GetShardIteratorRequest()
                    .withStreamArn(streamArn)
                    .withShardId(shardId)
                    .withShardIteratorType(ShardIteratorType.TRIM_HORIZON);
                GetShardIteratorResult getShardIteratorResult =
                    streamsClient.getShardIterator(getShardIteratorRequest);
                String currentShardIter = getShardIteratorResult.getShardIterator();

                // Shard iterator is not null until the Shard is sealed (marked as READ_ONLY).
                // To prevent running the loop until the Shard is sealed, which will be on average
                // 4 hours, we process only the items that were written into DynamoDB and then exit.
                int processedRecordCount = 0;
                while (currentShardIter != null && processedRecordCount < maxItemCount) {
                    System.out.println("    Shard iterator: " + currentShardIter.substring(380));

                    // Use the shard iterator to read the stream records

                    GetRecordsResult getRecordsResult = streamsClient.getRecords(new GetRecordsRequest()
                        .withShardIterator(currentShardIter));
                    List<Record> records = getRecordsResult.getRecords();
                    for (Record record : records) {
                        System.out.println("        " + record.getDynamodb());
                    }
                    processedRecordCount += records.size();
                    currentShardIter = getRecordsResult.getNextShardIterator();
                }
            }

            // If LastEvaluatedShardId is set, then there is
            // at least one more page of shard IDs to retrieve
            lastEvaluatedShardId = describeStreamResult.getStreamDescription().getLastEvaluatedShardId();

        } while (lastEvaluatedShardId != null);

        // Delete the table
        System.out.println("Deleting the table...");
        dynamoDBClient.deleteTable(tableName);

        System.out.println("Demo complete");

    }
}
// snippet-end:[dynamodb.java.codeexample.StreamsLowLevelDemo] 