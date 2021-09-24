using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using Amazon.Lambda.Core;
using Amazon.Lambda.RuntimeSupport;
using Amazon.Lambda.Serialization.SystemTextJson;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

// This project specifies the serializer used to convert a Lambda event into
// .NET classes in the project's main function. This assembly register a
// serializer for use when the project is being debugged using the
// AWS .NET Mock Lambda Test Tool.
[assembly: LambdaSerializer(typeof(Amazon.Lambda.Serialization.SystemTextJson.DefaultLambdaJsonSerializer))]

namespace CreateDynamoDBTableExample
{
    public class Function
    {
        private static async Task Main()
        {
            Func<string, ILambdaContext, Task> func = FunctionHandler;
            using(var handlerWrapper = HandlerWrapper.GetHandlerWrapper(func, new DefaultLambdaJsonSerializer()))
            using(var bootstrap = new LambdaBootstrap(handlerWrapper))
            {
                await bootstrap.RunAsync();
            }
        }

        /// <summary>
        /// Creates an Amazon DynamoDB table. The string input defines the name
        /// of the new table.
        /// </summary>
        /// <param name="input">The name of the table to create.</param>
        public static async Task FunctionHandler(string input, ILambdaContext context)
        {
            if (input is not null)
            {
                IAmazonDynamoDB client = new AmazonDynamoDBClient();
                var tableName = input;

                var response = await MakeTableAsync(client, tableName);

                Console.WriteLine($"Created table: {response.TableDescription.TableName}.");
            }
        }

        /// <summary>
        /// Defines the attributes for the new table and then creates it.
        /// </summary>
        /// <param name="client">An initialized DynamoDB client object.</param>
        /// <param name="tableName">The name of the table to create.</param>
        /// <returns>A CreateTableResponse object representing the results of
        /// of the call to CreateTableAsync.</returns>
        public static async Task<CreateTableResponse> MakeTableAsync(
            IAmazonDynamoDB client,
            string tableName)
        {
            var response = await client.CreateTableAsync(new CreateTableRequest
            {
                TableName = tableName,
                // The AttributeDefinitions in the CreateTableRequest describe
                // the attributes for the table's Index. In this case, the two
                // AttributeDefinition objects below describe two values, the
                // ID, a string, and the Area, also a string.
                AttributeDefinitions = new List<AttributeDefinition>
                {
                    new AttributeDefinition
                    {
                        AttributeName = "ID",
                        AttributeType = "S",
                    },
                    new AttributeDefinition
                    {
                        AttributeName = "Area",
                        AttributeType = "S",
                    },
                },
                // The KeySchema describes how the attributes will used used
                // in the table. In the follow KeySchema, the ID is an Index
                // value, a HASH and compriess the partition key. The Area
                // attribute, a RANGE, is a sort key.
                KeySchema = new List<KeySchemaElement>
                {
                    new KeySchemaElement
                    {
                        AttributeName = "ID",
                        KeyType = "HASH",
                    },
                    new KeySchemaElement
                    {
                        AttributeName = "Area",
                        KeyType = "RANGE",
                    },
                },
                ProvisionedThroughput = new ProvisionedThroughput
                {
                    ReadCapacityUnits = 10,
                    WriteCapacityUnits = 5,
                },
            });

            return response;
        }
    }
}
