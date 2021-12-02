// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// List the AWS Key Management Service (AWS KMS) aliases that have been defined for
/// the keys in the same AWS Region as the default user. If you want to list
/// the aliases in a different region, pass the region to the client
/// constructor. This example was created using the AWS SDK for .NET version
/// 3.7 and .NET Core 5.0.
/// </summary>
namespace ListAliasesExample
{
    // snippet-start:[KMS.dotnetv3.ListAliasesExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.KeyManagementService;
    using Amazon.KeyManagementService.Model;

    public class ListAliases
    {
        public static async Task Main()
        {
            var client = new AmazonKeyManagementServiceClient();
            var request = new ListAliasesRequest();
            var response = new ListAliasesResponse();

            do
            {
                response = await client.ListAliasesAsync(request);

                response.Aliases.ForEach(alias =>
                {
                    Console.WriteLine($"Created: {alias.CreationDate} Last Update: {alias.LastUpdatedDate} Name: {alias.AliasName}");
                });

                request.Marker = response.NextMarker;
            }
            while (response.Truncated);
        }
    }

    // snippet-end:[KMS.dotnetv3.ListAliasesExample]
}
