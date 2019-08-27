//snippet-sourcedescription:[DeleteItem.java demonstrates how to ...]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[dynamodb]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
package com.example.dynamodb;
// snippet-start:[dynamodb.java2.delete_item.complete]
// snippet-start:[dynamodb.java2.delete_item.import]

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
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
public class DeleteItem
{
    public static void main(String[] args)
    {
        final String USAGE = "\n" +
            "Usage:\n" +
            "    DeleteItem <table> <name>\n\n" +
            "Where:\n" +
            "    table - the table to delete the item from.\n" +
            "    name  - the item to delete from the table,\n" +
            "            using the primary key \"Name\"\n\n" +
            "Example:\n" +
            "    DeleteItem HelloTable World\n\n" +
            "**Warning** This program will actually delete the item\n" +
            "            that you specify!\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String table_name = args[0];
        String name = args[1];

        System.out.format("Deleting item \"%s\" from %s\n", name, table_name);

        // snippet-start:[dynamodb.java2.delete_item.main]
        HashMap<String,AttributeValue> key_to_get =
                new HashMap<String,AttributeValue>();

            key_to_get.put("Name", AttributeValue.builder()
            		.s(name)
            		.build());

        DeleteItemRequest deleteReq = DeleteItemRequest.builder()
        		.tableName(table_name)
        		.key(key_to_get)
        		.build();

        DynamoDbAsyncClient ddb = DynamoDbAsyncClient.create();

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
