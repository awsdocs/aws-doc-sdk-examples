// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.06_UpdatingItem]
using System;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.Model;

namespace GettingStarted
{
    public static partial class DdbIntro
    {
        public static async Task<bool> UpdatingMovie_async(UpdateItemRequest updateRequest, bool report)
        {
            UpdateItemResponse updateResponse = null;

            OperationSucceeded = false;
            OperationFailed = false;

            if (report)
            {
                Console.WriteLine("  -- Trying to update a movie item...");
                updateRequest.ReturnValues = "ALL_NEW";
            }

            try
            {
                updateResponse = await Client.UpdateItemAsync(updateRequest);
                Console.WriteLine("     -- SUCCEEDED in updating the movie item!");
            }
            catch (Exception ex)
            {
                Console.WriteLine("     -- FAILED to update the movie item, because:\n       {0}.", ex.Message);

                if (updateResponse != null)
                    Console.WriteLine("     -- The status code was " + updateResponse.HttpStatusCode.ToString());
                OperationFailed = true; return (false);
            }

            if (report)
            {
                Console.WriteLine("     Here is the updated movie information:");
                Console.WriteLine(MovieAttributesToJson(updateResponse.Attributes));
            }

            OperationSucceeded = true;
            return (true);
        }
    }
}
// snippet-end:[dynamodb.dotnet35.06_UpdatingItem]