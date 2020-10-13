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

namespace DynamoDBCRUD
{
    public class MidLevelQueryAndScanTest
    {
        private readonly ITestOutputHelper output;

        public MidLevelQueryAndScanTest(ITestOutputHelper output)
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
        public async void CheckMidLevelQueryAndScan()
        {
            var portUsed = IsPortInUse(port);
            if (portUsed)
            {
                throw new Exception("You must run local DynamoDB on port 8000");
            }

            var clientConfig = new AmazonDynamoDBConfig();
            clientConfig.ServiceURL = _endpointURL;
            var client = new AmazonDynamoDBClient(clientConfig);

            // Create tables.
            output.WriteLine("Creating Reply table");
            await CreateTablesLoadData.CreateTableReply(client);
            output.WriteLine("Creating ProductCatalog table");
            await CreateTablesLoadData.CreateTableProductCatalog(client);

            // Query examples.
            Table replyTable = Table.LoadTable(client, "Reply");
            string forumName = "Amazon DynamoDB";
            string threadSubject = "DynamoDB Thread 2";
            MidLevelQueryAndScan.FindRepliesInLast15Days(replyTable, forumName, threadSubject);
            MidLevelQueryAndScan.FindRepliesInLast15DaysWithConfig(replyTable, forumName, threadSubject);
            MidLevelQueryAndScan.FindRepliesPostedWithinTimePeriod(replyTable, forumName, threadSubject);

            // Get Example.
            Table productCatalogTable = Table.LoadTable(client, "ProductCatalog");
            int productId = 101;
            MidLevelQueryAndScan.GetProduct(productCatalogTable, productId);

            // Delete tables.
            output.WriteLine("Deleting Reply table");
            await CreateTablesLoadData.DeleteTable(client, "Reply");
            output.WriteLine("Deleting ProductCatalog table");
            await CreateTablesLoadData.DeleteTable(client, "ProductCatalog");

            output.WriteLine("Done");
        }
    }
}
