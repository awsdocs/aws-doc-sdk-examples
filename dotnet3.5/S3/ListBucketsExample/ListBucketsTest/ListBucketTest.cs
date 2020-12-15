// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

using Amazon;
using Amazon.S3;
using Amazon.S3.Model;
using Moq;
using System;
using System.Collections.Generic;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using Xunit;

namespace ListBucketsTest
{
    public class ListBucketsTest
    {
        private string _BucketName;

        private IAmazonS3 CreateMockS3Client()
        {
            var mockS3Client = new Mock<IAmazonS3>();
            mockS3Client.Setup(client => client.ListBucketsAsync(
                It.IsAny<CancellationToken>()
            )).Returns((CancellationToken token) =>
            {
                return Task.FromResult(new ListBucketsResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK
                });
            });

            return mockS3Client.Object;
        }

        [Fact]
        public async Task ListBucketsAsyncTest()
        {
            IAmazonS3 client = CreateMockS3Client();

            var response = await client.ListBucketsAsync();

            bool gotResult = response != null;
            Assert.True(gotResult, "List bucket objects failed.");

            bool ok = response.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, $"Could NOT list objects in bucket: {_BucketName}.");

        }
    }
}
