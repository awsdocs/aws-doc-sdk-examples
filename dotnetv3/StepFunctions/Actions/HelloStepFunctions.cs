// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// snippet-start:[StepFunctions.dotnetv3.HelloStepFunctions]
namespace StepFunctionsActions;

public class HelloStepFunctions
{
    private static ILogger _logger = null!;

    static async Task Main(string[] args)
    {
        // Set up dependency injection for AWS Step Functions.
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureLogging(logging =>
                logging.AddFilter("System", LogLevel.Debug)
                    .AddFilter<DebugLoggerProvider>("Microsoft", LogLevel.Information)
                    .AddFilter<ConsoleLoggerProvider>("Microsoft", LogLevel.Trace))
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonStepFunctions>()
                .AddTransient<StepFunctionsWrapper>()
            )
            .Build();

        _logger = LoggerFactory.Create(builder => { builder.AddConsole(); })
            .CreateLogger<HelloStepFunctions>();

        var stepFunctionsClient = host.Services.GetRequiredService<IAmazonStepFunctions>();

        Console.WriteLine("Welcome to AWS Step Functions. Let's list your state machines:");
        var stateMachineListRequest = new ListStateMachinesRequest { MaxResults = 10 };

        do
        {
            var response = await stepFunctionsClient.ListStateMachinesAsync(stateMachineListRequest);

            if (response.StateMachines.Count == 0)
            {
                Console.WriteLine("Couldn't find any state machines.");
            }
            if (response.NextToken is not null)
            {
                stateMachineListRequest.NextToken = response.NextToken;
            }

            response.StateMachines.ForEach(stateMachine =>
            {
                Console.WriteLine($"Activity Name: {stateMachine.Name}\tCreated on: {stateMachine.CreationDate}");
                Console.WriteLine($"{stateMachine.Type}");
            });
        }
        while (stateMachineListRequest.NextToken is not null);
    }
}

// snippet-end:[StepFunctions.dotnetv3.HelloStepFunctions]