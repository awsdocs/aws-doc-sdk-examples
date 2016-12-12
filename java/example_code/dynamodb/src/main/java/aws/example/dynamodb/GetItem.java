/*
   Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
package aws.example.dynamodb;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.AmazonServiceException;
import java.util.HashMap;
import java.util.Map;

/**
 * Get an item from a DynamoDB table.
 *
 * Takes the name of the table and the name of the item to retrieve from it.
 *
 * The primary key searched is "Name", and the value contained by the field
 * "Greeting" will be returned.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class GetItem
{
    public static void main(String[] args)
    {
        final String USAGE = "\n" +
            "Usage:\n" +
            "    GetItem <table> <name>\n\n" +
            "Where:\n" +
            "    table - the table to get an item from.\n" +
            "    name  - the item to get.\n\n" +
            "Example:\n" +
            "    GetItem HelloTable World\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String table_name = args[0];
        String name = args[1];

        System.out.format("Retrieving greeting for \"%s\" from %s\n", name,
              table_name);

        HashMap<String,AttributeValue> key_to_get =
            new HashMap<String,AttributeValue>();

        key_to_get.put("Name", new AttributeValue(name));

        GetItemRequest request = new GetItemRequest()
            .withKey(key_to_get)
            .withTableName(table_name);

        final AmazonDynamoDBClient ddb = new AmazonDynamoDBClient();

        try {
            Map<String,AttributeValue> returned_item =
               ddb.getItem(request).getItem();
            if (returned_item != null) {
                String greeting = returned_item.get("Greeting").getS();
                System.out.format("%s, %s!\n", greeting, name);
            } else {
                System.out.format("No greeting found for %s!\n", name);
            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }
}

