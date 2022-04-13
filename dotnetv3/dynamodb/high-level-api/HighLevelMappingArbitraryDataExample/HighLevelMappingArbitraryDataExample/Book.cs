// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace HighLevelMappingArbitraryDataExample
{
  using System.Collections.Generic;
  using Amazon.DynamoDBv2.DataModel;

  // snippet-start:[dynamodb.dotnet35.HighLevelMappingArbitraryData.Book]

  /// <summary>
  /// Maps data for a book to the Amazon DynamoDB table ProductCatalog.
  /// </summary>
  [DynamoDBTable("ProductCatalog")]
  public class Book
  {
    [DynamoDBHashKey] //Partition key
    public int Id { get; set; }

    [DynamoDBProperty]
    public string Title { get; set; }

    [DynamoDBProperty]
    public string Isbn { get; set; }

    // Multi-valued (set type) attribute.
    [DynamoDBProperty("Authors")]
    public List<string> BookAuthors { get; set; }

    // Arbitrary type, with a converter to map it to DynamoDB type.
    [DynamoDBProperty(typeof(DimensionTypeConverter))]
    public DimensionType Dimensions { get; set; }
  }

  // snippet-end:[dynamodb.dotnet35.HighLevelMappingArbitraryData.Book]
}
