// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.dynamodb;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

/**
 * Create a DynamoDB table.
 *
 * Takes the name of the table to create. The table will contain a single
 * primary key, "Name".
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class CreateTable {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateTable <table>\n\n" +
                "Where:\n" +
                "    table - the table to create.\n\n" +
                "Example:\n" +
                "    CreateTable HelloTable\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args */
        String table_name = args[0];

        System.out.format(
                "Creating table \"%s\" with a simple primary key: \"Name\".\n",
                table_name);

        CreateTableRequest request = new CreateTableRequest()
                .withAttributeDefinitions(new AttributeDefinition(
                        "Name", ScalarAttributeType.S))
                .withKeySchema(new KeySchemaElement("Name", KeyType.HASH))
                .withProvisionedThroughput(new ProvisionedThroughput(
                        new Long(10), new Long(10)))
                .withTableName(table_name);

        final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();

        try {
            CreateTableResult result = ddb.createTable(request);
            System.out.println(result.getTableDescription().getTableName());
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }
}
