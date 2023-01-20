// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace KeyspacesScenario;
/// <summary>
/// Properties to describe an entry in the movies table.
/// </summary>
public class Movie
{
    public int Year { get; set; }
    public string Title { get; set; }
    public MovieInfo Info { get; set; }
}
