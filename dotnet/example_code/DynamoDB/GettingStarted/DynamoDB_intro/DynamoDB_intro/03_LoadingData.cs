// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.03_LoadingData] 

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
using System.IO;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.DocumentModel;

using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace DynamoDB_intro
{
  public static partial class Ddb_Intro
  {

    /*--------------------------------------------------------------------------
     *     LoadingData_async
     *--------------------------------------------------------------------------*/
    public static async Task LoadingData_async( Table table, string filePath )
    {
      JArray movieArray;

      movieArray = await ReadJsonMovieFile_async( filePath );
      if( movieArray != null )
        await LoadJsonMovieData_async( table, movieArray );
    }

    /*--------------------------------------------------------------------------
     *                             ReadJsonMovieFile_async
     *--------------------------------------------------------------------------*/
    public static async Task<JArray> ReadJsonMovieFile_async( string JsonMovieFilePath )
    {
      StreamReader sr = null;
      JsonTextReader jtr = null;
      JArray movieArray = null;

      Console.WriteLine( "  -- Reading the movies data from a JSON file..." );
      operationSucceeded = false;
      operationFailed = false;
      try
      {
        sr = new StreamReader( JsonMovieFilePath );
        jtr = new JsonTextReader( sr );
        movieArray = (JArray) await JToken.ReadFromAsync( jtr );
        operationSucceeded = true;
      }
      catch( Exception ex )
      {
        Console.WriteLine( "     ERROR: could not read the file!\n          Reason: {0}.", ex.Message );
        operationFailed = true;
      }
      finally
      {
        if( jtr != null )
          jtr.Close( );
        if( sr != null )
          sr.Close( );
      }
      if( operationSucceeded )
      {
        Console.WriteLine( "     -- Succeeded in reading the JSON file!" );
        return ( movieArray );
      }
      return ( null );
    }


    /*--------------------------------------------------------------------------
     *                LoadJsonMovieData_async
     *--------------------------------------------------------------------------*/
    public static async Task LoadJsonMovieData_async( Table moviesTable, JArray moviesArray )
    {
      operationSucceeded = false;
      operationFailed = false;

      int n = moviesArray.Count;
      Console.Write( "     -- Starting to load {0:#,##0} movie records into the Movies table asynchronously...\n" + "" +
        "        Wrote: ", n );
      for( int i = 0, j = 99; i < n; i++ )
      {
        try
        {
          string itemJson = moviesArray[i].ToString();
          Document doc = Document.FromJson(itemJson);
          Task putItem = moviesTable.PutItemAsync(doc);
          if( i >= j )
          {
            j++;
            Console.Write( "{0,5:#,##0}, ", j );
            if( j % 1000 == 0 )
              Console.Write( "\n               " );
            j += 99;
          }
          await putItem;
        }
        catch( Exception ex )
        {
          Console.WriteLine( "\n     ERROR: Could not write the movie record #{0:#,##0}, because:\n       {1}",
                             i, ex.Message );
          operationFailed = true;
          break;
        }
      }
      if( !operationFailed )
      {
        operationSucceeded = true;
        Console.WriteLine( "\n     -- Finished writing all movie records to DynamoDB!" );
      }
    }
  }
}// snippet-end:[dynamodb.dotNET.CodeExample.03_LoadingData]