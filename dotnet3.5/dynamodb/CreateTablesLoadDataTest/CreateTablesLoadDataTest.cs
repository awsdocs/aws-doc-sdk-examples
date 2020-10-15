// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

using System;
using System.Net;
using System.Net.NetworkInformation;
using Amazon.DynamoDBv2;
using Xunit;
using Xunit.Abstractions;

namespace CreateTablesLoadDataTest
{
    public class CreateTablesLoadDataTest
    {
        private readonly ITestOutputHelper _output;

        public CreateTablesLoadDataTest(ITestOutputHelper output)
        {
            this._output = output;
        }

        private const string _ip = "localhost";
        private static readonly int _port = 8000;
        private static readonly string EndpointUrl = "http://" + _ip + ":" + _port;
        
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
        public async void CheckCreateTablesLoadData()
        {
            var portUsed = IsPortInUse();
            if(portUsed)            
            {
                throw new Exception("You must run local DynamoDB on port " + _port);
            }
            
            var clientConfig = new AmazonDynamoDBConfig();
            clientConfig.ServiceURL = EndpointUrl;
            var client = new AmazonDynamoDBClient(clientConfig);

            // Create, load, and delete a table
            await CreateTablesLoadData.CreateTablesLoadData.CreateTableForum(client);
            _output.WriteLine("Waiting for Forum table to be created");

            //await CreateTablesLoadData.WaitTillTableCreated(client, "Forum", createResult.Result);
            _output.WriteLine("Created Forum table");

            CreateTablesLoadData.CreateTablesLoadData.LoadSampleForums(client);
            _output.WriteLine("Loaded data into Forum table");

            _ = CreateTablesLoadData.CreateTablesLoadData.DeleteTable(client, "Forum");
            _output.WriteLine("Deleted Forum table");
        }
    }
}
