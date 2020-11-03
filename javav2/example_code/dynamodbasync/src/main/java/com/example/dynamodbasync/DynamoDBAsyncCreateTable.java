//snippet-sourcedescription:[DynamoDBAsyncCreateTable.java demonstrates how to create an Amazon DynamoDB table using DynamoDbAsyncClient and DynamoDbAsyncWaiter objects.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[10/15/2020]
//snippet-sourceauthor:[scmacdon- aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/


package com.example.dynamodbasync;

// snippet-start:[dynamodb.java2.dbasync.table.import]
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbAsyncWaiter;
// snippet-end:[dynamodb.java2.dbasync.table.import]

public class DynamoDBAsyncCreateTable {

    public static void main(String[] args) throws InterruptedException {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DynamoDBAsyncCreateTable <table> <key >\n\n" +
                "Where:\n" +
                "    table - the table to create (i.e., Music3)\n\n" +
                "    key   - the key for the table (i.e., Artist)\n" +
                "Example:\n" +
                "    Music3 Artist \n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args */
        String tableName =  args[0];
        String key = args[1];

        // Create the DynamoDbAsyncClient object
        Region region = Region.US_EAST_1;
        DynamoDbAsyncClient client = DynamoDbAsyncClient.builder()
                .region(region)
                .build();

        createTable(client, tableName, key);
    }

    // snippet-start:[dynamodb.java2.dbasync.table.main]
    public static void createTable(DynamoDbAsyncClient client, String tableName, String key) {

        // Create a DynamoDbAsyncWaiter object
        DynamoDbAsyncWaiter asyncWaiter = client.waiter();

        // Create the CreateTableRequest object
        CreateTableRequest request = CreateTableRequest.builder()
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName(key)
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .keySchema(KeySchemaElement.builder()
                        .attributeName(key)
                        .keyType(KeyType.HASH)
                        .build())
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(new Long(10))
                        .writeCapacityUnits(new Long(10))
                        .build())
                .tableName(tableName)
                .build();

         // Create the table by using the DynamoDbAsyncClient object
         CompletableFuture<CreateTableResponse> response =  client.createTable(request);

        // When future is complete (either successfully or in error) handle the response
        response.whenComplete((table, err) -> {
            try {
                if (table != null) {

                    // Create a DescribeTableRequest object required for waiter functionality
                    DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                            .tableName(table.tableDescription().tableName())
                            .build();

                    CompletableFuture<WaiterResponse<DescribeTableResponse>> waiterResponse = asyncWaiter.waitUntilTableExists(tableRequest);

                    // Fires when the table is ready
                    waiterResponse.whenComplete((r, t) -> {

                            // print out the new table's ARN when its ready
                            String tableARN =  r.matched().response().get().table().tableArn();
                            System.out.println("The table "+ tableARN +" is ready");

                    });
                    waiterResponse.join();

                } else {
                    // Handle error
                    err.printStackTrace();
                }
            } finally {
                // Lets the application shut down. Only close the client when you are completely done with it.
                client.close();
            }
        });
        response.join();
    }
    // snippet-end:[dynamodb.java2.dbasync.table.main]
}
