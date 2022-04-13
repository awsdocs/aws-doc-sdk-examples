// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace HighLevelBatchWriteItemExample
{
  using Amazon.DynamoDBv2.DataModel;

  // snippet-start:[dynamodb.dotnet35.HighLevelBatchWriteItem.ProductCatalog]

  /// <summary>
  /// Maps data about books to an Amazon DynamoDB table called ProductCatalog.
  /// </summary>
  [DynamoDBTable("ProductCatalog")]
  public class Book
  {
    [DynamoDBHashKey] // Partition key
    public int Id { get; set; }

    public string Title { get; set; }

    public string Isbn { get; set; }

    public int Price { get; set; }

    public string PageCount { get; set; }

    public string ProductCategory { get; set; }

    public bool InPublication { get; set; }
  }

  // snippet-end:[dynamodb.dotnet35.HighLevelBatchWriteItem.ProductCatalog]
}
