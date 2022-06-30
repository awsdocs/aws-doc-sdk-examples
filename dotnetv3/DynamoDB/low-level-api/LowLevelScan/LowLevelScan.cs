// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

// snippet-start:[dynamodb.dotnetv3.LowLevelScanExample]
using System;
using System.Collections.Generic;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace LowLevelScan
{
    public class LowLevelScan
    {
        public static async void FindProductsForPriceLessThanZero(AmazonDynamoDBClient client)
        {
            Dictionary<string, AttributeValue> lastKeyEvaluated = null;
            do
            {
                var request = new ScanRequest
                {
                    TableName = "ProductCatalog",
                    Limit = 2,
                    ExclusiveStartKey = lastKeyEvaluated,
                    ExpressionAttributeValues = new Dictionary<string, AttributeValue> {
                    {":val", new AttributeValue {
                         N = "0"
                     }}
                },
                    FilterExpression = "Price < :val",

                    ProjectionExpression = "Id, Title, Price"
                };

                var response = await client.ScanAsync(request);

                foreach (Dictionary<string, AttributeValue> item
                     in response.Items)
                {
                    Console.WriteLine("\nScanThreadTableUsePaging - printing.....");
                    PrintItem(item);
                }

                lastKeyEvaluated = response.LastEvaluatedKey;

            } while (lastKeyEvaluated != null && lastKeyEvaluated.Count != 0);
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

            FindProductsForPriceLessThanZero(client);
        }
    }
}
// snippet-end:[dynamodb.dotnetv3.LowLevelScanExample]