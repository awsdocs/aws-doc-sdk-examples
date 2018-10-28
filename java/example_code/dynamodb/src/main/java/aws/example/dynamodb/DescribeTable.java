//snippet-sourcedescription:[DescribeTable.java demonstrates how to get information about (describe) a DynamoDB table.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[dynamodb]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
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
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputDescription;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
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

        final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();

        try {
            TableDescription table_info =
               ddb.describeTable(table_name).getTable();

            if (table_info != null) {
                System.out.format("Table name  : %s\n",
                      table_info.getTableName());
                System.out.format("Table ARN   : %s\n",
                      table_info.getTableArn());
                System.out.format("Status      : %s\n",
                      table_info.getTableStatus());
                System.out.format("Item count  : %d\n",
                      table_info.getItemCount().longValue());
                System.out.format("Size (bytes): %d\n",
                      table_info.getTableSizeBytes().longValue());

                ProvisionedThroughputDescription throughput_info =
                   table_info.getProvisionedThroughput();
                System.out.println("Throughput");
                System.out.format("  Read Capacity : %d\n",
                      throughput_info.getReadCapacityUnits().longValue());
                System.out.format("  Write Capacity: %d\n",
                      throughput_info.getWriteCapacityUnits().longValue());

                List<AttributeDefinition> attributes =
                   table_info.getAttributeDefinitions();
                System.out.println("Attributes");
                for (AttributeDefinition a : attributes) {
                    System.out.format("  %s (%s)\n",
                          a.getAttributeName(), a.getAttributeType());
                }
            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("\nDone!");
    }
}
