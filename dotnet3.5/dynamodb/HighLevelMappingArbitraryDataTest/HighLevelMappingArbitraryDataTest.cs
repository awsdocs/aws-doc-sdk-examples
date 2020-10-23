// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

using System;
using System.Net;
using System.Net.NetworkInformation;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;
using Xunit;
using Xunit.Abstractions;

namespace HighLevelMappingArbitraryDataTest
{
    public class HighLevelMappingArbitraryDataTest
    {
        private readonly ITestOutputHelper _output;

        public HighLevelMappingArbitraryDataTest(ITestOutputHelper output)
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
        public async void CheckHighLevelMappingArbitraryData()
        {
            var portUsed = IsPortInUse();
            if (portUsed)
            {
                throw new Exception("You must run local DynamoDB on port " + _port);
            }

            var clientConfig = new AmazonDynamoDBConfig();
            clientConfig.ServiceURL = _endpointUrl;
            var client = new AmazonDynamoDBClient(clientConfig);
            var context = CreateMockDynamoDbContext(client);

            _output.WriteLine("Creating ProductCatalog table");
            await CreateTablesLoadData.CreateTablesLoadData.CreateTableProductCatalog(client);

            _output.WriteLine("Adding, retrieving, and updating a book in the ProductCatalog table");
            HighLevelMappingArbitraryData.HighLevelMappingArbitraryData.AddRetrieveUpdateBook(context);

            _output.WriteLine("Deleting ProductCatalog table");
            await CreateTablesLoadData.CreateTablesLoadData.DeleteTable(client, "ProductCatalog");

            _output.WriteLine("Done");
        }
    }
}
