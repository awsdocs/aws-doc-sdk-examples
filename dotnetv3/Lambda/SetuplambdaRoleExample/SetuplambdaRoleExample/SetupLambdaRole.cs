// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace SetuplambdaRoleExample
{
    // snippet-start:[Lambda.dotnetv3.SetupLambdaRoleExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.IdentityManagement;
    using Amazon.IdentityManagement.Model;

    /// <summary>
    /// Creates an AWS Identity and Access Management (IAM) to attach to an
    /// Amazon Lambda function that will management an Amazon Simple Storage
    /// Service (Amazon S3) Bucket. The example was created using AWS SDK for
    /// .NET version 3.7 and .NET Core 5.0.
    /// </summary>
    public class SetupLambdaRole
    {
        /// <summary>
        /// Defines the policy for the IAM role and then creates the role.
        /// </summary>
        static async Task Main()
        {
            // Policy that allows reading and writing to a specific Amazon S3
            // Bucket. This policy will allow managing the bucket as well as
            // working with the objects in that bucket.
            string s3ManagementPolicy = "{" +
                "   \"Version\": \"2012-10-17\"," +
                "	\"Statement\" : [{" +
                    "   \"Sid\": \"ListObjectsInBucket\"," +
                    "	\"Effect\" : \"Allow\"," +
                    "   \"Action\" : [\"s3: ListBucket\"]," +
                    "	\"Resource\" :[\"arn:aws:s3:::doc-example-bucket/*\"]" +
                "}," +
                    "   \"Sid\": \"AllObjectActions\"," +
                    "	\"Effect\" : \"Allow\"," +
                    "   \"Action\" : [\"s3:*Object*\"]," +
                    "	\"Resource\" :[\"arn:aws:s3:::doc-example-bucket/*\"]" +
                "}]" +
            "}";

            string roleName = "S3ManagementRole";

            // Create the IAM client object.
            var client = new AmazonIdentityManagementServiceClient();

            var request = new CreateRoleRequest
            {
                AssumeRolePolicyDocument = s3ManagementPolicy,
                RoleName = roleName,
            };

            var response = await client.CreateRoleAsync(request);

            if (response.Role is not null)
            {
                var r = response.Role;
                Console.WriteLine($"{r.RoleName} created on: {r.CreateDate}");
            }
            else
            {
                Console.WriteLine("Could not create role.");
            }
        }
    }
    // snippet-end:[Lambda.dotnetv3.SetupLambdaRoleExample]
}
