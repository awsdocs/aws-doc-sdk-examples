// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Retrieve and display information about an Amazon Simple Storage Service
/// Glacier vault. The example was created using the AWS SDK for .NET version
/// 3.7 and .NET Core 5.0.
/// </summary>
namespace DescribeVaultExample
{
    // snippet-start:[Glacier.dotnetv3.DescribeArchiveExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.Glacier;
    using Amazon.Glacier.Model;

    public class DescribeVault
    {
        public static async Task Main(string[] args)
        {
            string vaultName = "example-vault";
            var client = new AmazonGlacierClient();
            var request = new DescribeVaultRequest
            {
                AccountId = "-",
                VaultName = vaultName,
            };

            var response = await client.DescribeVaultAsync(request);

            // Display the information about the vault.
            Console.WriteLine($"{response.VaultName}\tARN: {response.VaultARN}");
            Console.WriteLine($"Created on: {response.CreationDate}\tNumber of Archives: {response.NumberOfArchives}\tSize (in bytes): {response.SizeInBytes}");
            if (response.LastInventoryDate != DateTime.MinValue)
            {
                Console.WriteLine($"Last inventory: {response.LastInventoryDate}");
            }
        }
    }

    // snippet-end:[Glacier.dotnetv3.DescribeArchiveExample]
}
