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

//snippet-sourcedescription:[UpdateItem.java demonstrates how to update a value located in an AWS DynamoDB table]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[dynamodb]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/5/2020]
//snippet-sourceauthor:[soo-aws]

package com.example.dynamodb;
// snippet-start:[dynamodb.java2.update_item.complete]
// snippet-start:[dynamodb.java2.update_item.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import java.util.HashMap;
// snippet-end:[dynamodb.java2.update_item.import]

/**
 * Updates an AWS DynamoDB table with an item.
 *
 * This code expects that you have AWS credentials set up, as described here:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class UpdateItem {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    UpdateItem <table> <key> <keyVal> <name> <updateVal>\n\n" +
                "Where:\n" +
                "    table   - the table to put the item in (i.e., Music3).\n" +
                "    key     - the name of the key in the table (i.e., Artist),\n" +
                "    keyVal  - the value of the key (i.e., Famous Band),\n" +
                "    name    - the name of the column where the value is updated (i.e., Awards),\n" +
                "    updateVal  - the value used to update an item (i.e., 14),\n" +
                "Additional fields can be specified by appending them to the end of the\n" +
                "input.\n\n" +
                "Example:\n" +
                "    UpdateItem Music3 Artist Famous Band Awards 14\n";

        if (args.length < 5) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // snippet-start:[dynamodb.java2.update_item.main]
        String tableName = args[0];
        String key = args[1];
        String keyVal = args[2];
        String name = args[3];
        String updateVal = args[4];

        // Create the DynamoDbClient object
        Region region = Region.US_WEST_2;
        DynamoDbClient ddb = DynamoDbClient.builder().region(region).build();

        HashMap<String,AttributeValue> itemKey = new HashMap<String,AttributeValue>();

        itemKey.put(key, AttributeValue.builder().s(keyVal).build());

        HashMap<String,AttributeValueUpdate> updatedValues =
                new HashMap<String,AttributeValueUpdate>();

        // Update the column specified by name with updatedVal
        updatedValues.put(name, AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(updateVal).build())
                .action(AttributeAction.PUT)
                .build());

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(itemKey)
                .attributeUpdates(updatedValues)
                .build();

        try {
            ddb.updateItem(request);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        // snippet-end:[dynamodb.java2.update_item.main]
        System.out.println("Done!");
    }
}

// snippet-end:[dynamodb.java2.update_item.complete]
