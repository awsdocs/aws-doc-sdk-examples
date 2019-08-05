//snippet-sourcedescription:[UpdateItem.java demonstrates how to update an item in a DynamoDB table.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon DynamoDB]
//snippet-service:[dynamodb]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-01-15]
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
package aws.example.dynamodb;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Update a DynamoDB item in a table.
 *
 * Takes the name of the table, an item to update (primary key value), and the
 * greeting to update it with.
 *
 * The primary key used is "Name", and the greeting will be added to the
 * "Greeting" field.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class UpdateItem
{
    public static void main(String[] args)
    {
        final String USAGE = "\n" +
            "Usage:\n" +
            "    UpdateItem <table> <name> <greeting>\n\n" +
            "Where:\n" +
            "    table    - the table to put the item in.\n" +
            "    name     - a name to update in the table. The name must exist,\n" +
            "               or an error will result.\n" +
            "Additional fields can be specified by appending them to the end of the\n" +
            "input.\n\n" +
            "Examples:\n" +
            "    UpdateItem SiteColors text default=000000 bold=b22222\n" +
            "    UpdateItem SiteColors background default=eeeeee code=d3d3d3\n\n";

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String table_name = args[0];
        String name = args[1];
        ArrayList<String[]> extra_fields = new ArrayList<String[]>();

        // any additional args (fields to add or update)?
        for (int x = 2; x < args.length; x++) {
            String[] fields = args[x].split("=", 2);
            if (fields.length == 2) {
                extra_fields.add(fields);
            } else {
                System.out.format("Invalid argument: %s\n", args[x]);
                System.out.println(USAGE);
                System.exit(1);
            }
        }

        System.out.format("Updating \"%s\" in %s\n", name, table_name);
        if (extra_fields.size() > 0) {
            System.out.println("Additional fields:");
            for (String[] field : extra_fields) {
                System.out.format("  %s: %s\n", field[0], field[1]);
            }
        }

        HashMap<String,AttributeValue> item_key =
           new HashMap<String,AttributeValue>();

        item_key.put("Name", new AttributeValue(name));

        HashMap<String,AttributeValueUpdate> updated_values =
            new HashMap<String,AttributeValueUpdate>();

        for (String[] field : extra_fields) {
            updated_values.put(field[0], new AttributeValueUpdate(
                        new AttributeValue(field[1]), AttributeAction.PUT));
        }

        final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();

        try {
            ddb.updateItem(table_name, item_key, updated_values);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }
}
