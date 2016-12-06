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
import com.amazonaws.AmazonServiceException;

/**
 * Delete a DynamoDB table.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class DeleteTable
{
    public static void main(String[] args)
    {
        final String USAGE = "\n" +
            "To run this example, supply the name of a table to delete!\n" +
            "Ex: DeleteTable <table_name>\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String table_name = args[0];

        System.out.format("Deleting table %s...\n", table_name);

        final AmazonDynamoDBClient ddb = new AmazonDynamoDBClient();

        try {
            ddb.deleteTable(table_name);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }
}

