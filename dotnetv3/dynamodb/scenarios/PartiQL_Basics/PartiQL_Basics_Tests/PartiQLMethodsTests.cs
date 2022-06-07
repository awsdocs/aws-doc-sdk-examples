using Xunit;
using DynamoDB_PartiQL_Basics_Scenario;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.Model;

namespace DynamoDB_PartiQL_Basics_Scenario.Tests
{
    [TestCaseOrderer("DynamoDB_PartiQL_Basics_Scenario.PriorityOrderer", " DynamoDB_PartiQL_Basics_Scenario.Tests")]
    public class PartiQLMethodsTests
    {
        private static readonly AmazonDynamoDBClient Client = new AmazonDynamoDBClient();
        const string tableName = "test_movie_table";

        public PartiQLMethodsTests()
        {
            var success = DynamoDBMethods.CreateMovieTableAsync(tableName);
        }

        [Fact]
        public void Dispose()
        {
            var success = DynamoDBMethods.DeleteTableAsync(tableName);
        }

        [Fact(), TestPriority(1)]
        public async Task InsertMoviesTest()
        {
            // Because InsertMovies calls ImportMovies, there is not
            // need to test ImportMovies separately.
            const string movieFileName = "moviedata.json";
            var success = await PartiQLMethods.InsertMovies(tableName, movieFileName);
            Assert.True(success, "Could not insert the movies into the table.");
        }

        [Fact(), TestPriority(2)]
        public async Task GetSingleMovieTestExistingMovie()
        {
            var title = "Star Wars";
            var movies = await PartiQLMethods.GetSingleMovie(tableName, title);
            var foundIt = movies.Count > 0;
            Assert.True(foundIt, $"Couldn't find {title}.");
        }

        [Fact(), TestPriority(3)]
        public async Task GetSingleMovieTestNonexistentMovie()
        {
            var title = "MASH";
            var movies = await PartiQLMethods.GetSingleMovie(tableName, title);
            var foundIt = movies.Count > 0;
            Assert.False(foundIt, $"Couldn't find {title}.");
        }

        [Fact(), TestPriority(4)]
        public async Task InsertSingleMovieTest()
        {
            var movieTitle = "Butch Cassidy and the Sundance Kid";
            var year = 1969;
            var success = await PartiQLMethods.InsertSingleMovie(tableName, movieTitle, year);
            Assert.True(success, $"Could not insert {movieTitle}.");
        }

        [Fact(), TestPriority(5)]
        public async Task UpdateSingleMovieTest()
        {
            var producer = "MGM";
            var movieTitle = "Wizard of Oz";
            var year = 1939;

            var success = await PartiQLMethods.UpdateSingleMovie(tableName, producer, movieTitle, year);
            Assert.True(success, $"Could not update {movieTitle}.");
        }

        [Fact(), TestPriority(6)]
        public async Task DeleteSingleMovieTest()
        {
            var movieTitle = "Butch Cassidy and the Sundance Kid";
            var year = 1969;
            var success = await PartiQLMethods.DeleteSingleMovie(tableName, movieTitle, year);
            Assert.True(success, $"Could not delete {movieTitle}.");
        }

        [Fact()]
        public void UpdateBatchTest()
        {
            throw new NotImplementedException();
        }
    }
}