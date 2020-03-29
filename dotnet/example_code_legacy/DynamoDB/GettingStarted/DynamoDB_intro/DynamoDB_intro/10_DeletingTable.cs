// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.10_DeletingTable] 

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

namespace DynamoDB_intro
{
  public static partial class Ddb_Intro
  {
    /*--------------------------------------------------------------------------
     *                DeletingTable_async
     *--------------------------------------------------------------------------*/
    public static async Task<bool> DeletingTable_async( string tableName )
    {
      operationSucceeded = false;
      operationFailed = false;

      Console.WriteLine( "  -- Trying to delete the table named \"{0}\"...", tableName );
      pause( );
      Task tblDelete = client.DeleteTableAsync( tableName );
      try
      {
        await tblDelete;
      }
      catch( Exception ex )
      {
        Console.WriteLine( "     ERROR: Failed to delete the table, because:\n            " + ex.Message );
        operationFailed = true;
        return ( false );
      }
      Console.WriteLine( "     -- Successfully deleted the table!" );
      operationSucceeded = true;
      pause( );
      return ( true );
    }
  }
}// snippet-end:[dynamodb.dotNET.CodeExample.10_DeletingTable]