// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace HighLevelQueryAndScanExample
{
  using System;
  using System.Collections.Generic;
  using Amazon.DynamoDBv2.DataModel;

  // snippet-start:[dynamodb.dotnet35.HighLevelQueryAndScan.Thread]

  /// <summary>
  /// Maps information about forum threads to the Amazon DynamoDB table Thread
  /// </summary>
  [DynamoDBTable("Thread")]
  public class Thread
  {
    // Partition key mapping.
    [DynamoDBHashKey] // Partition key
    public string ForumName { get; set; }

    [DynamoDBRangeKey] // Sort key
    public DateTime Subject { get; set; }

    // Implicit mapping.
    public string Message { get; set; }

    public string LastPostedBy { get; set; }

    public int Views { get; set; }

    public int Replies { get; set; }

    public bool Answered { get; set; }

    public DateTime LastPostedDateTime { get; set; }

    // Explicit mapping (property and table attribute names are different).
    [DynamoDBProperty("Tags")]
    public List<string> KeywordTags { get; set; }

    // Property to store version number for optimistic locking.
    [DynamoDBVersion]
    public int? Version { get; set; }
  }

  // snippet-end:[dynamodb.dotnet35.HighLevelQueryAndScan.Thread]
}
