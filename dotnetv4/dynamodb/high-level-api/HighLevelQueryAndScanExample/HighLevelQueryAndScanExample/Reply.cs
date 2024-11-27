﻿// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace HighLevelQueryAndScanExample
{
    using System;
    using Amazon.DynamoDBv2.DataModel;

    // snippet-start:[dynamodb.dotnetv4.HighLevelQueryAndScan.Reply]

    /// <summary>
    /// Maps information to the Amazon DynamoDB table Reply.
    /// </summary>
    [DynamoDBTable("Reply")]
    public class Reply
    {
        [DynamoDBHashKey] // Partition key
        public string Id { get; set; }

        [DynamoDBRangeKey] // Sort key
        public DateTime ReplyDateTime { get; set; }

        // Properties included implicitly.
        public string Message { get; set; }

        // Explicit property mapping with object persistence model attributes.
        [DynamoDBProperty("LastPostedBy")]
        public string PostedBy { get; set; }

        // Property to store version number for optimistic locking.
        [DynamoDBVersion]
        public int? Version { get; set; }
    }

    // snippet-end:[dynamodb.dotnetv4.HighLevelQueryAndScan.Reply]
}