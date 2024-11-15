// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace KeyspacesScenario;

/// <summary>
/// Extended information about a movie in the movies table.
/// </summary>
public class MovieInfo
{
    public string[] Directors { get; set; } = null!;
    public DateTime Release_Date { get; set; }
    public float Rating { get; set; }
    public string[] Genres { get; set; } = null!;
    public string Image_Url { get; set; } = null!;
    public string Plot { get; set; } = null!;
    public int Rank { get; set; }
    public int RunningTimeSecs { get; set; }
    public string[] Actors { get; set; } = null!;
}