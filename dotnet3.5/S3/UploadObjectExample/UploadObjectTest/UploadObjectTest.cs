using Amazon.S3;
using Amazon.S3.Model;
using Moq;
using System;
using System.Threading;
using System.Threading.Tasks;
using Xunit;

namespace UploadObjectTest
{
    public class UploadObjectTest
    {
        private string _BucketName;

        private IAmazonS3 CreateMockS3Client()
        {
            var mockS3Client = new Mock<IAmazonS3>();
            mockS3Client.Setup(client => client.PutObjectAsync(
                It.IsAny<PutObjectRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<ListObjectsV2Request, CancellationToken>((request, token) =>
            {
                if (!String.IsNullOrEmpty(request.BucketName))
                {
                    Assert.Equal(request.BucketName, _BucketName);
                }
            }).Returns((ListObjectsV2Request r, CancellationToken token) =>
            {
                return Task.FromResult(new PutObjectResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK
                });
            });

            return mockS3Client.Object;
        }

        [Fact]
        public async Task UploadObjectTest()
        {

        }

        public async Task UploadContentTest()
        {

        }
    }
}
