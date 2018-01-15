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
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalOperator;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDBClient;

import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.services.dynamodb.model.DynamoDBException;
import software.amazon.awssdk.core.regions.Region;

/**
 * Query a DynamoDB table.
 *
 * Takes the name of the table to update, the read capacity and the write
 * capacity to use.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class Query
{
    public static void main(String[] args)
    {
        final String USAGE = "\n" +
            "Usage:\n" +
            "    Query <table> <partitionkey> <partitionkeyvalue>\n\n" +
            "Where:\n" +
            "    table - the table to put the item in.\n" +
            "    partitionkey  - partition key name of the table.\n" +
            "    partitionkeyvalue - value of the partition key that should match.\n\n" +
            "Example:\n" +
            "    Query GreetingsTable Language eng \n";

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String table_name = args[0];
        String partition_key_name = args[1];
        String partition_key_val = args[2];
        String partition_alias = "#a";

        System.out.format("Querying %s", table_name);
        System.out.println("");


        Region region = Region.US_WEST_2;
        DynamoDBClient ddb = DynamoDBClient.builder().region(region).build();

        //set up an alias for the partition key name in case it's a reserved word
        HashMap<String,String> attrNameAlias =
                new HashMap<String,String>();
        attrNameAlias.put(partition_alias, partition_key_name);

        //set up mapping of the partition name with the value
        HashMap<String, AttributeValue> attrValues =
                new HashMap<String,AttributeValue>();
        attrValues.put(":"+partition_key_name, AttributeValue.builder().s(partition_key_val).build());

        QueryRequest queryReq = QueryRequest.builder()
        		.tableName(table_name)
        		.keyConditionExpression(partition_alias + " = :" + partition_key_name)
        		.expressionAttributeNames(attrNameAlias)
        		.expressionAttributeValues(attrValues)
        		.build();

        try {
        	QueryResponse response = ddb.query(queryReq);
        	System.out.println(response.count());
        } catch (DynamoDBException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }
}
