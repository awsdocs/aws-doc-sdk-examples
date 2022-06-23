// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DynamoDBAsyncGetItem.java demonstrates how to get an item by using the DynamoDbAsyncClient object]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[8/7/2020]
//snippet-sourceauthor:[scmacdon-aws]

/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */
// snippet-start:[dynamodb.Java.DynamoDBAsyncGetItem.complete]
package com.example.dynamodbasync;
// snippet-start:[dynamoasyn.java2.get_item.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
// snippet-end:[dynamoasyn.java2.get_item.import]

public class DynamoDBAsyncGetItem {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DynamoDBAsyncGetItem <table> <key> <keyVal>\n\n" +
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

        // Create the DynamoDbAsyncClient object
        Region region = Region.US_WEST_2;
        DynamoDbAsyncClient client = DynamoDbAsyncClient.builder()
                .region(region)
                .build();

        System.out.format("Retrieving item \"%s\" from \"%s\"\n", keyVal, tableName );
        getItem(client, tableName, key, keyVal);
    }

    // snippet-start:[dynamoasyc.java2.get_item.main]
    public static void getItem(DynamoDbAsyncClient client, String tableName, String key,  String keyVal) {

        HashMap<String, AttributeValue> keyToGet =
                new HashMap<String, AttributeValue>();

        keyToGet.put(key, AttributeValue.builder()
                .s(keyVal).build());

        try {

            // Create a GetItemRequest instance
            GetItemRequest request = GetItemRequest.builder()
                    .key(keyToGet)
                    .tableName(tableName)
                    .build();

            // Invoke the DynamoDbAsyncClient object's getItem
            java.util.Collection<AttributeValue> returnedItem = client.getItem(request).join().item().values();

            // Convert Set to Map
            Map<String, AttributeValue> map = returnedItem.stream().collect(Collectors.toMap(AttributeValue::s, s->s));
            Set<String> keys = map.keySet();
            for (String sinKey : keys) {
                System.out.format("%s: %s\n", sinKey, map.get(sinKey).toString());
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        // snippet-end:[dynamoasyc.java2.get_item.main]
    }
}

// snippet-end:[dynamodb.Java.DynamoDBAsyncGetItem.complete]
