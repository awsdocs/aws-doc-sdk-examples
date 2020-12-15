//snippet-sourcedescription:[Query.java demonstrates how to query an Amazon DynamoDB table.]
//snippet-keyword:[SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[10/30/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

 package com.example.dynamodb;
// snippet-start:[dynamodb.java2.query.import]
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import java.util.HashMap;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.regions.Region;
// snippet-end:[dynamodb.java2.query.import]

public class Query {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    Query <tableName> <partitionKeyName> <partitionKeyVal>\n\n" +
                "Where:\n" +
                "    tableName - the Amazon DynamoDB table to put the item in (for example, Music3).\n" +
                "    partitionKeyName - the partition key name of the Amazon DynamoDB table (for example, Artist).\n" +
                "    partitionKeyVal - value of the partition key that should match (for example, Famous Band).\n\n" +
                "Example:\n" ;

        if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String tableName = args[0];
        String partitionKeyName = args[1];
        String partitionKeyVal = args[2];
        String partitionAlias = "#a";

        System.out.format("Querying %s", tableName);
        System.out.println("");

        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .build();

        int count = queryTable(ddb, tableName, partitionKeyName, partitionKeyVal,partitionAlias ) ;
        System.out.println("There were "+count + "record(s) returned");
        ddb.close();
    }

    // snippet-start:[dynamodb.java2.query.main]
    public static int queryTable(DynamoDbClient ddb,
                                 String tableName,
                                 String partitionKeyName,
                                 String partitionKeyVal,
                                 String partitionAlias) {

        // Set up an alias for the partition key name in case it's a reserved word
        HashMap<String,String> attrNameAlias = new HashMap<String,String>();

        attrNameAlias.put(partitionAlias, partitionKeyName);

        // Set up mapping of the partition name with the value
        HashMap<String, AttributeValue> attrValues =
                new HashMap<String,AttributeValue>();
        attrValues.put(":"+partitionKeyName, AttributeValue.builder().s(partitionKeyVal).build());

        QueryRequest queryReq = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression(partitionAlias + " = :" + partitionKeyName)
                .expressionAttributeNames(attrNameAlias)
                .expressionAttributeValues(attrValues)
                .build();

        try {
            QueryResponse response = ddb.query(queryReq);
            return response.count();
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
       return -1;
    }
    // snippet-end:[dynamodb.java2.query.main]
}
