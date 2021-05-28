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

namespace DeleteItemTest 
{
    public class DeleteItemTest
    {
        private readonly ITestOutputHelper _output;

        public DeleteItemTest(ITestOutputHelper output)
        {
            this._output = output;
        }

        readonly string _tableName = "testtable";
        readonly string _id = "1";
        readonly string _area = "test";

        private IAmazonDynamoDB CreateMockDynamoDbClient()
        {
            var mockDynamoDbClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDbClient.Setup(client => client.DeleteItemAsync(
                It.IsAny<DeleteItemRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<DeleteItemRequest, CancellationToken>((request, token) =>
                {})
                .Returns((DeleteItemRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new DeleteItemResponse { HttpStatusCode = HttpStatusCode.OK });
                });

            return mockDynamoDbClient.Object;
        }

        [Fact]
        public async Task CheckDeleteItem()
        {
            IAmazonDynamoDB client = CreateMockDynamoDbClient();

            var result = await DeleteItem.DeleteItem.RemoveItemAsync(client, _tableName, _id, _area);

            bool gotResult = result != null;
            Assert.True(gotResult, "Could NOT get result");

            bool ok = result.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, "Could NOT delete item");

            _output.WriteLine("Deleted item");
        }
    }
}
