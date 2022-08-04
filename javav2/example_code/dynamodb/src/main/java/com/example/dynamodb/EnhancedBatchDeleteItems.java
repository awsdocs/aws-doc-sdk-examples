//snippet-sourcedescription:[EnhancedBatchDeleteItems.java demonstrates how to delete many items from an Amazon DynamoDB table by using the enhanced client.]
//snippet-keyword:[SDK for Java v2]
//snippet-service:[Amazon DynamoDB]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.dynamodb;

// snippet-start:[dynamodb.java2.mapping.batchdelete.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
// snippet-end:[dynamodb.java2.mapping.batchdelete.import]

/*
 * Before running this code example, create an Amazon DynamoDB table named Customer with these columns:
 *   - id - the id of the record that is the key
 *   - custName - the customer name
 *   - email - the email value
 *   - registrationDate - an instant value when the item was added to the table
 *
 * Also, ensure that you have set up your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class EnhancedBatchDeleteItems {

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
        deleteBatchRecords(enhancedClient);
        ddb.close();
    }

    // snippet-start:[dynamodb.java2.mapping.batchdelete.main]
    public static void deleteBatchRecords(DynamoDbEnhancedClient enhancedClient) {
        try {
            DynamoDbTable<Customer> mappedTable = enhancedClient.table("Customer", TableSchema.fromBean(Customer.class));
            Key key1 = Key.builder()
                .partitionValue("id110")
                .build();

            Key key2 = Key.builder()
                .partitionValue("id120")
                .build();

            BatchWriteItemEnhancedRequest request = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(WriteBatch.builder(Customer.class)
                    .mappedTableResource(mappedTable)
                    .addDeleteItem(DeleteItemEnhancedRequest.builder()
                        .key(key1)
                        .build())
                    .build(),
                WriteBatch.builder(Customer.class)
                    .mappedTableResource(mappedTable)
                    .addDeleteItem(DeleteItemEnhancedRequest.builder()
                        .key(key2)
                        .build())
                    .build())
                .build();

            // Delete these two items from the table.
            enhancedClient.batchWriteItem(request);
            System.out.println("Records deleted");

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[dynamodb.java2.mapping.batchdelete.main]
}