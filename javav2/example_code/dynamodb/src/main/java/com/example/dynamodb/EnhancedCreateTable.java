package com.example.dynamodb;
// snippet-start:[dynamodb.java2.mapping.enhancedcreatetable.import]
import software.amazon.awssdk.core.internal.waiters.ResponseOrException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;
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
            ResponseOrException<DescribeTableResponse> response = waiter
                    .waitUntilTableExists(builder -> builder.tableName("Customer").build())
                    .matched();
            DescribeTableResponse tableDescription = response.response().orElseThrow(
                    () -> new RuntimeException("Customer table was not created."));
            // The actual error can be inspected in response.exception()
            System.out.println(tableDescription.table().tableName() + " was created.");
        }
    }
}
// snippet-end:[dynamodb.java2.mapping.enhancedcreatetable.main]
