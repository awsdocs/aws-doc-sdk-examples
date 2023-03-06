// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.S3;
using Amazon.S3.Model;
using Amazon.S3.Util;
using Amazon.SecurityToken;
using Xunit;
using IamScenariosCommon;

namespace IAMTests
{
    // A class to test the methods in the S3Wrapper class
    // of the IamScenariosCommon project.
    public class S3WrapperTests
    {
        private readonly IConfiguration _configuration;
        private readonly IAmazonS3 _s3Client;
        private readonly IAmazonSecurityTokenService _stsClient;
        private readonly S3Wrapper _s3Wrapper;

        // Values needed for user, role, and policies.
        private readonly string _userName;
        private readonly string _s3PolicyName;
        private readonly string _roleName;
        private readonly string _assumePolicyName;
        private readonly string _bucketName;
        private static string _test_guid;

        public S3WrapperTests()
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
            _bucketName = _configuration["BucketName"];

            _s3Client = new AmazonS3Client();
            _stsClient = new AmazonSecurityTokenServiceClient();

            _s3Wrapper = new S3Wrapper(_s3Client, _stsClient);
        }

        /// <summary>
        /// Tests the PutBucketAsync method in the S3Wrapper class.
        /// </summary>
        /// <returns>Async Task.</returns>
        [Fact()]
        [Order(1)]
        [Trait("Category", "Integration")]
        public async Task PutBucketAsyncTest()
        {
            // Create the bucket.
            _test_guid = Guid.NewGuid().ToString();
            var bucketName = $"{_bucketName}{_test_guid}";
            await _s3Wrapper.PutBucketAsync(bucketName);

            // Test that the new bucket exists.
            var bucketExists = await AmazonS3Util.DoesS3BucketExistV2Async(_s3Client, bucketName);
            Assert.True(bucketExists, "Could not create the bucket.");
        }

        [Fact()]
        [Order(2)]
        [Trait("Category", "Integration")]
        public async Task ListMyBucketsAsyncTest()
        {
            // Since the PutBucketAsyncTest has been run, there
            // should be at least one bucket.
            var buckets = await _s3Wrapper.ListMyBucketsAsync();
            Assert.NotNull(buckets);
            Assert.True(buckets.Count >= 1, "There are no buckets to list.");
        }

        [Fact()]
        [Order(3)]
        [Trait("Category", "Integration")]
        public async Task DeleteBucketAsyncTest()
        {
            // Try to delete the bucket.
            var bucketName = $"{_bucketName}{_test_guid}";
            await _s3Wrapper.DeleteBucketAsync(bucketName);

            // See if the bucket still exists.
            var bucketExists = await AmazonS3Util.DoesS3BucketExistV2Async(_s3Client, bucketName);
            Assert.False(bucketExists, "Could not delete the bucket.");
        }

        [Fact()]
        [Trait("Category", "Quarantine")]
        public async Task AssumeS3RoleAsyncTest()
        {
            var roleSession = "temporary_session";
            var credentials = await _s3Wrapper.AssumeS3RoleAsync(roleSession, _roleName);
            Assert.NotNull(credentials);
        }
    }
}
