using System;
using System.Net;
using System.Net.NetworkInformation;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;

using Xunit;
using Xunit.Abstractions;

namespace DynamoDBCRUD
{
    public class LowLevelItemBinaryExampleTest
    {
        private readonly ITestOutputHelper output;

        public LowLevelItemBinaryExampleTest(ITestOutputHelper output)
        {
            this.output = output;
        }

        private static string ip = "localhost";
        private static int port = 8000;
        private readonly string _endpointURL = "http://" + ip + ":" + port.ToString();

        private IDynamoDBContext CreateMockDynamoDBContext(AmazonDynamoDBClient client)
        {
            var mockDynamoDBContext = new DynamoDBContext(client);

            return mockDynamoDBContext;
        }

        private bool IsPortInUse(int port)
        {
            bool isAvailable = true;

            // Evaluate current system tcp connections. This is the same information provided
            // by the netstat command line application, just in .Net strongly-typed object
            // form.  We will look through the list, and if our port we would like to use
            // in our TcpClient is occupied, we will set isAvailable to false.
            IPGlobalProperties ipGlobalProperties = IPGlobalProperties.GetIPGlobalProperties();
            IPEndPoint[] tcpConnInfoArray = ipGlobalProperties.GetActiveTcpListeners();

            foreach (IPEndPoint endpoint in tcpConnInfoArray)
            {
                if (endpoint.Port == port)
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
            var portUsed = IsPortInUse(port);
            if (portUsed)
            {
                throw new Exception("You must run local DynamoDB on port 8000");
            }

            var clientConfig = new AmazonDynamoDBConfig();
            clientConfig.ServiceURL = _endpointURL;
            var client = new AmazonDynamoDBClient(clientConfig);

            // Reply table primary key.
            string replyIdPartitionKey = "Amazon DynamoDB#DynamoDB Thread 1";
            string replyDateTimeSortKey = Convert.ToString(DateTime.UtcNow);

            // Create Reply table.
            output.WriteLine("Creating Reply table");
            await CreateTablesLoadData.CreateTableReply(client);

            output.WriteLine("Creating item");
            await LowLevelItemBinaryExample.CreateItem(client, replyIdPartitionKey, replyDateTimeSortKey);

            output.WriteLine("retrieving item");
            LowLevelItemBinaryExample.RetrieveItem(client, replyIdPartitionKey, replyDateTimeSortKey);

            output.WriteLine("Deleting item");
            LowLevelItemBinaryExample.DeleteItem(client, replyIdPartitionKey, replyDateTimeSortKey);

            // Delete Reply table.
            output.WriteLine("Deleting Reply table");
            await CreateTablesLoadData.DeleteTable(client, "Reply");
        }
    }
}
