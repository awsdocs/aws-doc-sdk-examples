using System;
using System.Net;
using System.Net.NetworkInformation;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;

using Xunit;
using Xunit.Abstractions;

namespace DynamoDBCRUD
{
    public class LowLevelItemCRUDExampleTest
    {
        private readonly ITestOutputHelper output;

        public LowLevelItemCRUDExampleTest(ITestOutputHelper output)
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
        public async void CheckLowLevelItemCRUDExample()
        {
            var portUsed = IsPortInUse(port);
            if (portUsed)
            {
                throw new Exception("You must run local DynamoDB on port 8000");
            }

            var clientConfig = new AmazonDynamoDBConfig();
            clientConfig.ServiceURL = _endpointURL;
            var client = new AmazonDynamoDBClient(clientConfig);

            // Create ProductCatalog table.
            output.WriteLine("Creating ProductCatalog table");
            await CreateTablesLoadData.CreateTableProductCatalog(client);

            output.WriteLine("Creating item");
            LowLevelItemCRUDExample.CreateItem(client);
            output.WriteLine("Retrieving item");
            LowLevelItemCRUDExample.RetrieveItem(client);

            // Perform various updates.
            output.WriteLine("Updating multipe attributes");
            LowLevelItemCRUDExample.UpdateMultipleAttributes(client);
            output.WriteLine("Updating attribute by condition");
            LowLevelItemCRUDExample.UpdateExistingAttributeConditionally(client);

            // Delete item.
            output.WriteLine("Deleting item");
            LowLevelItemCRUDExample.DeleteItem(client);

            // Delete ProductCatalog table.
            output.WriteLine("Deleting ProductCabalog table.");
            await CreateTablesLoadData.DeleteTable(client, "ProductCatalog");

            output.WriteLine("Done");
        }
    }
}
