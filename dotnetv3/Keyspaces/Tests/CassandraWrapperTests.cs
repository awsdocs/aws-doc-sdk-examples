// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace KeyspacesTests
{
    /// <summary>
    /// Tests for the CassandraWrapper class.
    /// </summary>
    public class CassandraWrapperTests
    {
        private readonly IConfiguration _configuration;
        private readonly IAmazonKeyspaces _client;
        private readonly CassandraWrapper _wrapper;
        private readonly string _keyspaceName;
        private readonly string _tableName;
        private readonly string _movieFileName;
        private readonly int _moviesToInsert = 5;
        private static string _movieToUpdate;
        private static int _movieToUpdateYear;

        /// <summary>
        /// Constructor for the test class.
        /// </summary>
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

            _movieFileName = _configuration["MovieFile"];

        }

        /// <summary>
        /// Tests the ImportMoviesFromJson method by importing 5 movies. The
        /// number of movies in the move list should be 5 after the call.
        /// </summary>
        [Fact(Skip = "Quarantined test.")]
        [Order(5)]
        [Trait("Category", "Integration")]
        public void ImportFiveMoviesTest()
        {
            var movies = _wrapper.ImportMoviesFromJson(_movieFileName, _moviesToInsert);
            Assert.Equal(_moviesToInsert, movies.Count);
        }

        /// <summary>
        /// Calls ImportMoviesFromJson with the default value of 0 for number
        /// of movies to import. This should result in loading all 4,000 movies
        /// in the table.
        /// </summary>
        [Fact(Skip = "Quarantined test.")]
        [Order(6)]
        [Trait("Category", "Integration")]
        public void ImportAllMoviesTest()
        {
            var movies = _wrapper.ImportMoviesFromJson(_movieFileName, 0);
            Assert.Equal(4609, movies.Count);
        }

        [Fact(Skip = "Quarantined test.")]
        [Order(7)]
        [Trait("Category", "Integration")]
        public async Task InsertIntoMovieTableTest()
        {
            var success = await _wrapper.InsertIntoMovieTable(_keyspaceName, _tableName, _movieFileName, 5);
            Assert.True(success, "Couldn't add the records to the table.");
        }

        /// <summary>
        /// Gets all movies in the table and checks to make sure that _moviesToInsert
        /// rows are returned from the call to CassandraWrapper.GetMovies.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact(Skip = "Quarantined test.")]
        [Order(8)]
        [Trait("Category", "Integration")]
        public async Task GetMoviesTest()
        {
            var rows = await _wrapper.GetMovies(_keyspaceName, _tableName);

            // Save these values for testing the MarkMovieAsWatchedTest.
            _movieToUpdate = rows[1].GetValue<string>("title");
            _movieToUpdateYear = rows[1].GetValue<int>("year");

            Assert.Equal(_moviesToInsert, rows.Count);
        }

        /// <summary>
        /// This marks one movie as watched. The order is 10 instead of
        /// 9 because it must run after the KeyspacesWrapperTests.UpdateTableTest
        /// method which adds the watched column to the table.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact(Skip = "Quarantined test.")]
        [Order(10)]
        [Trait("Category", "Integration")]
        public async Task MarkMovieAsWatchedTest()
        {
            var rows = await _wrapper.MarkMovieAsWatched(_keyspaceName, _tableName, _movieToUpdate, _movieToUpdateYear);

            // We are updating a single row by marking it as watched, so
            // the number of rows in the returned list should be one.
            Assert.Single(rows);
        }

        /// <summary>
        /// Tests the method to SELECT all watched movies from the table. Since
        /// only a single movie was updated as watched, this method tests that
        /// the returned title matches the title of the movie that was changed.
        /// </summary>
        /// <returns>An async Task.</returns>
        [Fact(Skip = "Quarantined test.")]
        [Order(11)]
        [Trait("Category", "Integration")]
        public async Task GetWatchedMoviesTest()
        {
            var rows = await _wrapper.GetWatchedMovies(_keyspaceName, _tableName);
            Assert.Equal(_movieToUpdate, rows[0].GetValue<string>("title"));
        }
    }
}