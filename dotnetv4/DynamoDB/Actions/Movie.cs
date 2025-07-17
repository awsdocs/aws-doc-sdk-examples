// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.DynamoDBv2.DataModel;

namespace DynamoDBActions;

/// <summary>
/// Represents a movie entity for DynamoDB operations.
/// </summary>
[DynamoDBTable("movie_table")]
public class Movie
{
    /// <summary>
    /// Gets or sets the year the movie was released. This serves as the hash key.
    /// </summary>
    [DynamoDBHashKey]
    [DynamoDBProperty("year")]
    public int Year { get; set; }

    /// <summary>
    /// Gets or sets the title of the movie. This serves as the range key.
    /// </summary>
    [DynamoDBRangeKey]
    [DynamoDBProperty("title")]
    public string Title { get; set; } = null!;

    /// <summary>
    /// Gets or sets additional information about the movie.
    /// </summary>
    [DynamoDBProperty("info")]
    public MovieInfo Info { get; set; } = null!;
}