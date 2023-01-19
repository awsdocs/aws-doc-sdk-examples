//snippet-sourcedescription:[EnhancedBatchWriteItems.java demonstrates how to insert many items into an Amazon DynamoDB table by using the enhanced client.]
//snippet-keyword:[SDK for Java v2]
//snippet-service:[Amazon DynamoDB]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.dynamodb.enhanced;

// snippet-start:[dynamodb.java2.mapping.batchitems.import]
import com.example.dynamodb.Customer;
import com.example.dynamodb.Music;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
// snippet-end:[dynamodb.java2.mapping.batchitems.import]

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
public class EnhancedBatchWriteItems {

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

        putBatchRecords(enhancedClient);
        ddb.close();
    }

    // snippet-start:[dynamodb.java2.mapping.batchitems.main]
    public static void putBatchRecords(DynamoDbEnhancedClient enhancedClient) {

        try {
            DynamoDbTable<Customer> mappedTable = enhancedClient.table("Customer", TableSchema.fromBean(Customer.class));
            DynamoDbTable<Music> musicTable = enhancedClient.table("Music", TableSchema.fromBean(Music.class));

            LocalDate localDate = LocalDate.parse("2020-04-07");
            LocalDateTime localDateTime = localDate.atStartOfDay();
            Instant instant = localDateTime.toInstant(ZoneOffset.UTC);

            Customer record2 = new Customer();
            record2.setCustName("Fred Pink13");
            record2.setId("id11022");
            record2.setEmail("fredp@noserver.com");
            record2.setRegistrationDate(instant) ;

            Customer record3 = new Customer();
            record3.setCustName("Susan Pink22");
            record3.setId("id122012");
            record3.setEmail("spink@noserver.com");
            record3.setRegistrationDate(instant) ;

            Music music3 = new Music();
            //music3.setId("10012");
            music3.setAlbumTitle("My Music");
            music3.setArtist("ME2");
            music3.setAwards("100");
            music3.setSongTitle("These days");

            BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(WriteBatch.builder(Customer.class)
                    .mappedTableResource(mappedTable)
                    .addPutItem(r -> r.item(record2))
                    .addPutItem(r -> r.item(record3))
                    .build())
                .writeBatches(WriteBatch.builder(Music.class)
                    .mappedTableResource(musicTable)
                    .addPutItem(r -> r.item(music3))
                    .build())
                .build();

            // Add these items to the tables.
            enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);
            System.out.println("done");

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[dynamodb.java2.mapping.batchitems.main]
}