// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using DynamoDBActions;

namespace DynamoDBTests;

public class DynamoDbMethodsTests
{
    readonly DynamoDbWrapper _wrapper = new(new AmazonDynamoDBClient());
    readonly string _tableName = "movie_table";
    readonly string _movieFileName = @"..\..\..\..\..\..\..\..\resources\sample_files\movies.json";
    readonly string _badMovieFile = "notareaddatafile";

    [Fact()]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task CreateMovieTableAsyncTest()
    {
        var success = await _wrapper.CreateMovieTableAsync(_tableName);

        Assert.True(success, "Failed to create table.");
    }

    [Fact()]
    [Order(2)]
    [Trait("Category", "Integration")]
    public async Task PutItemAsyncTest()
    {
        var newMovie = new Movie
        {
            Year = 1959,
            Title = "North by Northwest",
        };

        var success = await _wrapper.PutItemAsync(newMovie, _tableName);
        Assert.True(success, "Couldn't add the movie.");
    }

    [Fact()]
    [Order(3)]
    [Trait("Category", "Integration")]
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

        var success = await _wrapper.UpdateItemAsync(updateMovie, updateMovieInfo, _tableName);
        Assert.True(success, $"Couldn't update {updateMovie.Title}.");
    }

    [Fact()]
    [Order(4)]
    [Trait("Category", "Integration")]
    public void ImportMoviesWithBadFileNameShouldReturnNullTest()
    {
        var movies = _wrapper.ImportMovies(_badMovieFile);
        Assert.Null(movies);
    }

    [Fact()]
    [Order(5)]
    [Trait("Category", "Integration")]
    public async Task BatchWriteItemsAsyncTest()
    {
        var itemCount = await _wrapper.BatchWriteItemsAsync(_movieFileName, _tableName);
        Assert.Equal(250, itemCount);
    }

    [Fact()]
    [Order(6)]
    [Trait("Category", "Integration")]
    public async Task GetItemAsyncTest()
    {
        var lookupMovie = new Movie
        {
            Title = "Now You See Me",
            Year = 2013,
        };

        var item = await _wrapper.GetItemAsync(lookupMovie, _tableName);

        Assert.True(item["title"].S == lookupMovie.Title, $"Couldn't find {lookupMovie.Title}.");
    }

    [Fact()]
    [Order(8)]
    [Trait("Category", "Integration")]
    public async Task DeleteItemAsyncTest()
    {
        var movieToDelete = new Movie
        {
            Title = "Gravity",
            Year = 2013,
        };

        var success = await _wrapper.DeleteItemAsync(_tableName, movieToDelete);
        Assert.True(success, "Couldn't delete the item.");
    }

    [Fact()]
    [Order(9)]
    [Trait("Category", "Integration")]
    public async Task QueryMoviesAsyncTest()
    {
        // Use Query to find all the movies released in 2010.
        int findYear = 2013;
        var queryCount = await _wrapper.QueryMoviesAsync(_tableName, findYear);
        Assert.True(queryCount > 0, "Couldn't find any movies that match.");
    }

    [Fact()]
    [Order(10)]
    [Trait("Category", "Integration")]
    public async Task ScanTableAsyncTest()
    {
        int startYear = 2001;
        int endYear = 2011;
        var scanCount = await _wrapper.ScanTableAsync(_tableName, startYear, endYear);
        Assert.True(scanCount > 0, "Couldn't find any movies released in those years.");
    }

    [Fact()]
    [Order(11)]
    [Trait("Category", "Integration")]
    public async void DeleteTableAsyncTest()
    {
        var success = await _wrapper.DeleteTableAsync(_tableName);

        Assert.True(success, "Failed to delete table.");
    }
}