// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// snippet-start:[StepFunctions.dotnetv3.StepFunctionsBasics]
namespace StepFunctionsBasics;

public class StepFunctionsBasics
{
    private static ILogger _logger = null!;
    private static IConfigurationRoot _configuration;

    static async Task Main(string[] args)
    {
        // Set up dependency injection for AWS StepFunctions.
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
            .CreateLogger<StepFunctionsBasics>();

        // Load configuration settings.
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("settings.json") // Load test settings from .json file.
            .AddJsonFile("settings.local.json",
                true) // Optionally load local settings.
            .Build();

        var activityName = _configuration["ActivityName"];
        var stateMachineName = _configuration["StateMachineName"];
        var executionName = _configuration["ExecutionName"];
        var roleArn = _configuration["RoleArn"];

        var uiMethods = new UiMethods();
        var stepFunctionsWrapper = host.Services.GetRequiredService<StepFunctionsWrapper>();

        Console.Clear();
        uiMethods.DisplayOverview();
        uiMethods.PressEnter();

        uiMethods.DisplayTitle("Create activity");
        Console.WriteLine("Let's start by creating an activity.");
        var activityArn = await stepFunctionsWrapper.CreateActivity(activityName);

        uiMethods.DisplayTitle("Create state machine");
        Console.WriteLine("Now we'll create a state machine.");

        // Define the state machine.
        var stateDefinition = @"{
          ""Comment"": ""An example using a Task state."",
          ""StartAt"": ""getGreeting"",
          ""Version"": ""1.0"",
          ""TimeoutSeconds"": 300,
          ""States"":
          {
            ""getGreeting"": {
              ""Type"": ""Task"",
              ""Resource"": """ + activityArn + @""",
              ""End"": true
            }
          }
        }";

        // Create the state machine.
        var stateMachineArn = await stepFunctionsWrapper.CreateStateMachine(stateMachineName, stateDefinition, roleArn);
        uiMethods.PressEnter();

        Console.WriteLine("Now we'll start execution of the state machine.");
        // The json information to pass to the StartExecution call.
        var executionJson = @"{ ""who"" : ""AWS Step Functions"" }";

        // Start the state machine execution.
        var executionArn = await stepFunctionsWrapper.StartExecution(executionName, executionJson, stateMachineArn);
        Console.WriteLine("State machine started.");

        var success = await stepFunctionsWrapper.StopExecution(executionArn);
        Console.WriteLine("State machine stopped.");
        uiMethods.PressEnter();

        Console.WriteLine("Now let's take a look at the execution values for the state machine.");

        // List the executions.
        var executions = await stepFunctionsWrapper.ListExecutions(stateMachineArn);

        uiMethods.DisplayTitle("Step function executions");
        executions.ForEach(execution =>
        {
            Console.WriteLine($"{execution.Name}\t{execution.StartDate} to {execution.StopDate}");
        });

        // Now delete the state machine and the activity.
        uiMethods.DisplayTitle("Clean up resources");
        Console.WriteLine("Deleting the state machine...");

        success = await stepFunctionsWrapper.DeleteStateMachine(stateMachineArn);
        Console.WriteLine("State machine deleted.");

        Console.WriteLine("Deleting the activity...");
        success = await stepFunctionsWrapper.DeleteActivity(activityArn);
        Console.WriteLine("Activity deleted.");

        Console.WriteLine("The Amazon Step Functions scenario is now complete.");
    }
}

// snippet-end:[StepFunctions.dotnetv3.StepFunctionsBasics]