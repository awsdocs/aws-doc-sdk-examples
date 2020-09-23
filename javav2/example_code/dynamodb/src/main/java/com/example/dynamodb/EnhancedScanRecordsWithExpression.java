//snippet-sourcedescription:[EnhancedScanRecordsWithExpression.java demonstrates how to scan an Amazon DynamoDB table by using the enhanced client and an Expression object.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[3/15/2020]
//snippet-sourceauthor:[scmacdon-aws]

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
package com.example.dynamodb;

// snippet-start:[dynamodb.java2.mapping.scanEx.import]
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
// snippet-end:[dynamodb.java2.mapping.scanEx.import]

/*
    This code example uses an Expression object to select only Open items for the archive column.
    Prior to running this code example, create a Work table that contains the following fields:

    1.  id - Represents the key.
    2. date - Specifies the date the item was created.
    3. description - A value that describes the item.
    4. guide - A value that represents the deliverable being worked on.
    5. status - A value that describes the status.
    6. username - A value that represents the user who entered the item.
    7. archive - A value that represents whether this is an active or archive item. Specify Open and Closed items.
 */

public class EnhancedScanRecordsWithExpression {
    // Query the Record table
    public static void main(String[] args) {

        //Create a DynamoDbClient object
        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .build();
        // Create a DynamoDbEnhancedClient and use the DynamoDbClient object
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddb)
                .build();

        scan(enhancedClient);
    }

    // snippet-start:[dynamodb.java2.mapping.scanEx.main]
    public static void scan(DynamoDbEnhancedClient enhancedClient) {

        try {
            //Create a DynamoDbTable object
            DynamoDbTable<Work> table = enhancedClient.table("Work", TableSchema.fromBean(Work.class));

            AttributeValue attr = AttributeValue.builder()
                    .s("Open")
                    .build();

            // Get only Open items in the Work table
            Map<String, AttributeValue> myMap = new HashMap<>();
            myMap.put(":val1", attr);

            Map<String, String> myExMap = new HashMap<>();
            myExMap.put("#archive", "archive");

            // Set the Expression so only Closed items are queried from the Work table
            Expression expression = Expression.builder()
                    .expressionValues(myMap)
                    .expressionNames(myExMap)
                    .expression("#archive = :val1")
                    .build();

            ScanEnhancedRequest enhancedRequest = ScanEnhancedRequest.builder()
                    .filterExpression(expression)
                    .limit(15)
                    .build();

            // Get items in the Record table and write out the ID value
            Iterator<Work> results = table.scan().items().iterator();

            while (results.hasNext()) {

                Work rec = results.next();
                System.out.println("The record id is " + rec.getId());
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done");
    }
    // snippet-end:[dynamodb.java2.mapping.scanEx.main]
}
