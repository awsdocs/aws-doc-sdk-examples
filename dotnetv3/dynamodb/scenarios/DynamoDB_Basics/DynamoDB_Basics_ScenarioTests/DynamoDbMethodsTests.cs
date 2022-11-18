// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace DynamoDB_Basics_Scenario.Tests
{

    [TestCaseOrderer("OrchestrationService.Project.Orderers.PriorityOrderer", "OrchestrationService.Project")]
    public class DynamoDbMethodsTests
    {
        readonly AmazonDynamoDBClient client = new();
        readonly string _tableName = "movie_table";
        readonly string _movieFileName = @"..\..\..\..\..\..\..\..\resources\sample_files\movies.json";
        readonly string _badMovieFile = "notareaddatafile";

        [Fact()]
        [Order(1)]
        public async Task CreateMovieTableAsyncTest()
        {
            var success = await DynamoDbMethods.CreateMovieTableAsync(client, _tableName);

            Assert.True(success, "Failed to create table.");
        }

        [Fact()]
        [Order(2)]
        public async Task PutItemAsyncTest()
        {
            var newMovie = new Movie
            {
                Year = 1959,
                Title = "North by Northwest",
            };

            var success = await DynamoDbMethods.PutItemAsync(client, newMovie, _tableName);
            Assert.True(success, "Couldn't add the movie.");
        }

        [Fact()]
        [Order(3)]
        public async Task UpdateItemAsyncTest()
        {
            var updateMovie = new Movie
            {
                Title = "North by Northwest",
                Year = 1959,
            };

            var updateMovieInfo = new MovieInfo
            {
                Plot = "A classic Hitchcock Thriller.",
                Rank = 5,
            };

            var success = await DynamoDbMethods.UpdateItemAsync(client, updateMovie, updateMovieInfo, _tableName);
            Assert.True(success, $"Couldn't update {updateMovie.Title}.");
        }

        [Fact()]
        [Order(4)]
        public void ImportMoviesWithBadFileNameShouldReturnNullTest()
        {
            var movies = DynamoDbMethods.ImportMovies(_badMovieFile);
            Assert.Null(movies);
        }

        [Fact()]
        [Order(5)]
        public async Task BatchWriteItemsAsyncTest()
        {
            var itemCount = await DynamoDbMethods.BatchWriteItemsAsync(client, _movieFileName);
            Assert.Equal(250, itemCount);
        }

        [Fact()]
        [Order(6)]
        public async Task GetItemAsyncTest()
        {
            var lookupMovie = new Movie
            {
                Title = "Now You See Me",
                Year = 2013,
            };

            var item = await DynamoDbMethods.GetItemAsync(client, lookupMovie, _tableName);

            Assert.True(item["title"].S == lookupMovie.Title, $"Couldn't find {lookupMovie.Title}.");
        }

        [Fact()]
        [Order(8)]
        public async Task DeleteItemAsyncTest()
        {
            var movieToDelete = new Movie
            {
                Title = "Gravity",
                Year = 2013,
            };

            var success = await DynamoDbMethods.DeleteItemAsync(client, _tableName, movieToDelete);
            Assert.True(success, "Couldn't delete the item.");
        }

        [Fact()]
        [Order(9)]
        public async Task QueryMoviesAsyncTest()
        {
            // Use Query to find all the movies released in 2010.
            int findYear = 2013;
            var queryCount = await DynamoDbMethods.QueryMoviesAsync(client, _tableName, findYear);
            Assert.True(queryCount > 0, "Couldn't find any movies that match.");
        }

        [Fact()]
        [Order(10)]
        public async Task ScanTableAsyncTest()
        {
            int startYear = 2001;
            int endYear = 2011;
            var scanCount = await DynamoDbMethods.ScanTableAsync(client, _tableName, startYear, endYear);
            Assert.True(scanCount > 0, "Couldn't find any movies released in those years.");
        }

        [Fact()]
        [Order(11)]
        public async void DeleteTableAsyncTest()
        {
            var success = await DynamoDbMethods.DeleteTableAsync(client, _tableName);

            Assert.True(success, "Failed to delete table.");
        }
    }
}