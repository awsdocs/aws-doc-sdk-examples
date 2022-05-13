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
// snippet-start:[dynamodb.dotNET.CodeExample.07_DeletingItem] 
using System;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.DocumentModel;

namespace DynamoDB_intro
{
  public static partial class DdbIntro
  {
    public static async Task<bool> DeletingItem_async( Table table, int year, string title,
                                                       Expression condition=null )
    {
      Document deletedItem;
      // Create Primitives for the HASH and RANGE portions of the primary key
      Primitive hash = new Primitive(year.ToString(), true);
      Primitive range = new Primitive(title, false);
      DeleteItemOperationConfig deleteConfig = new DeleteItemOperationConfig( );
      deleteConfig.ConditionalExpression = condition;
      deleteConfig.ReturnValues = ReturnValues.AllOldAttributes;

      Console.WriteLine( "  -- Trying to delete the {0} movie \"{1}\"...", year, title );
      try
      {
        var delItem = table.DeleteItemAsync( hash, range, deleteConfig );
        deletedItem = await delItem;
      }
      catch( Exception ex )
      {
        Console.WriteLine( "     FAILED to delete the movie item, for this reason:\n       {0}\n", ex.Message );
        return false;
      }

      Console.WriteLine( "     -- SUCCEEDED in deleting the movie record that looks like this:\n" +
                            deletedItem.ToJsonPretty( ) );
     return true;
    }
  }
}
// snippet-end:[dynamodb.dotNET.CodeExample.07_DeletingItem]