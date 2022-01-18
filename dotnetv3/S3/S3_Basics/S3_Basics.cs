// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// This examples shows the basic API calls for the AWS Simple Storage System
/// (Amazon S3) service. The example steps you through the process of creating
/// an Amazon S3 bucket and uploading objects to the bucket from the local
/// computer. It also shows how to copy an object within an Amazon S3 bucket,
/// list the bucket's contents, and finally how to delete the objects in the
/// bucket before deleting the bucket itself.
/// </summary>
namespace S3_BasicsScenario
{
    // snippet-start:[S3.dotnetv3.S3_BasicsScenario]
    using System;
    using System.IO;
    using System.Threading.Tasks;
    using Amazon.S3;
    using Amazon.S3.Model;

    public class S3_Basics
    {
        public static async Task Main()
        {
            // Create an Amazon S3 client object. The constructor uses the
            // default user installed on the system. To work with Amazon S3
            // features in a different AWS Region, pass the AWS Region as a
            // parameter to the client constructor.
            IAmazonS3 client = new AmazonS3Client();
            string bucketName = string.Empty;
            string filePath = string.Empty;
            string keyName = string.Empty;

            // Create a bucket.
            Console.WriteLine("Create a new Amazon S3 bucket.\n");

            Console.Write("Please enter a name for the new bucket: ");
            bucketName = Console.ReadLine();

            var success = await S3Bucket.CreateBucketAsync(client, bucketName);
            if (success)
            {
                Console.WriteLine($"Successfully created bucket: {bucketName}.");
            }
            else
            {
                Console.WriteLine($"Could not create bucket: {bucketName}.");
            }

            // Upload a file to the bucket.
            while (string.IsNullOrEmpty(filePath))
            {
                // First get the path and name of the file to upload.
                Console.Write("Please enter the path and filename of the file to upload: ");
                filePath = Console.ReadLine();

                // Confirm that the file exists on the local computer.
                if (!File.Exists(filePath))
                {
                    Console.WriteLine($"Couldn't find {filePath}. Try again.");
                    filePath = string.Empty;
                }
            }

            while (string.IsNullOrEmpty(keyName))
            {
                // Get the name to give to the object once uploaded.
                Console.Write("Now enter the name that the file will have once uploaded: ");
                keyName = Console.ReadLine();
            }

            success = await S3Bucket.UploadFileAsync(client, bucketName, keyName, filePath);

            if (success)
            {
                Console.WriteLine($"Successfully uploaded {filePath} to {bucketName}.");
            }
            else
            {
                Console.WriteLine($"Could not upload {filePath}.");
            }

            // Download an object from a bucket.
            success = await S3Bucket.DownloadObjectFromBucketAsync(client, bucketName, keyName, filePath);

            // Copy the object to a different folder in the bucket.
            string folderName = string.Empty;

            while (string.IsNullOrEmpty(keyName))
            {
                // Get the name to give to the object once uploaded.
                Console.Write("Enter the name where the file will be copies: ");
                keyName = Console.ReadLine();
            }

            await S3Bucket.CopyObjectInBucketAsync(client, bucketName, keyName, folderName);

            // List the objects in the bucket.
            await S3Bucket.ListBucketContentsAsync(client, bucketName);

            // Delete the contents of the bucket.
            await S3Bucket.DeleteBucketContentsAsync(client, bucketName);

            // Delete the bucket.
            await S3Bucket.DeleteBucketAsync(client, bucketName);
        }
    }

    // snippet-end:[S3.dotnetv3.S3_BasicsScenario]
}
