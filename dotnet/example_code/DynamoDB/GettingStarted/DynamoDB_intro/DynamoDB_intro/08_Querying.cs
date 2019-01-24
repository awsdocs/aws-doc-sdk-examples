// snippet-sourcedescription:[08_Querying.cs demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.08_Querying] 

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
using System.Text;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.Model;
using Amazon.DynamoDBv2.DocumentModel;
using System.Collections.Generic;

namespace DynamoDB_intro
{
  public static partial class Ddb_Intro
  {
    /*--------------------------------------------------------------------------
     *                             SearchListing_async
     *--------------------------------------------------------------------------*/
    public static async Task<bool> SearchListing_async( Search search )
    {
      int i = 0;
      List<Document> docList = new List<Document>( );

      Console.WriteLine( "         Here are the movies retrieved:\n" +
                         "         --------------------------------------------------------------------------" );
      Task<List<Document>> getNextBatch;
      operationSucceeded = false;
      operationFailed = false;

      do
      {
        try
        {
          getNextBatch = search.GetNextSetAsync( );
          docList = await getNextBatch;
        }
        catch( Exception ex )
        {
          Console.WriteLine( "        FAILED to get the next batch of movies from Search! Reason:\n          " +
                             ex.Message );
          operationFailed = true;
          return ( false );
        }

        foreach( Document doc in docList )
        {
          i++;
          showMovieDocShort( doc );
        }
      } while( !search.IsDone );
      Console.WriteLine( "     -- Retrieved {0} movies.", i );
      operationSucceeded = true;
      return ( true );
    }


    /*--------------------------------------------------------------------------
     *                             ClientQuerying_async
     *--------------------------------------------------------------------------*/
    public static async Task<bool> ClientQuerying_async( QueryRequest qRequest )
    {
      operationSucceeded = false;
      operationFailed = false;

      QueryResponse qResponse;
      try
      {
        Task<QueryResponse> clientQueryTask = client.QueryAsync( qRequest );
        qResponse = await clientQueryTask;
      }
      catch( Exception ex )
      {
        Console.WriteLine( "      The low-level query FAILED, because:\n       {0}.", ex.Message );
        operationFailed = true;
        return ( false );
      }
      Console.WriteLine( "     -- The low-level query succeeded, and returned {0} movies!", qResponse.Items.Count );
      if( !pause( ) )
      {
        operationFailed = true;
        return ( false );
      }
      Console.WriteLine( "         Here are the movies retrieved:" +
                         "         --------------------------------------------------------------------------" );
      foreach( Dictionary<string, AttributeValue> item in qResponse.Items )
        showMovieAttrsShort( item );

      Console.WriteLine( "     -- Retrieved {0} movies.", qResponse.Items.Count );
      operationSucceeded = true;
      return ( true );
    }
  }
}// snippet-end:[dynamodb.dotNET.CodeExample.08_Querying]