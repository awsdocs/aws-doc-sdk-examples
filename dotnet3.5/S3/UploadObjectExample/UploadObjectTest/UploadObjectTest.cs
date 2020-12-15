using Amazon.S3;
using Amazon.S3.Model;
using Moq;
using System;
using System.Net;
using System.Threading;
using System.Threading.Tasks;
using Xunit;

namespace UploadObjectTest
{
    public class UploadObjectTest
    {
        private string _BucketName = "doc-example-bucket";

        private IAmazonS3 CreateMockS3Client()
        {
            var mockS3Client = new Mock<IAmazonS3>();
            mockS3Client.Setup(client => client.PutObjectAsync(
                It.IsAny<PutObjectRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<PutObjectRequest, CancellationToken>((request, token) =>
            {
                if (!String.IsNullOrEmpty(request.BucketName))
                {
                    Assert.Equal(request.BucketName, _BucketName);
                }
            }).Returns((PutObjectRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new PutObjectResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK
                });
            });

            return mockS3Client.Object;
        }

        [Fact]
        public async Task UploadObjectFromFileAsyncTest()
        {
            IAmazonS3 client = CreateMockS3Client();

            var request = new PutObjectRequest
            {
                BucketName = _BucketName,
                Key = "objectname1.txt",
                FilePath = @"./objectname1.txt",
                ContentType = "text/plain"
            };

            var response = await client.PutObjectAsync(request);

            bool gotResult = response != null;
            Assert.True(gotResult, "Uploading file to bucket failed.");

            bool ok = response.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, $"Could NOT upload objects to bucket: {_BucketName}.");

        }

        [Fact]
        public async Task UploadContentAsyncTest()
        {
            IAmazonS3 client = CreateMockS3Client();

            var request = new PutObjectRequest
            {
                BucketName = _BucketName,
                Key = "objectname2.txt",
                ContentBody = "And here is some test content."
            };

            var response = await client.PutObjectAsync(request);

            bool gotResult = response != null;
            Assert.True(gotResult, "Uploading objects failed.");

            bool ok = response.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, $"Could NOT upload objects to bucket: {_BucketName}.");

        }
    }
}
