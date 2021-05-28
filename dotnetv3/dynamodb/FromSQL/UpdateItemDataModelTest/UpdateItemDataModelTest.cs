// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX - License - Identifier: Apache - 2.0
using System.Threading.Tasks;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;

using Xunit;
using Xunit.Abstractions;

namespace UpdateItemDataModelTest
{
    public class UpdateItemDataModelTest
    {
        private readonly string _endpointUrl = "http://localhost:8000";
        private readonly string _tableName = "CustomersOrdersProducts";
        private static readonly string Id = "16";
        private readonly string _keys = "Area,Order_ID,Order_Customer,Order_Product,Order_Date,Order_Status";
        private readonly string _values = "Order,11,5,4,2020-05-11 12:00:00,delivered";
        private readonly string _status = "pending";
        private readonly ITestOutputHelper _output;

        public UpdateItemDataModelTest(ITestOutputHelper output)
        {
            this._output = output;
        }

        private IDynamoDBContext CreateMockDynamoDbContext(AmazonDynamoDBClient client)
        {
           
            var mockDynamoDbContext = new DynamoDBContext(client);

            return mockDynamoDbContext;
        }
   
        [Fact]
        public async Task CheckUpdateItemDataModel()
        {
            var clientConfig = new AmazonDynamoDBConfig();
            clientConfig.ServiceURL = _endpointUrl;            
            var client = new AmazonDynamoDBClient(clientConfig);

            _output.WriteLine("Created client with endpoint URL: " + _endpointUrl);

            IDynamoDBContext context = CreateMockDynamoDbContext(client);

            // Create the table.
            var makeTableResult = CreateTable.CreateTable.MakeTableAsync(client, _tableName);
            _output.WriteLine("Created table " + makeTableResult.Result.TableDescription.TableName);
            
            // Add an item to the table.
            var result = AddItem.AddItem.AddItemAsync(client, _tableName, Id, _keys, _values);

            if (result.Result)
            {
                _output.WriteLine("Added item to " + _tableName);
            }
            else
            {
                _output.WriteLine("Did not add item to " + _tableName);
            }

            // Update the item.
            var updateResult = await UpdateItemDataModel.UpdateItemDataModel.UpdateTableItemAsync(context, Id, _status);

            // Make sure it was updated correctly.
            bool gotResult = updateResult != null;
            Assert.True(gotResult, "Could NOT get result");

            bool ok = (updateResult.Id == Id) && (updateResult.OrderStatus == _status);
            Assert.True(ok, "Could NOT update item");

            _output.WriteLine("Updated item");

            // Delete the table.
            var removeResult = DeleteTable.DeleteTable.RemoveTableAsync(client, _tableName);

            if (removeResult.Result.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                _output.WriteLine("Removed table " + _tableName);
            }
            else
            {
                _output.WriteLine("Could not remove table " + _tableName);
            }
        }
    }
}
