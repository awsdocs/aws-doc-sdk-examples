// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace DeleteItems
{
    using System;
    using System.Collections.Generic;
    using System.Net;
    using System.Threading.Tasks;
    using Amazon.DynamoDBv2;
    using Amazon.DynamoDBv2.Model;

    // snippet-start:[dynamodb.dotnetv3.DeleteItemsExample]

    /// <summary>
    /// Deletes items from an Amazon DynamoDB table. The example was created
    /// using the AWS SDK for .NET version 3.7, and .NET Core 5.0.
    /// </summary>
    public class DeleteItems
    {
        /// <summary>
        /// Gets id and area values from the command line, initializes the
        /// DynamoDB client object, and calls the RemoveItemAsync method.
        /// </summary>
        /// <param name="args">The string array containing the command line
        /// arguments.</param>
        public static async Task Main(string[] args)
        {
            var tableName = "CustomersOrdersProducts";
            var idstring = string.Empty;
            var area = string.Empty;

            int i = 0;
            while (i < args.Length)
            {
                switch (args[i])
                {
                    case "-a":
                        i++;
                        area = args[i];
                        break;
                    case "-i":
                        i++;
                        idstring = args[i];
                        break;
                }

                i++;
            }

            if ((area == string.Empty) || (idstring == string.Empty))
            {
                Console.WriteLine("You must supply an area (-a AREA) and ids (-i \"id1 ... idN\")");
                return;
            }

            IAmazonDynamoDB client = new AmazonDynamoDBClient();

            var response = await RemoveItemsAsync(client, tableName, idstring, area);

            if (response.HttpStatusCode == HttpStatusCode.OK)
            {
                Console.WriteLine($"Removed item from {tableName} table.");
            }
        }

        /// <summary>
        /// Removes items from a DynamoDB table.
        /// </summary>
        /// <param name="client">An initialized DynamoDB client object.</param>
        /// <param name="tableName">The name of the table from which the item
        /// will be deleted.</param>
        /// <param name="idstring">The id of the item to be deleted.</param>
        /// <param name="area">The area of the item to delete.</param>
        /// <returns>The response from the call to BatchWriteItemAsync.</returns>
        public static async Task<BatchWriteItemResponse> RemoveItemsAsync(
            IAmazonDynamoDB client,
            string tableName,
            string idstring,
            string area)
        {
            var ids = idstring.Split(" ");

            var writeRequests = new List<WriteRequest>();
            var items = new Dictionary<string, List<WriteRequest>>();

            for (int i = 0; i < ids.Length; i++)
            {
                var writeRequest = new WriteRequest
                {
                    // For the operation to delete an item, if you provide a primary key value
                    // that does not exist in the table, there is no error.
                    DeleteRequest = new DeleteRequest
                    {
                        Key = new Dictionary<string, AttributeValue>()
                        {
                            {
                                "ID",
                                new AttributeValue
                                {
                                    S = ids[i],
                                }
                            },
                            {
                                "Area",
                                new AttributeValue
                                {
                                    S = area,
                                }
                            },
                        },
                    },
                };

                writeRequests.Add(writeRequest);
            }

            items.Add(tableName, writeRequests);

            var request = new BatchWriteItemRequest(items);

            var response = await client.BatchWriteItemAsync(request);

            return response;
        }
    }

    // snippet-end:[dynamodb.dotnetv3.DeleteItemsExample]
}
