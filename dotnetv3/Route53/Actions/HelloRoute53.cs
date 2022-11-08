// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Route53Domains;
using Amazon.Route53Domains.Model;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;

namespace Route53Actions;

// snippet-start:[Route53.dotnetv3.HelloRoute53]

public static class HelloRoute53Domains
{
    static async Task Main(string[] args)
    {
        // Use the AWS .NET Core Setup package to set up dependency injection for the AAmazon Route 53 domains service.
        // Use your AWS profile name, or leave it blank to use the default profile.
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonRoute53Domains>()
            ).Build();

        // Now the client is available for injection.
        var route53Client = host.Services.GetRequiredService<IAmazonRoute53Domains>();

        // You can use await and any of the async methods to get a response.
        var response = await route53Client.ListPricesAsync(new ListPricesRequest { Tld = "com" });
        Console.WriteLine($"Hello Amazon Route 53 Domains! Following are prices for .com domain operations:");
        var comPrices = response.Prices.FirstOrDefault();
        if (comPrices != null)
        {
            Console.WriteLine($"\tRegistration: {comPrices.RegistrationPrice?.Price} {comPrices.RegistrationPrice?.Currency}");
            Console.WriteLine($"\tRenewal: {comPrices.RenewalPrice?.Price} {comPrices.RenewalPrice?.Currency}");
            Console.WriteLine($"\tTransfer: {comPrices.TransferPrice?.Price} {comPrices.TransferPrice?.Currency}");
            Console.WriteLine($"\tChange Ownership: {comPrices.ChangeOwnershipPrice?.Price} {comPrices.ChangeOwnershipPrice?.Currency}");
            Console.WriteLine($"\tRestoration: {comPrices.RestorationPrice?.Price} {comPrices.RestorationPrice?.Currency}");
        }
    }
}
// snippet-end:[Route53.dotnetv3.HelloRoute53]
