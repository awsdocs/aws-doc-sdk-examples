// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.dotNET.CodeExample.06_UpdatingItem] 
using System;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.Model;

namespace DynamoDB_intro
{
    public static partial class DdbIntro
    {
        public static async Task<bool> UpdatingMovie_async(UpdateItemRequest updateRequest)
        {
            var result = false;

            try
            {
                await Client.UpdateItemAsync(updateRequest);
                result = true;
            }
            catch (Exception)
            {
                result = false;
            }

            return result;
        }
    }
}
// snippet-end:[dynamodb.dotNET.CodeExample.06_UpdatingItem]
