// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

/// <summary>
/// This example shows how to delete an existing empty Amazon Simple Storage
/// Service (Amazon S3) bucket.  The examples uses AWS SDK for .NET 3.5 and
/// .NET Core 5.0.
/// </summary>
namespace DeleteBucket
{
    // snippet-start:[S3.dotnetv3.DeleteBucketExample]
    using System;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.S3;

    public class DeleteBucket
    {
        static async Task Main()
        {
            // Specify the name of the bucket to delete.
            const string bucketName = "doc-example-bucket";

            var clieent = new AmazonS3Client();

            // Now delete the bucket. If the bucket you are trying to
            // delete contains any objects, the call will raise an exception.
            Console.WriteLine($"\nDeleting bucket {bucketName}...");
            await DeletingBucketAsync(clieent, bucketName);
        }

        /// <summary>
        /// DeletingBucketAsync calls the DeleteBucketAsync method
        /// to delete the Amazon S3 bucket bucketName.
        /// </summary>
        /// <param name="client">The Amazon S3 client object.</param>
        /// <param name="bucketName">The name of the bucket to be deleted.</param>
        static async Task DeletingBucketAsync(IAmazonS3 client, string bucketName)
        {
            try
            {
                var response = await client.DeleteBucketAsync(bucketName);
                if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
                {
                    Console.WriteLine($"\nThe Amazon S3 bucket, {bucketName}, has been successfully deleted.");
                }
            }
            catch (AmazonS3Exception ex)
            {
                Console.WriteLine($"Error: {ex.Message}");
            }
        }
    }

    // snippet-end:[S3.dotnetv3.DeleteBucketExample]
}
