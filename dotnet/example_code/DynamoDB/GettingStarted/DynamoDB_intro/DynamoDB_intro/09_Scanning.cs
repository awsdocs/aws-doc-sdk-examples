// snippet-sourcedescription:[09_Scanning.cs demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.d614fdaf-83ff-4f2d-9795-08dcba3a3ffd] 

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
using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.Model;
using Amazon.DynamoDBv2.DocumentModel;

namespace DynamoDB_intro
{
  public static partial class Ddb_Intro
  {
    /*--------------------------------------------------------------------------
     *                             ClientScanning_async
     *--------------------------------------------------------------------------*/
    public static async Task<bool> ClientScanning_async( ScanRequest sRequest )
    {
      operationSucceeded = false;
      operationFailed = false;

      ScanResponse sResponse;
      Task<ScanResponse> clientScan = client.ScanAsync(sRequest);
      try
      {
        sResponse = await clientScan;
      }
      catch( Exception ex )
      {
        Console.WriteLine( "     -- FAILED to retrieve the movies, because:\n        {0}", ex.Message );
        operationFailed = true;
        pause( );
        return( false );
      }
      Console.WriteLine( "     -- The low-level scan succeeded, and returned {0} movies!", sResponse.Items.Count );
      if( !pause( ) )
      {
        operationFailed = true;
        return ( false );
      }

      Console.WriteLine( "         Here are the movies retrieved:\n" +
                         "         --------------------------------------------------------------------------" );
      foreach( Dictionary<string, AttributeValue> item in sResponse.Items )
        showMovieAttrsShort( item );

      Console.WriteLine( "     -- Retrieved {0} movies.", sResponse.Items.Count );
      operationSucceeded = true;
      return ( true );
    }
  }
}// snippet-end:[dynamodb.dotNET.CodeExample.d614fdaf-83ff-4f2d-9795-08dcba3a3ffd]