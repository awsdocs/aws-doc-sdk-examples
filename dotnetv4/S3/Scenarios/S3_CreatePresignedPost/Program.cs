// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace S3Scenarios;

// snippet-start:[S3.dotnetv4.CreatePresignedPostProgram]
/// <summary>
/// Main entry point for the scenario program.
/// </summary>
public class Program
{
    /// <summary>
    /// Main method that sets up the services and runs the scenario.
    /// </summary>
    /// <param name="args">Command line arguments. Use --non-interactive to run in non-interactive mode.</param>
    /// <returns>Async task.</returns>
    public static async Task Main(string[] args)
    {
        // Check for non-interactive mode
        bool isInteractive = !args.Contains("--non-interactive");
        
        // Set up dependency injection for Amazon S3
        using var host = Microsoft.Extensions.Hosting.Host.CreateDefaultBuilder(args)
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonS3>()
                    .AddTransient<S3Wrapper>()
            )
            .Build();

        // Get the services
        var s3Client = host.Services.GetRequiredService<IAmazonS3>();
        var loggerFactory = LoggerFactory.Create(builder => builder.AddConsole());
        
        // Create the wrapper instance
        var s3Wrapper = new S3Wrapper(s3Client, loggerFactory.CreateLogger<S3Wrapper>());
        
        // Create UI methods
        var uiMethods = new UiMethods();
        
        // Create the scenario instance
        var logger = loggerFactory.CreateLogger<CreatePresignedPostScenario>();
        var scenario = new CreatePresignedPostScenario(s3Wrapper, logger, uiMethods, isInteractive);
        
        // Run the scenario
        await scenario.RunAsync();
    }
}
// snippet-end:[S3.dotnetv4.CreatePresignedPostProgram]
