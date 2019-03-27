//snippet-sourcedescription:[AssumeRole.cs demonstrates how to get temporary credentials using the AWS STS client.]
//snippet-keyword:[dotnet]
//snippet-keyword:[.NET]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Security Token Service (STS)]
//snippet-service:[sts]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-03-26]
//snippet-sourceauthor:[walkerk1980]
// snippet-start:[sts.dotNET.CodeExample.AssumeRoleWithSAML] 
/*******************************************************************************
* Copyright 2009-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
using System.IO;
using System.Text;
using Amazon.SecurityToken;
using Amazon.SecurityToken.Model;
using System.Threading.Tasks;
namespace AssumeRoleWithSAMLExample
{
    class AssumeRole
    {
        static void Main(string[] args)
        {
            var roleArnToAssume = "arn:aws:iam::123456789012:role/testAssumeRole";
            var principalArn = "arn:aws:iam::123456789012:saml-provider/testSamlProvider";

            string base64SamlFile = "saml.xml.b64";

	    if (File.Exists(base64SamlFile))
            {
                string samlAssertion = File.ReadAllText(base64SamlFile);

                var stsClient1 = new Amazon.SecurityToken.AmazonSecurityTokenServiceClient();

                var assumeRoleReq = new AssumeRoleWithSAMLRequest();
                assumeRoleReq.DurationSeconds = 3600;
                assumeRoleReq.RoleArn = roleArnToAssume;
                assumeRoleReq.PrincipalArn = principalArn;
                assumeRoleReq.SAMLAssertion = samlAssertion;

                var assumeRoleRes = GetAssumeRoleWithSAMLResponseAsync(client: stsClient1, request: assumeRoleReq);

                var stsClient2 = new Amazon.SecurityToken.AmazonSecurityTokenServiceClient(credentials: assumeRoleRes.Result.Credentials);
                var getCallerIdReq = new GetCallerIdentityRequest();
                var assumedRoleIdentity = GetCallerIdentityResponseAsync(client: stsClient2, request: getCallerIdReq);
                Console.WriteLine("AssumedRole Caller: " + assumedRoleIdentity.Result.Arn.ToString());
            }
            else
            {
                Console.WriteLine("Base64 Encoded SAML File: " + base64SamlFile + " does not exist in this directory.");
            }
        }

        static async Task<GetCallerIdentityResponse> GetCallerIdentityResponseAsync(AmazonSecurityTokenServiceClient client, GetCallerIdentityRequest request)
        {
            var caller = await client.GetCallerIdentityAsync(request);
            return caller;
        }

        static async Task<AssumeRoleWithSAMLResponse> GetAssumeRoleWithSAMLResponseAsync(AmazonSecurityTokenServiceClient client, AssumeRoleWithSAMLRequest request)
        {
            var response = await client.AssumeRoleWithSAMLAsync(request);
            return response;
        }
    }
}
// snippet-end:[sts.dotNET.CodeExample.AssumeRoleWithSAML] 
