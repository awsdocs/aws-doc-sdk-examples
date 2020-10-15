using System;
using System.Net;
using System.Net.NetworkInformation;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;
using Xunit;
using Xunit.Abstractions;

namespace LowLevelGlobalSecondaryIndexExampleTest
{
    public class LowLevelGlobalSecondaryIndexExampleTest
    {
        private readonly ITestOutputHelper _output;

        public LowLevelGlobalSecondaryIndexExampleTest(ITestOutputHelper output)
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

            // Evaluate current system tcp connections. This is the same information provided
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
        public async void CheckLowLevelGlobalSecondaryIndexExampleTest()
        {
            var portUsed = IsPortInUse();
            if (portUsed)
            {
                throw new Exception("You must run local DynamoDB on port " + _port);
            }

            var clientConfig = new AmazonDynamoDBConfig();
            clientConfig.ServiceURL = _endpointUrl;
            var client = new AmazonDynamoDBClient(clientConfig);

            _output.WriteLine("Creating table");
            var result = await LowLevelGlobalSecondaryIndexExample.LowLevelGlobalSecondaryIndexExample.CreateTable(client);

            if (!result)
            {
                Console.WriteLine("Could not create due date index");
                return;
            }

            _output.WriteLine("Loading data into table");
            result = await LowLevelGlobalSecondaryIndexExample.LowLevelGlobalSecondaryIndexExample.LoadData(client);

            if (!result)
            {
                Console.WriteLine("Could not load data");
                return;
            }

            _output.WriteLine("Creating date index");
            result = await LowLevelGlobalSecondaryIndexExample.LowLevelGlobalSecondaryIndexExample.QueryIndex(client, "CreateDateIndex");

            if (!result)
            {
                Console.WriteLine("Could not create date index");
                return;
            }
            _output.WriteLine("Creating title index");
            result = await LowLevelGlobalSecondaryIndexExample.LowLevelGlobalSecondaryIndexExample.QueryIndex(client, "TitleIndex");

            if (!result)
            {
                Console.WriteLine("Could not create title index");
                return;
            }
            _output.WriteLine("Creating due date index");
            result = await LowLevelGlobalSecondaryIndexExample.LowLevelGlobalSecondaryIndexExample.QueryIndex(client, "DueDateIndex");

            if (!result)
            {
                Console.WriteLine("Could not create due date index");
                return;
            }

            _output.WriteLine("Deleting table");
            result = await LowLevelGlobalSecondaryIndexExample.LowLevelGlobalSecondaryIndexExample.DeleteTable(client);

            if (!result)
            {
                Console.WriteLine("Could not delete table");
            }
        }
    }
}
