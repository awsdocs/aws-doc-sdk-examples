// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace DeleteTable
{
    using System;
    using System.Threading.Tasks;
    using Amazon.DynamoDBv2;
    using Amazon.DynamoDBv2.Model;

    /// <summary>
    /// Deletes an Amazon DynamoDB table. The example was created using the
    /// AWS SDK for .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class DeleteTable
    {
        // snippet-start:[dynamodb.dotnetv3.DeleteTableExample]

        /// <summary>
        /// Initializes the DynamoDB client and then calls RemoveTableAsync.
        /// </summary>
        public static async Task Main()
        {
            IAmazonDynamoDB client = new AmazonDynamoDBClient();
            string tableName = "CustomersOrdersProducts";

            var response = await RemoveTableAsync(client, tableName);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                Console.WriteLine($"Removed {tableName} table.");
            }
            else
            {
                Console.WriteLine($"Could not remove {tableName} table.");
            }
        }

        /// <summary>
        /// Deletes an Amazon DynamoDB table.
        /// </summary>
        /// <param name="client">An initialized DynamoDB client object.</param>
        /// <param name="table">The name of the DynamoDB table to delete.</param>
        /// <returns>The DeleteTableResponse object from DeleteTableAsync.</returns>
        public static async Task<DeleteTableResponse> RemoveTableAsync(IAmazonDynamoDB client, string table)
        {
            var response = await client.DeleteTableAsync(new DeleteTableRequest
            {
                TableName = table,
            });

            return response;
        }

        // snippet-end:[dynamodb.dotnetv3.DeleteTableExample]
    }
}
