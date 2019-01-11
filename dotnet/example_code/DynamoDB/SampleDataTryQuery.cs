// snippet-sourcedescription:[SampleDataTryQuery.cs demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.5447b1b6-45a2-4073-948a-1de122c972e8] 

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
    class SampleDataTryQuery
    {
        private static AmazonDynamoDBClient client = new AmazonDynamoDBClient();

        static void Main(string[] args)
        {
            try
            {
                // Get  - Get a book item.
                GetBook(101, "ProductCatalog");

                // Query - Get replies posted in the last 15 days for a forum thread.
                string forumName = "Amazon DynamoDB";
                string threadSubject = "DynamoDB Thread 1";

                FindRepliesInLast15DaysWithConfig(forumName, threadSubject);
            }
            catch (AmazonDynamoDBException e) { Console.WriteLine(e.Message); }
            catch (AmazonServiceException e) { Console.WriteLine(e.Message); }
            catch (Exception e) { Console.WriteLine(e.Message); }
        }

        private static void GetBook(int id, string tableName)
        {
            var request = new GetItemRequest
            {
                TableName = tableName,
                Key = new Dictionary<string, AttributeValue>()
            {
                { "Id", new AttributeValue {
                      N = id.ToString()
                  } }
            },
                ReturnConsumedCapacity = "TOTAL"
            };
            var response = client.GetItem(request);

            Console.WriteLine("No. of reads used (by get book item) {0}\n",
                      response.ConsumedCapacity.CapacityUnits);

            PrintItem(response.Item);


            Console.WriteLine("To continue, press Enter");
            Console.ReadLine();
        }

        private static void FindRepliesInLast15DaysWithConfig(string forumName, string threadSubject)
        {
            string replyId = forumName + "#" + threadSubject;

            DateTime twoWeeksAgoDate = DateTime.UtcNow - TimeSpan.FromDays(15);
            string twoWeeksAgoString =
                twoWeeksAgoDate.ToString(AWSSDKUtils.ISO8601DateFormat);

            Dictionary<string, AttributeValue> lastKeyEvaluated = null;
            do
            {
                var request = new QueryRequest
                {
                    TableName = "Reply",
                    KeyConditionExpression = "Id = :v_replyId and ReplyDateTime > :v_datetime",
                    ExpressionAttributeValues = new Dictionary<string, AttributeValue> {
                    {":v_replyId", new AttributeValue {
                         S = replyId
                     }},
                    {":v_datetime", new AttributeValue {
                         S = twoWeeksAgoString
                     }}
                },

                    // Optional parameter.
                    ProjectionExpression = "Id, ReplyDateTime, PostedBy",

                    // Optional parameter.
                    ConsistentRead = true,
                    Limit = 2, // The Reply table has only a few sample items. So the page size is smaller.
                    ExclusiveStartKey = lastKeyEvaluated,
                    ReturnConsumedCapacity = "TOTAL"
                };

                // Optional parameter.
                request.ProjectionExpression = "Id, ReplyDateTime, PostedBy";

                // Optional parameter.
                request.ConsistentRead = true;
                request.Limit = 2; // The Reply table has only a few sample items. So the page size is smaller.
                request.ExclusiveStartKey = lastKeyEvaluated;
                request.ReturnConsumedCapacity = "TOTAL";

                var response = client.Query(request);

                Console.WriteLine("No. of reads used (by query in FindRepliesForAThreadSpecifyLimit) {0}\n",
                          response.ConsumedCapacity.CapacityUnits);
                foreach (var item in response.Items)
                {
                    PrintItem(item);
                }
                lastKeyEvaluated = response.LastEvaluatedKey;
            } while (lastKeyEvaluated != null && lastKeyEvaluated.Count != 0);
            Console.WriteLine("To continue, press Enter");
            Console.ReadLine();
        }

        private static void PrintItem(Dictionary<string, AttributeValue> attributeList)
        {
            foreach (var kvp in attributeList)
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

// snippet-end:[dynamodb.dotNET.CodeExample.5447b1b6-45a2-4073-948a-1de122c972e8]