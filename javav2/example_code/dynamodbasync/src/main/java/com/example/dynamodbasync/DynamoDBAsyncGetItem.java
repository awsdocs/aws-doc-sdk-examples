/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[DynamoDBAsyncGetItem.java demonstrates how to ...]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[]
// snippet-sourceauthor:[AWS]

// snippet-start:[dynamodb.Java.DynamoDBAsyncGetItem.complete]
package com.example.dynamodbasync;
// snippet-start:[dynamoasyn.java2.get_item.import]
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import java.util.*;
import java.util.stream.Collectors;

// snippet-end:[dynamoasyn.java2.get_item.import]
public class DynamoDBAsyncGetItem {

    public static void main(String[] args)
    {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetItem <table> <name> [projection_expression]\n\n" +
                "Where:\n" +
                "    table - the table to get an item from.\n" +
                "    name  - the item to get.\n\n" +
                "You can add an optional projection expression (a quote-delimited,\n" +
                "comma-separated list of attributes to retrieve) to limit the\n" +
                "fields returned from the table.\n\n" +
                "Example:\n" +
                "    GetItem HelloTable World\n" +
                "    GetItem SiteColors text \"default, bold\"\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // snippet-start:[dynamoasyc.java2.get_item.main]
        //Get two input arguments
        String table_name = args[0];
        String name = args[1];
        System.out.format("Retrieving item \"%s\" from \"%s\"\n", name, table_name);

        HashMap<String, AttributeValue> key_to_get =
                new HashMap<String, AttributeValue>();

        key_to_get.put("Name", AttributeValue.builder().s(name).build());

        try {

            DynamoDbAsyncClient client = DynamoDbAsyncClient.create();
            //Create a GetItemRequest instance
            GetItemRequest request =  GetItemRequest.builder()
                    .key(key_to_get)
                    .tableName(table_name)
                    .build();

            //Invoke the DynamoDbAsyncClient object's getItem
            java.util.Collection<software.amazon.awssdk.services.dynamodb.model.AttributeValue>  returned_item = client.getItem(request).join().item().values();

            //Convert Set to Map
            Map<String, AttributeValue> map = returned_item.stream().collect(Collectors.toMap(AttributeValue::s, s->s));
            Set<String> keys = map.keySet();
            for (String key : keys) {
                System.out.format("%s: %s\n",  key, map.get(key).toString());
                }


        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        // snippet-end:[dynamoasyc.java2.get_item.main]
      }
 }
 
// snippet-end:[dynamodb.Java.DynamoDBAsyncGetItem.complete]