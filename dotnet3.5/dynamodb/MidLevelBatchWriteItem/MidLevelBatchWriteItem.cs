// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.MidLevelBatchWriteItem]
using System;
using System.Collections.Generic;

using Amazon.DynamoDBv2;
using Amazon.DynamoDBv2.DocumentModel;

namespace DynamoDBCRUD
{
    public class MidLevelBatchWriteItem
    {
        public static async void SingleTableBatchWrite(AmazonDynamoDBClient client)
        {
            Table productCatalog = Table.LoadTable(client, "ProductCatalog");
            var batchWrite = productCatalog.CreateBatchWrite();

            var book1 = new Document();
            book1["Id"] = 902;
            book1["Title"] = "My book1 in batch write using .NET helper classes";
            book1["ISBN"] = "902-11-11-1111";
            book1["Price"] = 10;
            book1["ProductCategory"] = "Book";
            book1["Authors"] = new List<string> { "Author 1", "Author 2", "Author 3" };
            book1["Dimensions"] = "8.5x11x.5";
            book1["InStock"] = new DynamoDBBool(true);
            book1["QuantityOnHand"] = new DynamoDBNull(); //Quantity is unknown at this time

            batchWrite.AddDocumentToPut(book1);
            // Specify delete item using overload that takes PK.
            batchWrite.AddKeyToDelete(12345);
            Console.WriteLine("Performing batch write in SingleTableBatchWrite()");
            await batchWrite.ExecuteAsync();
        }

        public static async void MultiTableBatchWrite(AmazonDynamoDBClient client)
        {
            // 1. Specify item to add in the Forum table.
            Table forum = Table.LoadTable(client, "Forum");
            var forumBatchWrite = forum.CreateBatchWrite();

            var forum1 = new Document();
            forum1["Name"] = "Test BatchWrite Forum";
            forum1["Threads"] = 0;
            forumBatchWrite.AddDocumentToPut(forum1);


            // 2a. Specify item to add in the Thread table.
            Table thread = Table.LoadTable(client, "Thread");
            var threadBatchWrite = thread.CreateBatchWrite();

            var thread1 = new Document();
            thread1["ForumName"] = "S3 forum";
            thread1["Subject"] = "My sample question";
            thread1["Message"] = "Message text";
            thread1["KeywordTags"] = new List<string> { "S3", "Bucket" };
            threadBatchWrite.AddDocumentToPut(thread1);

            // 2b. Specify item to delete from the Thread table.
            threadBatchWrite.AddKeyToDelete("someForumName", "someSubject");

            // 3. Create multi-table batch.
            var superBatch = new MultiTableDocumentBatchWrite();
            superBatch.AddBatch(forumBatchWrite);
            superBatch.AddBatch(threadBatchWrite);
            Console.WriteLine("Performing batch write in MultiTableBatchWrite()");
            await superBatch.ExecuteAsync();
        }

        static void Main(string[] args)
        {
            var client = new AmazonDynamoDBClient();
            SingleTableBatchWrite(client);
            MultiTableBatchWrite(client);
        }
    }
}
// snippet-end:[dynamodb.dotnet35.MidLevelBatchWriteItem]