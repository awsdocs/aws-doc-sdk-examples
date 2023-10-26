// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace PartiQL_Basics_Tests
{
    [TestCaseOrderer("PartiQL_Basics_Tests.PriorityOrderer", "PartiQL_Basics_Tests")]
    public class PartiQLMethodsTests
    {
        private static readonly AmazonDynamoDBClient Client = new AmazonDynamoDBClient();
        const string tableName = "test_movie_table";

        public PartiQLMethodsTests()
        {
            _ = DynamoDBMethods.CreateMovieTableAsync(tableName);
        }

        [Fact(Skip = "Quarantined test.")]
        [Trait("Category", "Integration")]
        public void Dispose()
        {
            _ = DynamoDBMethods.DeleteTableAsync(tableName);
        }


        [Fact(Skip = "Quarantined test."), TestPriority(1)]
        [Trait("Category", "Integration")]
        public async Task InsertMoviesTest()
        {
            // Because InsertMovies calls ImportMovies, there is no
            // need to test ImportMovies separately.
            const string movieFileName = "moviedata.json";
            var success = await PartiQLMethods.InsertMovies(tableName, movieFileName);
            Assert.True(success, "Could not insert the movies into the table.");
        }

        [Fact(Skip = "Quarantined test."), TestPriority(2)]
        [Trait("Category", "Integration")]
        public async Task GetSingleMovieTestExistingMovie()
        {
            var title = "Star Wars";
            var movies = await PartiQLMethods.GetSingleMovie(tableName, title);
            var foundIt = movies.Count > 0;
            Assert.True(foundIt, $"Couldn't find {title}.");
        }

        [Fact(Skip = "Quarantined test."), TestPriority(3)]
        [Trait("Category", "Integration")]
        public async Task GetSingleMovieTestNonexistentMovie()
        {
            var title = "MASH";
            var movies = await PartiQLMethods.GetSingleMovie(tableName, title);
            var foundIt = movies.Count > 0;
            Assert.False(foundIt, $"Found {title}.");
        }

        [Fact(Skip = "Quarantined test."), TestPriority(4)]
        [Trait("Category", "Integration")]
        public async Task GetMoviesTest()
        {
            int year = 2010;
            var movies = await PartiQLMethods.GetMovies(tableName, year);
            var foundIt = movies.Count > 0;
            Assert.True(foundIt, $"Couldn't find any movies released in {year}.");
        }

        [Fact(Skip = "Quarantined test."), TestPriority(5)]
        [Trait("Category", "Integration")]
        public async Task InsertSingleMovieTest()
        {
            var movieTitle = "Butch Cassidy and the Sundance Kid";
            var year = 1969;
            var success = await PartiQLMethods.InsertSingleMovie(tableName, movieTitle, year);
            Assert.True(success, $"Could not insert {movieTitle}.");
        }

        [Fact(Skip = "Quarantined test."), TestPriority(6)]
        [Trait("Category", "Integration")]
        public async Task UpdateSingleMovieTest()
        {
            var producer = "MGM";
            var movieTitle = "Wizard of Oz";
            var year = 1939;

            var success = await PartiQLMethods.UpdateSingleMovie(tableName, producer, movieTitle, year);
            Assert.True(success, $"Could not update {movieTitle}.");
        }

        [Fact(Skip = "Quarantined test."), TestPriority(7)]
        [Trait("Category", "Integration")]
        public async Task DeleteSingleMovieTest()
        {
            var movieTitle = "Butch Cassidy and the Sundance Kid";
            var year = 1969;
            var success = await PartiQLMethods.DeleteSingleMovie(tableName, movieTitle, year);
            Assert.True(success, $"Could not delete {movieTitle}.");
        }
    }
}