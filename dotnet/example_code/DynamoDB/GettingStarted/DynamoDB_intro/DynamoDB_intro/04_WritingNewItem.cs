// snippet-sourcedescription:[04_WritingNewItem.cs demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.04_WritingNewItem] 

/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/
using System;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.DocumentModel;

namespace DynamoDB_intro
{
  public static partial class Ddb_Intro
  {
    /*--------------------------------------------------------------------------
     *     WritingNewMovie
     *--------------------------------------------------------------------------*/
    public static async Task WritingNewMovie_async( Document newItem )
    {
      operationSucceeded = false;
      operationFailed = false;

      int year = (int) newItem["year"];
      string name = newItem["title"];

      if( await ReadingMovie_async( year, name, false ) )
        Console.WriteLine( "  The {0} movie \"{1}\" is already in the Movies table...\n" +
                           "  -- No need to add it again... its info is as follows:\n{2}",
                           year, name, movie_record.ToJsonPretty( ) );
      else
      {
        try
        {
          Task<Document> writeNew = moviesTable.PutItemAsync(newItem, token);
          Console.WriteLine("  -- Writing a new movie to the Movies table...");
          await writeNew;
          Console.WriteLine("      -- Wrote the item successfully!");
          operationSucceeded = true;
        }
        catch (Exception ex)
        {
          Console.WriteLine("      FAILED to write the new movie, because:\n       {0}.", ex.Message);
          operationFailed = true;
        }
      }
    }
  }
}// snippet-end:[dynamodb.dotNET.CodeExample.04_WritingNewItem]