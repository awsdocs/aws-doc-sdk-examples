// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace TransferUtilityBasics
{
    public class TransferMethods
    {
        // snippet-start:[S3.dotnetv3.TransferUtilityBasics.DownloadSingleFileAsync]
        public static async Task<bool> DownloadSingleFileAsync(
        TransferUtility transferUtil,
            string bucketName,
            string keyName,
            string localPath)
        {
            await transferUtil.DownloadAsync(new TransferUtilityDownloadRequest
            {
                BucketName = bucketName,
                Key = keyName,
                FilePath = $"{localPath}\\{keyName}",
            });

            if (File.Exists($"{localPath}\\{keyName}"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        // snippet-end:[S3.dotnetv3.TransferUtilityBasics.DownloadSingleFileAsync]

        // snippet-start:[S3.dotnetv3.TransferUtilityBasics.DownloadS3DirectoryAsync]
        public static async Task<bool> DownloadS3DirectoryAsync(
            TransferUtility transferUtil,
            string bucketName,
            string s3Path,
            string localPath)
        {
            await transferUtil.DownloadDirectoryAsync(new TransferUtilityDownloadDirectoryRequest
            {
                BucketName = bucketName,
                LocalDirectory = localPath,
                S3Directory = s3Path,
            });

            if (Directory.Exists(localPath))
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        // snippet-end:[S3.dotnetv3.TransferUtilityBasics.DownloadS3DirectoryAsync]

        // snippet-start:[S3.dotnetv3.TransferUtilityBasics.UploadSingleFileAsync]
        public static async Task<bool> UploadSingleFileAsync(
            TransferUtility transferUtil,
            string bucketName,
            string fileName,
            string localPath)
        {
            if (File.Exists($"{localPath}\\{fileName}"))
            {
                try
                {
                    await transferUtil.UploadAsync(new TransferUtilityUploadRequest
                    {
                        BucketName = bucketName,
                        Key = fileName,
                        FilePath = $"{localPath}\\{fileName}",
                    });

                    return true;
                }
                catch (AmazonS3Exception s3Ex)
                {
                    Console.WriteLine($"Could not upload {fileName} from {localPath} because:");
                    Console.WriteLine(s3Ex.Message);
                    return false;
                }
            }
            else
            {
                Console.WriteLine($"{fileName} does not exist in {localPath}");
                return false;
            }
        }
        // snippet-end:[S3.dotnetv3.TransferUtilityBasics.UploadSingleFileAsync]

        // snippet-start:[S3.dotnetv3.TransferUtilityBasics.UploadFullDirectoryAsync]
        public static async Task<bool> UploadFullDirectoryAsync(
            TransferUtility transferUtil,
            string bucketName,
            string keyPrefix,
            string localPath)
        {
            if (Directory.Exists(localPath))
            {
                try
                {
                    await transferUtil.UploadDirectoryAsync(new TransferUtilityUploadDirectoryRequest
                    {
                        BucketName = bucketName,
                        KeyPrefix = keyPrefix,
                        Directory = localPath,
                    });

                    return true;
                }
                catch (AmazonS3Exception s3Ex)
                {
                    Console.WriteLine($"Can't upload the contents of {localPath} because:");
                    Console.WriteLine(s3Ex?.Message);
                    return false;
                }
            }
            else
            {
                Console.WriteLine($"The directory {localPath} does not exist.");
                return false;
            }
        }

        // snippet-end:[S3.dotnetv3.TransferUtilityBasics.UploadFullDirectoryAsync]
    }
}
