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
    public class AddItemTest
    {
        private readonly ITestOutputHelper output;

        public AddItemTest(ITestOutputHelper output)
        {
            this.output = output;
        }

        readonly string _tableName = "testtable";
        readonly string _id = "3";
        readonly string _keys = "Area,Order_ID,Order_Customer,Order_Product,Order_Date,Order_Status";
        readonly string _values = "Order,1,1,6,2020-07-04 12:00:00,pending";

        private IAmazonDynamoDB CreateMockDynamoDBClient()
        {
            var mockDynamoDBClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDBClient.Setup(client => client.PutItemAsync(
                It.IsAny<PutItemRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<PutItemRequest, CancellationToken>((request, token) =>
                {})
                .Returns((PutItemRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new PutItemResponse { HttpStatusCode = HttpStatusCode.OK  });
                });

            return mockDynamoDBClient.Object;
        }

        [Fact]
        public async Task CheckAddItem()
        {
            IAmazonDynamoDB client = CreateMockDynamoDBClient();

            var result = await AddItem.AddItemAsync(client, _tableName, _id, _keys, _values);
                                    
            Assert.True(result, "Could NOT delete items");

            output.WriteLine("Deleted items");
        }
    }
}
