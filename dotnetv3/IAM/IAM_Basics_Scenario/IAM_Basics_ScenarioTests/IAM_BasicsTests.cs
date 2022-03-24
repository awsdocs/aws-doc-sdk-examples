// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace IAM_Basics_Scenario.Tests
{
    using Xunit;
    using System.Threading.Tasks;
    using Amazon.IdentityManagement;
    using Amazon;
    using Amazon.IdentityManagement.Model;
    using IAM_Basics_Scenario;
    using IAM_Basics_ScenarioTests;

    [TestCaseOrderer("OrechstrationService.Project.Orderers.PriorityOrderer", "OrechstrationService.Project")]
    public class IAM_BasicsTests
    {
        // Values needed for user, role, and policies.
        private const string UserName = "test-example-user";
        private const string S3PolicyName = "test-s3-list-buckets-policy";
        private const string RoleName = "test-temporary-role";
        private const string AssumePolicyName = "test-sts-trust-user";
        private string AccessKeyId = string.Empty;
        private string SecretKey = string.Empty;
        private ManagedPolicy TestPolicy;
        private string UserArn;

        string testPolicyDocument = "{" +
            "\"Version\": \"2012-10-17\"," +
            "	\"Statement\" : [{" +
                "	\"Action\" : [\"s3:ListAllMyBuckets\"]," +
                "	\"Effect\" : \"Allow\"," +
                "	\"Resource\" : \"*\"" +
            "}]" +
        "}";

        private static readonly RegionEndpoint Region = RegionEndpoint.USEast2;

        [Fact, TestPriority(1)]
        public async Task CreateUserAsyncTest()
        {
            // Create client object
            var client = new AmazonIdentityManagementServiceClient(Region);

            var user = await IAM_Basics.CreateUserAsync(client, UserName);

            Assert.NotNull(user);
            Assert.Equal(user.UserName, UserName);
            UserArn = user.Arn;
        }

        [Fact, TestPriority(2)]
        public async Task CreateAccessKeyAsyncTest()
        {
            // Create client object
            var client = new AmazonIdentityManagementServiceClient(Region);

            var accessKey = await IAM_Basics.CreateAccessKeyAsync(client, UserName);

            Assert.NotNull(accessKey);

            // Save the key values for use with other tests.
            AccessKeyId = accessKey.AccessKeyId;
            SecretKey = accessKey.SecretAccessKey;
        }

        [Fact, TestPriority(3)]
        public async Task CreateRoleAsyncTest()
        {
            string testAssumeRolePolicy = "{" +
                "\"Version\": \"2012-10-17\"," +
                "\"Statement\": [{" +
                "\"Effect\": \"Allow\"," +
                "\"Principal\": {" +
                $"	\"AWS\": \"{UserArn}\"" +
                "}," +
                    "\"Action\": \"sts:AssumeRole\"" +
                "}]" +
            "}";

            // Create client object
            var client = new AmazonIdentityManagementServiceClient(Region);

            // Create the role to allow listing the Amazon Simple Storage Service
            // (Amazon S3) buckets. Role names are not case sensitive and must
            // be unique to the account for which it is created.
            var role = await IAM_Basics.CreateRoleAsync(client, RoleName, testAssumeRolePolicy);
            var roleArn = role.Arn;

            Assert.NotNull(role);
            Assert.Equal(role.RoleName, RoleName);
        }

        [Fact, TestPriority(4)]
        public async Task CreatePolicyAsyncTest()
        {
            // Create client object
            var client = new AmazonIdentityManagementServiceClient(Region);

            // Create a policy with permissions to list Amazon S3 buckets
            var policy = await IAM_Basics.CreatePolicyAsync(client, S3PolicyName, testPolicyDocument);

            Assert.Equal(policy.PolicyName, S3PolicyName);
            TestPolicy = policy;
        }

        [Fact, TestPriority(5)]
        public async Task AttachRoleAsyncTest()
        {
            // Create client object
            var client = new AmazonIdentityManagementServiceClient(Region);

            // Attach the policy to the role we created earlier.
            await IAM_Basics.AttachRoleAsync(client, TestPolicy.Arn, RoleName);
        }

        [Fact, TestPriority(6)]
        public void DeleteResourcesTest()
        {
            // Delete client object created in CreateUserAsyncTest.
            var client = new AmazonIdentityManagementServiceClient(Region);
            var success = IAM_Basics.DeleteResourcesAsync(client, AccessKeyId, UserName, TestPolicy.Arn, RoleName);
        }
    }
}