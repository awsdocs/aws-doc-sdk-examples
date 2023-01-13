using Xunit;
using KeyspacesScenario;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace KeyspacesScenario.Tests
{
    public class CassandraWrapperTests
    {
        private readonly IConfiguration _configuration;
        private readonly IAmazonKeyspaces _client;
        private readonly CassandraWrapper _wrapper;
        private readonly string _keyspaceName;
        private readonly string _tableName;

        public CassandraWrapperTests()
        {
            _configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("testsettings.json") // Load test settings from .json file.
                .AddJsonFile("testsettings.local.json",
                    true) // Optionally load local settings.
                .Build();

            _client = new AmazonKeyspacesClient();
            _wrapper = new CassandraWrapper();

            _keyspaceName = _configuration["KeyspaceName"];
            _tableName = _configuration["TableName"];

        }

        [Fact()]
        [Order(20)]
        public void ImportFiveMoviesTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        [Order(20)]
        public void ImportAllMoviesTest()
        {
            Assert.True(false, "This test need an implementation");
        }

        [Fact()]
        [Order(21)]
        public void InsertIntoMovieTableTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        [Order(22)]
        public void GetMoviesTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        [Order(23)]
        public void MarkMovieAsWatchedTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        [Order(24)]
        public void GetWatchedMoviesTest()
        {
            Assert.True(false, "This test needs an implementation");
        }
    }
}