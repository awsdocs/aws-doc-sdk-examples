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
// snippet-start:[dynamodb.dotNET.CodeExample.05_ReadingItem] 
using System;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.DocumentModel;

namespace DynamoDB_intro
{
  public static partial class DdbIntro
  {
      public static async Task<Document> ReadingMovie_async(int year, string title)
    {
      // Create Primitives for the HASH and RANGE portions of the primary key
      Primitive hash = new Primitive(year.ToString(), true);
      Primitive range = new Primitive(title, false);

      try
      {
        var movieItem = await MoviesTable.GetItemAsync(hash, range, Token);
        return movieItem;
      }
      catch (Exception)
      {
          return null;
      }
    }
  }
}
// snippet-end:[dynamodb.dotNET.CodeExample.05_ReadingItem]