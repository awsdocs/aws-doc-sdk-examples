// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.AWSSupport;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;

namespace SupportActions;

/// <summary>
/// Hello AWS Support example.
/// </summary>
public static class HelloSupport
{
    static async Task Main(string[] args)
    {
        // snippet-start:[Support.dotnetv3.HelloSupport]

        // Use the AWS .NET Core Setup package to set up dependency injection for the AWS Support service.
        // Use your AWS profile name, or leave it blank to use the default profile.
        // You must have a Business, Enterprise On-Ramp, or Enterprise Support subscription, or an exception will be thrown.
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonAWSSupport>()
            ).Build();

        // For this example, get the client from the host after setup.
        var supportClient = host.Services.GetRequiredService<IAmazonAWSSupport>();

        var response = await supportClient.DescribeServicesAsync();
        Console.WriteLine($"\tHello AWS Support! There are {response.Services.Count} services available.");

        // snippet-end:[Support.dotnetv3.HelloSupport]

    }
}
