// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// Uses Amazon Simple Storage Service Glacier to retrieve a list of the vaults
/// that belong to the default user. As it was written, it shows the vaults
/// that ar in the same AWS Region as the default user. If you want to show
/// the vaults in another Region, pass the region endpoint to the Amazon
/// Glacier client constructor.
///
/// This example uses the AWS SDK for .NET version 3.7 and .NET Core 5.0.
/// </summary>
namespace ListVaultsExample
{
    // snippet-start:[Glacier.dotnetv3.ListVaultExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.Glacier;
    using Amazon.Glacier.Model;

    public class ListVaults
    {
        public static async Task Main(string[] args)
        {
            var client = new AmazonGlacierClient();
            var request = new ListVaultsRequest
            {
                AccountId = "-",
                Limit = 5,
            };

            var response = await client.ListVaultsAsync(request);

            List<DescribeVaultOutput> vaultList = response.VaultList;

            vaultList.ForEach(v => { Console.WriteLine($"{v.VaultName} ARN: {v.VaultARN}");  });
        }
    }

    // snippet-end:[Glacier.dotnetv3.ListVaultExample]
}
