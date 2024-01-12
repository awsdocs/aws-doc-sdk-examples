// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.dynamodb;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

/**
 * Delete a DynamoDB table.
 *
 * Takes the name of the table to delete.
 *
 * **Warning** The named table will actually be deleted!
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class DeleteTable {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteTable <table>\n\n" +
                "Where:\n" +
                "    table - the table to delete.\n\n" +
                "Example:\n" +
                "    DeleteTable Greetings\n\n" +
                "**Warning** This program will actually delete the table\n" +
                "            that you specify!\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String table_name = args[0];

        System.out.format("Deleting table %s...\n", table_name);

        final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();

        try {
            ddb.deleteTable(table_name);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }
}
