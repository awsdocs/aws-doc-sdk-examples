// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace KeyspacesTests
{
    public class KeyspacesWrapperTests
    {
        private readonly IConfiguration _configuration;
        private readonly IAmazonKeyspaces _client;
        private readonly KeyspacesWrapper _wrapper;
        private readonly string _keyspaceName;
        private static string _keyspaceArn;
        private readonly string _tableName;
        private readonly SchemaDefinition _schemaDefinition;
        private static DateTime _timeChanged;

        /// <summary>
        /// Constructor for the test class.
        /// </summary>
        public KeyspacesWrapperTests()
        {
            _configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("testsettings.json") // Load test settings from .json file.
                .AddJsonFile("testsettings.local.json",
                    true) // Optionally load local settings.
                .Build();

            _client = new AmazonKeyspacesClient();
            _wrapper = new KeyspacesWrapper(_client);

            _keyspaceName = _configuration["KeyspaceName"];
            _tableName = _configuration["TableName"];

            // Define the schema for the test table.
            var allColumns = new List<ColumnDefinition>
            {
                new ColumnDefinition { Name = "title", Type = "text" },
                new ColumnDefinition { Name = "year", Type = "int" },
                new ColumnDefinition { Name = "release_date", Type = "timestamp" },
                new ColumnDefinition { Name = "plot", Type = "text" },
            };

            var partitionKeys = new List<PartitionKey>
            {
                new PartitionKey { Name = "year", },
                new PartitionKey { Name = "title" },
            };

            _schemaDefinition = new SchemaDefinition
            {
                AllColumns = allColumns,
                PartitionKeys = partitionKeys,
            };
        }

        [Fact()]
        [Order(1)]
        public async Task CreateKeyspaceTest()
        {
            _keyspaceArn = await _wrapper.CreateKeyspace(_keyspaceName);

            // Wait until keyspace has been created.
            Thread.Sleep(10000);
            var getKeyspaceArn = "";
            do
            {
                getKeyspaceArn = await _wrapper.GetKeyspace(_keyspaceName);
            } while (getKeyspaceArn != _keyspaceArn);
            Assert.NotNull(_keyspaceArn);
        }

        [Fact()]
        [Order(2)]
        public async Task GetKeyspaceTest()
        {
            var keyspaceArn = await _wrapper.GetKeyspace(_keyspaceName);
            Assert.Equal(keyspaceArn, _keyspaceArn);
        }

        [Fact()]
        [Order(3)]
        public async Task CreateTableTest()
        {
            var tableArn = await _wrapper.CreateTable(_keyspaceName, _schemaDefinition, _tableName);

            // Wait for the table to be created.
            Thread.Sleep(10000);
            try
            {
                GetTableResponse resp;

                // Wait for the table to be created.
                do
                {
                    resp = await _wrapper.GetTable(_keyspaceName, _tableName);
                } while (resp.Status != TableStatus.ACTIVE);
            }
            catch (ResourceNotFoundException ex)
            {
                Console.WriteLine($"Error: {ex.Message}");
            }
            Assert.NotNull(tableArn);
        }

        [Fact()]
        [Order(4)]
        public async Task GetTableTest()
        {
            var response = await _wrapper.GetTable(_keyspaceName, _tableName);
            Assert.NotNull(response);
        }

        [Fact()]
        [Order(5)]
        public async Task UpdateTableTest()
        {
            _timeChanged = DateTime.UtcNow;
            var resourceArn = await _wrapper.UpdateTable(_keyspaceName, _tableName);
            Assert.NotNull(resourceArn);
        }

        [Fact()]
        [Order(6)]
        public async Task RestoreTableTest()
        {
            var resourceArn = await _wrapper.RestoreTable(_keyspaceName, _tableName, _timeChanged);
            Assert.NotNull(resourceArn);
        }

        [Fact()]
        [Order(49)]
        public async Task DeleteTableTest()
        {
            var success = await _wrapper.DeleteTable(_keyspaceName, _tableName);

            // Loop and call GetTable until the table is gone. Once it has been
            // deleted completely, GetTable will raise a ResourceNotFoundException.
            bool wasDeleted = false;

            try
            {
                do
                {
                    var resp = await _wrapper.GetTable(_keyspaceName, _tableName);
                } while (!wasDeleted);
            }
            catch (ResourceNotFoundException ex)
            {
                wasDeleted = true;
            }

            Assert.True(success);
        }

        [Fact()]
        [Order(50)]
        public async Task DeleteKeyspaceTest()
        {
            var success = await _wrapper.DeleteKeyspace(_keyspaceName);
            Assert.True(success);
        }
    }
}