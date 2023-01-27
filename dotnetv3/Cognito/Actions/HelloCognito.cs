// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Logging.Console;
using Microsoft.Extensions.Logging.Debug;

namespace CognitoActions;
/// <summary>
/// A class that introduces the Cognito Identity Provider by listing the
/// user pools for the account.
/// </summary>
public class HelloCognito
{
    private static ILogger logger = null!;

    static async Task Main(string[] args)
    {
        // Set up dependency injection for Amazon Cognito.
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureLogging(logging =>
                logging.AddFilter("System", LogLevel.Debug)
                    .AddFilter<DebugLoggerProvider>("Microsoft", LogLevel.Information)
                    .AddFilter<ConsoleLoggerProvider>("Microsoft", LogLevel.Trace))
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonCognitoIdentityProvider>()
                .AddTransient<CognitoWrapper>()
            )
            .Build();

        logger = LoggerFactory.Create(builder => { builder.AddConsole(); })
            .CreateLogger<HelloCognito>();

        var cognitoWrapper = host.Services.GetRequiredService<CognitoWrapper>();

        Console.Clear();
        Console.WriteLine("Hello Amazon Cognito.");
        Console.WriteLine("Let's get a list of your Amazon Cognito user pools.");

        var userPools = await cognitoWrapper.ListUserPoolsAsync();

        if (userPools.Count > 0)
        {
            userPools.ForEach(userPool =>
            {
                Console.WriteLine($"{userPool.Name}\t{userPool.Id}\t{userPool.Status}");
            });
        }
        else
        {
            Console.WriteLine("No user pools were found.");
        }
    }
}