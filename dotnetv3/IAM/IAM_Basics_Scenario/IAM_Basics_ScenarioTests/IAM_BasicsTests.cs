using Xunit;
using IAM_Basics_Scenario;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Amazon.IdentityManagement;
using Amazon;
using Amazon.IdentityManagement.Model;

namespace IAM_Basics_Scenario.Tests
{
    public class IAM_BasicsTests
    {
        // Values needed for user, role, and policies.
        private const string UserName = "test-example-user";
        private const string S3PolicyName = "test-s3-list-buckets-policy";
        private const string RoleName = "test-temporary-role";
        private const string AssumePolicyName = "test-sts-trust-user";
        private string AccessKeyId = string.Empty;
        private string SecretKey = string.Empty;
        private ManagedPolicy TestPolicy = null;

        string policyDocument = "{" +
            "\"Version\": \"2012-10-17\"," +
            "   \"Statement\" : [{" +
                "	\"Effect\" : \"Allow\"," +
                "	\"Action\" : [\"s3:ListAllMyBuckets\", \"sts:AssumeRole\"]," +
                "   \"Resource\": \"*\"," +
            "}]" +
        "}";

        private const string ManagedPolicy = "{" +
        "\"Version\": \"2012-10-17\"," +
        "\"Statement\": [{" +
        "\"Effect\": \"Allow\"," +
        "\"Principal\": {" +
        "	\"AWS\": \"arn:aws:iam::704825161248:user/example-user\"" +
        "}," +
            "\"Action\": \"sts:AssumeRole\"" +
        "}]" +
    "}";

        private static readonly RegionEndpoint Region = RegionEndpoint.USEast2;

        [Fact()]
        public async Task CreateUserAsyncTest()
        {
            // Create client object
            var client = new AmazonIdentityManagementServiceClient(Region);

            var user = await IAM_Basics.CreateUserAsync(client, UserName);

            Assert.NotNull(user);
            Assert.Equal(user.UserName, UserName);
        }

        [Fact()]
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

        [Fact()]
        public void CreateRoleAsyncTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        public void CreatePolicyAsyncTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        public void AttachRoleAsyncTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

        [Fact()]
        public void DeleteResourcesTest()
        {
            // Delete client object created in CreateUserAsyncTest.
            var client = new AmazonIdentityManagementServiceClient(Region);
            var success = IAM_Basics.DeleteResourcesAsync(client, AccessKeyId, UserName, S3PolicyName, TestPolicy.Arn, RoleName);
        }
    }
}