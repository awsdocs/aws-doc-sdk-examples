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
   
        [Fact]
        public async Task CheckUpdateItemDataModel()
        {
            var clientConfig = new AmazonDynamoDBConfig();
            clientConfig.ServiceURL = _endpointURL;            
            var client = new AmazonDynamoDBClient(clientConfig);

            output.WriteLine("Created client with endpoint URL: " + _endpointURL);

            IDynamoDBContext context = CreateMockDynamoDBContext(client);

            // Create the table.
            var makeTableResult = CreateTable.MakeTableAsync(client, _tableName);
            output.WriteLine("Created table " + makeTableResult.Result.TableDescription.TableName);
            
            // Add an item to the table.
            var result = AddItem.AddItemAsync(client, _tableName, _id, _keys, _values);

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
            var removeResult = DeleteTable.RemoveTableAsync(client, _tableName);

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
