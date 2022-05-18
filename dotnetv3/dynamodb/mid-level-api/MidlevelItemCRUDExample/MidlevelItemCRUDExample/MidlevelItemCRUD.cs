// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace MidlevelItemCRUDExample
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Threading.Tasks;
    using Amazon.DynamoDBv2;
    using Amazon.DynamoDBv2.DocumentModel;

    // snippet-start:[dynamodb.dotnet35.MidlevelItemCRUDExample]

    /// <summary>
    /// Performs CRUD operations on an Amazon DynamoDB table. The example was
    /// created using the AWS SDK for .NET 3.7 and .NET Core 5.0.
    /// </summary>
    class MidlevelItemCRUD
    {
        public static async Task Main()
        {
            var tableName = "ProductCatalog";
            var sampleBookId = 555;

            var client = new AmazonDynamoDBClient();
            var productCatalog = LoadTable(client, tableName);

            await CreateBookItem(client, productCatalog, sampleBookId);
            RetrieveBook(client, productCatalog, sampleBookId);

            // Couple of sample updates.
            UpdateMultipleAttributes(client, productCatalog, sampleBookId);
            UpdateBookPriceConditionally(client, productCatalog, sampleBookId);

            // Delete.
            await DeleteBook(client, productCatalog, sampleBookId);
        }

        /// <summary>
        /// Loads the contents of a DynamoDB table.
        /// </summary>
        /// <param name="client">An initialized DynamoDB client object.</param>
        /// <param name="tableName">The name of the table to load.</param>
        /// <returns>A DynamoDB table object.</returns>
        public static Table LoadTable(IAmazonDynamoDB client, string tableName)
        {
            Table productCatalog = Table.LoadTable(client, tableName);
            return productCatalog;
        }

        /// <summary>
        /// Creates an example book item and adds it to the DynamoDB table
        /// ProductCatalog.
        /// </summary>
        /// <param name="client">An initialized DynamoDB client object.</param>
        /// <param name="productCatalog">A DynamoDB table object.</param>
        /// <param name="sampleBookId">An integer value representing the book's ID.</param>
        public static async Task CreateBookItem(AmazonDynamoDBClient client, Table productCatalog, int sampleBookId)
        {
            Console.WriteLine("\n*** Executing CreateBookItem() ***");
            var book = new Document();
            book["Id"] = sampleBookId;
            book["Title"] = "Book " + sampleBookId;
            book["Price"] = 19.99;
            book["ISBN"] = "111-1111111111";
            book["Authors"] = new List<string> { "Author 1", "Author 2", "Author 3" };
            book["PageCount"] = 500;
            book["Dimensions"] = "8.5x11x.5";
            book["InPublication"] = new DynamoDBBool(true);
            book["InStock"] = new DynamoDBBool(false);
            book["QuantityOnHand"] = 0;

            // Adds the book to the ProductCatalog table.
            await productCatalog.PutItemAsync(book);
        }

        /// <summary>
        /// Retrieves an item, a book, from the DynamoDB ProductCatalog table
        /// </summary>
        /// <param name="client">An initialized DynamoDB client object.</param>
        /// <param name="productCatalog">A DynamoDB table object.</param>
        /// <param name="sampleBookId">An integer value representing the book's ID.</param>
        public static async void RetrieveBook(
          AmazonDynamoDBClient client,
          Table productCatalog,
          int sampleBookId)
        {
            Console.WriteLine("\n*** Executing RetrieveBook() ***");

            // Optional configuration.
            GetItemOperationConfig config = new GetItemOperationConfig
            {
                AttributesToGet = new List<string> { "Id", "ISBN", "Title", "Authors", "Price" },
                ConsistentRead = true,
            };

            Document document = await productCatalog.GetItemAsync(sampleBookId, config);
            Console.WriteLine("RetrieveBook: Printing book retrieved...");
            PrintDocument(client, document);
        }

        /// <summary>
        /// Updates multiple attributes for a book and writes the changes to the
        /// DynamoDB table ProductCatalog.
        /// </summary>
        /// <param name="client">An initialized DynamoDB client object.</param>
        /// <param name="productCatalog">A DynamoDB table object.</param>
        /// <param name="sampleBookId">An integer value representing the book's ID.</param>
        public static async void UpdateMultipleAttributes(
          AmazonDynamoDBClient client,
          Table productCatalog,
          int sampleBookId)
        {
            Console.WriteLine("\nUpdating multiple attributes....");
            int partitionKey = sampleBookId;

            var book = new Document();
            book["Id"] = partitionKey;

            // List of attribute updates.
            // The following replaces the existing authors list.
            book["Authors"] = new List<string> { "Author x", "Author y" };
            book["newAttribute"] = "New Value";
            book["ISBN"] = null; // Remove it.

            // Optional parameters.
            UpdateItemOperationConfig config = new UpdateItemOperationConfig
            {
                // Gets updated item in response.
                ReturnValues = ReturnValues.AllNewAttributes,
            };

            Document updatedBook = await productCatalog.UpdateItemAsync(book, config);
            Console.WriteLine("UpdateMultipleAttributes: Printing item after updates ...");
            PrintDocument(client, updatedBook);
        }

        /// <summary>
        /// Updates a book item if it meets the specified criteria.
        /// </summary>
        /// <param name="client">An initialized DynamoDB client object.</param>
        /// <param name="productCatalog">A DynamoDB table object.</param>
        /// <param name="sampleBookId">An integer value representing the book's ID.</param>
        public static async void UpdateBookPriceConditionally(
          AmazonDynamoDBClient client,
          Table productCatalog,
          int sampleBookId)
        {
            Console.WriteLine("\n*** Executing UpdateBookPriceConditionally() ***");

            int partitionKey = sampleBookId;

            var book = new Document();
            book["Id"] = partitionKey;
            book["Price"] = 29.99;

            // For conditional price update, creating a condition expression.
            Expression expr = new Expression();
            expr.ExpressionStatement = "Price = :val";
            expr.ExpressionAttributeValues[":val"] = 19.00;

            // Optional parameters.
            UpdateItemOperationConfig config = new UpdateItemOperationConfig
            {
                ConditionalExpression = expr,
                ReturnValues = ReturnValues.AllNewAttributes,
            };

            Document updatedBook = await productCatalog.UpdateItemAsync(book, config);
            Console.WriteLine("UpdateBookPriceConditionally: Printing item whose price was conditionally updated");
            PrintDocument(client, updatedBook);
        }

        /// <summary>
        /// Deletes the book with the supplied Id value from the DynamoDB table
        /// ProductCatalog.
        /// </summary>
        /// <param name="client">An initialized DynamoDB client object.</param>
        /// <param name="productCatalog">A DynamoDB table object.</param>
        /// <param name="sampleBookId">An integer value representing the book's ID.</param>
        public static async Task DeleteBook(
          AmazonDynamoDBClient client,
          Table productCatalog,
          int sampleBookId)
        {
            Console.WriteLine("\n*** Executing DeleteBook() ***");

            // Optional configuration.
            DeleteItemOperationConfig config = new DeleteItemOperationConfig
            {
                // Returns the deleted item.
                ReturnValues = ReturnValues.AllOldAttributes,
            };
            Document document = await productCatalog.DeleteItemAsync(sampleBookId, config);
            Console.WriteLine("DeleteBook: Printing deleted just deleted...");

            PrintDocument(client, document);
        }

        /// <summary>
        /// Prints the information for the supplied DynamoDB document.
        /// </summary>
        /// <param name="client">An initialized DynamoDB client object.</param>
        /// <param name="updatedDocument">A DynamoDB document object.</param>
        public static void PrintDocument(AmazonDynamoDBClient client, Document updatedDocument)
        {
            if (updatedDocument is null)
            {
                return;
            }

            foreach (var attribute in updatedDocument.GetAttributeNames())
            {
                string stringValue = null;
                var value = updatedDocument[attribute];

                if (value is null)
                {
                    continue;
                }

                if (value is Primitive)
                {
                    stringValue = value.AsPrimitive().Value.ToString();
                }
                else if (value is PrimitiveList)
                {
                    stringValue = string.Join(",", (from primitive
                      in value.AsPrimitiveList().Entries
                                                    select primitive.Value).ToArray());
                }

                Console.WriteLine($"{attribute} - {stringValue}", attribute, stringValue);
            }
        }
    }

    // snippet-end:[dynamodb.dotnet35.MidlevelItemCRUDExample]
}
