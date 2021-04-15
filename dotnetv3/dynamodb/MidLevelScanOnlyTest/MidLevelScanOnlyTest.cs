// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
using System;
using System.Net;
using System.Net.NetworkInformation;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;
using Amazon.DynamoDBv2.DocumentModel;

using Xunit;
using Xunit.Abstractions;

namespace MidLevelScanOnlyTest
{
    public class MidLevelScanOnlyTest
    {
        private readonly ITestOutputHelper _output;

        public MidLevelScanOnlyTest(ITestOutputHelper output)
        {
            this._output = output;
        }

        private static string _ip = "localhost";
        private static int _port = 8000;
        private readonly string _endpointUrl = "http://" + _ip + ":" + _port.ToString();

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
        public async void CheckMidLevelScanOnly()
        {
            var portUsed = IsPortInUse();
            if (portUsed)
            {
                throw new Exception("You must run local DynamoDB on port " + _port);
            }

            var clientConfig = new AmazonDynamoDBConfig();
            clientConfig.ServiceURL = _endpointUrl;
            var client = new AmazonDynamoDBClient(clientConfig);

            // Create ProductCatalog table
            _output.WriteLine("Creating ProductCatalog table");
            await CreateTablesLoadData.CreateTablesLoadData.CreateTableProductCatalog(client);

            Table productCatalogTable = Table.LoadTable(client, "ProductCatalog");
            // Scan example.
            MidLevelScanOnly.MidLevelScanOnly.FindProductsWithNegativePrice(client, productCatalogTable);
            MidLevelScanOnly.MidLevelScanOnly.FindProductsWithNegativePriceWithConfig(client, productCatalogTable);

            // Delete ProductCatalog table
            _output.WriteLine("Deleting ProductCatalog table");
            await CreateTablesLoadData.CreateTablesLoadData.DeleteTable(client, "ProductCatalog");

            _output.WriteLine("Done");
        }
    }
}
