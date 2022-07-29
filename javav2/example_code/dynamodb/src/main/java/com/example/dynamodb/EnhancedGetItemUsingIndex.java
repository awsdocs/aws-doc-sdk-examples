//snippet-sourcedescription:[GetItemUsingIndex.java demonstrates how to retrieve item from an Amazon DynamoDB table using a secondary index and the Enhanced Client.]
//snippet-keyword:[SDK for Java v2]
//snippet-service:[Amazon DynamoDB]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.dynamodb;

// snippet-start:[dynamodb.java2.get_item_index.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import java.util.List;
// snippet-end:[dynamodb.java2.get_item_index.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * To get an item from an Amazon DynamoDB table using the AWS SDK for Java V2, its better practice to use the
 * Enhanced Client, see the EnhancedGetItem example.
 *
 *  Create the Movies table by running the Scenario example and loading the Movie data from the JSON file. Next create a secondary
 *  index for the Movies table that uses only the year column. Name the index **year-index**. For more information, see:
 *
 * https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/GSI.html
 */
public class EnhancedGetItemUsingIndex {

    public static void main(String[] args) {

        String tableName = "Movies" ;
        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
            .credentialsProvider(credentialsProvider)
            .region(region)
            .build();

        queryIndex(ddb, tableName);
        ddb.close();
    }

    // snippet-start:[dynamodb.java2.get_item_index.main]
    public static void queryIndex(DynamoDbClient ddb, String tableName) {

        try {
            // Create a DynamoDbEnhancedClient and use the DynamoDbClient object.
            DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddb)
                .build();

            //Create a DynamoDbTable object based on Movies.
            DynamoDbTable<Movies> table = enhancedClient.table("Movies", TableSchema.fromBean(Movies.class));
            String dateVal = "2013";

            DynamoDbIndex<Movies> secIndex = enhancedClient.table("Movies", TableSchema.fromBean(Movies.class)) .index("year-index");
            AttributeValue attVal = AttributeValue.builder()
                .n(dateVal)
                .build();

            // Create a QueryConditional object that's used in the query operation.
            QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(attVal)
                .build());

            // Get items in the table.
            SdkIterable<Page<Movies>> results = secIndex.query(QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .limit(300)
                .build());

            // Display the results.
            results.forEach(page -> {
                List<Movies> allMovies = page.items();
                for (Movies myMovies: allMovies) {
                    System.out.println("The movie title is " + myMovies.getTitle() + ". The year is " + myMovies.getYear());
                }
            });

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[dynamodb.java2.get_item_index.main]
}