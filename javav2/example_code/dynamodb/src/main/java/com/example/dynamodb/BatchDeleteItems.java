// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.dynamodb;

// snippet-start:[dynamodb.java2.batch.delete.items.main]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.BatchWriteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.DeleteRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.WriteRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchDeleteItems {

    public static void main(String[] args){
        final String usage = """

                Usage:
                    <tableName> 

                Where:
                    tableName - The Amazon DynamoDB table (for example, Music).\s
                """;

        String tableName = "Music";
        Region region = Region.US_EAST_1;
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(region)
            .build();

        deleteBatchItems(dynamoDbClient, tableName);
    }

    public static void deleteBatchItems(DynamoDbClient dynamoDbClient, String tableName) {
        // Specify the deletions you want to perform.
        List<WriteRequest> writeRequests = new ArrayList<>();

        // Set item 1 primary key values.
        Map<String, AttributeValue> item1Key = new HashMap<>();
        item1Key.put("Artist", AttributeValue.builder().s("Artist1").build());
        writeRequests.add(WriteRequest.builder().deleteRequest(DeleteRequest.builder().key(item1Key).build()).build());

        // Set item 2 primary key values.
        Map<String, AttributeValue> item2Key = new HashMap<>();
        item2Key.put("Artist", AttributeValue.builder().s("Artist2").build());
        writeRequests.add(WriteRequest.builder().deleteRequest(DeleteRequest.builder().key(item2Key).build()).build());

        try {
            // Create the BatchWriteItemRequest.
            BatchWriteItemRequest batchWriteItemRequest = BatchWriteItemRequest.builder()
                .requestItems(Map.of(tableName, writeRequests))
                .build();

            // Execute the BatchWriteItem operation.
            BatchWriteItemResponse batchWriteItemResponse = dynamoDbClient.batchWriteItem(batchWriteItemRequest);

            // Process the response.
            System.out.println("Batch delete successful: " + batchWriteItemResponse);

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[dynamodb.java2.batch.delete.items.main]