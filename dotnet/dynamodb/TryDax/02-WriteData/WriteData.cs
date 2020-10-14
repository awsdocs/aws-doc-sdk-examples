// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.TryDaxWriteData]
using System;
using System.Collections.Generic;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace DynamoDBCRUD
{
    public class TryDaxWriteData
    {
        public static readonly string _tableName = "TryDaxTable";

        public static async void AddItem(AmazonDynamoDBClient client)
        {
            string someData = new String('X', 1000);
            var pkmax = 10;
            var skmax = 10;

            for (var ipk = 1; ipk <= pkmax; ipk++)
            {
                Console.WriteLine("Writing " + skmax + " items for partition key: " + ipk);
                for (var isk = 1; isk <= skmax; isk++)
                {
                    var request = new PutItemRequest()
                    {
                        TableName = _tableName,
                        Item = new Dictionary<string, AttributeValue>()
                        {
                            { "pk", new AttributeValue{N = ipk.ToString() } },
                            { "sk", new AttributeValue{N = isk.ToString() } },
                            { "someData", new AttributeValue{S = someData } }
                        }
                    };

                    await client.PutItemAsync(request);
                }
            }
        }

        static void Main()
        {
            var client = new AmazonDynamoDBClient();

            AddItem(client);
        }
    }
}
// snippet-end:[dynamodb.dotnet35.TryDaxWriteData]