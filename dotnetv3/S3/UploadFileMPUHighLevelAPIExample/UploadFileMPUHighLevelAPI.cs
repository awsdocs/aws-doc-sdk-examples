// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// This example shows how to use the TransferUtility api for Amazon Simple
/// Storage Service (Amazon S3) to upload a single file to an Amazon S3
/// bucket. The example was created using the AWS SDK for .NET version 3.7
/// and .NET Core 5.0.
/// </summary>
namespace UploadFileMPUHighLevelAPIExample
{
    // snippet-start:[S3.dotnetv3.UploadFileMPUHighLevelAPIExample]
    using System;
    using System.IO;
    using System.Threading.Tasks;
    using Amazon.S3;
    using Amazon.S3.Transfer;

    public class UploadFileMPUHighLevelAPI
    {
        public static async Task Main()
        {
            string bucketName = "doc-example-bucket";
            string keyName = "sample_pic.png";
            string path = "filepath/directory/";
            string filePath = $"{path}{keyName}";

            // If the AWS Region defined for your default user is different
            // from the Region where your Amazon S3 bucket is located,
            // pass the Region name to the Amazon S3 client object's constructor.
            // For example: RegionEndpoint.USWest2 or RegionEndpoint.USEast2.
            IAmazonS3 client = new AmazonS3Client();

            await UploadFileAsync(client, bucketName, keyName, filePath);
        }

        /// <summary>
        /// This method uploads a file to an Amazon S3 bucket using a TransferUtility
        /// object.
        /// </summary>
        /// <param name="client">The initialized Amazon S3 client object used to
        /// perform the multi-part upload.</param>
        /// <param name="bucketName">The name of the bucket to which to upload
        /// the file.</param>
        /// <param name="keyName">The file name to be used in the
        /// destination Amazon S3 bucket.</param>
        /// <param name="filePath">The path, including the file name of the
        /// file to be uploaded to the Amazon S3 bucket.</param>
        private static async Task UploadFileAsync(
            IAmazonS3 client,
            string bucketName,
            string keyName,
            string filePath)
        {
            try
            {
                var fileTransferUtility =
                    new TransferUtility(client);

                // Upload a file. The file name is used as the object key name.
                await fileTransferUtility.UploadAsync(filePath, bucketName);
                Console.WriteLine("Upload 1 completed");

                // Specify object key name explicitly.
                await fileTransferUtility.UploadAsync(filePath, bucketName, keyName);
                Console.WriteLine("Upload 2 completed");

                // Upload data from a System.IO.Stream object.
                using (var fileToUpload =
                    new FileStream(filePath, FileMode.Open, FileAccess.Read))
                {
                    await fileTransferUtility.UploadAsync(
                        fileToUpload,
                        bucketName,
                        keyName);
                }

                Console.WriteLine("Upload 3 completed");

                // Option 4. Specify advanced settings.
                var fileTransferUtilityRequest = new TransferUtilityUploadRequest
                {
                    BucketName = bucketName,
                    FilePath = filePath,
                    StorageClass = S3StorageClass.StandardInfrequentAccess,
                    PartSize = 6291456, // 6 MB.
                    Key = keyName,
                    CannedACL = S3CannedACL.PublicRead,
                };

                fileTransferUtilityRequest.Metadata.Add("param1", "Value1");
                fileTransferUtilityRequest.Metadata.Add("param2", "Value2");

                await fileTransferUtility.UploadAsync(fileTransferUtilityRequest);
                Console.WriteLine("Upload 4 completed");
            }
            catch (AmazonS3Exception ex)
            {
                Console.WriteLine($"Error: {ex.Message}");
            }
        }
    }

    // snippet-end:[S3.dotnetv3.UploadFileMPUHighLevelAPIExample]
}
