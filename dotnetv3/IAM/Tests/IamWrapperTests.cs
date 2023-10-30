// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.IdentityManagement;
using Amazon.IdentityManagement.Model;

namespace IAMActions.Tests
{
    public class IamWrapperTests
    {
        private readonly IConfiguration _configuration;
        private readonly IAmazonIdentityManagementService _iamService;
        private readonly IAMWrapper _iamWrapper;

        // Values needed for IAM Basics scenario.
        private readonly string? _s3ListBucketsPolicyName;
        private readonly string? _rolePolicyName;

        // Values for tests related to the IAM Groups scenario.
        private readonly string? _groupUserName;
        private readonly string? _groupPolicyName;
        private readonly string? _groupName;
        private readonly string? _s3FullAccessPolicyName;

        private readonly string? _listBucketsPolicyDocument;
        private readonly string? _s3FullAccessPolicyDocument;

        private readonly string? _roleName;
        private readonly string? _assumePolicyName;

        private readonly string? _userName;
        private readonly string? _userPolicyName;

        public static string? _assumeRolePolicyDocument;
        public static string? _policyArn;
        public static string? _rolePolicyArn;
        public static string? _accessKeyId;

        /// <summary>
        /// Constructor for IAMWrapper tests.
        /// </summary>
        public IamWrapperTests()
        {
            _configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("testsettings.json") // Load test settings from .json file.
                .AddJsonFile("testsettings.local.json",
                    true) // Optionally load local settings.
                .Build();

            _groupUserName = _configuration["GroupUserName"];
            _groupPolicyName = _configuration["GroupPolicyName"];
            _groupName = _configuration["GroupName"];

            _s3ListBucketsPolicyName = _configuration["S3ListBucketsPolicyName"];
            _s3FullAccessPolicyName = _configuration["S3FullAccessPolicyName"];

            _roleName = _configuration["RoleName"];
            _rolePolicyName = _configuration["RolePolicyName"];
            _userName = _configuration["UserName"];
            _userPolicyName = _configuration["UserPolicyName"];


            _iamService = new AmazonIdentityManagementServiceClient();
            _iamWrapper = new IAMWrapper(_iamService);

            // Permissions to list all buckets.
            _listBucketsPolicyDocument = "{" +
                "\"Version\": \"2012-10-17\"," +
                "	\"Statement\" : [{" +
                    "	\"Action\" : [\"s3:ListAllMyBuckets\"]," +
                    "	\"Effect\" : \"Allow\"," +
                    "	\"Resource\" : \"*\"" +
                "}]" +
            "}";

            // Permissions for full access to Amazon Simple Storage Service
            // (Amazon S3).
            _s3FullAccessPolicyDocument = "{" +
                    "	\"Statement\" : [{" +
                    "	\"Action\" : [\"s3:*\"]," +
                    "	\"Effect\" : \"Allow\"," +
                    "	\"Resource\" : \"*\"" +
                "}]" +
            "}";
        }

        /// <summary>
        /// Test the method to list SAML providers. The returned provider list
        /// should not be null.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Trait("Category", "Integration")]
        public async Task ListSAMLProvidersAsyncTest()
        {
            var providers = await _iamWrapper.ListSAMLProvidersAsync();
            Assert.NotNull(providers);
        }

        /// <summary>
        /// Test the call to retrieve the account's password policy. This test
        /// is marked as "Quarantine" because it is possible that an account
        /// doesn't have a password policy.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact(Skip = "Quarantined test.")]
        [Trait("Category", "Quarantine")]
        public async Task GetAccountPasswordPolicy()
        {
            var passwordPolicy = await _iamWrapper.GetAccountPasswordPolicyAsync();
            Assert.NotNull(passwordPolicy);
        }

        /// <summary>
        /// Test the call to create an AWS Identity and Access Management (IAM)
        /// group. The resulting group object should not be null.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(1)]
        [Trait("Category", "Integration")]
        public async Task CreateGroupAsyncTest()
        {
            if (_groupName is not null)
            {
                var group = await _iamWrapper.CreateGroupAsync(_groupName);
                Assert.Equal(_groupName, group.GroupName);
                Assert.NotNull(group);
            }
        }

        /// <summary>
        /// Tests the call to create an IAM user. The IAM user returned should
        /// not be null.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(3)]
        [Trait("Category", "Integration")]
        public async Task CreateUserAsyncTest()
        {
            var user = await _iamWrapper.CreateUserAsync(_userName);

            // Define a role policy document that allows the new user
            // to assume the role.

            // Wait 15 seconds for the user to be active.
            System.Threading.Thread.Sleep(15000);

            // Create the policy document here so we can save the value
            // of the user's Amazon Resource Name (ARN).
            _assumeRolePolicyDocument = "{" +
                "\"Version\": \"2012-10-17\"," +
                    "\"Statement\": [{" +
                    "\"Effect\": \"Allow\"," +
                    "\"Principal\": {" +
                    $"	\"AWS\": \"{user.Arn}\"" +
                    "}," +
                    "\"Action\": \"sts:AssumeRole\"" +
                "}]" +
            "}";

            // Make sure we got an actual user value back from the
            // call to CreateUserAsync.
            Assert.NotNull(user);
        }

        /// <summary>
        /// Test the call to create an IAM policy. The resulting policy object
        /// should not be null.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(2)]
        [Trait("Category", "Integration")]
        public async Task CreatePolicyAsyncTest()
        {
            var policy = await _iamWrapper.CreatePolicyAsync(_s3ListBucketsPolicyName, _listBucketsPolicyDocument);
            var rolePolicy = await _iamWrapper.CreatePolicyAsync(_rolePolicyName, _listBucketsPolicyDocument);
            _policyArn = policy.Arn;
            _rolePolicyArn = rolePolicy.Arn;
            Assert.NotNull(policy);
            Assert.NotNull(rolePolicy);
        }

        /// <summary>
        /// Test the call to create an IAM role. The returned IAM role object
        /// should not be null.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(4)]
        [Trait("Category", "Integration")]
        public async Task CreateRoleAsyncTest()
        {
            var role = await _iamWrapper.CreateRoleAsync(_roleName, _assumeRolePolicyDocument);

            Assert.NotNull(role);
        }

        /// <summary>
        /// Test the call to attach an IAM policy to a role. Success should
        /// be true.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(5)]
        [Trait("Category", "Integration")]
        public async Task AttachRolePolicyAsyncTest()
        {
            var success = await _iamWrapper.AttachRolePolicyAsync(_rolePolicyArn, _roleName);
            Assert.True(success, "Couldn't attach the policy.");
        }

        /// <summary>
        /// Test the call to create an IAM service-linked role. the role object
        /// returned from the call should not be null.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(6)]
        [Trait("Category", "Integration")]
        public async Task CreateServiceLinkedRoleAsyncTest()
        {
            // Create the service-linked role for AWS Elastic Beanstalk.
            var serviceName = "elasticbeanstalk.amazonaws.com";
            var description = "A role created for testing IAMWrapper methods.";
            var role = await _iamWrapper.CreateServiceLinkedRoleAsync(serviceName, description);
            Assert.NotNull(role);

            // Now clean up.
            await _iamService.DeleteServiceLinkedRoleAsync(new DeleteServiceLinkedRoleRequest
            { RoleName = role.RoleName });
        }

        /// <summary>
        /// Tests the call to list groups. The list returned by the call should
        /// have at least one group in it.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(7)]
        [Trait("Category", "Integration")]
        public async Task ListGroupsAsyncTest()
        {
            var groups = await _iamWrapper.ListGroupsAsync();
            Assert.NotNull(groups);
        }

        /// <summary>
        /// Test the call to add an IAM policy to a group. Success should
        /// be true.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(8)]
        [Trait("Category", "Integration")]
        public async Task PutGroupPolicyAsyncTest()
        {
            var success = await _iamWrapper.PutGroupPolicyAsync(_groupName, _groupPolicyName, _listBucketsPolicyDocument);
            Assert.True(success, $"Could not embed policy {_s3ListBucketsPolicyName} to {_groupName}");
        }

        [Fact()]
        [Order(9)]
        [Trait("Category", "Integration")]
        public async Task PutRolePollicyTest()
        {
            var success = await _iamWrapper.PutRolePolicyAsync(_rolePolicyName, _roleName, _listBucketsPolicyDocument);
            Assert.True(success, "Could not embed policy {_s3ListBucketsPolicyName}.");
        }

        [Fact()]
        [Order(10)]
        [Trait("Category", "Integration")]
        public async Task PutUserPolicyAsyncTest()
        {
            var success = await _iamWrapper.PutUserPolicyAsync(_userName, _userPolicyName, _listBucketsPolicyDocument);
            Assert.True(success, "Couldn't insert inline policy.");
        }

        /// <summary>
        /// Tests the call to list IAM roles. The list returned by the call
        /// should contain at least one IAM role.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(11)]
        [Trait("Category", "Integration")]
        public async Task ListRolesAsyncTest()
        {
            var roles = await _iamWrapper.ListRolesAsync();
            Assert.True(roles.Count >= 1, "There are no roles to list.");
        }

        /// <summary>
        /// Test the call to list IAM policies. The list of policies returned
        /// by the call should contain at least one policy.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(12)]
        [Trait("Category", "Integration")]
        public async Task ListPoliciesAsyncTest()
        {
            var policies = await _iamWrapper.ListPoliciesAsync();
            Assert.True(policies.Count >= 1, "No policies to list.");
        }

        /// <summary>
        /// Test the call to list IAM role policies. The list of rolePolicies
        /// returned from the call should have at least one role policy in it.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(13)]
        [Trait("Category", "Integration")]
        public async Task ListRolePoliciesAsyncTest()
        {
            var rolePolicies = await _iamWrapper.ListRolePoliciesAsync(_roleName);
            Assert.NotNull(rolePolicies);
        }

        /// <summary>
        /// Tests the call to list users. The list returned by the call should
        /// contain at least one IAM user.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(14)]
        [Trait("Category", "Integration")]
        public async Task ListUsersAsyncTest()
        {
            var users = await _iamWrapper.ListUsersAsync();
            Assert.True(users.Count >= 1, "No users to list.");
        }

        /// <summary>
        /// Tests the call to add an IAM user to a group. Success should be
        /// true.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(15)]
        [Trait("Category", "Integration")]
        public async Task AddUserToGroupTest()
        {
            var success = await _iamWrapper.AddUserToGroupAsync(_userName, _groupName);
            Assert.True(success, $"Couldn't add user, {_userName}, to group, {_groupName}.");
        }

        /// <summary>
        /// Tests the call to create an IAM access key for a user. The returned
        /// key should not be null.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(16)]
        [Trait("Category", "Integration")]
        public async Task CreateAccessKeyAsyncTest()
        {
            var key = await _iamWrapper.CreateAccessKeyAsync(_userName);

            // Save the access key Id for use in a later test.
            _accessKeyId = key.AccessKeyId;
            Assert.NotNull(key);
        }

        /// <summary>
        /// Test the call to retrieve information about an IAM role. The role
        /// returned from the call should not be null.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(17)]
        [Trait("Category", "Integration")]
        public async Task GetRoleAsyncTest()
        {
            var role = await _iamWrapper.GetRoleAsync(_roleName);
            Assert.NotNull(role);
        }

        /// <summary>
        /// Test the call to get information about an IAM policy. The Amazon
        /// Resource Name (ARN) of the returned policy should be the same as
        /// the value stored in _policyArn.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(18)]
        [Trait("Category", "Integration")]
        public async Task GetPolicyAsyncTest()
        {
            var policy = await _iamWrapper.GetPolicyAsync(_policyArn);
            Assert.Equal(_policyArn, policy.Arn);
        }

        /// <summary>
        /// Test the call to get information about an IAM user. The user object
        /// returned from the call should have the same username as the value
        /// of _userName.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(19)]
        [Trait("Category", "Integration")]
        public async Task GetUserAsyncTest()
        {
            var user = await _iamWrapper.GetUserAsync(_userName);
            Assert.Equal(_userName, user.UserName);
        }

        /// <summary>
        /// Test the call to retrieve a list of IAM attached role policies. The
        /// list of policies returned by the call should contain at least one policy.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(20)]
        [Trait("Category", "Integration")]
        public async Task ListAttachedRolePoliciesTest()
        {
            var policies = await _iamWrapper.ListAttachedRolePoliciesAsync(_roleName);
            Assert.True(policies.Count >= 1, $"No policies attached to {_roleName}");
        }

        /// <summary>
        /// Tests the call to delete an IAM access key. The value of success
        /// should be true.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(21)]
        [Trait("Category", "Integration")]
        public async Task DeleteAccessKeyAsyncTest()
        {
            var success = await _iamWrapper.DeleteAccessKeyAsync(_accessKeyId, _userName);
            Assert.True(success);
        }

        /// <summary>
        /// Tests the call to remove an IAM user from a group. Success should
        /// be true.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact]
        [Order(22)]
        [Trait("Category", "Integration")]
        public async Task RemoveUserFromGroupTest()
        {
            var success = await _iamWrapper.RemoveUserFromGroupAsync(_userName, _groupName);
            Assert.True(success, $"Couldn't remove user {_userName} from the group {_groupName}");
        }

        /// <summary>
        /// Tests the ability to delete an IAM user policy. The value success
        /// should be true.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(23)]
        [Trait("Category", "Integration")]
        public async Task DeleteUserPolicyAsyncTest()
        {
            var success = await _iamWrapper.DeleteUserPolicyAsync(_userPolicyName, _userName);
            Assert.True(success, $"Could not delete {_userPolicyName}.");
        }

        /// <summary>
        /// Tests the call to delete a user. After the call returns, the test
        /// proves that the user no longer exists by attempting to get
        /// information about the user. This should raise an exception because,
        /// if properly deleted, the user no longer exists.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(24)]
        [Trait("Category", "Integration")]
        public async Task DeleteUserAsyncTest()
        {
            // Delete the user.
            var success = await _iamWrapper.DeleteUserAsync(_userName);

            // Make sure that the user no longer exists. If the user has
            // been deleted, a call to GetUserAsync will raise a NoSuchEntityException.
            var iamException = await Record.ExceptionAsync(() =>
                _iamService.GetUserAsync(new GetUserRequest { UserName = _userName }));
            Assert.NotNull(iamException);
            Assert.IsType<NoSuchEntityException>(iamException);
            Assert.Equal($"The user with name {_userName} cannot be found.", iamException.Message);
        }

        /// <summary>
        /// Tests the call to detach an IAM role policy from a role. The value
        /// of success should be true.
        /// </summary>
        /// <returns>Aync Task.</returns>
        [Fact()]
        [Order(25)]
        [Trait("Category", "Integration")]
        public async Task DetachRolePolicyAsyncTest()
        {
            var success = await _iamWrapper.DetachRolePolicyAsync(_rolePolicyArn, _roleName);
            Assert.True(success, $"Couldn't detach policy, {_rolePolicyName} from {_roleName}.");
        }

        /// <summary>
        /// Tests the ability to delete an IAM role policy. Success should be
        /// true.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(26)]
        [Trait("Category", "Integration")]
        public async Task DeleteRolePolicyAsyncTest()
        {
            var success = await _iamWrapper.DeleteRolePolicyAsync(_roleName, _rolePolicyName);
            Assert.True(success, "Could not delete the role policy.");
        }

        /// <summary>
        /// Tests the call to delete an IAM role. The value of success should
        /// be true.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(27)]
        [Trait("Category", "Integration")]
        public async Task DeleteRoleAsyncTest()
        {
            var success = await _iamWrapper.DeleteRoleAsync(_roleName);
            Assert.True(success, "Could not delete the role.");
        }

        /// <summary>
        /// Tests the call to delete a policy. The value of success should be
        /// true.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(28)]
        [Trait("Category", "Integration")]
        public async Task DeletePolicyAsyncTest()
        {
            var success = await _iamWrapper.DeletePolicyAsync(_policyArn);
            Assert.True(success, "Could not delete the policy.");
        }

        [Fact()]
        [Order(29)]
        [Trait("Category", "Integration")]
        public async Task DeleteGroupPolicyAsyncTest()
        {
            var success = await _iamWrapper.DeleteGroupPolicyAsync(_groupName, _groupPolicyName);
            Assert.True(success);
        }

        [Fact()]
        [Order(30)]
        [Trait("Category", "Integration")]
        public async Task DeleteGroupAsyncTest()
        {
            var success = await _iamWrapper.DeleteGroupAsync(_groupName);
            Assert.True(success, $"Couldn't delete the IAM group {_groupName}");
        }
    }
}