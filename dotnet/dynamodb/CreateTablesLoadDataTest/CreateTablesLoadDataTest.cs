using System;
using System.Collections.Generic;
using System.Globalization;
using System.Net;
using System.Net.NetworkInformation;
using System.Threading.Tasks;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using Amazon.DynamoDBv2.DataModel;

using Xunit;
using Xunit.Abstractions;
using System.Net.Sockets;

namespace DynamoDBCRUD
{
    public class CreateTablesLoadDataTest
    {
        private readonly ITestOutputHelper output;

        public CreateTablesLoadDataTest(ITestOutputHelper output)
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
        public async void CheckCreateTablesLoadData()
        {
            var portUsed = IsPortInUse(port);
            if(portUsed)            
            {
                throw new Exception("You must run local DynamoDB on port 8000");
            }
            
            var clientConfig = new AmazonDynamoDBConfig();
            clientConfig.ServiceURL = _endpointURL;
            var client = new AmazonDynamoDBClient(clientConfig);

            // Create, load, and delete a table
            var createResult = CreateTablesLoadData.CreateTableForum(client);
            output.WriteLine("Waiting for Forum table to be created");

            await CreateTablesLoadData.WaitTillTableCreated(client, "Forum", createResult.Result);
            output.WriteLine("Created Forum table");

            CreateTablesLoadData.LoadSampleForums(client);
            output.WriteLine("Loaded data into Forum table");

            var deleteResult = CreateTablesLoadData.DeleteTable(client, "Forum");
            output.WriteLine("Deleted Forum table");
        }
    }
}
