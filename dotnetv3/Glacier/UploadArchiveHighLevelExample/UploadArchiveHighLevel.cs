// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Upload a file to an Amazon Simple Storage Service Glacier vault. This
/// example was created with the AWS SDK for .NET version 3.7 and .NET Core 5.0.
/// </summary>
namespace UploadArchiveHighLevelExample
{
    // snippet-start:[Glacier.dotnetv3.UploadArchiveHighLevelExample]
    using System;
    using System.Threading.Tasks;
    using Amazon;
    using Amazon.Glacier;
    using Amazon.Glacier.Transfer;

    public class UploadArchiveHighLevel
    {
        private static readonly string VaultName = "example-vault";
        private static readonly string ArchiveToUpload = "*** Provide file name (with full path) to upload ***";

        public static async Task Main()
        {
            try
            {
                var manager = new ArchiveTransferManager(RegionEndpoint.USWest2);

                // Upload an archive.
                var response = await manager.UploadAsync(VaultName, "upload archive test", ArchiveToUpload);

                Console.WriteLine("Copy and save the ID for use in other examples.");
                Console.WriteLine($"Archive ID: {response.ArchiveId}");
                Console.WriteLine("To continue, press Enter");
                Console.ReadKey();
            }
            catch (AmazonGlacierException ex)
            {
                Console.WriteLine(ex.Message);
            }
        }
    }

    // snippet-end:[Glacier.dotnetv3.UploadArchiveHighLevelExample]
}
