// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.SecurityToken;

namespace IAM_Basics_Scenario.Tests
{
    public class IAM_BasicsTests
    {
        // Values needed for user, role, and policies.
        private static readonly RegionEndpoint Region = RegionEndpoint.USEast2;
        private readonly IConfiguration _configuration;

        private readonly AmazonIdentityManagementServiceClient _client;

        private readonly string _userName;
        private readonly string _s3PolicyName;
        private readonly string _roleName;
        private string _accessKeyId = string.Empty;
        private string _secretKey = string.Empty;
        private ManagedPolicy _testPolicy;
        private string _userArn;
        private string _roleArn;

        string testPolicyDocument = "{" +
            "\"Version\": \"2012-10-17\"," +
            "	\"Statement\" : [{" +
                "	\"Action\" : [\"s3:ListAllMyBuckets\"]," +
                "	\"Effect\" : \"Allow\"," +
                "	\"Resource\" : \"*\"" +
            "}]" +
        "}";

        public IAM_BasicsTests()
        {
            _configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("testsettings.json") // Load test settings from JSON file.
                .AddJsonFile("testsettings.local.json",
                    true) // Optionally load local settings.
                .Build();

            _userName = _configuration["UserName"];
            _s3PolicyName = _configuration["S3PolicyName"];
            _roleName = _configuration["RoleName"];

            _client = new AmazonIdentityManagementServiceClient(Region);
        }

        [Fact()]
        [Order(1)]
        public async Task CreateUserAsyncTest()
        {
            var user = await IAM_Basics.CreateUserAsync(_client, _userName);

            Assert.NotNull(user);
            Assert.Equal(user.UserName, _userName);
            _userArn = user.Arn;
        }

        [Fact()]
        [Order(2)]
        public async Task CreateAccessKeyAsyncTest()
        {
            var accessKey = await IAM_Basics.CreateAccessKeyAsync(_client, _userName);

            Assert.NotNull(accessKey);

            // Save the key values for use with other tests.
            _accessKeyId = accessKey.AccessKeyId;
            _secretKey = accessKey.SecretAccessKey;
        }

        [Fact()]
        [Order(3)]
        public async Task CreateRoleAsyncTest()
        {
            if (string.IsNullOrEmpty(_userArn))
            {
                _userArn = _configuration["UserArn"];
            }

            string testAssumeRolePolicy = "{" +
                "\"Version\": \"2012-10-17\"," +
                "\"Statement\": [{" +
                "\"Effect\": \"Allow\"," +
                "\"Principal\": {" +
                $"	\"AWS\": \"{_userArn}\"" +
                "}," +
                    "\"Action\": \"sts:AssumeRole\"" +
                "}]" +
            "}";

            // Create the role to allow listing the Amazon Simple Storage Service
            // (Amazon S3) buckets. Role names are not case sensitive and must
            // be unique to the account for which it is created.
            var role = await IAM_Basics.CreateRoleAsync(
                _client,
                _roleName,
                testAssumeRolePolicy);

            _roleArn = role.Arn;

            Assert.NotNull(role);
            Assert.Equal(role.RoleName, _roleName);
        }

        [Fact()]
        [Order(4)]
        public async Task CreatePolicyAsyncTest()
        {
            // Create a policy with permissions to list Amazon S3 buckets
            var policy = await IAM_Basics.CreatePolicyAsync(_client, _s3PolicyName, testPolicyDocument);

            Assert.Equal(policy.PolicyName, _s3PolicyName);
            _testPolicy = policy;

            // Wait 15 seconds for the policy to be available.
            System.Threading.Thread.Sleep(15000);
        }

        [Fact()]
        [Order(5)]
        public async Task AttachRoleAsyncTest()
        {
            var stsClient = new AmazonSecurityTokenServiceClient(
                _accessKeyId,
                _secretKey);

            // Wait for credentials to be valid.
            System.Threading.Thread.Sleep(10000);

            // Attach the policy to the role we created earlier.
            await IAM_Basics.AttachRoleAsync(_client, _testPolicy.Arn, _roleName);

            // Waiting 15 seconds for the policy to be attached
            System.Threading.Thread.Sleep(15000);
        }

        [Fact()]
        [Order(6)]
        public async Task AssumeS3RoleAsyncTest()
        {
            var stsClient = new AmazonSecurityTokenServiceClient(
                _accessKeyId,
                _secretKey);

            // Wait for credentials to be valid.
            System.Threading.Thread.Sleep(10000);

            var assumedRoleCredentials = await IAM_Basics.AssumeS3RoleAsync(
                stsClient,
                "temporary-session",
                _roleArn);
        }

        [Fact()]
        [Order(7)]
        public async Task DeleteResourcesTest()
        {
            // Delete all the resources created for the various tests.
            var success = await IAM_Basics.DeleteResourcesAsync(
                _client,
                _accessKeyId,
                _userName,
                _testPolicy.Arn,
                _roleName);
            Assert.True(success, "Couldn't delete resources.");
        }
    }
}