// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

using Amazon.S3;
using Amazon.S3.Model;
using Moq;
using System;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using Xunit;

namespace DeleteBucketText
{
    public class DeleteBucketTest
    {
        private string _BucketName;

        private IAmazonS3 CreateMockS3Client()
        {
            var mockS3Client = new Mock<IAmazonS3>();
            mockS3Client.Setup(client => client.DeleteBucketAsync(
                It.IsAny<DeleteBucketRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<DeleteBucketRequest, CancellationToken>((request, token) =>
            {
                if (!String.IsNullOrEmpty(request.BucketName))
                {
                    Assert.Equal(request.BucketName, _BucketName);
                }
            }).Returns((DeleteBucketRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new DeleteBucketResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK
                });
            });

            return mockS3Client.Object;
        }

        [Fact]
        public async Task CheckDeleteBucketAsync()
        {
            IAmazonS3 client = CreateMockS3Client();

            // Set field values
            _BucketName = "deletebucketname";

            var deleteBucketRequest = new DeleteBucketRequest
            {
                BucketName = _BucketName,
                UseClientRegion = true
            };

            var deleteBucketResponse = await client.DeleteBucketAsync(deleteBucketRequest);

            bool gotResult = deleteBucketResponse != null;
            Assert.True(gotResult, "Copy operation failed.");

            bool ok = deleteBucketResponse.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, $"Could NOT create bucket {_BucketName}.");
        }
    }
}
