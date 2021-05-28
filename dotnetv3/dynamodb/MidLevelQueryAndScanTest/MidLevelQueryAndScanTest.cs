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

namespace MidLevelQueryAndScanTest
{
    public class MidLevelQueryAndScanTest
    {
        private readonly ITestOutputHelper _output;

        public MidLevelQueryAndScanTest(ITestOutputHelper output)
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
        public async void CheckMidLevelQueryAndScan()
        {
            var portUsed = IsPortInUse();
            if (portUsed)
            {
                throw new Exception("You must run local DynamoDB on port " + _port);
            }

            var clientConfig = new AmazonDynamoDBConfig();
            clientConfig.ServiceURL = _endpointUrl;
            var client = new AmazonDynamoDBClient(clientConfig);

            // Create tables.
            _output.WriteLine("Creating Reply table");
            await CreateTablesLoadData.CreateTablesLoadData.CreateTableReply(client);
            _output.WriteLine("Creating ProductCatalog table");
            await CreateTablesLoadData.CreateTablesLoadData.CreateTableProductCatalog(client);

            // Query examples.
            Table replyTable = Table.LoadTable(client, "Reply");
            string forumName = "Amazon DynamoDB";
            string threadSubject = "DynamoDB Thread 2";
            MidLevelQueryAndScan.MidLevelQueryAndScan.FindRepliesInLast15Days(replyTable, forumName, threadSubject);
            MidLevelQueryAndScan.MidLevelQueryAndScan.FindRepliesInLast15DaysWithConfig(replyTable, forumName, threadSubject);
            MidLevelQueryAndScan.MidLevelQueryAndScan.FindRepliesPostedWithinTimePeriod(replyTable, forumName, threadSubject);

            // Get Example.
            Table productCatalogTable = Table.LoadTable(client, "ProductCatalog");
            int productId = 101;
            MidLevelQueryAndScan.MidLevelQueryAndScan.GetProduct(productCatalogTable, productId);

            // Delete tables.
            _output.WriteLine("Deleting Reply table");
            await CreateTablesLoadData.CreateTablesLoadData.DeleteTable(client, "Reply");
            _output.WriteLine("Deleting ProductCatalog table");
            await CreateTablesLoadData.CreateTablesLoadData.DeleteTable(client, "ProductCatalog");

            _output.WriteLine("Done");
        }
    }
}
