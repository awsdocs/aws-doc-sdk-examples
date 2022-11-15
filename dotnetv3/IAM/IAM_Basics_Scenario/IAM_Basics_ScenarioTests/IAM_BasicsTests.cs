// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace IAM_Basics_Scenario.Tests
{
    [TestCaseOrderer("OrchestrationService.Project.Orderers.PriorityOrderer", "OrchestrationService.Project")]
    public class IAM_BasicsTests
    {
        // Values needed for user, role, and policies.
        private static readonly RegionEndpoint Region = RegionEndpoint.USEast2;
        private readonly IConfiguration _configuration;

        private readonly AmazonIdentityManagementServiceClient _client;
        private readonly string _userName;
        private readonly string _s3PolicyName;
        private readonly string _roleName;
        private readonly string _assumePolicyName;
        private string _accessKeyId = string.Empty;
        private string _secretKey = string.Empty;
        private ManagedPolicy _testPolicy;
        private string _userArn;

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
            _assumePolicyName = _configuration["AssumePolicyName"];

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
            var role = await IAM_Basics.CreateRoleAsync(_client, _roleName, testAssumeRolePolicy);
            var roleArn = role.Arn;

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
        }

        [Fact()]
        [Order(5)]
        public async Task AttachRoleAsyncTest()
        {
            // Attach the policy to the role we created earlier.
            await IAM_Basics.AttachRoleAsync(_client, _testPolicy.Arn, _roleName);
        }

        [Fact()]
        [Order(6)]
        public async Task DeleteResourcesTest()
        {
            // Delete all the resources created for the various tests.
            var success = await IAM_Basics.DeleteResourcesAsync(
                _client, _accessKeyId,
                _userName,
                _testPolicy.Arn,
                _roleName);
            Assert.True(success, "Couldn't delete resources.");
        }
    }
}