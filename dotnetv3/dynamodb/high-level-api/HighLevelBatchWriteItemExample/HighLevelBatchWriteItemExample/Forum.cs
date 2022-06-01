
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace HighLevelBatchWriteItemExample
{
    using System;
    using Amazon.DynamoDBv2.DataModel;

    // snippet-start:[dynamodb.dotnetv3.HighLevelBatchWriteItem.Forum]

    /// <summary>
    /// Maps data about a forum to an Amazon DynamoDB table called Forum.
    /// </summary>
    [DynamoDBTable("Forum")]
    public class Forum
    {
        [DynamoDBHashKey] // Partition key
        public string Name { get; set; }

        // All the following properties are explicitly mapped,
        // only to show how to provide mapping.
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

    // snippet-end:[dynamodb.dotnetv3.HighLevelBatchWriteItem.Forum]
}
