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

import software.amazon.awssdk.services.dynamodb.DynamoDBClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;

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
public class DeleteTable
{
    public static void main(String[] args)
    {
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

        DynamoDBClient ddb = DynamoDBClient.create();
        
        DeleteTableRequest request = DeleteTableRequest.builder()
        		.tableName(table_name)
        		.build();

        try {
            ddb.deleteTable(request);
        } catch (S3Exception e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }
}

