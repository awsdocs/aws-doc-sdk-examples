// snippet-sourcedescription:[07_DeletingItem.cs demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[dotNET]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.dotNET.CodeExample.07_DeletingItem] 

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
     *                       DeletingItem_async
     *--------------------------------------------------------------------------*/
    public static async Task<bool> DeletingItem_async( Table table, int year, string title,
                                                       Expression condition=null )
    {
      Document deletedItem = null;
      operationSucceeded = false;
      operationFailed = false;

      // Create Primitives for the HASH and RANGE portions of the primary key
      Primitive hash = new Primitive(year.ToString(), true);
      Primitive range = new Primitive(title, false);
      DeleteItemOperationConfig deleteConfig = new DeleteItemOperationConfig( );
      deleteConfig.ConditionalExpression = condition;
      deleteConfig.ReturnValues = ReturnValues.AllOldAttributes;

      Console.WriteLine( "  -- Trying to delete the {0} movie \"{1}\"...", year, title );
      try
      {
        Task<Document> delItem = table.DeleteItemAsync( hash, range, deleteConfig );
        deletedItem = await delItem;
      }
      catch( Exception ex )
      {
        Console.WriteLine( "     FAILED to delete the movie item, for this reason:\n       {0}\n", ex.Message );
        operationFailed = true;
        return ( false );
      }
      Console.WriteLine( "     -- SUCCEEDED in deleting the movie record that looks like this:\n" +
                            deletedItem.ToJsonPretty( ) );
      operationSucceeded = true;
      return ( true );
    }
  }
}// snippet-end:[dynamodb.dotNET.CodeExample.07_DeletingItem]