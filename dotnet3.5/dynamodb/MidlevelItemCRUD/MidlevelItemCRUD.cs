// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.MidlevelItemCRUD]
using System;
using System.Collections.Generic;
using System.Linq;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DocumentModel;

namespace DynamoDBCRUD
{
    public class MidlevelItemCRUD
    {
        public static string _tableName = "ProductCatalog";
        public static int _sampleBookId = 555;

        public static Table LoadTable(AmazonDynamoDBClient client, string tableName)
        {
            Table productCatalog = Table.LoadTable(client, tableName);
            return productCatalog;
        }

        // Creates a sample book item.
        public static async void CreateBookItem(AmazonDynamoDBClient client, Table productCatalog)
        {
            Console.WriteLine("\n*** Executing CreateBookItem() ***");
            var book = new Document();
            book["Id"] = _sampleBookId;
            book["Title"] = "Book " + _sampleBookId;
            book["Price"] = 19.99;
            book["ISBN"] = "111-1111111111";
            book["Authors"] = new List<string> { "Author 1", "Author 2", "Author 3" };
            book["PageCount"] = 500;
            book["Dimensions"] = "8.5x11x.5";
            book["InPublication"] = new DynamoDBBool(true);
            book["InStock"] = new DynamoDBBool(false);
            book["QuantityOnHand"] = 0;

            await productCatalog.PutItemAsync(book);
        }

        public static async void RetrieveBook(AmazonDynamoDBClient client, Table productCatalog)
        {
            Console.WriteLine("\n*** Executing RetrieveBook() ***");
            // Optional configuration.
            GetItemOperationConfig config = new GetItemOperationConfig
            {
                AttributesToGet = new List<string> { "Id", "ISBN", "Title", "Authors", "Price" },
                ConsistentRead = true
            };
            Document document = await productCatalog.GetItemAsync(_sampleBookId, config);
            Console.WriteLine("RetrieveBook: Printing book retrieved...");
            PrintDocument(client, document);
        }

        public static async void UpdateMultipleAttributes(AmazonDynamoDBClient client, Table productCatalog)
        {
            Console.WriteLine("\n*** Executing UpdateMultipleAttributes() ***");
            Console.WriteLine("\nUpdating multiple attributes....");
            int partitionKey = _sampleBookId;

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
                // Get updated item in response.
                ReturnValues = ReturnValues.AllNewAttributes
            };
            Document updatedBook = await productCatalog.UpdateItemAsync(book, config);
            Console.WriteLine("UpdateMultipleAttributes: Printing item after updates ...");
            PrintDocument(client, updatedBook);
        }

        public static async void UpdateBookPriceConditionally(AmazonDynamoDBClient client, Table productCatalog)
        {
            Console.WriteLine("\n*** Executing UpdateBookPriceConditionally() ***");

            int partitionKey = _sampleBookId;

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
                ReturnValues = ReturnValues.AllNewAttributes
            };
            Document updatedBook = await productCatalog.UpdateItemAsync(book, config);
            Console.WriteLine("UpdateBookPriceConditionally: Printing item whose price was conditionally updated");
            PrintDocument(client, updatedBook);
        }

        public static async void DeleteBook(AmazonDynamoDBClient client, Table productCatalog)
        {
            Console.WriteLine("\n*** Executing DeleteBook() ***");
            // Optional configuration.
            DeleteItemOperationConfig config = new DeleteItemOperationConfig
            {
                // Return the deleted item.
                ReturnValues = ReturnValues.AllOldAttributes
            };
            Document document = await productCatalog.DeleteItemAsync(_sampleBookId, config);
            Console.WriteLine("DeleteBook: Printing deleted just deleted...");

            PrintDocument(client, document);
        }

        public static void PrintDocument(AmazonDynamoDBClient client, Document updatedDocument)
        {
            if (null == updatedDocument)
            {
                return;
            }

            foreach (var attribute in updatedDocument.GetAttributeNames())
            {
                string stringValue = null;
                var value = updatedDocument[attribute];

                if (null == value)
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

                Console.WriteLine("{0} - {1}", attribute, stringValue);
            }
        }

        static void Main()
        {
            var client = new AmazonDynamoDBClient();
            var productCatalog = LoadTable(client, _tableName);
        
            CreateBookItem(client, productCatalog);
            RetrieveBook(client, productCatalog);

            // Couple of sample updates.
            UpdateMultipleAttributes(client, productCatalog);
            UpdateBookPriceConditionally(client, productCatalog);

            // Delete.
            DeleteBook(client, productCatalog);
        }
    }
}
// snippet-end:[dynamodb.dotnet35.MidlevelItemCRUD]