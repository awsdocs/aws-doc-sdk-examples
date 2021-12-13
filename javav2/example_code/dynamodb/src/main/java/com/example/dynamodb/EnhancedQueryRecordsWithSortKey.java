//snippet-sourcedescription:[EnhancedQueryRecordsWithSortKey.java demonstrates how to query a table with a sort key.]
//snippet-keyword:[SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[09/28/2021]
//snippet-sourceauthor:[dito - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.dynamodb;

// snippet-start:[dynamodb.java2.mapping.querykey.import]
import java.time.Instant;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
// snippet-end:[dynamodb.java2.mapping.querykey.import]

/*
 * Prior to running this code example, create an Amazon DynamoDB table named Customer with these columns:
 *   - id - the id of the record that is the key
 *   - custName - the customer name
 *   - email - the email value
 *   - registrationDate - an instant value when the item was added to the table
 *
 * Also, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class EnhancedQueryRecordsWithSortKey {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddb)
                .build();

        queryTableSortKeyBetween(enhancedClient);
        ddb.close();
    }

    // snippet-start:[dynamodb.java2.mapping.querykey.main]
    public static void queryTableSortKeyBetween(DynamoDbEnhancedClient enhancedClient) {

        try {

            // Create a DynamoDbTable object based on Issues.
            DynamoDbTable<Issues> table = enhancedClient.table("Issues", TableSchema.fromBean(Issues.class));
            String dateVal = "2013-11-19";
            DynamoDbIndex<Issues> secIndex =
                    enhancedClient.table("Issues",
                                    TableSchema.fromBean(Issues.class))
                            .index("dueDateIndex");

            AttributeValue attVal = AttributeValue.builder()
                    .s(dateVal)
                    .build();

            // Create a QueryConditional object that's used in the query operation.
            QueryConditional queryConditional = QueryConditional
                    .keyEqualTo(Key.builder().partitionValue(attVal)
                            .build());

            // Get items in the Issues table.
            SdkIterable<Page<Issues>> results =  secIndex.query(
                    QueryEnhancedRequest.builder()
                            .queryConditional(queryConditional)
                            .build());

            AtomicInteger atomicInteger = new AtomicInteger();
            atomicInteger.set(0);
            results.forEach(page -> {

                Issues issue = (Issues) page.items().get(atomicInteger.get());
                System.out.println("The issue title is "+issue.getTitle());
                atomicInteger.incrementAndGet();
            });
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done");
    }
    // snippet-end:[dynamodb.java2.mapping.querykey.main]
}
