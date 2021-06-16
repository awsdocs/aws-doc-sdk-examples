// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace BucketACLExample.Tests
{
    using System;
    using System.Threading;
    using System.Threading.Tasks;
    using Amazon.S3;
    using Amazon.S3.Model;
    using Moq;
    using Xunit;

    public class BucketACLTests
    {
        const string BucketName = "new-doc-example-bucket";

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
                    Assert.Equal(request.BucketName, BucketName);
                }
            }).Returns((PutBucketRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new PutBucketResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK
                });
            });

            mockS3Client.Setup(client => client.GetACLAsync(
                It.IsAny<GetACLRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<GetACLRequest, CancellationToken>((request, token) =>
            {
                if (!String.IsNullOrEmpty(request.BucketName))
                {
                    Assert.Equal(request.BucketName, BucketName);
                }
            }).Returns((GetACLRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new GetACLResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK
                });
            });

            return mockS3Client.Object;
        }

        [Fact()]
        public async Task CreateBucketUseCannedACLAsyncTest()
        {
            const string NewBucketName = BucketName;

            IAmazonS3 client = CreateMockS3Client();
            var ok = await BucketACL.CreateBucketUseCannedACLAsync(client, NewBucketName);

            Assert.True(ok, "Error occurred creating bucket.");
        }
    }
}