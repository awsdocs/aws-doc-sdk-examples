// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.S3;
using Amazon.S3.Model;
using Moq;
using System;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using Xunit;

namespace CreateBucketTest
{
    public class CreateBucketTest
    {
        private string _NewBucketName;

        private IAmazonS3 CreateMockS3Client()
        {
            var mockS3Client = new Mock<IAmazonS3>();
            mockS3Client.Setup(client => client.PutBucketAsync(
                It.IsAny<PutBucketRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<PutBucketRequest, CancellationToken>((request, token) =>
            {
                if (!String.IsNullOrEmpty(request.BucketName))
                {
                    Assert.Equal(request.BucketName, _NewBucketName);
                }
            }).Returns((PutBucketRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new PutBucketResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK
                });
            });

            return mockS3Client.Object;
        }

        [Fact]
        public async Task CheckCreateBucket()
        {
            IAmazonS3 client = CreateMockS3Client();

            _NewBucketName = "doc-example-bucket";

            var putBucketRequest = new PutBucketRequest
            {
                BucketName = _NewBucketName,
                UseClientRegion = true
            };

            var putBucketResponse = await client.PutBucketAsync(putBucketRequest);

            bool gotResult = putBucketResponse != null;
            Assert.True(gotResult, "Copy operation failed.");

            bool ok = putBucketResponse.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, $"Could NOT create bucket {_NewBucketName}.");

        }
    }
}
