// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.DynamoDBv2.DataModel;

namespace DynamoDB_Actions
{
    [DynamoDBTable("movie_table")]
    public class Movie
    {
        [DynamoDBHashKey]
        [DynamoDBProperty("year")]
        public int Year { get; set; }

        [DynamoDBRangeKey]
        [DynamoDBProperty("title")]
        public string Title { get; set; }

        [DynamoDBProperty("info")]
        public MovieInfo Info { get; set; }
    }
}