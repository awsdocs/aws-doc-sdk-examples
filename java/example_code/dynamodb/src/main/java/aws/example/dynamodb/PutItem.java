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
import com.amazonaws.AmazonServiceException;
import java.util.HashMap;

/**
 * Put an item in a DynamoDB table.
 *
 * Takes the name of the table, a name (primary key value) and a greeting
 * (associated with the key value).
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class PutItem
{
    public static void main(String[] args)
    {
        final String USAGE = "\n" +
            "Usage:\n" +
            "    PutItem <table> <name> <greeting>\n\n" +
            "Where:\n" +
            "    table   - the table to put the item in.\n" +
            "    name    - a name to add to the table. If the name already\n" +
            "              exists, its entry will be updated.\n" +
            "    greeing - a greeting to associate with the name.\n\n" +
            "Examples:\n" +
            "    PutItem HelloTable World Hello.\n" +
            "    PutItem HelloTable Friend \"Good Morning!\"\n";

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String table_name = args[0];
        String name = args[1];
        String greeting = args[2];

        System.out.format("Putting item in %s\n", table_name);
        System.out.format("  Name    : %s\n", name);
        System.out.format("  Greeting: %s\n", greeting);

        HashMap<String,AttributeValue> item_values = new HashMap<String,AttributeValue>();
        item_values.put("Name", new AttributeValue(name));
        item_values.put("Greeting", new AttributeValue(greeting));

        final AmazonDynamoDBClient ddb = new AmazonDynamoDBClient();

        try {
            ddb.putItem(table_name, item_values);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }
}

