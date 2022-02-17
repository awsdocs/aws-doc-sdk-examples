// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.MidLevelQueryAndScan] 

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
using System.Linq;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DocumentModel;
using Amazon.Runtime;
using Amazon.SecurityToken;

namespace com.amazonaws.codesamples
{
    class MidLevelQueryAndScan
    {
        private static AmazonDynamoDBClient client = new AmazonDynamoDBClient();

        static void Main(string[] args)
        {
            try
            {
                // Query examples.
                Table replyTable = Table.LoadTable(client, "Reply");
                string forumName = "Amazon DynamoDB";
                string threadSubject = "DynamoDB Thread 2";
                FindRepliesInLast15Days(replyTable, forumName, threadSubject);
                FindRepliesInLast15DaysWithConfig(replyTable, forumName, threadSubject);
                FindRepliesPostedWithinTimePeriod(replyTable, forumName, threadSubject);

                // Get Example.
                Table productCatalogTable = Table.LoadTable(client, "ProductCatalog");
                int productId = 101;
                GetProduct(productCatalogTable, productId);

                Console.WriteLine("To continue, press Enter");
                Console.ReadLine();
            }
            catch (AmazonDynamoDBException e) { Console.WriteLine(e.Message); }
            catch (AmazonServiceException e) { Console.WriteLine(e.Message); }
            catch (Exception e) { Console.WriteLine(e.Message); }
        }

        private static void GetProduct(Table tableName, int productId)
        {
            Console.WriteLine("*** Executing GetProduct() ***");
            Document productDocument = tableName.GetItem(productId);
            if (productDocument != null)
            {
                PrintDocument(productDocument);
            }
            else
            {
                Console.WriteLine("Error: product " + productId + " does not exist");
            }
        }

        private static void FindRepliesInLast15Days(Table table, string forumName, string threadSubject)
        {
            string Attribute = forumName + "#" + threadSubject;

            DateTime twoWeeksAgoDate = DateTime.UtcNow - TimeSpan.FromDays(15);
            QueryFilter filter = new QueryFilter("Id", QueryOperator.Equal, partitionKey);
            filter.AddCondition("ReplyDateTime", QueryOperator.GreaterThan, twoWeeksAgoDate);

            // Use Query overloads that takes the minimum required query parameters.
            Search search = table.Query(filter);

            List<Document> documentSet = new List<Document>();
            do
            {
                documentSet = search.GetNextSet();
                Console.WriteLine("\nFindRepliesInLast15Days: printing ............");
                foreach (var document in documentSet)
                    PrintDocument(document);
            } while (!search.IsDone);
        }

        private static void FindRepliesPostedWithinTimePeriod(Table table, string forumName, string threadSubject)
        {
            DateTime startDate = DateTime.UtcNow.Subtract(new TimeSpan(21, 0, 0, 0));
            DateTime endDate = DateTime.UtcNow.Subtract(new TimeSpan(1, 0, 0, 0));

            QueryFilter filter = new QueryFilter("Id", QueryOperator.Equal, forumName + "#" + threadSubject);
            filter.AddCondition("ReplyDateTime", QueryOperator.Between, startDate, endDate);

            QueryOperationConfig config = new QueryOperationConfig()
            {
                Limit = 2, // 2 items/page.
                Select = SelectValues.SpecificAttributes,
                AttributesToGet = new List<string> { "Message",
                                 "ReplyDateTime",
                                 "PostedBy" },
                ConsistentRead = true,
                Filter = filter
            };

            Search search = table.Query(config);

            List<Document> documentList = new List<Document>();

            do
            {
                documentList = search.GetNextSet();
                Console.WriteLine("\nFindRepliesPostedWithinTimePeriod: printing replies posted within dates: {0} and {1} ............", startDate, endDate);
                foreach (var document in documentList)
                {
                    PrintDocument(document);
                }
            } while (!search.IsDone);
        }

        private static void FindRepliesInLast15DaysWithConfig(Table table, string forumName, string threadName)
        {
            DateTime twoWeeksAgoDate = DateTime.UtcNow - TimeSpan.FromDays(15);
            QueryFilter filter = new QueryFilter("Id", QueryOperator.Equal, forumName + "#" + threadName);
            filter.AddCondition("ReplyDateTime", QueryOperator.GreaterThan, twoWeeksAgoDate);
            // You are specifying optional parameters so use QueryOperationConfig.
            QueryOperationConfig config = new QueryOperationConfig()
            {
                Filter = filter,
                // Optional parameters.
                Select = SelectValues.SpecificAttributes,
                AttributesToGet = new List<string> { "Message", "ReplyDateTime",
                                 "PostedBy" },
                ConsistentRead = true
            };

            Search search = table.Query(config);

            List<Document> documentSet = new List<Document>();
            do
            {
                documentSet = search.GetNextSet();
                Console.WriteLine("\nFindRepliesInLast15DaysWithConfig: printing ............");
                foreach (var document in documentSet)
                    PrintDocument(document);
            } while (!search.IsDone);
        }

        private static void PrintDocument(Document document)
        {
            //   count++;
            Console.WriteLine();
            foreach (var attribute in document.GetAttributeNames())
            {
                string stringValue = null;
                var value = document[attribute];
                if (value is Primitive)
                    stringValue = value.AsPrimitive().Value.ToString();
                else if (value is PrimitiveList)
                    stringValue = string.Join(",", (from primitive
                                    in value.AsPrimitiveList().Entries
                                                    select primitive.Value).ToArray());
                Console.WriteLine("{0} - {1}", attribute, stringValue);
            }
        }
    }
}
// snippet-end:[dynamodb.dotNET.CodeExample.MidLevelQueryAndScan]