using Xunit;
using IAM_Basics_Scenario;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Amazon.IdentityManagement;
using Amazon;

namespace IAM_Basics_Scenario.Tests
{
    public class IAM_BasicsTests
    {
        // Values needed for user, role, and policies.
        private const string UserName = "test-example-user";
        private const string S3PolicyName = "test-s3-list-buckets-policy";
        private const string RoleName = "test-temporary-role";
        private const string AssumePolicyName = "test-sts-trust-user";

        private const string RolePermissions = @"{
                'Version': '2012-10-17',
                'Statement': [
                    {
                        'Effect': 'Allow',
                        'Action': 's3:ListAllMyBuckets',
                        'Resource': 'arn:aws:s3:::*'
                    }
                ]
            }";

        private const string ManagedPolicy = @"{
          'Version': '2012-10-17',
          'Statement': [
            {
              'Effect': 'Allow',
              'Action': ['s3:ListAllMyBuckets', 'sts:AssumeRole'],
              'Resource': '*',
            },
          ],
        }";

        private static readonly RegionEndpoint Region = RegionEndpoint.USEast2;

        [Fact()]
        public void MainTest()
        {
            Assert.True(false, "This test needs an implementation");
        }

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
        public void DeleteResourcesTest()
        {
            // Delete client object created in CreateUserAsyncTest.
            var client = new AmazonIdentityManagementServiceClient(Region);
            var success = IAM_Basics.DeleteResources(client, UserName);
        }
    }
}