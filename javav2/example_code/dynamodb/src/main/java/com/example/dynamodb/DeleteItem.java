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

//snippet-sourcedescription:[DeleteItem.java demonstrates how to delete an item from an AWS DynamoDB table]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[dynamodb]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/5/2020]
//snippet-sourceauthor:[soo-aws]

package com.example.dynamodb;
// snippet-start:[dynamodb.java2.delete_item.complete]
// snippet-start:[dynamodb.java2.delete_item.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import java.util.HashMap;
// snippet-end:[dynamodb.java2.delete_item.import]

/**
 * Delete an item from a DynamoDB table.
 *
 * Takes the table name and item (primary key: "Name") to delete.
 *
 * **Warning** The named item will actually be deleted!
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class DeleteItem {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteItem <table> <name>\n\n" +
                "Where:\n" +
                "    table - the table to delete the item from.\n" +
                "    key -  the key used in the table.\n" +
                "    keyval;  - the key value that represents the item to delete,\n" +
                "            using the primary key \"Name\"\n\n" +
                "Example:\n" +
                "    Music3 Artist Famous Band\n\n" +
                "**Warning** This program will actually delete the item\n" +
                "            that you specify!\n";

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // snippet-start:[dynamodb.java2.delete_item.main]

        /* Read the name from command args */
        String tableName = args[0];
        String key = args[1];
        String keyVal = args[2];

        System.out.format("Deleting item \"%s\" from %s\n", keyVal, tableName);

        // Create the DynamoDbClient object
        Region region = Region.US_WEST_2;
        DynamoDbClient ddb = DynamoDbClient.builder().region(region).build();

        HashMap<String,AttributeValue> keyToGet =
                new HashMap<String,AttributeValue>();

        keyToGet.put(key, AttributeValue.builder()
                .s(keyVal)
                .build());

        DeleteItemRequest deleteReq = DeleteItemRequest.builder()
                .tableName(tableName)
                .key(keyToGet)
                .build();

        try {
            ddb.deleteItem(deleteReq);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        // snippet-end:[dynamodb.java2.delete_item.main]
        System.out.println("Done!");
    }
}

// snippet-end:[dynamodb.java2.delete_item.complete]
