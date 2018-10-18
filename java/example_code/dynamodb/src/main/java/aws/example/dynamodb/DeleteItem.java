 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[dynamodb]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
package aws.example.dynamodb;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.AmazonServiceException;
import java.util.HashMap;

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

        HashMap<String,AttributeValue> key_to_get =
            new HashMap<String,AttributeValue>();

        key_to_get.put("Name", new AttributeValue(name));

        final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();

        try {
            ddb.deleteItem(table_name, key_to_get);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }

        System.out.println("Done!");
    }
}

