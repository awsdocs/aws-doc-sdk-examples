// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// snippet-start:[Cognito.dotnetv3.HelloCognito]

namespace CognitoActions;

/// <summary>
/// A class that introduces the Amazon Cognito Identity Provider by listing the
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

        var amazonClient = host.Services.GetRequiredService<IAmazonCognitoIdentityProvider>();

        Console.Clear();
        Console.WriteLine("Hello Amazon Cognito.");
        Console.WriteLine("Let's get a list of your Amazon Cognito user pools.");

        var userPools = new List<UserPoolDescriptionType>();

        var userPoolsPaginator = amazonClient.Paginators.ListUserPools(new ListUserPoolsRequest());

        await foreach (var response in userPoolsPaginator.Responses)
        {
            userPools.AddRange(response.UserPools);
        }

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

// snippet-end:[Cognito.dotnetv3.HelloCognito]
