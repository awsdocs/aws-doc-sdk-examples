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

namespace GetOrdersForProductGSITest
{
    public class GetOrdersForProductGsiTest
    {
        private readonly ITestOutputHelper _output;

        public GetOrdersForProductGsiTest(ITestOutputHelper output)
        {
            this._output = output;
        }

        readonly string _tableName = "testtable";
        readonly string _index = "ProductOrdered";
        readonly string _productId = "3";

        private IAmazonDynamoDB CreateMockDynamoDbClient()
        {
            var mockDynamoDbClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDbClient.Setup(client => client.QueryAsync(
                It.IsAny<QueryRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<QueryRequest, CancellationToken>((request, token) =>
                {
                    if (!string.IsNullOrEmpty(_tableName))
                    {
                        bool areEqual = _tableName == request.TableName;
                        Assert.True(areEqual, "The provided table name is not the one used to access the table");
                    }
                })
                .Returns((QueryRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new QueryResponse { HttpStatusCode = HttpStatusCode.OK });
                });

            return mockDynamoDbClient.Object;
        }

        [Fact]
        public async Task CheckGetOrdersForProductGsi()
        {
            IAmazonDynamoDB client = CreateMockDynamoDbClient();

            var result = await GetOrdersForProductGSI.GetOrdersForProductGsi.GetProductOrdersAsync(client, _tableName, _index, _productId);

            bool gotResult = result != null;
            Assert.True(gotResult, "Could NOT get results from scanning table");

            bool ok = result.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, "Could NOT get items from scanning table");

            _output.WriteLine("Got items from table");
        }
    }
}
