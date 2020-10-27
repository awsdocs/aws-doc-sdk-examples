// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.MidLevelQueryAndScan]
using System;
using System.Collections.Generic;
using System.Linq;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DocumentModel;

namespace MidLevelQueryAndScan
{
    public class MidLevelQueryAndScan
    {
        public static async void GetProduct(Table tableName, int productId)
        {
            Console.WriteLine("*** Executing GetProduct() ***");
            Document productDocument = await tableName.GetItemAsync(productId);
            if (productDocument != null)
            {
                PrintDocument(productDocument);
            }
            else
            {
                Console.WriteLine("Error: product " + productId + " does not exist");
            }
        }

        public static async void FindRepliesInLast15Days(Table table, string forumName, string threadSubject)
        {
            DateTime twoWeeksAgoDate = DateTime.UtcNow - TimeSpan.FromDays(15);
            QueryFilter filter = new QueryFilter("Id", QueryOperator.Equal, "Id");
            filter.AddCondition("ReplyDateTime", QueryOperator.GreaterThan, twoWeeksAgoDate);

            // Use Query overloads that takes the minimum required query parameters.
            Search search = table.Query(filter);

            do
            {
                var documentSet = await search.GetNextSetAsync();
                Console.WriteLine("\nFindRepliesInLast15Days: printing ............");

                foreach (var document in documentSet)
                    PrintDocument(document);

            } while (!search.IsDone);
        }

        public static async void FindRepliesPostedWithinTimePeriod(Table table, string forumName, string threadSubject)
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

            do
            {
                var documentList = await search.GetNextSetAsync();
                Console.WriteLine("\nFindRepliesPostedWithinTimePeriod: printing replies posted within dates: {0} and {1} ............", startDate, endDate);
                
                foreach (var document in documentList)
                {
                    PrintDocument(document);
                }

            } while (!search.IsDone);
        }

        public static async void FindRepliesInLast15DaysWithConfig(Table table, string forumName, string threadName)
        {
            DateTime twoWeeksAgoDate = DateTime.UtcNow - TimeSpan.FromDays(15);
            QueryFilter filter = new QueryFilter("Id", QueryOperator.Equal, forumName + "#" + threadName);
            filter.AddCondition("ReplyDateTime", QueryOperator.GreaterThan, twoWeeksAgoDate);
            
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

            do
            {
                var documentSet = await search.GetNextSetAsync();
                Console.WriteLine("\nFindRepliesInLast15DaysWithConfig: printing ............");

                foreach (var document in documentSet)
                    PrintDocument(document);

            } while (!search.IsDone);
        }

        public static void PrintDocument(Document document)
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
        static void Main()
        {
            var client = new AmazonDynamoDBClient();

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
        }
    }
}
// snippet-end:[dynamodb.dotnet35.MidLevelQueryAndScan]