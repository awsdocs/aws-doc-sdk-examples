// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.AWSSupport;
using Amazon.Extensions.NETCore.Setup;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Logging.Console;
using Microsoft.Extensions.Logging.Debug;

namespace SupportActions;

// snippet-start:[Support.dotnetv3.HelloSupport]

/// <summary>
/// Hello AWS Support example.
/// </summary>
public static class HelloSupport
{
    static async Task Main(string[] args)
    {
        // Set up dependency injection for the AWS Support service. 
        // Use your AWS profile name, or leave blank to use the default profile.
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureLogging(logging =>
                logging.AddFilter("System", LogLevel.Debug)
                    .AddFilter<DebugLoggerProvider>("Microsoft", LogLevel.Information)
                    .AddFilter<ConsoleLoggerProvider>("Microsoft", LogLevel.Trace))
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonAWSSupport>(new AWSOptions(){Profile = "dotnettesting"})
                    .AddTransient<SupportWrapper>()
            )
            .Build();

        var logger = LoggerFactory.Create(builder =>
        {
            builder.AddConsole();
        }).CreateLogger(typeof(HelloSupport));

        var supportWrapper = host.Services.GetRequiredService<SupportWrapper>();

        Console.WriteLine(new string('-', 80));
        Console.WriteLine("Welcome to the AWS Support Hello Support example.");
        Console.WriteLine(new string('-', 80));

        try
        {
            var apiSupported = await supportWrapper.VerifySubscription();
            if (!apiSupported)
            {
                logger.LogError("You must have a Business, Enterprise On-Ramp, or Enterprise Support " +
                                 "plan to use the AWS Support API. \n\tPlease upgrade your subscription to run these examples.");
                return;
            }

            var services = await supportWrapper.DescribeServices();
            Console.WriteLine($"AWS Support client returned {services.Count} services.");

            Console.WriteLine(new string('-', 80));
            Console.WriteLine("Hello Support example is complete.");
            Console.WriteLine(new string('-', 80));
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "There was a problem executing the scenario.");
        }
    }
}
// snippet-end:[Support.dotnetv3.HelloSupport]