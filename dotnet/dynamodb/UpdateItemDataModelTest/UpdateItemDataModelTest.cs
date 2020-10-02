// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: MIT-0
using System;
using System.Net;
using System.Threading;
using System.Threading.Tasks;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using Amazon.DynamoDBv2.DataModel;

using Moq;

using Xunit;
using Xunit.Abstractions;
using Amazon.DynamoDBv2.DocumentModel;
using System.Collections.Generic;
using System.Linq;
using Amazon;
using Amazon.Runtime;
using System.Globalization;
using Amazon.Runtime.SharedInterfaces;

namespace DynamoDBCRUD
{
    public class UpdateItemDataModelTest
    {
        private readonly string _endpointURL = "http://localhost:8000";
        private readonly string _tableName = "testtable";
        private static readonly string _id = "16";        
        private readonly string _keys = "ID,Area,Order_ID,Order_Customer,Order_Product,Order_Date,Order_Status";
        private readonly string _values = _id + ",Order,11,5,4,2020-05-11 12:00:00,delivered";
        private readonly string _status = "pending";
        private readonly ITestOutputHelper output;

        public UpdateItemDataModelTest(ITestOutputHelper output)
        {
            this.output = output;
        }

        private IDynamoDBContext CreateMockDynamoDBContext(AmazonDynamoDBClient client)
        {
           
            var mockDynamoDBContext = new DynamoDBContext(client);

            return mockDynamoDBContext;
        }

        public static async Task<ListTablesResponse> ShowTablesAsync(IAmazonDynamoDB client)
        {
            var response = await client.ListTablesAsync(new ListTablesRequest { });

            return response;
        }

        public static async Task<CreateTableResponse> MakeTableAsync(IAmazonDynamoDB client, string table)
        {
            var response = await client.CreateTableAsync(new CreateTableRequest
            {
                TableName = table,
                AttributeDefinitions = new List<AttributeDefinition>
                {
                    new AttributeDefinition
                    {
                        AttributeName = "ID",
                        AttributeType = "S"
                    },
                    new AttributeDefinition
                    {
                        AttributeName = "Area",
                        AttributeType = "S"
                    }
                },
                KeySchema = new List<KeySchemaElement>
                {
                    new KeySchemaElement
                    {
                        AttributeName = "ID",
                        KeyType = "HASH"
                    },
                    new KeySchemaElement
                    {
                        AttributeName = "Area",
                        KeyType = "RANGE"
                    }
                },
                ProvisionedThroughput = new ProvisionedThroughput
                {
                    ReadCapacityUnits = 10,
                    WriteCapacityUnits = 5
                }
            });

            return response;
        }

        public static async Task<bool> AddItemAsync(IAmazonDynamoDB client, string table, string id, string keystring, string valuestring)
        {
            // Get individual keys and values
            string[] keys = keystring.Split(",");
            string[] values = valuestring.Split(",");

            var item = new Dictionary<string, AttributeValue>
            {
                { "ID", new AttributeValue { S = id } }
            };

            for (int i = 0; i < keys.Length; i++)
            {
                if ((keys[i] == "Customer_ID") || (keys[i] == "Order_ID") || (keys[i] == "Order_Customer") || (keys[i] == "Order_Product") || (keys[i] == "Product_ID") || (keys[i] == "Product_Quantity") || (keys[i] == "Product_Cost"))
                {
                    item.Add(keys[i], new AttributeValue { N = values[i] });
                }
                else if (keys[i] == "Order_Date")
                {
                    DateTime MyDateTime = DateTime.ParseExact(values[i], "yyyy-MM-dd HH:mm:ss", CultureInfo.InvariantCulture);

                    TimeSpan timeSpan = MyDateTime - new DateTime(1970, 1, 1, 0, 0, 0);

                    item.Add(keys[i], new AttributeValue { N = ((long)timeSpan.TotalSeconds).ToString() });
                }
                else
                {
                    item.Add(keys[i], new AttributeValue { S = values[i] });
                }
            }

            PutItemRequest request = new PutItemRequest
            {
                TableName = table,
                Item = item
            };

            var response = false;

            try
            {
                await client.PutItemAsync(request);
                response = true;
            }
            catch (Exception e)
            {
                Console.WriteLine("Caught exception adding item to table:");
                Console.WriteLine(e.Message);
            }

            return response;

        }

        public static async Task<DeleteTableResponse> RemoveTableAsync(IAmazonDynamoDB client, string table)
        {
            var response = await client.DeleteTableAsync(new DeleteTableRequest
            {
                TableName = table
            });

            return response;
        }

        [Fact]
        public async Task CheckUpdateItemDataModel()
        {
            output.WriteLine("Creating client with endpoint URL: " + _endpointURL);

            var clientConfig = new AmazonDynamoDBConfig();
            clientConfig.ServiceURL = _endpointURL;
            clientConfig.Timeout = TimeSpan.FromSeconds(10); // Timeout after 10 seconds
            var client = new AmazonDynamoDBClient(clientConfig);
            IDynamoDBContext context = CreateMockDynamoDBContext(client);

            // Get list of existing tables to make sure the local DynamoDB service is running
            try
            {
                output.WriteLine("Getting list of tables");

                Task<ListTablesResponse> response = ShowTablesAsync(client);
                output.WriteLine("Found " + response.Result.TableNames.Count.ToString() + " tables");
            }
            catch (Exception e)
            {
                output.WriteLine("Could not get list of tables. Make sure the local DynamoDB is running");
                output.WriteLine("Got exception:");
                output.WriteLine(e.Message);
                return;
            }           

            // Create the table
            var makeTableResult = MakeTableAsync(client, _tableName);
            output.WriteLine("Created table " + makeTableResult.Result.TableDescription.TableName);

            // Add an item that matches the update
            var addResult = AddItemAsync(client, _tableName, _id, _keys, _values);

            if (addResult.Result)
            {
                output.WriteLine("Added item to " + _tableName);
            }
            else
            {
                output.WriteLine("Did not add item to " + _tableName);
            }

            // Update the item            
            var updateResult = await UpdateItemDataModel.UpdateTableItemAsync(context, _id, _status);

            // Make sure it was updated correctly
            bool gotResult = updateResult != null;
            Assert.True(gotResult, "Could NOT get result");

            bool ok = (updateResult.ID == _id) && (updateResult.Order_Status == _status);
            Assert.True(ok, "Could NOT update item");

            output.WriteLine("Updated item");

            // Delete the table
            var removeResult = RemoveTableAsync(client, _tableName);

            if (removeResult.Result.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                output.WriteLine("Removed table " + _tableName);
            }
            else
            {
                output.WriteLine("Could not remove table " + _tableName);
            }
        }
    }
}
