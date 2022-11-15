// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

// snippet-start:[dynamodb.dotnetv3.LowLevelQueryExample]
using System;
using System.Collections.Generic;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using Amazon.Util;

namespace LowLevelQuery
{
    public class LowLevelQuery
    {
        // Query a specific forum and thread.
        private static readonly string ForumName = "Amazon DynamoDB";
        private static readonly string ThreadSubject = "DynamoDB Thread 1";

        public static async void FindRepliesPostedWithinTimePeriod(AmazonDynamoDBClient client)
        {
            Console.WriteLine("*** Executing FindRepliesPostedWithinTimePeriod() ***");
            string replyId = ForumName + "#" + ThreadSubject;
            // You must provide date value based on your test data.
            DateTime startDate = DateTime.UtcNow - TimeSpan.FromDays(21);
            string start = startDate.ToString(AWSSDKUtils.ISO8601DateFormat);

            // You provide date value based on your test data.
            DateTime endDate = DateTime.UtcNow - TimeSpan.FromDays(5);
            string end = endDate.ToString(AWSSDKUtils.ISO8601DateFormat);

            var request = new QueryRequest
            {
                TableName = "Reply",
                ReturnConsumedCapacity = "TOTAL",
                KeyConditionExpression = "Id = :v_replyId and ReplyDateTime between :v_start and :v_end",
                ExpressionAttributeValues = new Dictionary<string, AttributeValue> {
                {":v_replyId", new AttributeValue {
                     S = replyId
                 }},
                {":v_start", new AttributeValue {
                     S = start
                 }},
                {":v_end", new AttributeValue {
                     S = end
                 }}
            }
            };

            var response = await client.QueryAsync(request);

            Console.WriteLine("\nNo. of reads used (by query in FindRepliesPostedWithinTimePeriod) {0}.",
                      response.ConsumedCapacity.CapacityUnits);
            foreach (Dictionary<string, AttributeValue> item
                 in response.Items)
            {
                PrintItem(item);
            }
            Console.WriteLine("To continue, press Enter.");
            Console.ReadLine();
        }

        public static async void FindRepliesInLast15DaysWithConfig(AmazonDynamoDBClient client)
        {
            Console.WriteLine("*** Executing FindRepliesInLast15DaysWithConfig() ***");
            string replyId = ForumName + "#" + ThreadSubject;

            DateTime twoWeeksAgoDate = DateTime.UtcNow - TimeSpan.FromDays(15);
            string twoWeeksAgoString =
                twoWeeksAgoDate.ToString(AWSSDKUtils.ISO8601DateFormat);

            var request = new QueryRequest
            {
                TableName = "Reply",
                ReturnConsumedCapacity = "TOTAL",
                KeyConditionExpression = "Id = :v_replyId and ReplyDateTime > :v_interval",
                ExpressionAttributeValues = new Dictionary<string, AttributeValue> {
                {":v_replyId", new AttributeValue {
                     S = replyId
                 }},
                {":v_interval", new AttributeValue {
                     S = twoWeeksAgoString
                 }}
            },

                // Optional parameter.
                ProjectionExpression = "Id, ReplyDateTime, PostedBy",
                // Optional parameter.
                ConsistentRead = true
            };

            var response = await client.QueryAsync(request);

            Console.WriteLine("No. of reads used (by query in FindRepliesInLast15DaysWithConfig) {0}.",
                      response.ConsumedCapacity.CapacityUnits);
            foreach (Dictionary<string, AttributeValue> item
                 in response.Items)
            {
                PrintItem(item);
            }
            Console.WriteLine("To continue, press Enter.");
            Console.ReadLine();
        }

        public static async void FindRepliesForAThreadSpecifyOptionalLimit(AmazonDynamoDBClient client)
        {
            Console.WriteLine("*** Executing FindRepliesForAThreadSpecifyOptionalLimit() ***");
            string replyId = ForumName + "#" + ThreadSubject;

            Dictionary<string, AttributeValue> lastKeyEvaluated = null;
            do
            {
                var request = new QueryRequest
                {
                    TableName = "Reply",
                    ReturnConsumedCapacity = "TOTAL",
                    KeyConditionExpression = "Id = :v_replyId",
                    ExpressionAttributeValues = new Dictionary<string, AttributeValue> {
                    {":v_replyId", new AttributeValue {
                         S = replyId
                     }}
                },
                    Limit = 2, // The Reply table has only a few sample items. So the page size is smaller.
                    ExclusiveStartKey = lastKeyEvaluated
                };

                var response = await client.QueryAsync(request);

                Console.WriteLine("No. of reads used (by query in FindRepliesForAThreadSpecifyLimit) {0}\n",
                          response.ConsumedCapacity.CapacityUnits);
                foreach (Dictionary<string, AttributeValue> item
                     in response.Items)
                {
                    PrintItem(item);
                }
                lastKeyEvaluated = response.LastEvaluatedKey;
            } while (lastKeyEvaluated != null && lastKeyEvaluated.Count != 0);

            Console.WriteLine("To continue, press Enter.");


            Console.ReadLine();
        }

        public static async void FindRepliesForAThread(AmazonDynamoDBClient client)
        {
            Console.WriteLine("*** Executing FindRepliesForAThread() ***");
            string replyId = ForumName + "#" + ThreadSubject;

            var request = new QueryRequest
            {
                TableName = "Reply",
                ReturnConsumedCapacity = "TOTAL",
                KeyConditionExpression = "Id = :v_replyId",
                ExpressionAttributeValues = new Dictionary<string, AttributeValue> {
                {":v_replyId", new AttributeValue {
                     S = replyId
                 }}
            }
            };

            var response = await client.QueryAsync(request);
            Console.WriteLine("No. of reads used (by query in FindRepliesForAThread) {0}.\n",
                      response.ConsumedCapacity.CapacityUnits);
            foreach (Dictionary<string, AttributeValue> item in response.Items)
            {
                PrintItem(item);
            }
            Console.WriteLine("To continue, press Enter.");
            Console.ReadLine();
        }

        private static void PrintItem(
            Dictionary<string, AttributeValue> attributeList)
        {
            foreach (KeyValuePair<string, AttributeValue> kvp in attributeList)
            {
                string attributeName = kvp.Key;
                AttributeValue value = kvp.Value;

                Console.WriteLine(
                    attributeName + " " +
                    (value.S == null ? "" : "S=[" + value.S + "]") +
                    (value.N == null ? "" : "N=[" + value.N + "]") +
                    (value.SS == null ? "" : "SS=[" + string.Join(",", value.SS.ToArray()) + "]") +
                    (value.NS == null ? "" : "NS=[" + string.Join(",", value.NS.ToArray()) + "]")
                    );
            }
            Console.WriteLine("************************************************");
        }

        static void Main()
        {
            var client = new AmazonDynamoDBClient();

            FindRepliesForAThread(client);
            FindRepliesForAThreadSpecifyOptionalLimit(client);
            FindRepliesInLast15DaysWithConfig(client);
            FindRepliesPostedWithinTimePeriod(client);
        }
    }
}
// snippet-end:[dynamodb.dotnetv3.LowLevelQueryExample]