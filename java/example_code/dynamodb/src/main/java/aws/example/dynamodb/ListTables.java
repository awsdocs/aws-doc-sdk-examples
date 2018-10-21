//snippet-sourcedescription:[ListTables.java demonstrates how to list DynamoDB tables for the current AWS account.]
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
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ListTablesRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import java.util.List;

/**
 * List DynamoDB tables for the current AWS account.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class ListTables
{
    public static void main(String[] args)
    {
        System.out.println("Your DynamoDB tables:\n");

        final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();

        ListTablesRequest request;

        boolean more_tables = true;
        String last_name = null;

        while(more_tables) {
            try {
                if (last_name == null) {
                	request = new ListTablesRequest().withLimit(10);
                }
                else {
                	request = new ListTablesRequest()
                			.withLimit(10)
                			.withExclusiveStartTableName(last_name);
                }

                ListTablesResult table_list = ddb.listTables(request);
                List<String> table_names = table_list.getTableNames();

                if (table_names.size() > 0) {
                    for (String cur_name : table_names) {
                        System.out.format("* %s\n", cur_name);
                    }
                } else {
                    System.out.println("No tables found!");
                    System.exit(0);
                }

                last_name = table_list.getLastEvaluatedTableName();
                if (last_name == null) {
                    more_tables = false;
                }

            } catch (AmazonServiceException e) {
                System.err.println(e.getErrorMessage());
                System.exit(1);
            }
        }
        System.out.println("\nDone!");
    }
}
