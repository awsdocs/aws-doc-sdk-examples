// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.DynamoDBv2.DataModel;

namespace DynamoDBActions;

/// <summary>
/// Contains additional information about a movie.
/// </summary>
public class MovieInfo
{
    /// <summary>
    /// Gets or sets the directors of the movie.
    /// </summary>
    [DynamoDBProperty("directors")]
    public string[] Directors { get; set; } = null!;

    /// <summary>
    /// Gets or sets the release date of the movie.
    /// </summary>
    [DynamoDBProperty("release_date")]
    public DateTime ReleaseDate { get; set; }

    /// <summary>
    /// Gets or sets the rating of the movie.
    /// </summary>
    [DynamoDBProperty("rating")]
    public float Rating { get; set; }

    /// <summary>
    /// Gets or sets the genres of the movie.
    /// </summary>
    [DynamoDBProperty("genres")]
    public string[] Genres { get; set; } = null!;

    /// <summary>
    /// Gets or sets the image URL for the movie poster.
    /// </summary>
    [DynamoDBProperty("image_url")]
    public string ImageUrl { get; set; } = null!;

    /// <summary>
    /// Gets or sets the plot summary of the movie.
    /// </summary>
    [DynamoDBProperty("plot")]
    public string Plot { get; set; } = null!;

    /// <summary>
    /// Gets or sets the ranking of the movie.
    /// </summary>
    [DynamoDBProperty("rank")]
    public int Rank { get; set; }

    /// <summary>
    /// Gets or sets the running time of the movie in seconds.
    /// </summary>
    [DynamoDBProperty("running_time_secs")]
    public int RunningTimeSecs { get; set; }

    /// <summary>
    /// Gets or sets the actors in the movie.
    /// </summary>
    [DynamoDBProperty("actors")]
    public string[] Actors { get; set; } = null!;
}