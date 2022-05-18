//snippet-sourcedescription:[EnhancedHandleList.java demonstrates how to put an item that includes a list into an Amazon DynamoDB table by using the enhanced client.]
//snippet-keyword:[SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/16/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.dynamodb;

// snippet-start:[dynamodb.java2.mapping.putitemlist.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import java.util.ArrayList;
import java.util.List;
// snippet-end:[dynamodb.java2.mapping.putitemlist.import]

/*
 * Before running this code example, create an Amazon DynamoDB table named Contact with this column:
 *   id - The id of the record that is the key
 *
 *
 * You should also set up your development environment, including your credentials. For more information,
 * see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class EnhancedHandleList {

    public static void main(String[] args) {

        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(ddb)
                .build();

        putRecord(enhancedClient) ;
        ddb.close();
    }

    // snippet-start:[dynamodb.java2.mapping.putitemlist.main]
    public static void putRecord(DynamoDbEnhancedClient enhancedClient) {

        try {
            DynamoDbTable<Contact> contactTable = (DynamoDbTable<Contact>) enhancedClient.table("Contact", TableSchema.fromBean(Contact.class));
            List<String> names = new ArrayList<>();
            names.add("Scott");
            names.add("LAM");
            names.add("Madison");

            // Populate the table.
            Contact record = new Contact();
            record.setId("103");
            record.setPid(names);
            contactTable.putItem(record);

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("done");
    }
    // snippet-end:[dynamodb.java2.mapping.putitemlist.main]
}
