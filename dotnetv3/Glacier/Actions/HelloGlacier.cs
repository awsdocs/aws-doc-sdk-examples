// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace ServiceActions;

using Amazon.Glacier;
using Amazon.Glacier.Model;

public class HelloGlacier
{
    static async Task Main()
    {
        var glacierService = new AmazonGlacierClient();

        Console.WriteLine("Hello, Amazon Glacier");
        Console.WriteLine("Let's list your Glacier vaults:");

        var glacierVaultPaginator = glacierService.Paginators.ListVaults(
            new ListVaultsRequest { AccountId = "-" });

        await foreach (var vault in glacierVaultPaginator.VaultList)
        {
            Console.WriteLine($"{vault.CreationDate,-24}{vault.VaultName,-30}\t{vault.VaultARN}");
        }
    }
}
