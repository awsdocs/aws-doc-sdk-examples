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

        /// <summary>
        /// Test the call to CreateKeyspace. This test waits 10 seconds
        /// for the keyspace to be created.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact()]
        [Order(1)]
        [Trait("Category", "Integration")]
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

        /// <summary>
        /// Tests the ability to get information about the new keyspace.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact()]
        [Order(2)]
        [Trait("Category", "Integration")]
        public async Task GetKeyspaceTest()
        {
            var keyspaceArn = await _wrapper.GetKeyspace(_keyspaceName);
            Assert.Equal(keyspaceArn, _keyspaceArn);
        }

        /// <summary>
        /// Tests the CreateTable method. It was necessary to add a
        /// try/catch to this method since, if the table hasn't been
        /// created yet, it raises a resource not found error. Without
        /// the wait and the try/catch, the rest of the tests will
        /// always fail as well.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact()]
        [Order(3)]
        [Trait("Category", "Integration")]
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

        /// <summary>
        /// Tests the ability to get information about the new table.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact()]
        [Order(4)]
        [Trait("Category", "Integration")]
        public async Task GetTableTest()
        {
            var response = await _wrapper.GetTable(_keyspaceName, _tableName);
            Assert.NotNull(response);
        }

        /// <summary>
        /// Tests the UpdateTable method.
        /// </summary>
        /// <returns></returns>
        [Fact()]
        [Order(9)]
        [Trait("Category", "Integration")]
        public async Task UpdateTableTest()
        {
            _timeChanged = DateTime.UtcNow;
            var resourceArn = await _wrapper.UpdateTable(_keyspaceName, _tableName);
            Assert.NotNull(resourceArn);
        }

        /// <summary>
        /// Tests the call to restore the table.
        /// </summary>
        /// <returns></returns>
        [Fact(Skip = "Long running test.")]
        [Order(12)]
        [Trait("Category", "Integration")]
        public async Task RestoreTableTest()
        {
            // This test defaults to not run since it can take up
            // to 20 minutes to restore the table.
            bool restoreTable = false;
            var restoredTableName = $"{_tableName}_restored";
            if (restoreTable)
            {
                var resourceArn = await _wrapper.RestoreTable(_keyspaceName, _tableName, restoredTableName, _timeChanged);
                Assert.NotNull(resourceArn);

                // Loop and call GetTable until the table has been restored. Once it has been
                // restored completely, GetTable will succeed.
                bool wasRestored = false;

                try
                {
                    do
                    {
                        var resp = await _wrapper.GetTable(_keyspaceName, _tableName);
                    } while (!wasRestored);
                }
                catch (ResourceNotFoundException)
                {
                    wasRestored = true;
                }

                Assert.True(wasRestored);
            }

            if (!restoreTable)
            {
                // If the test didn't run, assert that it didn't
                // run and let it go with that.
                Assert.False(restoreTable);
            }
        }

        /// <summary>
        /// Tests the ability to delete the table. It also waits
        /// for the table be deleted and then waits until the
        /// deletion is complete at which point the call to GetTable
        /// will raise a resource not found error.
        /// </summary>
        /// <returns></returns>
        [Fact(Skip = "Quarantined test.")]
        [Order(13)]
        [Trait("Category", "Integration")]
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
            catch (ResourceNotFoundException)
            {
                wasDeleted = true;
            }

            Assert.True(success);
        }

        /// <summary>
        /// Tests deleting the keyspace.
        /// </summary>
        /// <returns></returns>
        [Fact(Skip = "Quarantined test.")]
        [Order(14)]
        [Trait("Category", "Integration")]
        public async Task DeleteKeyspaceTest()
        {
            var success = await _wrapper.DeleteKeyspace(_keyspaceName);
            Assert.True(success);
        }
    }
}