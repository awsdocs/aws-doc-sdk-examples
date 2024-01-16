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
            var roleArnToAssume = "arn:aws:iam::123456789012:role/testAssumeRole";

            var client = new Amazon.SecurityToken.AmazonSecurityTokenServiceClient();
            var getCallerIdReq = new GetCallerIdentityRequest();
            var caller = GetCallerIdentityResponseAsync(client: client, request: getCallerIdReq);
            Console.WriteLine("Original Caller: " + caller.Result.Arn.ToString());

            var assumeRoleReq = new AssumeRoleRequest();
            assumeRoleReq.DurationSeconds = 1600;
            assumeRoleReq.RoleSessionName = "Session1";
            assumeRoleReq.RoleArn = roleArnToAssume;
            var assumeRoleRes = GetAssumeRoleResponseAsync(client: client, request: assumeRoleReq);

            var client2 = new Amazon.SecurityToken.AmazonSecurityTokenServiceClient(credentials: assumeRoleRes.Result.Credentials);
            var caller2 = GetCallerIdentityResponseAsync(client: client2, request: getCallerIdReq);
            Console.WriteLine("AssumedRole Caller: " + caller2.Result.Arn.ToString());
        }

        static async Task<GetCallerIdentityResponse> GetCallerIdentityResponseAsync(AmazonSecurityTokenServiceClient client, GetCallerIdentityRequest request)
        {
            var caller = await client.GetCallerIdentityAsync(request);
            return caller;
        }

        static async Task<AssumeRoleResponse> GetAssumeRoleResponseAsync(AmazonSecurityTokenServiceClient client, AssumeRoleRequest request)
        {
            var response = await client.AssumeRoleAsync(request);
            return response;
        }
    }
}
