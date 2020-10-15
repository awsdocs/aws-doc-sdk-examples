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

namespace ListTablesTest 
{
    public class ListTablesTest
    {
        private readonly ITestOutputHelper _output;

        public ListTablesTest(ITestOutputHelper output)
        {
            this._output = output;
        }

        private IAmazonDynamoDB CreateMockDynamoDbClient()
        {
            var mockDynamoDbClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDbClient.Setup(client => client.ListTablesAsync(
                It.IsAny<ListTablesRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<ListTablesRequest, CancellationToken>((request, token) =>
                {})
                .Returns((ListTablesRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new ListTablesResponse { HttpStatusCode = HttpStatusCode.OK });
                });

            return mockDynamoDbClient.Object;
        }

        [Fact]
        public async Task CheckListTables()
        {
            IAmazonDynamoDB client = CreateMockDynamoDbClient();

            var result = await ListTables.ListTables.ShowTablesAsync(client);

            bool gotResult = result != null;
            Assert.True(gotResult, "Could NOT get result");

            bool ok = result.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, "Could NOT get tables ");

            _output.WriteLine("Got tables");
        }
    }
}
