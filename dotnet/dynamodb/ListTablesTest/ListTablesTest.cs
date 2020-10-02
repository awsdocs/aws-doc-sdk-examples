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

namespace DynamoDBCRUD 
{
    public class ListTablesTest
    {
        private readonly ITestOutputHelper output;

        public ListTablesTest(ITestOutputHelper output)
        {
            this.output = output;
        }

        private IAmazonDynamoDB CreateMockDynamoDBClient()
        {
            var mockDynamoDBClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDBClient.Setup(client => client.ListTablesAsync(
                It.IsAny<ListTablesRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<ListTablesRequest, CancellationToken>((request, token) =>
                {})
                .Returns((ListTablesRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new ListTablesResponse { HttpStatusCode = HttpStatusCode.OK });
                });

            return mockDynamoDBClient.Object;
        }

        [Fact]
        public async Task CheckListTables()
        {
            IAmazonDynamoDB client = CreateMockDynamoDBClient();

            var result = await ListTables.ShowTablesAsync(client);

            bool gotResult = result != null;
            Assert.True(gotResult, "Could NOT get result");

            bool ok = result.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, "Could NOT get tables ");

            output.WriteLine("Got tables");
        }
    }
}
