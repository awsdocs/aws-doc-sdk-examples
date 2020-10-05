// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX - License - Identifier: Apache - 2.0
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
    public class UpdateItemTest
    {
        private readonly ITestOutputHelper output;

        public UpdateItemTest(ITestOutputHelper output)
        {
            this.output = output;
        }
    
        readonly string _tableName = "testtable";
        readonly string _id = "3";
        readonly string _status = "pending";

        private IAmazonDynamoDB CreateMockDynamoDBClient()
        {
            var mockDynamoDBClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDBClient.Setup(client => client.UpdateItemAsync(
                It.IsAny<UpdateItemRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<UpdateItemRequest, CancellationToken>((request, token) =>
                {})
                .Returns((UpdateItemRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new UpdateItemResponse { HttpStatusCode = HttpStatusCode.OK  });
                });

            return mockDynamoDBClient.Object;
        }

        [Fact]
        public async Task CheckUpdateItem()
        {
            IAmazonDynamoDB client = CreateMockDynamoDBClient();
            
            var result = await UpdateItem.ModifyOrderStatusAsync(client, _tableName, _id, _status);

            bool gotResult = result != null;
            Assert.True(gotResult, "Could NOT get result");

            bool ok = result.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, "Could NOT update item");

            output.WriteLine("Updated item");
        }
    }
}
