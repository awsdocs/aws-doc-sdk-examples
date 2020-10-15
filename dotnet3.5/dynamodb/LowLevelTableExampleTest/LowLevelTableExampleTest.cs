using System;
using System.Net;
using System.Net.NetworkInformation;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;
using Xunit;
using Xunit.Abstractions;

namespace LowLevelTableExampleTest
{
    public class LowLevelTableExampleTest
    {
        private readonly ITestOutputHelper _output;

        public LowLevelTableExampleTest(ITestOutputHelper output)
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
        public void CheckLowLevelTableExample()
        {
            var portUsed = IsPortInUse();
            if (portUsed)
            {
                throw new Exception("You must run local DynamoDB on port " + _port);
            }

            var clientConfig = new AmazonDynamoDBConfig();
            clientConfig.ServiceURL = _endpointUrl;
            var client = new AmazonDynamoDBClient(clientConfig);

            _output.WriteLine("Creating example table.");
            var result = LowLevelTableExample.LowLevelTableExample.CreateExampleTable(client);

            if (!result.Result)
            {
                _output.WriteLine("Could not create example table.");
                return;
            }

            _output.WriteLine("Listing tables.");
            result = LowLevelTableExample.LowLevelTableExample.ListMyTables(client);

            if (!result.Result)
            {
                _output.WriteLine("Could not create example table.");
                return;
            }

            _output.WriteLine("Getting example table information.");
            result = LowLevelTableExample.LowLevelTableExample.GetTableInformation(client);

            if (!result.Result)
            {
                _output.WriteLine("Could not get example table information.");
                return;
            }

            _output.WriteLine("Updating example table.");
            result = LowLevelTableExample.LowLevelTableExample.UpdateExampleTable(client);

            if (!result.Result)
            {
                _output.WriteLine("Could not update example table");
                return;
            }

            _output.WriteLine("Deleting example table.");
            result = LowLevelTableExample.LowLevelTableExample.DeleteExampleTable(client);

            if (!result.Result)
            {
                _output.WriteLine("Could not delete example table");
                return;
            }

            _output.WriteLine("Done");




        }
    }
}
