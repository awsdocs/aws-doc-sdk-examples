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
    public class GetOrdersForProductTest
    {
        private readonly ITestOutputHelper output;

        public GetOrdersForProductTest(ITestOutputHelper output)
        {
            this.output = output;
        }

        readonly string _tableName = "testtable";
        readonly string _productId = "3";

        private IAmazonDynamoDB CreateMockDynamoDBClient()
        {
            var mockDynamoDBClient = new Mock<IAmazonDynamoDB>();

            mockDynamoDBClient.Setup(client => client.ScanAsync(
                It.IsAny<ScanRequest>(),
                It.IsAny<CancellationToken>()))
                .Callback<ScanRequest, CancellationToken>((request, token) =>
                {
                    if (!string.IsNullOrEmpty(_tableName))
                    {
                        bool areEqual = _tableName == request.TableName;
                        Assert.True(areEqual, "The provided table name is not the one used to access the table");
                    }
                })
                .Returns((ScanRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new ScanResponse { HttpStatusCode = HttpStatusCode.OK });
                });

            return mockDynamoDBClient.Object;
        }

        [Fact]
        public async Task Test1()
        {
            IAmazonDynamoDB client = CreateMockDynamoDBClient();

            var result = await GetOrdersForProduct.GetProductOrdersAsync(client, _tableName, _productId);

            bool gotResult = result != null;
            Assert.True(gotResult, "Could NOT get results from scanning table " + _tableName);

            bool ok = result.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, "Could NOT get results from scanning table " + _tableName);

            output.WriteLine("Got results from table");
        }
    }
}
