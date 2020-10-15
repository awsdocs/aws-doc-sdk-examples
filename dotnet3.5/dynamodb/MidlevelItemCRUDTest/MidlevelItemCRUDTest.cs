// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
using System;
using System.Net;
using System.Net.NetworkInformation;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;

using Xunit;
using Xunit.Abstractions;

namespace MidlevelItemCRUDTest
{
    public class MidlevelItemCrudTest
    {
        private readonly ITestOutputHelper _output;

        public MidlevelItemCrudTest(ITestOutputHelper output)
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
        public async void CheckMidlevelItemCrud()
        {
            var portUsed = IsPortInUse();
            if (portUsed)
            {
                throw new Exception("You must run local DynamoDB on port " + _port);
            }

            var clientConfig = new AmazonDynamoDBConfig();
            clientConfig.ServiceURL = _endpointUrl;
            var client = new AmazonDynamoDBClient(clientConfig);

            // Create and load ProductCatalog table.
            _output.WriteLine("Creating ProductCatalog table");
            await CreateTablesLoadData.CreateTablesLoadData.CreateTableProductCatalog(client);

            _output.WriteLine("Loading ProductCatalog table");
            var productCatalog = MidlevelItemCRUD.MidlevelItemCrud.LoadTable(client, MidlevelItemCRUD.MidlevelItemCrud.TableName);

            // Create, retrieve, and update Book item.
            _output.WriteLine("Creating Book item");
            MidlevelItemCRUD.MidlevelItemCrud.CreateBookItem(client, productCatalog);
            _output.WriteLine("Retrieving Book item");
            MidlevelItemCRUD.MidlevelItemCrud.RetrieveBook(client, productCatalog);
            _output.WriteLine("Updating Book item");
            MidlevelItemCRUD.MidlevelItemCrud.UpdateMultipleAttributes(client, productCatalog);
            _output.WriteLine("Updating Book item price");
            MidlevelItemCRUD.MidlevelItemCrud.UpdateBookPriceConditionally(client, productCatalog);

            // Delete Book item.
            _output.WriteLine("Deleting item");
            MidlevelItemCRUD.MidlevelItemCrud.DeleteBook(client, productCatalog);

            // Delete ProductCatalog table.
            _output.WriteLine("Deleting ProductCatalog table");
            await CreateTablesLoadData.CreateTablesLoadData.DeleteTable(client, "ProductCatalog");

            _output.WriteLine("Done");
        }
    }
}
