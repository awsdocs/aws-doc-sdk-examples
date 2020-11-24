// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
using Amazon;
using Amazon.S3;
using Amazon.S3.Model;
using Moq;
using System;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using Xunit;

namespace CopyObjectTest
{
    public class CopyObjectTest
    {
        // This application uses XUnit and moq to test the CopyObject Example.

        private string _SourceBucket;
        private string _DestinationBucket;
        private string _SourceObjectKey;
        private string _DestinationObjectKey;

        private IAmazonS3 CreateMockS3Client()
        {
            var mockS3Client = new Mock<IAmazonS3>();
            mockS3Client.Setup(client => client.CopyObjectAsync(
                It.IsAny<CopyObjectRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<CopyObjectRequest, CancellationToken>((request, token) =>
            {
                if (!String.IsNullOrEmpty(request.SourceBucket))
                {
                    Assert.Equal(request.SourceBucket, _SourceBucket);
                }
            }).Returns((CopyObjectRequest r, CancellationToken token) =>
                {
                    return Task.FromResult(new CopyObjectResponse()
                    {
                        HttpStatusCode = System.Net.HttpStatusCode.OK
                    });
                });

            return mockS3Client.Object;
        }

        [Fact]
        public async Task CheckCopyObject()
        {
            IAmazonS3 client = CreateMockS3Client();

            _SourceBucket = "doc-example-bucket1";
            _DestinationBucket = "doc-example-bucket2";
            _SourceObjectKey = "cute_photo.jpg";
            _DestinationObjectKey = "even_cuter_copy.jpg";

            var result = await CopyObject.CopyObject.CopyingObjectAsync(
                client,
                _SourceObjectKey,
                _DestinationObjectKey,
                _SourceBucket,
                _DestinationBucket
            );

            bool gotResult = result != null;
            Assert.True(gotResult, "Copy operation failed.");

            bool ok = result.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, $"Could NOT copy item {_SourceObjectKey} from {_SourceBucket}.");

        }
    }
}
