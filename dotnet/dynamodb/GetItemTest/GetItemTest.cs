// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: MIT-0
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

using Microsoft.VisualStudio.TestTools.UnitTesting;
using Microsoft.VisualStudio.TestTools.UnitTesting.Logging;

using Moq;

using System.Net;
using System.Threading;
using System.Threading.Tasks;

namespace DynamoDBCRUD
{
    [TestClass]
    public class GetItemTest
    {
        readonly string table = "testtable";

        private IAmazonDynamoDB CreateMockDynamoDBClient()
        {
            var mockDynamoDBClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDBClient.Setup(client => client.QueryAsync(
                It.IsAny<QueryRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<QueryRequest, CancellationToken>((request, token) =>
                {
                    if (string.IsNullOrEmpty(table))
                    {
                        throw new System.ArgumentNullException("You must supply a table value");
                    }
                })
                .Returns((QueryRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new QueryResponse { HttpStatusCode = HttpStatusCode.OK });
                });

            return mockDynamoDBClient.Object;
        }

        [TestMethod]
        public async Task CheckGetItem()
        {
            IAmazonDynamoDB client = CreateMockDynamoDBClient();

            Logger.LogMessage("Calling AddItem.AddItemAsync(client, tableName, keys, values)");

            // Create random ID value
            var _random = new System.Random();
            int id = _random.Next(100, 1000);

            var result = await GetItem.GetItemAsync(client, table, id.ToString());

            if (result.HttpStatusCode == HttpStatusCode.OK)
            {
                Logger.LogMessage("Retrieved item from table " + table);
            }
            else
            {
                Logger.LogMessage("Could NOT retrieve item from table " + table);
            }
        }
    }
}
