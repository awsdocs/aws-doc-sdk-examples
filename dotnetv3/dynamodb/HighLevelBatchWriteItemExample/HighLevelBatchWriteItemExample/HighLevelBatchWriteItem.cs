// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace HighLevelBatchWriteItemExample
{
  using System;
  using System.Collections.Generic;
  using System.Threading.Tasks;
  using Amazon.DynamoDBv2;
  using Amazon.DynamoDBv2.DataModel;

  // snippet-start:[dynamodb.dotnet35.HighLevelBatchWriteItem]

  /// <summary>
  /// Performs high level batch write operations to an Amazon DynamoDB table.
  /// This example was written using AWS SDK for .NET verion 3.7 and .NET
  /// Core 5.0.
  /// </summary>
  public class HighLevelBatchWriteItem
  {
    static async Task Main()
    {
      AmazonDynamoDBClient client = new AmazonDynamoDBClient();
      DynamoDBContext context = new DynamoDBContext(client);

      await SingleTableBatchWrite(context);
      await MultiTableBatchWrite(context);
    }

    public static async Task SingleTableBatchWrite(IDynamoDBContext context)
    {
      Book book1 = new Book
      {
        Id = 902,
        InPublication = true,
        Isbn = "902-11-11-1111",
        PageCount = "100",
        Price = 10,
        ProductCategory = "Book",
        Title = "My book3 in batch write",
      };

      Book book2 = new Book
      {
        Id = 903,
        InPublication = true,
        Isbn = "903-11-11-1111",
        PageCount = "200",
        Price = 10,
        ProductCategory = "Book",
        Title = "My book4 in batch write",
      };

      var bookBatch = context.CreateBatchWrite<Book>();
      bookBatch.AddPutItems(new List<Book> { book1, book2 });

      Console.WriteLine("Adding two books to ProductCatalog table.");
      await bookBatch.ExecuteAsync();
    }

    public static async Task MultiTableBatchWrite(IDynamoDBContext context)
    {
      // New Forum item.
      Forum newForum = new Forum
      {
        Name = "Test BatchWrite Forum",
        Threads = 0,
      };
      var forumBatch = context.CreateBatchWrite<Forum>();
      forumBatch.AddPutItem(newForum);

      // New Thread item.
      Thread newThread = new Thread
      {
        ForumName = "S3 forum",
        Subject = "My sample question",
        KeywordTags = new List<string> { "S3", "Bucket" },
        Message = "Message text",
      };

      DynamoDBOperationConfig config = new DynamoDBOperationConfig();
      config.SkipVersionCheck = true;
      var threadBatch = context.CreateBatchWrite<Thread>(config);
      threadBatch.AddPutItem(newThread);
      threadBatch.AddDeleteKey("some partition key value", "some sort key value");

      var superBatch = new MultiTableBatchWrite(forumBatch, threadBatch);

      Console.WriteLine("Performing batch write in MultiTableBatchWrite().");
      await superBatch.ExecuteAsync();
    }
  }

  // snippet-end:[dynamodb.dotnet35.HighLevelBatchWriteItem]
}
