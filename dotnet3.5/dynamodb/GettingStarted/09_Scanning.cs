// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[dynamodb.dotnet35.09_Scanning]
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Amazon.DynamoDBv2.Model;

namespace GettingStarted
{
    public static partial class DdbIntro
    {
        /*--------------------------------------------------------------------------
         *                             ClientScanning_async
         *--------------------------------------------------------------------------*/
        public static async Task<bool> ClientScanning_async(ScanRequest sRequest)
        {
            OperationSucceeded = false;
            OperationFailed = false;

            ScanResponse sResponse;
            Task<ScanResponse> clientScan = Client.ScanAsync(sRequest);

            try
            {
                sResponse = await clientScan;
            }
            catch (Exception ex)
            {
                Console.WriteLine("     -- FAILED to retrieve the movies, because:\n        {0}", ex.Message);
                OperationFailed = true;
                Pause();
                return (false);
            }

            Console.WriteLine("     -- The low-level scan succeeded, and returned {0} movies!", sResponse.Items.Count);

            if (!Pause())
            {
                OperationFailed = true;
                return (false);
            }

            Console.WriteLine("         Here are the movies retrieved:\n" +
                              "         --------------------------------------------------------------------------");
            foreach (Dictionary<string, AttributeValue> item in sResponse.Items)
                ShowMovieAttrsShort(item);

            Console.WriteLine("     -- Retrieved {0} movies.", sResponse.Items.Count);
            OperationSucceeded = true;
            return (true);
        }
    }
}
// snippet-end:[dynamodb.dotnet35.09_Scanning]