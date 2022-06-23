// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace UploadDirMPUHighLevelAPIExample
{
    // snippet-start:[S3.dotnetv3.UploadDirMPUHighLevelAPIExample]
    using System;
    using System.IO;
    using System.Threading.Tasks;
    using Amazon.S3;
    using Amazon.S3.Transfer;

    /// <summary>
    /// This example uses the Amazon Simple Storage Service (Amazon S3)
    /// TransferUtility to copy an entire local directory to an Amazon S3
    /// bucket. The example was created using the AWS SDK for .NET version
    /// 3.7 and .NET Core 5.0.
    /// </summary>
    public class UploadDirMPUHighLevelAPI
    {
        public static async Task Main()
        {
            string existingBucketName = "doc-example-bucket";
            string directoryPath = @"directory_to_upload\";

            // The example uploads only .txt files.
            string wildCard = "*.txt";

            // If the AWS Region defined for your default user is different
            // from the Region where your Amazon S3 bucket is located,
            // pass the Region name to the Amazon S3 client object's constructor.
            // For example: RegionEndpoint.USWest2 or RegionEndpoint.USEast2.
            IAmazonS3 client = new AmazonS3Client();

            await UploadDirAsync(client, existingBucketName, directoryPath, wildCard);
        }

        /// <summary>
        /// Uses an Amazon S3 multipart transfer to upload all of the text files in
        /// a local directory to an Amazon S3 bucket.
        /// </summary>
        /// <param name="client">The initialized Amazon S3 client object used to
        /// perform the multipart upload.</param>
        /// <param name="bucketName">The name of the bucket to which the files
        /// will be uploaded from the local directory.</param>
        /// <param name="directoryPath">The path of the local directory that
        /// contains the files to upload to the Amazon S3 bucket.</param>
        /// <param name="wildCard">The wild card used to filter the files to
        /// be uploaded.</param>
        private static async Task UploadDirAsync(
            IAmazonS3 client,
            string bucketName,
            string directoryPath,
            string wildCard)
        {
            try
            {
                var directoryTransferUtility =
                    new TransferUtility(client);

                // Upload the entire contents of a local directory to an S3
                // bucket.
                await directoryTransferUtility.UploadDirectoryAsync(
                    directoryPath,
                    bucketName);
                Console.WriteLine("Upload statement 1 completed");

                // Upload only the text files from a local directory using a
                // recursive search to find text files in child directories.
                await directoryTransferUtility.UploadDirectoryAsync(
                                               directoryPath,
                                               bucketName,
                                               wildCard,
                                               SearchOption.AllDirectories);
                Console.WriteLine("Upload statement 2 completed");

                // Performs the same as before using the
                // TransferUtilityUploadDirectoryRequest instead of individual
                // parameters.
                var request = new TransferUtilityUploadDirectoryRequest
                {
                    BucketName = bucketName,
                    Directory = directoryPath,
                    SearchOption = SearchOption.AllDirectories,
                    SearchPattern = wildCard,
                };

                await directoryTransferUtility.UploadDirectoryAsync(request);
                Console.WriteLine("Upload statement 3 completed");
            }
            catch (AmazonS3Exception ex)
            {
                Console.WriteLine(
                        $"Error: {ex.Message}");
            }
        }
    }

    // snippet-end:[S3.dotnetv3.UploadDirMPUHighLevelAPIExample]
}
