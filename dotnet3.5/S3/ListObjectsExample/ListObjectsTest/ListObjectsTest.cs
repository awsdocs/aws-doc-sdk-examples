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

namespace ListObjectsTest
{
    public class ListObjectsTest
    {
        // This application uses XUnit and moq to test the CreateBucket Example.

        private string _BucketName;

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
                    Assert.Equal(request.BucketName, _BucketName);
                }
            }).Returns((ListObjectsV2Request r, CancellationToken token) =>
            {
                return Task.FromResult(new ListObjectsV2Response()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK
                });
            });

            return mockS3Client.Object;
        }

        [Fact]
        public async Task CheckListObjects()
        {
            IAmazonS3 client = CreateMockS3Client();

            _BucketName = "doc-example-bucket";

            var request = new ListObjectsV2Request
            {
                BucketName = _BucketName,
                MaxKeys = 10
            };

            var response = await client.ListObjectsV2Async(request);

            bool gotResult = response != null;
            Assert.True(gotResult, "Copy operation failed.");

            bool ok = response.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, $"Could NOT list objects in bucket: {_BucketName}.");

        }
    }
}
