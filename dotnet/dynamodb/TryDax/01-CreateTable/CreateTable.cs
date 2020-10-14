// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.TryDaxCreateTable]
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace DynamoDBCRUD
{
    public class TryDaxCreateTable
    {
        private static readonly string _tableName = "TryDaxTable";

        public static async Task<CreateTableResponse> CreateDaxTable(AmazonDynamoDBClient client)
        {
            var request = new CreateTableRequest()
            {
                TableName = _tableName,
                KeySchema = new List<KeySchemaElement>()
                {
                    new KeySchemaElement{ AttributeName = "pk",KeyType = "HASH"},
                    new KeySchemaElement{ AttributeName = "sk",KeyType = "RANGE"}
                },
                AttributeDefinitions = new List<AttributeDefinition>() {
                    new AttributeDefinition{ AttributeName = "pk",AttributeType = "N"},
                    new AttributeDefinition{ AttributeName = "sk",AttributeType  = "N"}
                },
                ProvisionedThroughput = new ProvisionedThroughput()
                {
                    ReadCapacityUnits = 10,
                    WriteCapacityUnits = 10
                }
            };

            var response = await client.CreateTableAsync(request);

            return response;
        }

        static void Main(string[] args)
        {
            var client = new AmazonDynamoDBClient();

            var result = CreateDaxTable(client);

            Console.WriteLine("Creating Dax table returned HTTP status code: " + result.Result.HttpStatusCode);
        }
    }
}
// snippet-end:[dynamodb.dotnet35.TryDaxCreateTable]