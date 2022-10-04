package com.example.dynamodb;
// snippet-start:[dynamodb.java2.mapping.enhancedcreatetable.import]
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.util.NoSuchElementException;
// snippet-end:[dynamodb.java2.mapping.enhancedcreatetable.import]

// snippet-start:[dynamodb.java2.mapping.enhancedcreatetable.main]
public class EnhancedCreateTable {
    public static void createTable(DynamoDbEnhancedClient  enhancedClient) {
        // Create a DynamoDbTable object
        DynamoDbTable<Customer> customerTable = enhancedClient.table("Customer", TableSchema.fromBean(Customer.class));
        // Create the table
        customerTable.createTable(builder -> builder
                .provisionedThroughput(b -> b
                        .readCapacityUnits(10L)
                        .writeCapacityUnits(10L)
                        .build())
        );

        System.out.println("Waiting for table creation...");

        try (DynamoDbWaiter waiter = DynamoDbWaiter.create()) { // DynamoDbWaiter is Autocloseable
            WaiterResponse<DescribeTableResponse> response = waiter
                    .waitUntilTableExists(builder -> builder.tableName("Customer").build());
            try {
                // get() can throw a NoSuchElementException if a successful response is not received
                System.out.println(response.matched().response().get().table().tableName() + " table created.");
            } catch (NoSuchElementException e) {
                throw new RuntimeException("Customer table was not created.");
            }
        }
    }
}
// snippet-end:[dynamodb.java2.mapping.enhancedcreatetable.main]
