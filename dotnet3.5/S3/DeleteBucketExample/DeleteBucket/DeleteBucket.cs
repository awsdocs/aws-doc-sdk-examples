// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
// snippet-start:[S3.dotnet35.DeleteBucket]

using Amazon;
using Amazon.S3;
using System;
using System.Threading.Tasks;

namespace DeleteBucket
{
    class DeleteBucket
    {
        // This example shows how to delete an existing and empty bucket.
        //  The examples uses AWS .NET SDK 3.5 and .NET 5.0

        // Change the name of the following constant to the AWS Region containing your bucket
        // The value below is just an example.
        private static readonly RegionEndpoint BUCKET_REGION = RegionEndpoint.USWest2;
        private static IAmazonS3 _s3Client;

        // Specify the name of the bucket to delete.
        private const string BUCKET_NAME = "doc-example-bucket";

        static void Main()
        {
            _s3Client = new AmazonS3Client(BUCKET_REGION);

            // Now delete the bucket. If the bucket you are trying to
            // delete contains any objects, the call will raise an exception.
            Console.WriteLine($"\nDeleting bucket {BUCKET_NAME}...");
            DeletingBucketAsync(_s3Client, BUCKET_NAME).Wait();
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="client"></param>
        /// <param name="bucketName"></param>
        /// <returns></returns>
        static async Task DeletingBucketAsync(IAmazonS3 client, string bucketName)
        {
            try
            {
                var deleteResponse = await client.DeleteBucketAsync(bucketName);
                Console.WriteLine($"\nResult: {deleteResponse.HttpStatusCode.ToString()}");
            }
            catch(AmazonS3Exception ex)
            {
                Console.WriteLine($"Error: {ex.Message}");
            }
        }
    }
}
// snippet-end:[S3.dotnet35.DeleteBucket]