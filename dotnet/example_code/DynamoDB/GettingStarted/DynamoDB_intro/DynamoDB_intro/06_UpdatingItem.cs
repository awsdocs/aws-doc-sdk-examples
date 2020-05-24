// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.06_UpdatingItem] 

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
using System.Collections.Generic;
using System.Text;
using Amazon.DynamoDBv2.Model;

namespace DynamoDB_intro
{
  public static partial class Ddb_Intro
  {
    /*--------------------------------------------------------------------------
     *                             UpdatingMovie_async
     *--------------------------------------------------------------------------*/
    public static async Task<bool> UpdatingMovie_async( UpdateItemRequest updateRequest, bool report )
    {
      UpdateItemResponse updateResponse = null;

      operationSucceeded = false;
      operationFailed = false;
      if( report )
      {
        Console.WriteLine( "  -- Trying to update a movie item..." );
        updateRequest.ReturnValues = "ALL_NEW";
      }

      try
      {
        updateResponse = await client.UpdateItemAsync( updateRequest );
        Console.WriteLine( "     -- SUCCEEDED in updating the movie item!" );
      }
      catch( Exception ex )
      {
        Console.WriteLine( "     -- FAILED to update the movie item, because:\n       {0}.", ex.Message );
        if( updateResponse != null )
          Console.WriteLine( "     -- The status code was " + updateResponse.HttpStatusCode.ToString( ) );
        operationFailed = true;return ( false );
      }
      if( report )
      {
        Console.WriteLine( "     Here is the updated movie informtion:" );
        Console.WriteLine( movieAttributesToJson( updateResponse.Attributes ) );
      }
      operationSucceeded = true;
      return ( true );
    }
  }
}

// snippet-end:[dynamodb.dotNET.CodeExample.06_UpdatingItem]