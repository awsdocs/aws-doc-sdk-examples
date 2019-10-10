// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.05_ReadingItem] 

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
     *                             ReadingMovie_async
     *--------------------------------------------------------------------------*/
    public static async Task<bool> ReadingMovie_async( int year, string title, bool report )
    {
      // Create Primitives for the HASH and RANGE portions of the primary key
      Primitive hash = new Primitive(year.ToString(), true);
      Primitive range = new Primitive(title, false);

      operationSucceeded = false;
      operationFailed = false;
      try
      {
        Task<Document> readMovie = moviesTable.GetItemAsync(hash, range, token);
        if( report )
          Console.WriteLine( "  -- Reading the {0} movie \"{1}\" from the Movies table...", year, title );
        movie_record = await readMovie;
        if( movie_record == null )
        {
          if( report )
            Console.WriteLine( "     -- Sorry, that movie isn't in the Movies table." );
          return ( false );
        }
        else
        {
          if( report )
            Console.WriteLine( "     -- Found it!  The movie record looks like this:\n" +
                                movie_record.ToJsonPretty( ) );
          operationSucceeded = true;
          return ( true );
        }
      }
      catch( Exception ex )
      {
        Console.WriteLine( "     FAILED to get the movie, because: {0}.", ex.Message );
        operationFailed = true;
      }
      return ( false );
    }
  }
}// snippet-end:[dynamodb.dotNET.CodeExample.05_ReadingItem]