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

//snippet-sourcedescription:[CreateTable.java demonstrates how to create an AWS DynamoDB table]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[dynamodb]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/5/2020]
//snippet-sourceauthor:[soo-aws]

package com.example.dynamodb;
// snippet-start:[dynamodb.java2.create_table.complete]
// snippet-start:[dynamodb.java2.create_table.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
// snippet-end:[dynamodb.java2.create_table.import]

/**
 * Creates an AWS DynamoDB table.
 *
 * Takes the name of the table to create . The table contains a single
 * primary key as specified by args[1]
 *
 * This code expects that you have AWS credentials set up, as described here:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class CreateTable {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateTable <table> <key >\n\n" +
                "Where:\n" +
                "    table - the table to create (i.e., Music3)\n\n" +
                "    key   - the key for the table (i.e., Artist)\n" +
                "Example:\n" +
                "    Music3 Artist \n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // snippet-start:[dynamodb.java2.create_table.main]
        /* Read the name from command args */
        String tableName = args[0];
        String key = args[1];

        System.out.format(
                "Creating table \"%s\" with a simple primary key: \"Name\".\n",
                tableName);

        // Create the DynamoDbClient object
        Region region = Region.US_WEST_2;
        DynamoDbClient ddb = DynamoDbClient.builder().region(region).build();

        // Create the CreateTableRequest object
        CreateTableRequest request = CreateTableRequest.builder()
                .attributeDefinitions(AttributeDefinition.builder()
                        .attributeName(key)
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .keySchema(KeySchemaElement.builder()
                        .attributeName(key)
                        .keyType(KeyType.HASH)
                        .build())
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(new Long(10))
                        .writeCapacityUnits(new Long(10))
                        .build())
                .tableName(tableName)
                .build();

        try {
            CreateTableResponse response = ddb.createTable(request);
            System.out.println(response.tableDescription().tableName());
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        // snippet-end:[dynamodb.java2.create_table.main]
        System.out.println("Done!");
    }
}
// snippet-end:[dynamodb.java2.create_table.complete]
