using Xunit;
using ListObjectsExample;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Amazon.S3;
using Moq;
using Amazon.S3.Model;
using System.Threading;
using System.Net;

namespace ListObjectsExample.Tests
{
    public class ListObjectsTests
    {
        const string BucketName = "doc-example-bucket";
        private IAmazonS3 CreateMockS3Client()
        {
            var mockS3Client = new Mock<IAmazonS3>();
            mockS3Client.Setup(client => client.ListObjectsAsync(
                It.IsAny<ListObjectsRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<ListObjectsRequest, CancellationToken>((request, token) =>
            {
                if (!String.IsNullOrEmpty(request.BucketName))
                {
                    Assert.Equal(request.BucketName, BucketName);
                }
            }).Returns((ListObjectsRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new ListObjectsResponse()
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

            var request = new ListObjectsRequest
            {
                BucketName = BucketName,
            };

            var response = await client.ListObjectsAsync(request);

            bool gotResult = response != null;
            Assert.True(gotResult, "List bucket objects failed.");

            bool ok = response.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, $"Could NOT list objects in bucket: {BucketName}.");
        }
    }
}