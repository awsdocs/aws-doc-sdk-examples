// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace ListObjectsExample.Tests
{
    using Xunit;
    using System;
    using System.Threading.Tasks;
    using Amazon.S3;
    using Moq;
    using Amazon.S3.Model;
    using System.Threading;
    using System.Net;

    public class ListObjectsTests
    {
        const string BucketName = "doc-example-bucket";
        private IAmazonS3 CreateMockS3Client()
        {
            var mockS3Client = new Mock<IAmazonS3>();
            mockS3Client.Setup(client => client.ListObjectsV2Async(
                It.IsAny<ListObjectsV2Request>(),
                It.IsAny<CancellationToken>()
            )).Callback<ListObjectsV2Request, CancellationToken>((request, token) =>
            {
                if (!String.IsNullOrEmpty(request.BucketName))
                {
                    Assert.Equal(request.BucketName, BucketName);
                }
            }).Returns((ListObjectsV2Request r, CancellationToken token) =>
            {
                return Task.FromResult(new ListObjectsV2Response()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK,
                });
            });

            return mockS3Client.Object;
        }

        [Fact()]
        public async Task ListingObjectsAsyncTest()
        {
            IAmazonS3 client = CreateMockS3Client();

            var request = new ListObjectsV2Request
            {
                BucketName = BucketName,
            };

            var response = await client.ListObjectsV2Async(request);

            bool gotResult = response is not null;
            Assert.True(gotResult, "List bucket objects failed.");

            bool ok = response.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, $"Could NOT list objects in bucket: {BucketName}.");
        }
    }
}