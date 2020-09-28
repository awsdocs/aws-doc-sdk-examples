// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: MIT-0
using System.Net;
using System.Threading;
using System.Threading.Tasks;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

using Moq;

using Xunit;
using Xunit.Abstractions;

namespace DynamoDBCRUD
{
    public class AddItemsTest
    {
        private readonly ITestOutputHelper output;

        public AddItemsTest(ITestOutputHelper output)
        {
            this.output = output;
        }

        readonly string _tableName = "testtable";
	    readonly int _id = 3;
        readonly string _keys = "Area,Order_ID,Order_Customer,Order_Product,Order_Date,Order_Status";
        readonly string _values = "Order,1,1,6,2020-07-04 12:00:00,pending";

        private IAmazonDynamoDB CreateMockDynamoDBClient()
        {
            var mockDynamoDBClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDBClient.Setup(client => client.BatchWriteItemAsync(
                It.IsAny<BatchWriteItemRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<BatchWriteItemRequest, CancellationToken>((request, token) =>
                {})
                .Returns((BatchWriteItemRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new BatchWriteItemResponse { HttpStatusCode = HttpStatusCode.OK });
                });

            return mockDynamoDBClient.Object;
        }

        [Fact]
        public async Task CheckAddItems()
        {
            IAmazonDynamoDB client = CreateMockDynamoDBClient();

            var inputs = new string[2];
            inputs[0] = _keys;
            inputs[1] = _values;

            var result = await AddItems.AddItemsAsync(false, client, _tableName, inputs, _id);

            bool gotResult = result != null;
            Assert.True(gotResult, "Could NOT get result");

            bool ok = result.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, "Could NOT add items");

            output.WriteLine("Added items");
        }
    }
}
