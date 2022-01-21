// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.04_WritingNewItem] 
using System;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.DocumentModel;

namespace DynamoDB_intro
{
    public static partial class DdbIntro
    {
        public static async Task<bool> CheckingForMovie_async(Document newItem)
        {
            int year = (int)newItem["year"];
            string name = newItem["title"];

            var response = await ReadingMovie_async(year, name);

            return response.Count > 0;
        }

        public static async Task<bool> WritingNewMovie_async(Document newItem)
        {
            var result = false;

            try
            {
                var writeNew = await MoviesTable.PutItemAsync(newItem);
                Console.WriteLine("  -- Writing a new movie to the Movies table...");

                Console.WriteLine("      -- Wrote the item successfully!");
                result = true;
            }
            catch (Exception ex)
            {
                Console.WriteLine("      FAILED to write the new movie, because:\n       {0}.", ex.Message);
            }

            return result;
        }
    }
}
// snippet-end:[dynamodb.dotNET.CodeExample.04_WritingNewItem]