//snippet-sourcedescription:[GetItem.java demonstrates how to retrieve an item from an AWS DynamoDB table]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[dynamodb]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/5/2020]
//snippet-sourceauthor:[scmacdon]

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

package com.example.dynamodb;
// snippet-start:[dynamodb.java2.get_item.complete]
// snippet-start:[dynamodb.java2.get_item.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
// snippet-end:[dynamodb.java2.get_item.import]

/**
 * Gets an item from an AWS DynamoDB table.
 *
 * This code expects that you have AWS credentials set up, as described here:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class GetItem {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetItem <table> <key> <keyVal>\n\n" +
                "Where:\n" +
                "    table - the table from which an item is retrieved (i.e., Music3)\n" +
                "    key -  the key used in the table (i.e., Artist) \n" +
                "    keyval  - the key value that represents the item to get (i.e., Famous Band)\n" +
                " Example:\n" +
                "    Music3 Artist Famous Band\n" +
                "  **Warning** This program will actually retrieve an item\n" +
                "            that you specify!\n";

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String tableName = args[0];
        String key = args[1];
        String keyVal = args[2];

        System.out.format("Retrieving item \"%s\" from \"%s\"\n",
                keyVal, tableName);

        // Create the DynamoDbClient object
        Region region = Region.US_WEST_2;
        DynamoDbClient ddb = DynamoDbClient.builder().region(region).build();

        HashMap<String,AttributeValue> keyToGet = new HashMap<String,AttributeValue>();

        keyToGet.put(key, AttributeValue.builder()
                .s(keyVal).build());

        // Create a GetItemRequest object
        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(tableName)
                .build();

        try {
            Map<String,AttributeValue> returnedItem = ddb.getItem(request).item();

            if (returnedItem != null) {
                Set<String> keys = returnedItem.keySet();
                System.out.println("Table Attributes: \n");

                for (String key1 : keys) {
                    System.out.format("%s: %s\n", key1, returnedItem.get(key1).toString());
                }
            } else {
                System.out.format("No item found with the key %s!\n", key);
            }
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        // snippet-end:[dynamodb.java2.get_item.main]
    }
}
// snippet-end:[dynamodb.java2.get_item.complete]
