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
    public class CreateIndexTest
    {
        private readonly ITestOutputHelper output;

        public CreateIndexTest(ITestOutputHelper output)
        {
            this.output = output;
        }

        readonly string _tableName = "testtable";
        readonly string _indexName = "Test";
        readonly string _partitionKey = "Key";
        readonly string _partitionKeyType = "string";
        readonly string _sortKey = "Date";
        readonly string _sortKeyType = "number";

        private IAmazonDynamoDB CreateMockDynamoDBClient()
        {
            var mockDynamoDBClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDBClient.Setup(client => client.UpdateTableAsync(
                It.IsAny<UpdateTableRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<UpdateTableRequest, CancellationToken>((request, token) =>
                {})
                .Returns((UpdateTableRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new UpdateTableResponse { HttpStatusCode = HttpStatusCode.OK });
                });

            return mockDynamoDBClient.Object;
        }

        [Fact]
        public async Task CheckCreateIndex()
        {
            IAmazonDynamoDB client = CreateMockDynamoDBClient();

            var result = await CreateIndex.AddIndexAsync(client, _tableName, _indexName, _partitionKey, _partitionKeyType, _sortKey, _sortKeyType);

            bool gotResult = result != null;
            Assert.True(gotResult, "Could NOT get result");

            bool ok = result.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, "Could NOT create index");

            output.WriteLine("Created index");
        }
    }
}
