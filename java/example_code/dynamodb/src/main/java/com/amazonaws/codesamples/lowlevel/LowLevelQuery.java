// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.java.codeexample.LowLevelQuery] 
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


package com.amazonaws.codesamples.lowlevel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

public class LowLevelQuery {

    static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
    static String tableName = "Reply";

    public static void main(String[] args) throws Exception {

        String forumName = "Amazon DynamoDB";
        String threadSubject = "DynamoDB Thread 1";

        findRepliesForAThread(forumName, threadSubject);
        findRepliesForAThreadSpecifyOptionalLimit(forumName, threadSubject);
        findRepliesInLast15DaysWithConfig(forumName, threadSubject);
        findRepliesPostedWithinTimePeriod(forumName, threadSubject);
        findRepliesUsingAFilterExpression(forumName, threadSubject);
    }

    private static void findRepliesForAThread(String forumName, String threadSubject) {

        String replyId = forumName + "#" + threadSubject;

        Condition partitionKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
            .withAttributeValueList(new AttributeValue().withS(replyId));

        Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        keyConditions.put("Id", partitionKeyCondition);

        QueryRequest queryRequest = new QueryRequest().withTableName(tableName).withKeyConditions(keyConditions);

        QueryResult result = client.query(queryRequest);
        for (Map<String, AttributeValue> item : result.getItems()) {
            printItem(item);
        }
    }

    private static void findRepliesForAThreadSpecifyOptionalLimit(String forumName, String threadSubject) {

        Map<String, AttributeValue> lastEvaluatedKey = null;
        do {
            QueryRequest queryRequest = new QueryRequest().withTableName(tableName)
                .withKeyConditions(makeReplyKeyConditions(forumName, threadSubject)).withLimit(1)
                .withExclusiveStartKey(lastEvaluatedKey);

            QueryResult result = client.query(queryRequest);
            for (Map<String, AttributeValue> item : result.getItems()) {
                printItem(item);
            }
            lastEvaluatedKey = result.getLastEvaluatedKey();
        } while (lastEvaluatedKey != null);
    }

    private static void findRepliesInLast15DaysWithConfig(String forumName, String threadSubject) {

        long twoWeeksAgoMilli = (new Date()).getTime() - (15L * 24L * 60L * 60L * 1000L);
        Date twoWeeksAgo = new Date();
        twoWeeksAgo.setTime(twoWeeksAgoMilli);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String twoWeeksAgoStr = df.format(twoWeeksAgo);

        Condition sortKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.GT.toString())
            .withAttributeValueList(new AttributeValue().withS(twoWeeksAgoStr));

        Map<String, Condition> keyConditions = makeReplyKeyConditions(forumName, threadSubject);
        keyConditions.put("ReplyDateTime", sortKeyCondition);

        QueryRequest queryRequest = new QueryRequest().withTableName(tableName).withKeyConditions(keyConditions)
            .withProjectionExpression("Message, ReplyDateTime, PostedBy");

        QueryResult result = client.query(queryRequest);
        for (Map<String, AttributeValue> item : result.getItems()) {
            printItem(item);
        }

    }

    private static void findRepliesPostedWithinTimePeriod(String forumName, String threadSubject) {

        long startDateMilli = (new Date()).getTime() - (15L * 24L * 60L * 60L * 1000L);
        long endDateMilli = (new Date()).getTime() - (5L * 24L * 60L * 60L * 1000L);
        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String startDate = df.format(startDateMilli);
        String endDate = df.format(endDateMilli);

        Condition sortKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.BETWEEN.toString())
            .withAttributeValueList(new AttributeValue().withS(startDate), new AttributeValue().withS(endDate));

        Map<String, Condition> keyConditions = makeReplyKeyConditions(forumName, threadSubject);
        keyConditions.put("ReplyDateTime", sortKeyCondition);

        QueryRequest queryRequest = new QueryRequest().withTableName(tableName).withKeyConditions(keyConditions)
            .withProjectionExpression("Message, ReplyDateTime, PostedBy");

        QueryResult result = client.query(queryRequest);
        for (Map<String, AttributeValue> item : result.getItems()) {
            printItem(item);
        }
    }

    private static void findRepliesUsingAFilterExpression(String forumName, String threadSubject) {

        Map<String, Condition> keyConditions = makeReplyKeyConditions(forumName, threadSubject);

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":val", new AttributeValue().withS("User B"));

        QueryRequest queryRequest = new QueryRequest().withTableName(tableName).withKeyConditions(keyConditions)
            .withFilterExpression("PostedBy = :val").withExpressionAttributeValues(expressionAttributeValues)
            .withProjectionExpression("Message, ReplyDateTime, PostedBy");

        QueryResult result = client.query(queryRequest);
        for (Map<String, AttributeValue> item : result.getItems()) {
            printItem(item);
        }
    }

    private static void printItem(Map<String, AttributeValue> attributeList) {
        for (Map.Entry<String, AttributeValue> item : attributeList.entrySet()) {
            String attributeName = item.getKey();
            AttributeValue value = item.getValue();
            System.out.println(attributeName + " " + (value.getS() == null ? "" : "S=[" + value.getS() + "]")
                + (value.getN() == null ? "" : "N=[" + value.getN() + "]")
                + (value.getB() == null ? "" : "B=[" + value.getB() + "]")
                + (value.getSS() == null ? "" : "SS=[" + value.getSS() + "]")
                + (value.getNS() == null ? "" : "NS=[" + value.getNS() + "]")
                + (value.getBS() == null ? "" : "BS=[" + value.getBS() + "] \n"));
        }
    }

    /**
     * A simple helper function that returns a KeyCondition map filled in with
     * the partition key equality condition for a the Reply example table, given
     * a forumName and threadSubject.
     */
    private static Map<String, Condition> makeReplyKeyConditions(String forumName, String threadSubject) {
        String replyId = forumName + "#" + threadSubject;

        Condition partitionKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
            .withAttributeValueList(new AttributeValue().withS(replyId));

        Map<String, Condition> keyConditions = new HashMap<String, Condition>();
        keyConditions.put("Id", partitionKeyCondition);

        return keyConditions;
    }
}

// snippet-end:[dynamodb.java.codeexample.LowLevelQuery] 