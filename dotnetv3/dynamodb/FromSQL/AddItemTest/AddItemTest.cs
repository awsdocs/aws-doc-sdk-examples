// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX - License - Identifier: Apache - 2.0

using System.Net;
using System.Threading;
using System.Threading.Tasks;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using Moq;
using Xunit;
using Xunit.Abstractions;

namespace AddItemTest
{
    public class AddItemTest
    {
        private readonly ITestOutputHelper _output;

        public AddItemTest(ITestOutputHelper output)
        {
            this._output = output;
        }

        readonly string _tableName = "testtable";
        readonly string _id = "3";
        readonly string _keys = "Area,Order_ID,Order_Customer,Order_Product,Order_Date,Order_Status";
        readonly string _values = "Order,1,1,6,2020-07-04 12:00:00,pending";

        private IAmazonDynamoDB CreateMockDynamoDbClient()
        {
            var mockDynamoDbClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDbClient.Setup(client => client.PutItemAsync(
                It.IsAny<PutItemRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<PutItemRequest, CancellationToken>((request, token) =>
                {})
                .Returns((PutItemRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new PutItemResponse { HttpStatusCode = HttpStatusCode.OK  });
                });

            return mockDynamoDbClient.Object;
        }

        [Fact]
        public async Task CheckAddItem()
        {
            IAmazonDynamoDB client = CreateMockDynamoDbClient();

            var result = await AddItem.AddItem.AddItemAsync(client, _tableName, _id, _keys, _values);
                                    
            Assert.True(result, "Could NOT delete items");

            _output.WriteLine("Deleted items");
        }
    }
}
