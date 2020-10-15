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

namespace CreateIndexTest 
{
    public class CreateIndexTest
    {
        private readonly ITestOutputHelper _output;

        public CreateIndexTest(ITestOutputHelper output)
        {
            this._output = output;
        }

        readonly string _tableName = "testtable";
        readonly string _indexName = "Test";
        readonly string _partitionKey = "Key";
        readonly string _partitionKeyType = "string";
        readonly string _sortKey = "Date";
        readonly string _sortKeyType = "number";

        private IAmazonDynamoDB CreateMockDynamoDbClient()
        {
            var mockDynamoDbClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDbClient.Setup(client => client.UpdateTableAsync(
                It.IsAny<UpdateTableRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<UpdateTableRequest, CancellationToken>((request, token) =>
                {})
                .Returns((UpdateTableRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new UpdateTableResponse { HttpStatusCode = HttpStatusCode.OK });
                });

            return mockDynamoDbClient.Object;
        }

        [Fact]
        public async Task CheckCreateIndex()
        {
            IAmazonDynamoDB client = CreateMockDynamoDbClient();

            var result = await CreateIndex.CreateIndex.AddIndexAsync(client, _tableName, _indexName, _partitionKey, _partitionKeyType, _sortKey, _sortKeyType);

            bool gotResult = result != null;
            Assert.True(gotResult, "Could NOT get result");

            bool ok = result.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, "Could NOT create index");

            _output.WriteLine("Created index");
        }
    }
}
