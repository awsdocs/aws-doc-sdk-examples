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
    public class GetOrdersInDateRangeGSITest
    {
        private readonly ITestOutputHelper output;

        public GetOrdersInDateRangeGSITest(ITestOutputHelper output)
        {
            this.output = output;
        }

        readonly string _tableName = "testtable";
        readonly string _index = "OrderDate";
        readonly string _start = "2020-05-04 05:00:00";
        readonly string _end = "2020-08-13 09:00:00";

        private IAmazonDynamoDB CreateMockDynamoDBClient()
        {
            var mockDynamoDBClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDBClient.Setup(client => client.QueryAsync(
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

            return mockDynamoDBClient.Object;
        }

        [Fact]
        public async Task CheckGetOrdersInDateRangeGSI()
        {
            IAmazonDynamoDB client = CreateMockDynamoDBClient();

            var result = await GetOrdersInDateRangeGSI.GetOrdersInDateRangeAsync(client, _tableName, _index, _start, _end);

            bool gotResult = result != null;
            Assert.True(gotResult, "Could NOT get results from scanning table");

            bool ok = result.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, "Could NOT get items from scanning table");

            output.WriteLine("Got items from table");
        }
    }
}
