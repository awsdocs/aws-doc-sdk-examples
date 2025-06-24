// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[ControlTower.dotnetv4.HelloControlTower]

using LogLevel = Microsoft.Extensions.Logging.LogLevel;

namespace ControlTowerActions;

/// <summary>
/// A class that introduces the AWS Control Tower by listing the
/// landing zones for the account.
/// </summary>
public class HelloControlTower
{
    private static ILogger logger = null!;

    static async Task Main(string[] args)
    {
        // Set up dependency injection for AWS Control Tower.
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureLogging(logging =>
                logging.AddFilter("System", LogLevel.Debug)
                    .AddFilter<DebugLoggerProvider>("Microsoft", LogLevel.Information)
                    .AddFilter<ConsoleLoggerProvider>("Microsoft", LogLevel.Trace))
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonControlTower>()
                .AddTransient<ControlTowerWrapper>()
            )
            .Build();

        logger = LoggerFactory.Create(builder => { builder.AddConsole(); })
            .CreateLogger<HelloControlTower>();

        var amazonClient = host.Services.GetRequiredService<IAmazonControlTower>();

        Console.Clear();
        Console.WriteLine("Hello AWS Control Tower.");
        Console.WriteLine("Let's get a list of your AWS Control Tower landing zones.");

        var landingZones = new List<LandingZoneSummary>();

        var landingZonesPaginator = amazonClient.Paginators.ListLandingZones(new ListLandingZonesRequest());

        await foreach (var response in landingZonesPaginator.Responses)
        {
            landingZones.AddRange(response.LandingZones);
        }

        if (landingZones.Count > 0)
        {
            landingZones.ForEach(landingZone =>
            {
                Console.WriteLine($"{landingZone.Arn}\t{landingZone.Status}");
            });
        }
        else
        {
            Console.WriteLine("No landing zones were found.");
        }
    }
}

// snippet-end:[ControlTower.dotnetv4.HelloControlTower]