// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
using System;
using System.Net;
using System.Net.NetworkInformation;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;

using Xunit;
using Xunit.Abstractions;

namespace HighLevelItemCRUDTest
{
    public class HighLevelItemCrudTest
    {
        private readonly ITestOutputHelper _output;

        public HighLevelItemCrudTest(ITestOutputHelper output)
        {
            this._output = output;
        }

        private static string _ip = "localhost";
        private static int _port = 8000;
        private static readonly string EndpointUrl = "http://" + _ip + ":" + _port;

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
        public async void CheckHighLevelItemCrud()
        {
            var portUsed = IsPortInUse();
            if (portUsed)
            {
                throw new Exception("You must run local DynamoDB on port " + _port);
            }

            var clientConfig = new AmazonDynamoDBConfig();
            clientConfig.ServiceURL = EndpointUrl;
            var client = new AmazonDynamoDBClient(clientConfig);
            var context = CreateMockDynamoDbContext(client);

            // Create ProductCatalog table
            _output.WriteLine("Creating ProductCatalog table");
            await CreateTablesLoadData.CreateTablesLoadData.CreateTableProductCatalog(client);

            _output.WriteLine("Adding and deleting a book to/from ProductCatalog table");
            HighLevelItemCRUD.HighLevelItemCrud.TestCrudOperations(context);

            // Delete ProductCatalog table
            _output.WriteLine("Deleting ProductCatalog table");
            await CreateTablesLoadData.CreateTablesLoadData.DeleteTable(client, "ProductCatalog");

            _output.WriteLine("Done");
        }
    }
}
