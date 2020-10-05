using System;
using System.Collections.Generic;
using System.Globalization;
using System.Threading.Tasks;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;
using Amazon.DynamoDBv2.DataModel;

using Xunit;
using Xunit.Abstractions;


namespace DynamoDBCRUD
{
    public class CreateTablesLoadDataTest
    {
        private readonly string _endpointURL = "http://localhost:8000";

        private IDynamoDBContext CreateMockDynamoDBContext(AmazonDynamoDBClient client)
        {
            

            var mockDynamoDBContext = new DynamoDBContext(client);

            return mockDynamoDBContext;
        }
        [Fact]
        public void CheckCreateTablesLoadData()
        {
            var clientConfig = new AmazonDynamoDBConfig();
            clientConfig.ServiceURL = _endpointURL;
            var client = new AmazonDynamoDBClient(clientConfig);

            // Create, load, and delete a table
            var createResult = CreateTablesLoadData.CreateTableForum(client);
            CreateTablesLoadData.LoadSampleForums(client);
            var deleteResult = CreateTablesLoadData.DeleteTable(client, "Forum");
        }
    }
}
