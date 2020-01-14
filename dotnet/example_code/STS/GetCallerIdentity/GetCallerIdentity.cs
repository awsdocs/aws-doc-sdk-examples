//snippet-sourcedescription:[AssumeRole.cs demonstrates how to get caller identity using the AWS STS client.]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-sourcesyntax:[.net]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Security Token Service (STS)]
//snippet-service:[sts]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[walkerk1980]
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
