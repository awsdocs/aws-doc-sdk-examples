// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier:  Apache - 2.0

using Amazon;
using Amazon.S3;
using Amazon.S3.Model;
using System;
using System.Threading.Tasks;

namespace UploadObject
{
    // The following example uploads an object to an Amazon Simple Storage
    // Service (Amazon S3) bucket in two different ways. First, it uploads
    // an existing file from the supposed file path and then it creates an
    // object in the example bucket by putting text into the named file. It
    // was created using AWS SDK for .NET 3.5 and .NET 5.0.

    class UploadObject
    {
        // Specify your AWS Region (an example Region is shown).
        private static readonly RegionEndpoint BUCKET_REGION = RegionEndpoint.USWest2;
        private static IAmazonS3 _s3Client;

        private const string BUCKET_NAME = "doc-example-bucket";
        private const string OBJECT_NAME1 = "objectname1.txt";
        private const string OBJECT_NAME2 = "objectname2.txt";

        private static string LOCAL_PATH = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);

        static async Task Main()
        {
            _s3Client = new AmazonS3Client(BUCKET_REGION);

            // The method expects the full path, including the file name.
            var path = $"{LOCAL_PATH}\\{OBJECT_NAME1}";

            await UploadObjectFromFileAsync(_s3Client, BUCKET_NAME, OBJECT_NAME1, path);
            await UploadObjectFromContentAsync(_s3Client, BUCKET_NAME, OBJECT_NAME2, "This is a test...");
        }

        /// <summary>
        /// This method uploads a single file to an Amazon S3 bucket. This
        /// example method also adds metadata for the uploaded file.
        /// </summary>
        /// <param name="client">An initialized Amazon S3 client object.</param>
        /// <param name="bucketName">The name of the S3 bucket to upload the
        /// file to.</param>
        /// <param name="objectName">The destination file name.</param>
        /// <param name="filePath">The full path, including file name, to the
        /// file to upload. This doesn't necessarily have to be the same as the
        /// name of the destination file.</param>
        private static async Task UploadObjectFromFileAsync(
            IAmazonS3 client,
            string bucketName,
            string objectName,
            string filePath)
        {
            try
            {
                var putRequest = new PutObjectRequest
                {
                    BucketName = bucketName,
                    Key = objectName,
                    FilePath = filePath,
                    ContentType = "text/plain"
                };

                putRequest.Metadata.Add("x-amz-meta-title", "someTitle");

                PutObjectResponse response = await client.PutObjectAsync(putRequest);
            }
            catch (AmazonS3Exception e)
            {
                Console.WriteLine($"Error: {e.Message}");
            }
        }

        /// <summary>
        /// This method creates a file in an Amazon S3 bucket that contains the text
        /// passed to the method.
        /// </summary>
        /// <param name="client">An initialized S3 client object.</param>
        /// <param name="bucketName">The name of the bucket where the file will
        /// be created.</param>
        /// <param name="objectName">The name of the file that will be created.</param>
        /// <param name="content">A string containing the content to put in the
        /// file on the destination S3 bucket.</param>
        private static async Task UploadObjectFromContentAsync(IAmazonS3 client,
            string bucketName,
            string objectName,
            string content)
        {
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
