// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX - License - Identifier: Apache - 2.0
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Threading.Tasks;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using Amazon.DynamoDBv2.DataModel;

using Xunit;
using Xunit.Abstractions;

namespace DynamoDBCRUD
{
    public class UpdateItemDataModelTest
    {
        private readonly string _endpointURL = "http://localhost:8000";
        private readonly string _tableName = "CustomersOrdersProducts";
        private static readonly string _id = "16";
        private readonly string _keys = "Area,Order_ID,Order_Customer,Order_Product,Order_Date,Order_Status";
        private readonly string _values = "Order,11,5,4,2020-05-11 12:00:00,delivered";
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
            // Get the individual keys and values.
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
            var clientConfig = new AmazonDynamoDBConfig();
            clientConfig.ServiceURL = _endpointURL;            
            var client = new AmazonDynamoDBClient(clientConfig);

            output.WriteLine("Created client with endpoint URL: " + _endpointURL);

            IDynamoDBContext context = CreateMockDynamoDBContext(client);

            // Create the table.
            var makeTableResult = MakeTableAsync(client, _tableName);
            output.WriteLine("Created table " + makeTableResult.Result.TableDescription.TableName);
            
            // Add an item to the table.
            var result = AddItemAsync(client, _tableName, _id, _keys, _values);

            if (result.Result)
            {
                output.WriteLine("Added item to " + _tableName);
            }
            else
            {
                output.WriteLine("Did not add item to " + _tableName);
            }

            // Update the item.
            var updateResult = await UpdateItemDataModel.UpdateTableItemAsync(context, _id, _status);

            // Make sure it was updated correctly.
            bool gotResult = updateResult != null;
            Assert.True(gotResult, "Could NOT get result");

            bool ok = (updateResult.ID == _id) && (updateResult.Order_Status == _status);
            Assert.True(ok, "Could NOT update item");

            output.WriteLine("Updated item");

            // Delete the table.
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
