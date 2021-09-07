// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace HighLevelItemCRUDExample
{
  using System;
  using System.Collections.Generic;
  using System.Threading.Tasks;
  using Amazon.DynamoDBv2;
  using Amazon.DynamoDBv2.DataModel;

  // snippet-start:[dynamodb.dotnet35.HighLevelItemCRUD]

  /// <summary>
  /// Shows how to perform high-level CRUD operations on a Amazon DynamoDB
  /// table. The example was created with AWS SDK for .NET version 3.7 and
  /// .NET Core 5.0.
  /// </summary>
  public class HighLevelItemCrud
  {
    public static async Task Main()
    {
      var client = new AmazonDynamoDBClient();
      DynamoDBContext context = new DynamoDBContext(client);
      await PerformCRUDOperations(context);
    }

    public static async Task PerformCRUDOperations(IDynamoDBContext context)
    {
      int bookId = 1001; // Some unique value.
      Book myBook = new Book
      {
        Id = bookId,
        Title = "object persistence-AWS SDK for.NET SDK-Book 1001",
        Isbn = "111-1111111001",
        BookAuthors = new List<string> { "Author 1", "Author 2" },
      };

      // Save the book to the ProductCatalog table.
      await context.SaveAsync(myBook);

      // Retrieve the book from the ProductCatalog table.
      Book bookRetrieved = await context.LoadAsync<Book>(bookId);

      // Update few properties.
      bookRetrieved.Isbn = "222-2222221001";

      // Update existing authors list with this.
      bookRetrieved.BookAuthors = new List<string> { " Author 1", "Author x" };
      await context.SaveAsync(bookRetrieved);

      // Retrieve the updated book. This time add the optional ConsistentRead
      // parameter using DynamoDBContextConfig object.
      await context.LoadAsync<Book>(bookId, new DynamoDBContextConfig
      {
        ConsistentRead = true,
      });

      // Delete the book.
      await context.DeleteAsync<Book>(bookId);

      // Try to retrieve deleted book. It should return null.
      Book deletedBook = await context.LoadAsync<Book>(bookId, new DynamoDBContextConfig
      {
        ConsistentRead = true,
      });

      if (deletedBook == null)
      {
        Console.WriteLine("Book is deleted");
      }
    }
  }

  // snippet-end:[dynamodb.dotnet35.HighLevelItemCRUD]
}
