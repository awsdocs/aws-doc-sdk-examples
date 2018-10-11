 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


/*******************************************************************************
* Copyright 2009-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License"). You may
* not use this file except in compliance with the License. A copy of the
* License is located at
*
* http://aws.amazon.com/apache2.0/
*
* or in the "license" file accompanying this file. This file is
* distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the specific
* language governing permissions and limitations under the License.
*******************************************************************************/

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
