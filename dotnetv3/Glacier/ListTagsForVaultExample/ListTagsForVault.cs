// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// List the Amazon Simple Storage Service Glacier (Amazon S3 Glacier) tags
/// that are attached to a vault. This example was created using the AWS SDK
/// for .NET version 3.7 and .NET Core 5.0.
/// </summary>
namespace ListTagsForVaultExample
{
    // snippet-start:[Glacier.dotnetv3.ListTagsForVaultExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.Glacier;
    using Amazon.Glacier.Model;

    class ListTagsForVault
    {
        static async Task Main(string[] args)
        {
            var client = new AmazonGlacierClient();
            var vaultName = "example-vault";

            var request = new ListTagsForVaultRequest
            {
                // Using a hyphen "=" for the Account Id will
                // cause the SDK to use the Account Id associated
                // with the default user.
                AccountId = "-",
                VaultName = vaultName,
            };

            var response = await client.ListTagsForVaultAsync(request);

            if (response.Tags.Count > 0)
            {
                foreach (KeyValuePair<string, string> tag in response.Tags)
                {
                    Console.WriteLine($"Key: {tag.Key}, value: {tag.Value}");
                }
            }
            else
            {
                Console.WriteLine($"{vaultName} has no tags.");
            }
        }
    }

    // snippet-end:[Glacier.dotnetv3.ListTagsForVaultExample]
}
