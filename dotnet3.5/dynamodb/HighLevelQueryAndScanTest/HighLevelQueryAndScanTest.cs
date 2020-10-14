// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
using System;
using System.Net;
using System.Net.NetworkInformation;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DataModel;

using Xunit;
using Xunit.Abstractions;

namespace DynamoDBCRUD
{
    public class HighLevelQueryAndScanTest
    {
        private readonly ITestOutputHelper output;

        public HighLevelQueryAndScanTest(ITestOutputHelper output)
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
        public async void CheckHighLevelQueryAndScan()
        {
            var portUsed = IsPortInUse(port);
            if (portUsed)
            {
                throw new Exception("You must run local DynamoDB on port 8000");
            }

            var clientConfig = new AmazonDynamoDBConfig();
            clientConfig.ServiceURL = _endpointURL;
            var client = new AmazonDynamoDBClient(clientConfig);
            var context = CreateMockDynamoDBContext(client);

            // Create tables.
            output.WriteLine("Creating Reply table");
            await CreateTablesLoadData.CreateTableReply(client);

            output.WriteLine("Creating Thread table");
            await CreateTablesLoadData.CreateTableThread(client);

            output.WriteLine("Creating Forum table");
            await CreateTablesLoadData.CreateTableForum(client);

            output.WriteLine("Creating ProductCatalog table");
            await CreateTablesLoadData.CreateTableProductCatalog(client);

            // Load tables with data.
            output.WriteLine("Loading data into Reply table");
            CreateTablesLoadData.LoadSampleReplies(client);

            output.WriteLine("Loading data into Thread table");
            CreateTablesLoadData.LoadSampleThreads(client);

            output.WriteLine("Loading data into Forum table");
            CreateTablesLoadData.LoadSampleForums(client);

            output.WriteLine("Loading data into ProductCatalog table");
            CreateTablesLoadData.LoadSampleProducts(client);

            // Tests
            // Get an item.
            HighLevelQueryAndScan.GetBook(context, 101);

            // Sample forum and thread to test queries.
            string forumName = "Amazon DynamoDB";
            string threadSubject = "DynamoDB Thread 1";
            // Sample queries.
            HighLevelQueryAndScan.FindRepliesInLast15Days(context, forumName, threadSubject);
            HighLevelQueryAndScan.FindRepliesPostedWithinTimePeriod(context, forumName, threadSubject);

            // Scan table.
            HighLevelQueryAndScan.FindProductsPricedLessThanZero(context);

            // Delete tables.
            output.WriteLine("Deleting Reply table");
            await CreateTablesLoadData.DeleteTable(client, "Reply");

            output.WriteLine("Deleting Thread table");
            await CreateTablesLoadData.DeleteTable(client, "Thread");

            output.WriteLine("Deleting Forum table");
            await CreateTablesLoadData.DeleteTable(client, "Forum");

            output.WriteLine("Deleting ProductCatalog table");
            await CreateTablesLoadData.DeleteTable(client, "ProductCatalog");

        }
    }
}
