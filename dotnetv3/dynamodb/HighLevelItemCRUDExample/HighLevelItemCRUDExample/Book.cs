// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace HighLevelItemCRUDExample
{
  using System.Collections.Generic;
  using Amazon.DynamoDBv2.DataModel;

  // snippet-start:[dynamodb.dotnet35.HighLevelItemCRUD.Book]

  /// <summary>
  /// A class representing book information to be added to the Amazon DynamoDB
  /// ProductCatalog table.
  /// </summary>
  [DynamoDBTable("ProductCatalog")]
  public class Book
  {
    [DynamoDBHashKey] // Partition key
    public int Id { get; set; }

    [DynamoDBProperty]
    public string Title { get; set; }

    [DynamoDBProperty]
    public string Isbn { get; set; }

    [DynamoDBProperty("Authors")] // String Set datatype
    public List<string> BookAuthors { get; set; }
  }

  // snippet-end:[dynamodb.dotnet35.HighLevelItemCRUD.Book]
}
