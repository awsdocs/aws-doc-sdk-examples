// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.java.codeexample.SampleDataTryQuery] 
/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/


package com.amazonaws.codesamples;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

public class SampleDataTryQuery {

    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    static DynamoDB dynamoDB = new DynamoDB(client);
    static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static void main(String[] args) throws Exception {

        try {

            String forumName = "Amazon DynamoDB";
            String threadSubject = "DynamoDB Thread 1";

            // Get an item.
            getBook(101, "ProductCatalog");

            // Query replies posted in the past 15 days for a forum thread.
            findRepliesInLast15DaysWithConfig("Reply", forumName, threadSubject);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void getBook(int id, String tableName) {

        Table table = dynamoDB.getTable(tableName);

        Item item = table.getItem("Id", // attribute name
            id, // attribute value
            "Id, ISBN, Title, Authors", // projection expression
            null); // name map - don't need this

        System.out.println("GetItem: printing results...");
        System.out.println(item.toJSONPretty());

    }

    private static void findRepliesInLast15DaysWithConfig(String tableName, String forumName, String threadSubject) {

        String replyId = forumName + "#" + threadSubject;
        long twoWeeksAgoMilli = (new Date()).getTime() - (15L * 24L * 60L * 60L * 1000L);
        Date twoWeeksAgo = new Date();
        twoWeeksAgo.setTime(twoWeeksAgoMilli);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String twoWeeksAgoStr = df.format(twoWeeksAgo);

        Table table = dynamoDB.getTable(tableName);

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("Id = :v1 and ReplyDateTime > :v2")
            .withValueMap(new ValueMap().withString(":v1", replyId).withString(":v2", twoWeeksAgoStr))
            .withProjectionExpression("Message, ReplyDateTime, PostedBy");

        ItemCollection<QueryOutcome> items = table.query(querySpec);
        Iterator<Item> iterator = items.iterator();

        System.out.println("Query: printing results...");

        while (iterator.hasNext()) {
            System.out.println(iterator.next().toJSONPretty());
        }
    }

}

// snippet-end:[dynamodb.java.codeexample.SampleDataTryQuery] 