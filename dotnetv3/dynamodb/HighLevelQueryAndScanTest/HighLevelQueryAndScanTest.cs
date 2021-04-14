// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

using System;
using System.Net;
using System.Net.NetworkInformation;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;
using Xunit;
using Xunit.Abstractions;

namespace HighLevelQueryAndScanTest
{
    public class HighLevelQueryAndScanTest
    {
        private readonly ITestOutputHelper _output;

        public HighLevelQueryAndScanTest(ITestOutputHelper output)
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
        public async void CheckHighLevelQueryAndScan()
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

            // Create tables.
            _output.WriteLine("Creating Reply table");
            await CreateTablesLoadData.CreateTablesLoadData.CreateTableReply(client);

            _output.WriteLine("Creating Thread table");
            await CreateTablesLoadData.CreateTablesLoadData.CreateTableThread(client);

            _output.WriteLine("Creating Forum table");
            await CreateTablesLoadData.CreateTablesLoadData.CreateTableForum(client);

            _output.WriteLine("Creating ProductCatalog table");
            await CreateTablesLoadData.CreateTablesLoadData.CreateTableProductCatalog(client);

            // Load tables with data.
            _output.WriteLine("Loading data into Reply table");
            CreateTablesLoadData.CreateTablesLoadData.LoadSampleReplies(client);

            _output.WriteLine("Loading data into Thread table");
            CreateTablesLoadData.CreateTablesLoadData.LoadSampleThreads(client);

            _output.WriteLine("Loading data into Forum table");
            CreateTablesLoadData.CreateTablesLoadData.LoadSampleForums(client);

            _output.WriteLine("Loading data into ProductCatalog table");
            CreateTablesLoadData.CreateTablesLoadData.LoadSampleProducts(client);

            // Tests
            // Get an item.
            HighLevelQueryAndScan.HighLevelQueryAndScan.GetBook(context, 101);

            // Sample forum and thread to test queries.
            string forumName = "Amazon DynamoDB";
            string threadSubject = "DynamoDB Thread 1";
            // Sample queries.
            HighLevelQueryAndScan.HighLevelQueryAndScan.FindRepliesInLast15Days(context, forumName, threadSubject);
            HighLevelQueryAndScan.HighLevelQueryAndScan.FindRepliesPostedWithinTimePeriod(context, forumName, threadSubject);

            // Scan table.
            HighLevelQueryAndScan.HighLevelQueryAndScan.FindProductsPricedLessThanZero(context);

            // Delete tables.
            _output.WriteLine("Deleting Reply table");
            await CreateTablesLoadData.CreateTablesLoadData.DeleteTable(client, "Reply");

            _output.WriteLine("Deleting Thread table");
            await CreateTablesLoadData.CreateTablesLoadData.DeleteTable(client, "Thread");

            _output.WriteLine("Deleting Forum table");
            await CreateTablesLoadData.CreateTablesLoadData.DeleteTable(client, "Forum");

            _output.WriteLine("Deleting ProductCatalog table");
            await CreateTablesLoadData.CreateTablesLoadData.DeleteTable(client, "ProductCatalog");

        }
    }
}
