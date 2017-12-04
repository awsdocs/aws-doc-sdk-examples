/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
package com.example.dynamodb;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDBClient;

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

        DynamoDBClient ddb = DynamoDBClient.create();
        
        ListTablesRequest request = ListTablesRequest.builder().build();
        
        boolean more_tables = true;
        while(more_tables) {
            String last_name = null;
            try {
                ListTablesResponse response = null;
                if (last_name == null) {
                    response = ddb.listTables(request);
                }

                List<String> table_names = response.tableNames();

                if (table_names.size() > 0) {
                    for (String cur_name : table_names) {
                        System.out.format("* %s\n", cur_name);
                    }
                } else {
                    System.out.println("No tables found!");
                    System.exit(0);
                }

                last_name = response.lastEvaluatedTableName();
                if (last_name == null) {
                    more_tables = false;
                }
            } catch (S3Exception e) {
                System.err.println(e.getErrorMessage());
                System.exit(1);
            }
        }
        System.out.println("\nDone!");
    }
}

