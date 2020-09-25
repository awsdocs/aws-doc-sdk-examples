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

namespace DynamoDBCRUD
{        
    public class CreateTableTest
    {
        readonly string _tableName = "testtable";

        private IAmazonDynamoDB CreateMockDynamoDBClient()
        {
            var mockDynamoDBClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDBClient.Setup(client => client.CreateTableAsync(
                It.IsAny<CreateTableRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<CreateTableRequest, CancellationToken>((request, token) =>
                {
                    if (!string.IsNullOrEmpty(_tableName))
                    {
                        bool areEqual = _tableName == request.TableName;
                        Assert.True(areEqual, "The provided table name is not the one used to create the table");
                    }
                })
                .Returns((CreateTableRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new CreateTableResponse { HttpStatusCode = HttpStatusCode.OK });
                });

            return mockDynamoDBClient.Object;
        }

        [Fact]
        public async Task CheckCreateTable()
        {
            IAmazonDynamoDB client = CreateMockDynamoDBClient();

            var result = await CreateTable.MakeTableAsync(client, _tableName);

            bool ok = result.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, "Could NOT create table " + _tableName);
        }
    }
}