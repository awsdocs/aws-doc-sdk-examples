// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.05_ReadingItem]
using System;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.DocumentModel;

namespace GettingStarted
{
    public static partial class DdbIntro
    {
        public static async Task<bool> ReadingMovie_async(int year, string title, bool report)
        {
            // Create Primitives for the HASH and RANGE portions of the primary key
            Primitive hash = new Primitive(year.ToString(), true);
            Primitive range = new Primitive(title, false);

            OperationSucceeded = false;
            OperationFailed = false;

            try
            {
                Task<Document> readMovie = MoviesTable.GetItemAsync(hash, range, Token);

                if (report)
                    Console.WriteLine("  -- Reading the {0} movie \"{1}\" from the Movies table...", year, title);
                MovieRecord = await readMovie;

                if (MovieRecord == null)
                {
                    if (report)
                        Console.WriteLine("     -- Sorry, that movie isn't in the Movies table.");
                    return (false);
                }
                else
                {
                    if (report)
                        Console.WriteLine("     -- Found it!  The movie record looks like this:\n" +
                                          MovieRecord.ToJsonPretty());

                    OperationSucceeded = true;
                    return (true);
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("     FAILED to get the movie, because: {0}.", ex.Message);
                OperationFailed = true;
            }

            return (false);
        }
    }
}
// snippet-end:[dynamodb.dotnet35.05_ReadingItem]