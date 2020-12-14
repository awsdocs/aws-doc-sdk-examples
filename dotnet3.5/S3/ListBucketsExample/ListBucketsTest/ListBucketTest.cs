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
        private ListBucketsResponse _response = new ListBucketsResponse {
            Buckets = new List<S3Bucket> {
                new S3Bucket {
                    BucketName = "doc-example-bucket1",
                    CreationDate = new DateTime (2020,9,2,9,15,59)
                },
                new S3Bucket {
                    BucketName = "doc-example-bucket2",
                    CreationDate = new DateTime (2020, 11, 23, 6, 13, 0)
                }
            },
            HttpStatusCode = HttpStatusCode.OK
        };

        private IAmazonS3 CreateMockS3Client()
        {
            var mockS3Client = new Mock<IAmazonS3>();
            mockS3Client.Setup(client => client.ListBucketsAsync())
            {
                return _response;
            });

            return mockS3Client.Object;
        }

        [Fact]
        public void CheckListBuckets()
        {
            IAmazonS3 client = CreateMockS3Client();
            var response = client.ListBucketsAsync();

            bool gotResult = response.Result != null;
            Assert.True(gotResult, "List buckets failed.");

            bool ok = response.Result.HttpStatusCode == HttpStatusCode.OK;
            Assert.True(ok, "Could NOT list buckets.");

        }
    }
}
