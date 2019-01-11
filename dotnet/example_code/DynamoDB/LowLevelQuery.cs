// snippet-sourcedescription:[LowLevelQuery.cs demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.b45a6d68-8270-4c03-9476-10e6a3fa673c] 

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
using System;
using System.Collections.Generic;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using Amazon.Runtime;
using Amazon.Util;

namespace com.amazonaws.codesamples
{
    class LowLevelQuery
    {
        private static AmazonDynamoDBClient client = new AmazonDynamoDBClient();

        static void Main(string[] args)
        {
            try
            {
                // Query a specific forum and thread.
                string forumName = "Amazon DynamoDB";
                string threadSubject = "DynamoDB Thread 1";

                FindRepliesForAThread(forumName, threadSubject);
                FindRepliesForAThreadSpecifyOptionalLimit(forumName, threadSubject);
                FindRepliesInLast15DaysWithConfig(forumName, threadSubject);
                FindRepliesPostedWithinTimePeriod(forumName, threadSubject);

                Console.WriteLine("Example complete. To continue, press Enter");
                Console.ReadLine();
            }
            catch (AmazonDynamoDBException e) { Console.WriteLine(e.Message); Console.ReadLine(); }
            catch (AmazonServiceException e) { Console.WriteLine(e.Message); Console.ReadLine(); }
            catch (Exception e) { Console.WriteLine(e.Message); Console.ReadLine(); }
        }

        private static void FindRepliesPostedWithinTimePeriod(string forumName, string threadSubject)
        {
            Console.WriteLine("*** Executing FindRepliesPostedWithinTimePeriod() ***");
            string replyId = forumName + "#" + threadSubject;
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

            var response = client.Query(request);

            Console.WriteLine("\nNo. of reads used (by query in FindRepliesPostedWithinTimePeriod) {0}",
                      response.ConsumedCapacity.CapacityUnits);
            foreach (Dictionary<string, AttributeValue> item
                 in response.Items)
            {
                PrintItem(item);
            }
            Console.WriteLine("To continue, press Enter");
            Console.ReadLine();
        }

        private static void FindRepliesInLast15DaysWithConfig(string forumName, string threadSubject)
        {
            Console.WriteLine("*** Executing FindRepliesInLast15DaysWithConfig() ***");
            string replyId = forumName + "#" + threadSubject;

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

            var response = client.Query(request);

            Console.WriteLine("No. of reads used (by query in FindRepliesInLast15DaysWithConfig) {0}",
                      response.ConsumedCapacity.CapacityUnits);
            foreach (Dictionary<string, AttributeValue> item
                 in response.Items)
            {
                PrintItem(item);
            }
            Console.WriteLine("To continue, press Enter");
            Console.ReadLine();
        }

        private static void FindRepliesForAThreadSpecifyOptionalLimit(string forumName, string threadSubject)
        {
            Console.WriteLine("*** Executing FindRepliesForAThreadSpecifyOptionalLimit() ***");
            string replyId = forumName + "#" + threadSubject;

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

                var response = client.Query(request);

                Console.WriteLine("No. of reads used (by query in FindRepliesForAThreadSpecifyLimit) {0}\n",
                          response.ConsumedCapacity.CapacityUnits);
                foreach (Dictionary<string, AttributeValue> item
                     in response.Items)
                {
                    PrintItem(item);
                }
                lastKeyEvaluated = response.LastEvaluatedKey;
            } while (lastKeyEvaluated != null && lastKeyEvaluated.Count != 0);

            Console.WriteLine("To continue, press Enter");


            Console.ReadLine();
        }

        private static void FindRepliesForAThread(string forumName, string threadSubject)
        {
            Console.WriteLine("*** Executing FindRepliesForAThread() ***");
            string replyId = forumName + "#" + threadSubject;

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

            var response = client.Query(request);
            Console.WriteLine("No. of reads used (by query in FindRepliesForAThread) {0}\n",
                      response.ConsumedCapacity.CapacityUnits);
            foreach (Dictionary<string, AttributeValue> item in response.Items)
            {
                PrintItem(item);
            }
            Console.WriteLine("To continue, press Enter");
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
    }
}

// snippet-end:[dynamodb.dotNET.CodeExample.b45a6d68-8270-4c03-9476-10e6a3fa673c]