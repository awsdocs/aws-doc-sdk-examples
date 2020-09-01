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
    public class CreateTableTest
    {
        string tableName = "testtable";

        private IAmazonDynamoDB CreateMockDynamoDBClient()
        {
            var mockDynamoDBClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDBClient.Setup(client => client.CreateTableAsync(
                It.IsAny<CreateTableRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<CreateTableRequest, CancellationToken>((request, token) =>
                {
                    if (!string.IsNullOrEmpty(tableName))
                    {
                        Assert.AreEqual(tableName, request.TableName);
                    }
                })
                .Returns((CreateTableRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new CreateTableResponse { HttpStatusCode = HttpStatusCode.OK });
                });

            return mockDynamoDBClient.Object;
        }

        [TestMethod]
        public async Task CheckCreateTable()
        {
            IAmazonDynamoDB client = CreateMockDynamoDBClient();

            var result = await CreateTable.MakeTableAsync(client, tableName);

            if (result.HttpStatusCode == HttpStatusCode.OK)
            {
                Logger.LogMessage("Created table " + tableName);
            }
            else
            {
                Logger.LogMessage("Could NOT create table " + tableName);
            }
        }
    }
}