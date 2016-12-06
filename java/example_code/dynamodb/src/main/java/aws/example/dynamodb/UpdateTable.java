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
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.AmazonServiceException;
import java.util.ArrayList;

/**
 * Update a DynamoDB table (change provisioned throughput).
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class UpdateTable
{
    public static void main(String[] args)
    {
        final String USAGE = "\n" +
            "To run this example, supply the name of the table to update, and\n" +
            "read/write capacity values to use.\n\n" +
            "Ex: UpdateTable <table_name> <read_capacity> <write_capacity>\n";

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String table_name = args[0];
        ProvisionedThroughput table_throughput = new ProvisionedThroughput(
            Long.parseLong(args[1]), Long.parseLong(args[2]));

        System.out.format(
                "Updating %s with new provisioned throughput values\n",
                table_name);
        System.out.format("Read capacity : %d\n",
                table_throughput.getReadCapacityUnits().longValue());
        System.out.format("Write capacity : %d\n",
                table_throughput.getWriteCapacityUnits().longValue());

        final AmazonDynamoDBClient ddb = new AmazonDynamoDBClient();

        try {
            ddb.updateTable(table_name, table_throughput);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }
}

