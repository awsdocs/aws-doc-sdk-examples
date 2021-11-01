// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

/// <summary>
/// List the Amazon Simple Storage Service Glacier jobs for a vault. This
/// example was created using the AWS .NET SDK version 3.7 and .NET Core 5.0.
/// </summary>
namespace ListJobsExample
{
    // snippet-start:[Glacier.dotnetv3.ListJobsExample]
    using System;
    using System.Collections.Generic;
    using System.Threading.Tasks;
    using Amazon.Glacier;
    using Amazon.Glacier.Model;

    class ListJobs
    {
        static async Task Main(string[] args)
        {
            var client = new AmazonGlacierClient();
            var vaultName = "example-vault";

            var request = new ListJobsRequest
            {
                // Using a hyphen "=" for the Account Id will
                // cause the SDK to use the Account Id associated
                // with the default user.
                AccountId = "-",
                VaultName = vaultName,
            };

            var response = await client.ListJobsAsync(request);

            if (response.JobList.Count > 0)
            {
                response.JobList.ForEach(job => {
                    Console.WriteLine($"{job.CreationDate} {job.JobDescription}");
                });
            }
            else
            {
                Console.WriteLine($"No jobs were found for {vaultName}.");
            }
        }
    }

    // snippet-end:[Glacier.dotnetv3.ListJobsExample]
}
