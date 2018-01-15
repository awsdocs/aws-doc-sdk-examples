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
package com.example.dynamodb;
import software.amazon.awssdk.services.dynamodb.model.DynamoDBException;
import software.amazon.awssdk.services.dynamodb.DynamoDBClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughputDescription;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;
import java.util.List;

/**
 * Get information about (describe) a DynamoDB table.
 *
 * Takes the name of the table as input.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class DescribeTable
{
    public static void main(String[] args)
    {
        final String USAGE = "\n" +
            "Usage:\n" +
            "    DescribeTable <table>\n\n" +
            "Where:\n" +
            "    table - the table to get information about.\n\n" +
            "Example:\n" +
            "    DescribeTable HelloTable\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String table_name = args[0];
        System.out.format("Getting description for %s\n\n", table_name);

        DynamoDBClient ddb = DynamoDBClient.create();

        DescribeTableRequest request = DescribeTableRequest.builder()
        		.tableName(table_name)
        		.build();

        try {
            TableDescription table_info =
               ddb.describeTable(request).table();

            if (table_info != null) {
                System.out.format("Table name  : %s\n",
                      table_info.tableName());
                System.out.format("Table ARN   : %s\n",
                      table_info.tableArn());
                System.out.format("Status      : %s\n",
                      table_info.tableStatus());
                System.out.format("Item count  : %d\n",
                      table_info.itemCount().longValue());
                System.out.format("Size (bytes): %d\n",
                      table_info.tableSizeBytes().longValue());

                ProvisionedThroughputDescription throughput_info =
                   table_info.provisionedThroughput();
                System.out.println("Throughput");
                System.out.format("  Read Capacity : %d\n",
                      throughput_info.readCapacityUnits().longValue());
                System.out.format("  Write Capacity: %d\n",
                      throughput_info.writeCapacityUnits().longValue());

                List<AttributeDefinition> attributes =
                   table_info.attributeDefinitions();
                System.out.println("Attributes");
                for (AttributeDefinition a : attributes) {
                    System.out.format("  %s (%s)\n",
                          a.attributeName(), a.attributeType());
                }
            }
        } catch (DynamoDBException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("\nDone!");
    }
}
