// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: MIT-0
using System;
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
    public class DeleteTableTest
    {
        private readonly ITestOutputHelper output;

        public DeleteTableTest(ITestOutputHelper output)
        {
            this.output = output;
        }

        readonly string _tableName = "testtable";

        private IAmazonDynamoDB CreateMockDynamoDBClient()
        {
            var mockDynamoDBClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDBClient.Setup(client => client.DeleteTableAsync(
                It.IsAny<DeleteTableRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<DeleteTableRequest, CancellationToken>((request, token) =>
                {})
                .Returns((DeleteTableRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new DeleteTableResponse { HttpStatusCode = HttpStatusCode.OK });
                });

            return mockDynamoDBClient.Object;
        }

        [Fact]
        public async Task CheckDeleteTable()
        {
            IAmazonDynamoDB client = CreateMockDynamoDBClient();

            var result = await DeleteTable.RemoveTableAsync(client, _tableName);
	    
            bool gotResult = result != null;
            Assert.True(gotResult, "Could NOT get result");

            bool ok = result.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, "Could NOT delete table " + _tableName);

            output.WriteLine("Deleted table");
        }
    }
}