// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Add tags to an Amazon Simple Storage Service Glacier vault. This example
/// was created using the AWS SDK for .NET version 3.7 and .NET Core 5.0.
/// </summary>
namespace AddTagsToVaultExample
{
    // snippet-start:[Glacier.dotnetv3.AddTagsToVaultExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.Glacier;
    using Amazon.Glacier.Model;

    public class AddTagsToVault
    {
        public static async Task Main(string[] args)
        {
            string vaultName = "example-vault";

            var client = new AmazonGlacierClient();
            var request = new AddTagsToVaultRequest
            {
                Tags = new Dictionary<string, string>
                {
                    { "examplekey1", "examplevalue1" },
                    { "examplekey2", "examplevalue2" },
                },
                AccountId = "-",
                VaultName = vaultName,
            };

            var response = await client.AddTagsToVaultAsync(request);
        }
    }

    // snippet-end:[Glacier.dotnetv3.AddTagsToVaultExample]
}
