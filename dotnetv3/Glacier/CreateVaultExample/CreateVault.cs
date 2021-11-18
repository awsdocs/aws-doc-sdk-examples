// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// This example creates an Amazon Simple Storage Service Glacier vault. This
/// example was created using the AWS SDK for .NET 3.7 and .NET Core 5.0.
/// </summary>
namespace CreateVaultExample
{
    // snippet-start:[Glacier.dotnetv3.CreateVaultExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.Glacier;
    using Amazon.Glacier.Model;

    public class CreateVault
    {
        static async Task Main(string[] args)
        {
            string vaultName = "example-vault";
            var client = new AmazonGlacierClient();
            var request = new CreateVaultRequest
            {
                // Setting the AccountId to "-" means that
                // the account associated with the default
                // client will be used.
                AccountId = "-",
                VaultName = vaultName,
            };

            var response = await client.CreateVaultAsync(request);

            Console.WriteLine($"Created {vaultName} at: {response.Location}");
        }
    }

    // snippet-end:[Glacier.dotnetv3.CreateVaultExample]
}
