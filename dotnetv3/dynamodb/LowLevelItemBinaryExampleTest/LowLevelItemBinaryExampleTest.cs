using System;
using System.Net;
using System.Net.NetworkInformation;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;
using Xunit;
using Xunit.Abstractions;

namespace LowLevelItemBinaryExampleTest
{
    public class LowLevelItemBinaryExampleTest
    {
        private readonly ITestOutputHelper _output;

        public LowLevelItemBinaryExampleTest(ITestOutputHelper output)
        {
            this._output = output;
        }

        private static string _ip = "localhost";
        private static int _port = 8000;
        private readonly string _endpointUrl = "http://" + _ip + ":" + _port;

        private IDynamoDBContext CreateMockDynamoDbContext(AmazonDynamoDBClient client)
        {
            var mockDynamoDbContext = new DynamoDBContext(client);

            return mockDynamoDbContext;
        }

        private bool IsPortInUse()
        {
            bool isAvailable = true;

            // Evaluate current system TCP connections. This is the same information provided
            // by the netstat command line application, just in .Net strongly-typed object
            // form.  We will look through the list, and if our port we would like to use
            // in our TcpClient is occupied, we will set isAvailable to false.
            IPGlobalProperties ipGlobalProperties = IPGlobalProperties.GetIPGlobalProperties();
            IPEndPoint[] tcpConnInfoArray = ipGlobalProperties.GetActiveTcpListeners();

            foreach (IPEndPoint endpoint in tcpConnInfoArray)
            {
                if (endpoint.Port == _port)
                {
                    isAvailable = false;
                    break;
                }
            }

            return isAvailable;
        }

        [Fact]
        public async void CheckLowLevelItemBinaryExample()
        {
            var portUsed = IsPortInUse();
            if (portUsed)
            {
                throw new Exception("You must run local DynamoDB on port " + _port);
            }

            var clientConfig = new AmazonDynamoDBConfig();
            clientConfig.ServiceURL = _endpointUrl;
            var client = new AmazonDynamoDBClient(clientConfig);

            // Reply table primary key.
            string replyIdPartitionKey = "Amazon DynamoDB#DynamoDB Thread 1";
            string replyDateTimeSortKey = Convert.ToString(DateTime.UtcNow);

            // Create Reply table.
            _output.WriteLine("Creating Reply table");
            await CreateTablesLoadData.CreateTablesLoadData.CreateTableReply(client);

            _output.WriteLine("Creating item");
            await LowLevelItemBinaryExample.LowLevelItemBinaryExample.CreateItem(client, replyIdPartitionKey, replyDateTimeSortKey);

            _output.WriteLine("retrieving item");
            LowLevelItemBinaryExample.LowLevelItemBinaryExample.RetrieveItem(client, replyIdPartitionKey, replyDateTimeSortKey);

            _output.WriteLine("Deleting item");
            LowLevelItemBinaryExample.LowLevelItemBinaryExample.DeleteItem(client, replyIdPartitionKey, replyDateTimeSortKey);

            // Delete Reply table.
            _output.WriteLine("Deleting Reply table");
            await CreateTablesLoadData.CreateTablesLoadData.DeleteTable(client, "Reply");
        }
    }
}
