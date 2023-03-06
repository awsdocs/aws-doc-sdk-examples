// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.IdentityManagement;
using Amazon.IdentityManagement.Model;
using Xunit;
using IAMActions;
using Xunit.Sdk;

namespace IAMActions.Tests
{
    public class IamWrapperTests
    {
        private readonly IConfiguration _configuration;
        private readonly IAmazonIdentityManagementService _iamService;
        private readonly IAMWrapper _iamWrapper;

        // Values needed for user, role, and policies.
        private readonly string _userName;
        private readonly string _s3PolicyName;
        private readonly string _roleName;
        private readonly string _assumePolicyName;
        private readonly string _groupName;

        private readonly string _policyDocument;

        public static string _policyArn;
        public static string _accessKeyId;

        public IamWrapperTests()
        {
            _configuration = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("testsettings.json") // Load test settings from .json file.
                .AddJsonFile("testsettings.local.json",
                    true) // Optionally load local settings.
                .Build();

            _userName = _configuration["UserName"];
            _s3PolicyName = _configuration["S3PolicyName"];
            _roleName = _configuration["RoleName"];
            _assumePolicyName = _configuration["AssumePolicyName"];
            _groupName = _configuration["GroupName"];

            // Permissions to list all buckets.
            _policyDocument = "{" +
                "\"Version\": \"2012-10-17\"," +
                "	\"Statement\" : [{" +
                    "	\"Action\" : [\"s3:ListAllMyBuckets\"]," +
                    "	\"Effect\" : \"Allow\"," +
                    "	\"Resource\" : \"*\"" +
                "}]" +
            "}";

            _iamService = new AmazonIdentityManagementServiceClient();
            _iamWrapper = new IAMWrapper(_iamService);
        }

        [Fact()]
        [Trait("Category", "Integration")]
        public async Task ListSAMLProvidersAsyncTest()
        {
            var providers = await _iamWrapper.ListSAMLProvidersAsync();
            Assert.True(providers.Count > 0, "There are not SAML Providers available.");
        }

        [Fact()]
        [Trait("Category", "Integration")]
        public async Task GetAccountPasswordPolicy()
        {
            var policy = await _iamWrapper.GetAccountPasswordPolicyAsync();
            Assert.NotNull(policy);
        }

        [Fact()]
        [Order(1)]
        [Trait("Category", "Integration")]
        public async Task CreateGroupAsyncTest()
        {
            var group = await _iamWrapper.CreateGroupAsync(_groupName);
            Assert.Equal(_groupName, group.GroupName);
            Assert.NotNull(group);
        }

        [Fact()]
        [Order(2)]
        [Trait("Category", "Integration")]
        public async Task CreatePolicyAsyncTest()
        {
            var policy = await _iamWrapper.CreatePolicyAsync(_s3PolicyName, _policyDocument);
            _policyArn = policy.Arn;
            Assert.NotNull(policy);
        }

        [Fact()]
        [Order(3)]
        [Trait("Category", "Integration")]
        public async Task CreateRoleAsyncTest()
        {
            var role = await _iamWrapper.CreateRoleAsync(_s3PolicyName, _policyDocument);
            Assert.NotNull(role);
        }

        [Fact()]
        [Order(4)]
        [Trait("Category", "Integration")]
        public async Task AttachRolePolicyAsyncTest()
        {
            var success = await _iamWrapper.AttachRolePolicyAsync(_policyArn, _roleName);
            Assert.True(success, "Couldn't attach the policy.");
        }

        [Fact()]
        [Order(5)]
        [Trait("Category", "Integration")]
        public async Task CreateServiceLinkedRoleAsyncTest()
        {
            // Create the service-linked role for Elastic Beanstalk.
            var serviceName = "elasticbeanstalk.amazonaws.com";
            var description = "A role created for testing IAMWrapper methods.";
            var role = await _iamWrapper.CreateServiceLinkedRoleAsync(serviceName, description);
            Assert.NotNull(role);

            // Now clean up
            var response = await _iamService.DeleteServiceLinkedRoleAsync(new DeleteServiceLinkedRoleRequest
                { RoleName = role.RoleName });
        }

        [Fact()]
        [Order(6)]
        [Trait("Category", "Integration")]
        public async Task CreateUserAsyncTest()
        {
            var user = await _iamWrapper.CreateUserAsync(_userName);
            Assert.NotNull(user);
        }

        [Fact()]
        [Order(7)]
        [Trait("Category", "Integration")]
        public async Task ListGroupsAsyncTest()
        {

        }

        [Fact()]
        [Order(8)]
        [Trait("Category", "Integration")]
        public async Task PutGroupPolicyAsyncTest()
        {

        }

        [Fact()]
        [Order(9)]
        [Trait("Category", "Integration")]
        public async Task ListRolesAsyncTest()
        {

        }

        [Fact()]
        [Order(10)]
        [Trait("Category", "Integration")]
        public async Task ListPoliciesAsyncTest()
        {

        }

        [Fact()]
        [Order(11)]
        [Trait("Category", "Integration")]
        public async Task ListRolePoliciesAsyncTest()
        {

        }

        [Fact()]
        [Order(12)]
        [Trait("Category", "Integration")]
        public async Task ListUsersAsyncTest()
        {

        }

        [Fact()]
        [Order(13)]
        [Trait("Category", "Integration")]
        public async Task AddUserToGroupTest()
        {
            var success = await _iamWrapper.AddUserToGroupAsync(_userName, _groupName);
            Assert.True(success, $"Couldn't add user, {_userName}, to group, {_groupName}.");
        }

        [Fact()]
        [Order(14)]
        [Trait("Category", "Integration")]
        public async Task CreateAccessKeyAsyncTest()
        {
            var key = await _iamWrapper.CreateAccessKeyAsync(_userName);
            _accessKeyId = key.AccessKeyId;
            Assert.NotNull(key);
        }

        [Fact()]
        [Order(15)]
        [Trait("Category", "Integration")]
        public async Task GetRoleAsyncTest()
        {

        }

        [Fact()]
        [Order(16)]
        [Trait("Category", "Integration")]
        public async Task GetPolicyAsyncTest()
        {

        }

        [Fact()]
        [Order(17)]
        [Trait("Category", "Integration")]
        public async Task GetUserAsyncTest()
        {

        }

        [Fact()]
        [Order(18)]
        [Trait("Category", "Integration")]
        public async Task ListAttachedRolePoliciesTest()
        {

        }

        [Fact()]
        [Order(19)]
        [Trait("Category", "Integration")]
        public async Task DeleteAccessKeyAsyncTest()
        {
            var success = await _iamWrapper.DeleteAccessKeyAsync(_accessKeyId, _userName);
            Assert.True(success);
        }

        [Fact]
        [Order(20)]
        [Trait("Category", "Integration")]
        public async Task RemoveUserFromGroupTest()
        {
            var success = await _iamWrapper.RemoveUserFromGroupAsync(_userName, _groupName);
            Assert.True(success, $"Couldn't remove user {_userName} from the group {_groupName}");
        }

        [Fact()]
        [Order(21)]
        [Trait("Category", "Integration")]
        public async Task DeleteUserAsyncTest()
        {
            // Delete the user.
            var success = await _iamWrapper.DeleteUserAsync(_userName);

            // Make sure that the user now longer exists. If the user has
            // been deleted, a call to GetUserAsync will raise a NotSuchEntityException.
            var iamException = await Record.ExceptionAsync(() =>
                _iamService.GetUserAsync(new GetUserRequest { UserName = _userName }));
            Assert.NotNull(iamException);
            Assert.IsType<NoSuchEntityException>(iamException);
            Assert.Equal($"The user with name {_userName} cannot be found.", iamException.Message);
        }

        [Fact()]
        [Order(22)]
        [Trait("Category", "Integration")]
        public async Task DetachRolePolicyAsyncTest()
        {
            var success = await _iamWrapper.DetachRolePolicyAsync(_policyArn, _roleName);
            Assert.True(success, $"Couldn't detach policy, {_s3PolicyName} from {_roleName}.");
        }

        [Fact()]
        [Order(23)]
        [Trait("Category", "Integration")]
        public async Task DeleteRolePolicyAsyncTest()
        {

        }

        [Fact()]
        [Order(24)]
        [Trait("Category", "Integration")]
        public async Task DeleteRoleAsyncTest()
        {

        }

        [Fact()]
        [Order(25)]
        [Trait("Category", "Integration")]
        public async Task DeleteUserPolicyAsyncTest()
        {

        }

        [Fact()]
        [Order(26)]
        [Trait("Category", "Integration")]
        public async Task DeletePolicyAsyncTest()
        {

        }
    }
}

