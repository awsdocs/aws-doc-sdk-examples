// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace HighLevelQueryAndScanExample
{
    using System;
    using Amazon.DynamoDBv2.DataModel;

    // snippet-start:[dynamodb.dotnet35.HighLevelQueryAndScan.Forum]

    /// <summary>
    /// Maps information to the Amazon DynamoDB table Forum.
    /// </summary>
    [DynamoDBTable("Forum")]
    public class Forum
    {
        [DynamoDBHashKey]
        public string Name { get; set; }

        // All the following properties are explicitly mapped
        // to show how to provide mapping.
        [DynamoDBProperty]
        public int Threads { get; set; }

        [DynamoDBProperty]
        public int Views { get; set; }

        [DynamoDBProperty]
        public string LastPostBy { get; set; }

        [DynamoDBProperty]
        public DateTime LastPostDateTime { get; set; }

        [DynamoDBProperty]
        public int Messages { get; set; }
    }

    // snippet-end:[dynamodb.dotnet35.HighLevelQueryAndScan.Forum]
}
