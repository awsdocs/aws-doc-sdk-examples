// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace PartiQL_Basics_Scenario
{
    public class MovieInfo
    {
        public string[] Directors { get; set; } = null!;
        public DateTime ReleaseDate { get; set; }
        public float Rating { get; set; }
        public string[] Genres { get; set; } = null!;
        public string ImageUrl { get; set; } = null!;
        public string Plot { get; set; } = null!;
        public int Rank { get; set; }
        public int RunningTimeSecs { get; set; }
        public string[] Actors { get; set; } = null!;
    }
}