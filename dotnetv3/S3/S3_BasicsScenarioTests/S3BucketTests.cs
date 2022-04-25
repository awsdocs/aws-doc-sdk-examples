// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace S3_BasicsScenario.Tests
{
    using Xunit;
    using System;
    using System.Threading.Tasks;
    using Amazon.S3;
    using Moq;
    using Amazon.S3.Model;
    using System.Threading;
    using System.Collections.Generic;

    public class S3BucketTests
    {
        private string newBucket = "";
        private string keyName = "sample.txt";
        private string destinationFolderName = "saved_pictures";
        private string sourceFolderPath = "~/pictures";

        public IAmazonS3 CreateMockS3Client()
        {
            var mockS3Client = new Mock<IAmazonS3>();

            // PutBucketAsync called by S3Bucket.CreateBucketAsync
            mockS3Client.Setup(client => client.PutBucketAsync(
                It.IsAny<PutBucketRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<PutBucketRequest, CancellationToken>((request, token) =>
            {
                if (!String.IsNullOrEmpty(request.BucketName))
                {
                    Assert.Equal(request.BucketName, newBucket);
                }
            }).Returns((PutBucketRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new PutBucketResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK
                });
            });

            // PutObjectAsync called by S3Bucket.UploadFileAsync
            mockS3Client.Setup(client => client.PutObjectAsync(
                It.IsAny<PutObjectRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<PutObjectRequest, CancellationToken>((request, token) =>
            {
                if (!String.IsNullOrEmpty(request.BucketName))
                {
                    Assert.Equal(request.BucketName, newBucket);
                }
            }).Returns((PutObjectRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new PutObjectResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK
                });
            });

            // PutObjectAsync called by S3Bucket.DownloadObjectFromBucketAsync
            mockS3Client.Setup(client => client.GetObjectAsync(
                It.IsAny<GetObjectRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<GetObjectRequest, CancellationToken>((request, token) =>
            {
                if (!String.IsNullOrEmpty(request.BucketName))
                {
                    Assert.Equal(request.BucketName, newBucket);
                    Assert.Equal(request.Key, keyName);
                }
            }).Returns((GetObjectRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new GetObjectResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK
                });
            });

            // PutObjectAsync called by S3Bucket.CopyObjectInBucketAsync
            mockS3Client.Setup(client => client.CopyObjectAsync(
                It.IsAny<CopyObjectRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<CopyObjectRequest, CancellationToken>((request, token) =>
            {
                if (!String.IsNullOrEmpty(request.DestinationBucket))
                {
                    Assert.Equal(request.DestinationBucket, newBucket);
                    Assert.Equal(request.SourceBucket, newBucket);
                    Assert.Equal(request.DestinationBucket, newBucket);
                    Assert.Equal(request.DestinationKey, $"{destinationFolderName}\\{keyName}");
                }
            }).Returns((CopyObjectRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new CopyObjectResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK
                });
            });

            // PutObjectAsync called by S3Bucket.ListBucketContentsAsync
            mockS3Client.Setup(client => client.ListObjectsV2Async(
                It.IsAny<ListObjectsV2Request>(),
                It.IsAny<CancellationToken>()
            )).Callback<ListObjectsV2Request, CancellationToken>((request, token) =>
            {
                if (!String.IsNullOrEmpty(request.BucketName))
                {
                    Assert.Equal(request.BucketName, newBucket);
                }
            }).Returns((ListObjectsV2Request r, CancellationToken token) =>
            {
                return Task.FromResult(new ListObjectsV2Response()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK,
                    S3Objects = new List<S3Object>
                    {
                        new S3Object { BucketName = newBucket, Key = "SampleKey1"},
                        new S3Object { BucketName = newBucket, Key = "SampleKey2" }
                    }
                });
            });

            // PutObjectAsync called by S3Bucket.DeleteBucketContentsAsync
            mockS3Client.Setup(client => client.DeleteObjectAsync(
                It.IsAny<DeleteObjectRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<DeleteObjectRequest, CancellationToken>((request, token) =>
            {
                if (!String.IsNullOrEmpty(request.Key))
                {
                    Assert.Equal(request.BucketName, newBucket);
                    Assert.Equal(request.Key, keyName);
                }
            }).Returns((DeleteObjectRequest r, CancellationToken token) =>
            {
                return Task.FromResult(new DeleteObjectResponse()
                {
                    HttpStatusCode = System.Net.HttpStatusCode.OK
                });
            });

            // PutObjectAsync called by S3Bucket.DeleteBucketAsync
            mockS3Client.Setup(client => client.DeleteBucketAsync(
                It.IsAny<DeleteBucketRequest>(),
                It.IsAny<CancellationToken>()
            )).Callback<DeleteBucketRequest, CancellationToken>((request, token) =>
            {
                if (!String.IsNullOrEmpty(request.BucketName))
                {
                    Assert.Equal(request.BucketName, newBucket);
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

        [Fact()]
        public async Task CreateBucketAsyncTest()
        {
            IAmazonS3 client = CreateMockS3Client();

            var request = new PutBucketRequest
            {
                BucketName = newBucket,
            };

            var response = await client.PutBucketAsync(request);

            Assert.True(
                response.HttpStatusCode == System.Net.HttpStatusCode.OK,
                $"Could not create {newBucket}");
        }

        [Fact()]
        public async Task UploadFileAsyncTest()
        {
            IAmazonS3 client = CreateMockS3Client();

            var request = new PutObjectRequest
            {
                BucketName = newBucket,
                Key = keyName,
            };

            var response = await client.PutObjectAsync(request);

            Assert.True(
                response.HttpStatusCode == System.Net.HttpStatusCode.OK,
                $"File upload of {keyName} to {newBucket} failed.");
        }

        [Fact()]
        public async Task DownloadObjectFromBucketAsyncTest()
        {
            IAmazonS3 client = CreateMockS3Client();

            var request = new GetObjectRequest
            {
                BucketName = newBucket,
                Key = keyName,
            };

            var response = await client.GetObjectAsync(request);

            Assert.True(response.HttpStatusCode == System.Net.HttpStatusCode.OK, $"Download of {keyName} was unsuccessful.");
        }

        [Fact()]
        public async Task CopyObjectInBucketAsyncTest()
        {
            IAmazonS3 client = CreateMockS3Client();

            var request = new CopyObjectRequest
            {
                SourceBucket = newBucket,
                SourceKey = keyName,
                DestinationBucket = newBucket,
                DestinationKey = $"{destinationFolderName}\\{keyName}",
            };

            var response = await client.CopyObjectAsync(request);

            Assert.True(response.HttpStatusCode == System.Net.HttpStatusCode.OK, $"Could not copy {keyName}.");
        }

        [Fact()]
        public async Task ListBucketContentsAsyncTest()
        {
            IAmazonS3 client = CreateMockS3Client();

            var request = new ListObjectsV2Request
            {
                BucketName = newBucket,
            };

            var response = await client.ListObjectsV2Async(request);

            Assert.True(response.HttpStatusCode == System.Net.HttpStatusCode.OK, $"Could not list the objects in {newBucket}");
            Assert.True(response.S3Objects.Count == 2, $"Could not list of objects in {newBucket}");
        }

        [Fact()]
        public async Task DeleteBucketContentsAsyncTest()
        {
            IAmazonS3 client = CreateMockS3Client();

            var request = new ListObjectsV2Request
            {
                BucketName = newBucket,
            };

            var response = await client.ListObjectsV2Async(request);

            Assert.True(response.HttpStatusCode == System.Net.HttpStatusCode.OK, $"Could not list the objects in {newBucket}");
            Assert.True(response.S3Objects.Count == 2, $"Could not list of objects in {newBucket}");

            var keys = response.S3Objects;

            keys
                .ForEach(async obj => await client.DeleteObjectAsync(newBucket, obj.Key));
        }

        [Fact()]
        public async Task DeleteBucketAsyncTest()
        {
            IAmazonS3 client = CreateMockS3Client();

            var request = new DeleteBucketRequest
            {
                BucketName = newBucket,
            };

            var response = await client.DeleteBucketAsync(request);

            Assert.True(response.HttpStatusCode == System.Net.HttpStatusCode.OK, "Could not delete the bucket.");
        }
    }
}