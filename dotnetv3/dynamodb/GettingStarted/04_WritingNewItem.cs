// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.04_WritingNewItem]
using System;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.DocumentModel;

namespace GettingStarted
{
    public static partial class DdbIntro
    {
        public static async Task WritingNewMovie_async(Document newItem)
        {
            OperationSucceeded = false;
            OperationFailed = false;

            int year = (int)newItem["year"];
            string name = newItem["title"];

            if (await ReadingMovie_async(year, name, false))
                Console.WriteLine("  The {0} movie \"{1}\" is already in the Movies table...\n" +
                                  "  -- No need to add it again... its info is as follows:\n{2}",
                    year, name, MovieRecord.ToJsonPretty());
            else
            {
                try
                {
                    Task<Document> writeNew = MoviesTable.PutItemAsync(newItem, Token);
                    Console.WriteLine("  -- Writing a new movie to the Movies table...");
                    await writeNew;
                    Console.WriteLine("      -- Wrote the item successfully!");
                    OperationSucceeded = true;
                }
                catch (Exception ex)
                {
                    Console.WriteLine("      FAILED to write the new movie, because:\n       {0}.", ex.Message);
                    OperationFailed = true;
                }
            }
        }
    }
}
// snippet-end:[dynamodb.dotnet35.04_WritingNewItem]