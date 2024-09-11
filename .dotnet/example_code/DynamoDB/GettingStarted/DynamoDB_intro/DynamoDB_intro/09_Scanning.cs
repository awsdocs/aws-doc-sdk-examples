// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.dotNET.CodeExample.09_Scanning]
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.Model;

namespace DynamoDB_intro
{
  public static partial class DdbIntro
  {
    public static async Task<ScanResponse> ClientScanning_async(ScanRequest sRequest)
    {
      var response = await Client.ScanAsync(sRequest);
      return response;
    }
  }
}
// snippet-end:[dynamodb.dotNET.CodeExample.09_Scanning]