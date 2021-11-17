// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Download an Amazon Simple Storage Service Glacier archive using the
/// high level API. This example was created using the AWS SDK for .NET version
/// 3.7 and .NET Core 5.0.
/// </summary>
namespace DownloadArchiveHighLevelExample
{
    // snippet-start:[Glacier.dotnetv3.DownloadArchiveHighLevelExample]
    using System;
    using Amazon;
    using Amazon.Glacier;
    using Amazon.Glacier.Transfer;
    using Amazon.Runtime;

    class DownloadArchiveHighLevel
    {
        private static readonly string VaultName = "examplevault";
        private static readonly string ArchiveId = "*** Provide archive ID ***";
        private static readonly string DownloadFilePath = "*** Provide the file name and path to where to store the download ***";
        private static int currentPercentage = -1;

        static void Main()
        {
            try
            {
                var manager = new ArchiveTransferManager(RegionEndpoint.USEast2);

                var options = new DownloadOptions
                {
                    StreamTransferProgress = Progress,
                };

                // Download an archive.
                Console.WriteLine("Intiating the archive retrieval job and then polling SQS queue for the archive to be available.");
                Console.WriteLine("Once the archive is available, downloading will begin.");
                manager.DownloadAsync(VaultName, ArchiveId, DownloadFilePath, options);
                Console.WriteLine("To continue, press Enter");
                Console.ReadKey();
            }
            catch (AmazonGlacierException ex)
            {
                Console.WriteLine(ex.Message);
            }

            Console.WriteLine("To continue, press Enter");
            Console.ReadKey();
        }

        static void Progress(object sender, StreamTransferProgressArgs args)
        {
            if (args.PercentDone != currentPercentage)
            {
                currentPercentage = args.PercentDone;
                Console.WriteLine("Downloaded {0}%", args.PercentDone);
            }
        }
    }

    // snippet-end:[Glacier.dotnetv3.DownloadArchiveHighLevelExample]
}
