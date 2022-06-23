// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace CreatePolicyExample
{
    using System;
    using System.Threading.Tasks;
    using Amazon.IdentityManagement;
    using Amazon.IdentityManagement.Model;

    /// <summary>
    /// Shows how to create an AWS Identity and Access Management (IAM) Policy.
    /// The example was created using the AWS SDK for .NET version 3.7 and
    /// .NET Core 5.0.
    /// </summary>
    public class CreatePolicy
    {
        /// <summary>
        /// Initializes an IAM Client object and then calls CreatePolicyAsync
        /// to create the policy.
        /// </summary>
        public static async Task Main()
        {
            // Represents json code for AWS full access policy for Amazon Simple
            // Storage Service (Amazon S3).
            string s3FullAccessPolicy = "{" +
                "	\"Statement\" : [{" +
                    "	\"Action\" : [\"s3:*\"]," +
                    "	\"Effect\" : \"Allow\"," +
                    "	\"Resource\" : \"*\"" +
                "}]" +
            "}";

            string policyName = "S3FullAccess";

            var client = new AmazonIdentityManagementServiceClient();
            var response = await client.CreatePolicyAsync(new CreatePolicyRequest
            {
                PolicyDocument = s3FullAccessPolicy,
                PolicyName = policyName,
            });

            if (response is not null)
            {
                var policy = response.Policy;
                Console.WriteLine($"{policy.PolicyName} created with ID: {policy.PolicyId}.")
            }
            else
            {
                Console.WriteLine("Coultn't create policy.");
            }
        }
    }
}
