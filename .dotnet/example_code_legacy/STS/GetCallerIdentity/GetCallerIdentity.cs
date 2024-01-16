// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using System;
using Amazon.SecurityToken;
using Amazon.SecurityToken.Model;
using System.Threading.Tasks;
namespace AssumeRoleExample
{
    class AssumeRole
    {
        static void Main(string[] args)
        {
            var client = new Amazon.SecurityToken.AmazonSecurityTokenServiceClient();
            var getCallerIdReq = new GetCallerIdentityRequest();
            var caller = GetCallerIdentityResponseAsync(client: client, request: getCallerIdReq);
            Console.WriteLine("Caller Identity ARN: " + caller.Result.Arn.ToString());

        }

        static async Task<GetCallerIdentityResponse> GetCallerIdentityResponseAsync(AmazonSecurityTokenServiceClient client, GetCallerIdentityRequest request)
        {
            var caller = await client.GetCallerIdentityAsync(request);
            return caller;
        }
    }
}
