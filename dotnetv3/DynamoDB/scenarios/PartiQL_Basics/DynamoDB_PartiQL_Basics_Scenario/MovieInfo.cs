// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace DynamoDB_PartiQL_Basics_Scenario
{
    using System;
    using Amazon.DynamoDBv2.DataModel;

    public class MovieInfo
    {
        [DynamoDBProperty("directors")]
        public string[] Directors { get; set; }

        [DynamoDBProperty("release_date")]
        public DateTime ReleaseDate { get; set; }

        [DynamoDBProperty("rating")]
        public float Rating { get; set; }

        [DynamoDBProperty("genres")]
        public string[] Genres { get; set; }

        [DynamoDBProperty("image_url")]
        public string ImageUrl { get; set; }

        [DynamoDBProperty("plot")]
        public string Plot { get; set; }

        [DynamoDBProperty("rank")]
        public int Rank { get; set; }

        [DynamoDBProperty("running_time_secs")]
        public int RunningTimeSecs { get; set; }

        [DynamoDBProperty("actors")]
        public string[] Actors { get; set; }
    }
}
