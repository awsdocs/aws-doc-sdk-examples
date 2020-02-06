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

//snippet-sourcedescription:[Query.java demonstrates how to query an AWS DynamoDB table]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[dynamodb]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/5/2020]
//snippet-sourceauthor:[soo-aws]

 package com.example.dynamodb;
// snippet-start:[dynamodb.java2.query.complete]
// snippet-start:[dynamodb.java2.query.import]
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import java.util.HashMap;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.regions.Region;
// snippet-end:[dynamodb.java2.query.import]

/**
 * Query an AWS DynamoDB table.
 *
 *
 * This code expects that you have AWS credentials set up, as described here:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class Query {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    Query <table> <partitionkey> <partitionkeyvalue>\n\n" +
                "Where:\n" +
                "    table - the table to put the item in (i.e., Music3)\n" +
                "    partitionkey - partition key name of the table (i.e., Artist)\n" +
                "    partitionkeyvalue - value of the partition key that should match (i.e., Famous Band)\n\n" +
                "Example:\n" +
                "    Query Music3 Artist Famous Band \n";

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String tableName = args[0];
        String partitionKeyName = args[1];
        String partitionKeyVal = args[2];
        String partitionAlias = "#a";

        System.out.format("Querying %s", tableName);
        System.out.println("");


        // snippet-start:[dynamodb.java2.query.main]
        Region region = Region.US_WEST_2;
        DynamoDbClient ddb = DynamoDbClient.builder().region(region).build();

        //set up an alias for the partition key name in case it's a reserved word
        HashMap<String,String> attrNameAlias = new HashMap<String,String>();

        attrNameAlias.put(partitionAlias, partitionKeyName);

        //set up mapping of the partition name with the value
        HashMap<String, AttributeValue> attrValues =
                new HashMap<String,AttributeValue>();
        attrValues.put(":"+partitionKeyName, AttributeValue.builder().s(partitionKeyVal).build());

        // Cretae a QueryRequest object
        QueryRequest queryReq = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression(partitionAlias + " = :" + partitionKeyName)
                .expressionAttributeNames(attrNameAlias)
                .expressionAttributeValues(attrValues)
                .build();

        try {
            QueryResponse response = ddb.query(queryReq);
            System.out.println(response.count());
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        // snippet-end:[dynamodb.java2.query.main]
        System.out.println("Done!");
    }
}
// snippet-end:[dynamodb.java2.query.complete]
