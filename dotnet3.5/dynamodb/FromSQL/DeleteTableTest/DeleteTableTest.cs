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

namespace DeleteTableTest
{        
    public class DeleteTableTest
    {
        private readonly ITestOutputHelper _output;

        public DeleteTableTest(ITestOutputHelper output)
        {
            this._output = output;
        }

        readonly string _tableName = "testtable";

        private IAmazonDynamoDB CreateMockDynamoDbClient()
        {
            var mockDynamoDbClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDbClient.Setup(client => client.DeleteTableAsync(
                It.IsAny<DeleteTableRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<DeleteTableRequest, CancellationToken>((request, token) =>
                {})
                .Returns((DeleteTableRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new DeleteTableResponse { HttpStatusCode = HttpStatusCode.OK });
                });

            return mockDynamoDbClient.Object;
        }

        [Fact]
        public async Task CheckDeleteTable()
        {
            IAmazonDynamoDB client = CreateMockDynamoDbClient();

            var result = await DeleteTable.DeleteTable.RemoveTableAsync(client, _tableName);
	    
            bool gotResult = result != null;
            Assert.True(gotResult, "Could NOT get result");

            bool ok = result.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, "Could NOT delete table " + _tableName);

            _output.WriteLine("Deleted table");
        }
    }
}
