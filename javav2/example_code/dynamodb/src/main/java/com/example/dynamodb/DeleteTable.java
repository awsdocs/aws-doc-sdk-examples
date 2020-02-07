/*
   Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/


//snippet-sourcedescription:[DeleteTable.java demonstrates how to delete an AWS DynamoDB table]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[dynamodb]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/5/2020]
//snippet-sourceauthor:[soo-aws]

package com.example.dynamodb;
// snippet-start:[dynamodb.java2.delete_table.complete]
// snippet-start:[dynamodb.java2.delete_table.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;

// snippet-end:[dynamodb.java2.delete_table.import]

/**
 * Deletes an AWS DynamoDB table.
 *
 * **Warning** The named table will actually be deleted!
 *
 * This code expects that you have AWS credentials set up, as described here:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class DeleteTable {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteTable <table>\n\n" +
                "Where:\n" +
                "    table - the table to delete (i.e., Music3)\n\n" +
                "Example:\n" +
                "    DeleteTable Music3\n\n" +
                "**Warning** This program will actually delete the table\n" +
                "            that you specify!\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String tableName = args[0];

        System.out.format("Deleting table %s...\n", tableName);

        // snippet-start:[dynamodb.java2.delete_table.main]
        // Create the DynamoDbClient object
        Region region = Region.US_WEST_2;
        DynamoDbClient ddb = DynamoDbClient.builder().region(region).build();

        DeleteTableRequest request = DeleteTableRequest.builder()
                .tableName(tableName)
                .build();

        try {
            ddb.deleteTable(request);

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        // snippet-end:[dynamodb.java2.delete_table.main]
        System.out.println(tableName +" was successfully deleted!");
    }
}
// snippet-end:[dynamodb.java2.delete_table.complete]
