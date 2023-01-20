// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace KeyspacesScenario;

/// <summary>
/// Extended information about a movie in the movies table.
/// </summary>
public class MovieInfo
{
    public string[] Directors { get; set; }
    public DateTime Release_Date { get; set; }
    public float Rating { get; set; }
    public string[] Genres { get; set; }
    public string Image_Url { get; set; }
    public string Plot { get; set; }
    public int Rank { get; set; }
    public int RunningTimeSecs { get; set; }
    public string[] Actors { get; set; }
}
