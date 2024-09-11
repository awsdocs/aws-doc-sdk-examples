// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.dotNET.CodeExample.10_DeletingTable]
using System;
using System.Threading.Tasks;

namespace DynamoDB_intro
{
  public static partial class DdbIntro
  {
    public static async Task<bool> DeletingTable_async(string tableName)
    {
      var tblDelete = await Client.DeleteTableAsync(tableName);
      return true;
    }
  }
}
// snippet-end:[dynamodb.dotNET.CodeExample.10_DeletingTable]