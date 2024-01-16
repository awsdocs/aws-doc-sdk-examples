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
 * Takes the name of the table to create. The table will contain a
 * composite key.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class CreateTableCompositeKey {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateTable <table>\n\n" +
                "Where:\n" +
                "    table - the table to create.\n\n" +
                "Example:\n" +
                "    CreateTable GreetingsTable\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args */
        String table_name = args[0];

        System.out.format("Creating table %s\n with a composite primary key:\n");
        System.out.format("* Language - partition key\n");
        System.out.format("* Greeting - sort key\n");

        CreateTableRequest request = new CreateTableRequest()
                .withAttributeDefinitions(
                        new AttributeDefinition("Language", ScalarAttributeType.S),
                        new AttributeDefinition("Greeting", ScalarAttributeType.S))
                .withKeySchema(
                        new KeySchemaElement("Language", KeyType.HASH),
                        new KeySchemaElement("Greeting", KeyType.RANGE))
                .withProvisionedThroughput(
                        new ProvisionedThroughput(new Long(10), new Long(10)))
                .withTableName(table_name);

        final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();

        try {
            CreateTableResult result = ddb.createTable(request);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }
}
