// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: MIT-0

// snippet-start:[s3.dotNET.UploadObject]

using Amazon;
using Amazon.S3;
using Amazon.S3.Model;
using System;
using System.Threading.Tasks;

namespace UploadObject
{
    // The following example uploads an object to an Amazon Simple Storage
    // Service (Amazon S3) bucket. It was created using AWS SDK for .NET 3.5
    // and .NET 5.0.

    class UploadObject
    {
        // Specify your AWS Region (an example Region is shown).
        private static readonly RegionEndpoint BUCKET_REGION = RegionEndpoint.USWest2;
        private static IAmazonS3 _s3Client;

        private const string BUCKET_NAME = "doc-example-bucket";
        private const string OBJECT_NAME1 = "objectname1.txt";
        private const string OBJECT_NAME2 = "objectname2.txt";

        // For simplicity, look for the files in the Documents directory.
        private string LOCAL_PATH = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);

        static async Task Main()
        {
            _s3Client = new AmazonS3Client(BUCKET_REGION);

        }

        private static async Task UploadObjectFromFileAsync(
            IAmazonS3 client,
            string bucketName,
            string objectName,
            string filePath)
        {
            try
            {

                // 2. Put the object-set ContentType and add metadata.
                var putRequest2 = new PutObjectRequest
                {
                    BucketName = bucketName,
                    Key = objectName,
                    FilePath = filePath,
                    ContentType = "text/plain"
                };

                putRequest2.Metadata.Add("x-amz-meta-title", "someTitle");
                PutObjectResponse response = await client.PutObjectAsync(putRequest2);
            }
            catch (AmazonS3Exception e)
            {
                Console.WriteLine($"Error: {e.Message}");
            }
        }

        private static async Task UploadObjectFromContentAsync(IAmazonS3 client,
            string bucketName,
            string objectName,
            string content)
        {
            // 1. Put object-specify only key name for the new object.
            var putRequest = new PutObjectRequest
            {
                BucketName = bucketName,
                Key = objectName,
                ContentBody = content
            };

            PutObjectResponse response = await client.PutObjectAsync(putRequest);
        }
    }
}
// snippet-end:[s3.dotNET.UploadObject]