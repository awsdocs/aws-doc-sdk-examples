package com.example.dynamodb;

// snippet-start:[dynamodb.java2.mapping.queryfilter.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
// snippet-end:[dynamodb.java2.mapping.queryfilter.import]

public class EnhancedQueryRecordsWithFilter {

    // Query the Customer table using a filter.
    public static void main(String[] args) {

        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddb)
                .build();

        queryTableFilter(enhancedClient);
        ddb.close();
    }

    // snippet-start:[dynamodb.java2.mapping.queryfilter.main]
    public static Integer queryTableFilter(DynamoDbEnhancedClient enhancedClient) {

        Integer countOfCustomers = 0;

        try {
            DynamoDbTable<Customer> mappedTable = enhancedClient.table("Customer", TableSchema.fromBean(Customer.class));

            AttributeValue att = AttributeValue.builder()
                    .s("Tom red")
                    .build();

            Map<String, AttributeValue> expressionValues = new HashMap<>();
            expressionValues.put(":value", att);

            Expression expression = Expression.builder()
                    .expression("custName = :value")
                    .expressionValues(expressionValues)
                    .build();

            // Create a QueryConditional object to query by partitionValue.
            // Since the Customer table has a sort key attribute (email), we can use an expression
            // to filter the query results if multiple items have the same partition key value.
            QueryConditional queryConditional = QueryConditional
                    .keyEqualTo(Key.builder().partitionValue("id101")
                            .build());

            // Perform the query

            for (Customer customer : mappedTable.query(
                    r -> r.queryConditional(queryConditional)
                            .filterExpression(expression)
            ).items()) {
                countOfCustomers++;
                System.out.println(customer);
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done");
        return countOfCustomers;
    }    // snippet-end:[dynamodb.java2.mapping.queryfilter.main]
}