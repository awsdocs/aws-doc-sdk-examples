// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using System.Diagnostics;
using Amazon.CloudWatch;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Logging.Console;
using Microsoft.Extensions.Logging.Debug;

namespace ServiceActions;

public class Program
{
    private static ILogger logger = null!;
    private static CloudWatchWrapper _cloudWatchWrapper = null!;
    private static IConfiguration _configuration = null!;

    static async Task Main(string[] args)
    {
        // Set up dependency injection for the Amazon service.
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureLogging(logging =>
                logging.AddFilter("System", LogLevel.Debug)
                    .AddFilter<DebugLoggerProvider>("Microsoft", LogLevel.Information)
                    .AddFilter<ConsoleLoggerProvider>("Microsoft", LogLevel.Trace))
            .ConfigureServices((_, services) => 
            services.AddAWSService<IAmazonCloudWatch>()
            .AddTransient<CloudWatchWrapper>()
        )
        .Build();

        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("settings.json") // Load settings from .json file.
            .AddJsonFile("settings.local.json",
                true) // Optionally, load local settings.
            .Build();

        logger = LoggerFactory.Create(builder => { builder.AddConsole(); })
            .CreateLogger<Program>();

        _cloudWatchWrapper = host.Services.GetRequiredService<CloudWatchWrapper>();

        Console.WriteLine(new string('-', 80));
        Console.WriteLine("Welcome to the Amazon CloudWatch example scenario.");
        Console.WriteLine(new string('-', 80));

        Console.WriteLine(new string('-', 80));
        Console.WriteLine("Demo of getting a CPU Utilization metric image from CloudWatch.");
        Console.WriteLine("\tGetting Image data.");
        // todo: load this from configuration
        var metricImageTest =
            "{\"metrics\":[[\"AWS/EC2\",\"CPUUtilization\",\"InstanceId\",\"i-00e1e7a1d0c6dffd4\",{\"period\":900,\"stat\":\"Average\"}],[\"AWS/EC2\",\"CPUUtilization\",\"InstanceId\",\"i-04eaea7802140257f\",{\"period\":900,\"stat\":\"Average\"}]],\"legend\":{\"position\":\"bottom\"},\"region\":\"us-east-1\",\"liveData\":false,\"title\":\"CPU Utilization: Average\",\"view\":\"timeSeries\",\"stacked\":false}";
        var memoryStream = await _cloudWatchWrapper.GetMetricImage(metricImageTest);
        var file = _cloudWatchWrapper.SaveMericImage(memoryStream, "CpuUtilization");

        ProcessStartInfo info = new ProcessStartInfo();

        Console.WriteLine($"\tFile saved to {file}.");
        Console.WriteLine($"\tPress enter to open the image.");
        Console.ReadLine();
        info.FileName = Path.Combine("ms-photos://", file);
        info.UseShellExecute = true;
        info.CreateNoWindow = true;
        info.Verb = string.Empty;

        Process.Start(info);

        Console.WriteLine(new string('-', 80));

    }
}
