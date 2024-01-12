// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[sts.dotNET.CodeExample.AssumeRoleWithSAML] 

using System;
using System.IO;
using System.Text;
using Amazon.SecurityToken;
using Amazon.SecurityToken.Model;
using System.Threading.Tasks;
using Amazon.Runtime;

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

                var stsClient1 = new Amazon.SecurityToken.AmazonSecurityTokenServiceClient(new AnonymousAWSCredentials());

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
