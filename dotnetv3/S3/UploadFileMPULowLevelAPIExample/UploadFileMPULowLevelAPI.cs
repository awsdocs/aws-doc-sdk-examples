// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Uses the Amazon Simploe Storage Service (Amazon S3) low-level API to
/// upload an object from the local system to an Amazon S3 bucket. This
/// example was created using the AWS SDK for .NET verion 3.7 and
/// .NET Core 5.0.
/// </summary>
namespace UploadFileMPULowLevelAPIExample
{
    // snippet-start:[S3.dotnetv3.UploadFileMPULowLevelAPIExample]
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Threading.Tasks;
    using Amazon.Runtime;
    using Amazon.S3;
    using Amazon.S3.Model;

    public class UploadFileMPULowLevelAPI
    {
        public static async Task Main()
        {
            string bucketName = "doc-example-bucket";
            string keyName = "example.png";
            string filePath = $"example_files\\{keyName}";

            // If the AWS Region defined for your default user is different
            // from the Region where your Amazon S3 bucket is located,
            // pass the Region name to the Amazon S3 client object's constructor.
            // For example: RegionEndpoint.USWest2 or RegionEndpoint.USEast2.
            IAmazonS3 client = new AmazonS3Client();

            Console.WriteLine("Uploading an object...");
            await UploadObjectAsync(client, bucketName, keyName, filePath);
        }

        /// <summary>
        /// Uses the low-level API to upload an object from the local system to
        /// to an Amazon S3 bucket.
        /// </summary>
        /// <param name="client">The initialized Amazon S3 client object used to
        /// perform the multi-part upload.</param>
        /// <param name="bucketName">>The name of the bucket to which to upload
        /// the file.</param>
        /// <param name="keyName">The file name to be used in the
        /// destination Amazon S3 bucket.</param>
        /// <param name="filePath">The path, including the file name of the
        /// file to be uploaded to the Amazon S3 bucket.</param>
        public static async Task UploadObjectAsync(
            IAmazonS3 client,
            string bucketName,
            string keyName,
            string filePath)
        {
            // Create list to store upload part responses.
            List<UploadPartResponse> uploadResponses = new();

            // Setup information required to initiate the multipart upload.
            InitiateMultipartUploadRequest initiateRequest = new()
            {
                BucketName = bucketName,
                Key = keyName,
            };

            // Initiate the upload.
            InitiateMultipartUploadResponse initResponse =
                await client.InitiateMultipartUploadAsync(initiateRequest);

            // Upload parts.
            long contentLength = new FileInfo(filePath).Length;
            long partSize = 5 * (long)Math.Pow(2, 20); // 5 MB

            try
            {
                Console.WriteLine("Uploading parts");

                long filePosition = 0;
                for (int i = 1; filePosition < contentLength; i++)
                {
                    UploadPartRequest uploadRequest = new()
                    {
                        BucketName = bucketName,
                        Key = keyName,
                        UploadId = initResponse.UploadId,
                        PartNumber = i,
                        PartSize = partSize,
                        FilePosition = filePosition,
                        FilePath = filePath,
                    };

                    // Track upload progress.
                    uploadRequest.StreamTransferProgress +=
                        new EventHandler<StreamTransferProgressArgs>(UploadPartProgressEventCallback);

                    // Upload a part and add the response to our list.
                    uploadResponses.Add(await client.UploadPartAsync(uploadRequest));

                    filePosition += partSize;
                }

                // Setup to complete the upload.
                CompleteMultipartUploadRequest completeRequest = new()
                {
                    BucketName = bucketName,
                    Key = keyName,
                    UploadId = initResponse.UploadId,
                };
                completeRequest.AddPartETags(uploadResponses);

                // Complete the upload.
                CompleteMultipartUploadResponse completeUploadResponse =
                    await client.CompleteMultipartUploadAsync(completeRequest);
            }
            catch (Exception exception)
            {
                Console.WriteLine($"An AmazonS3Exception was thrown: {exception.Message}");

                // Abort the upload.
                AbortMultipartUploadRequest abortMPURequest = new()
                {
                    BucketName = bucketName,
                    Key = keyName,
                    UploadId = initResponse.UploadId,
                };
                await client.AbortMultipartUploadAsync(abortMPURequest);
            }
        }

        /// <summary>
        /// Handles the UploadProgress even to display the progress of the
        /// Amazon S3 multi-part upload.
        /// </summary>
        /// <param name="sender">The object that raised the event.</param>
        /// <param name="e">The event parameters.</param>
        public static void UploadPartProgressEventCallback(object sender, StreamTransferProgressArgs e)
        {
            Console.WriteLine($"{e.TransferredBytes}/{e.TotalBytes}");
        }
    }

    // snippet-end:[S3.dotnetv3.UploadFileMPULowLevelAPIExample]
}
