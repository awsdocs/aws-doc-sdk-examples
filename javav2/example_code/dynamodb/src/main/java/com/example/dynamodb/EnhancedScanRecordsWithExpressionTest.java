package com.example.dynamodb;

import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.Select;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
// snippet-end:[dynamodb.java2.mapping.scanEx.import]

/*
 * Ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class EnhancedScanRecordsWithExpressionTest {

    public static void main(String[] args) {

        String tableName = "Issues";
        System.out.format(
                "Creating table \"%s\" with a simple primary key: \"Name\".\n",
                tableName);

        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .build();

         scanIndex(ddb, tableName, "CreateDateIndex");
         ddb.close();
    }



    // snippet-start:[dynamodb.java2.mapping.scanEx.main]
    // Scan the table and retrieve only items where createDate is 2013-11-15.
    public static void scanIndex(DynamoDbClient ddb, String tableName, String indexName) {

        System.out.println("\n***********************************************************\n");
        System.out.print("Select items for "+tableName +" where a specific string is displayed");

        try {
            // Create a DynamoDbEnhancedClient and use the DynamoDbClient object.
            DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(ddb)
                    .build();

            // Create a DynamoDbTable object based on Issues.
            DynamoDbTable<Issues> table = enhancedClient.table("Issues", TableSchema.fromBean(Issues.class));

            // Setup the scan based on the index.
             System.out.println("Query records with word issue in title");

                AttributeValue attVal = AttributeValue.builder()
                        .s("issue")
                        .build();

                // Get only items in the Issues table for 2013-11-15.
                Map<String, AttributeValue> myMap = new HashMap<>();
                myMap.put(":val1", attVal);

                Map<String, String> myExMap = new HashMap<>();
                myExMap.put("#title", "title");

                Expression expression = Expression.builder()
                        .expressionValues(myMap)
                        .expressionNames(myExMap)
                        .expression("contains(#title, :val1)")
                        .build();

                ScanEnhancedRequest enhancedRequest = ScanEnhancedRequest.builder()
                        .filterExpression(expression)
                        .limit(15)
                        .build();

                // Get items in the Issues table.
                Iterator<Issues> results = table.scan(enhancedRequest).items().iterator();

                while (results.hasNext()) {
                    Issues issue = results.next();
                    System.out.println("The record description is " + issue.getDescription());
                    System.out.println("The record title is " + issue.getTitle());
                }


        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
 }

