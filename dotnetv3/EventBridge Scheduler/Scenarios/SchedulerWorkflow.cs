// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.CloudFormation;
using Amazon.CloudFormation.Model;
using Amazon.Scheduler;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Logging.Console;
using Microsoft.Extensions.Logging.Debug;
using SchedulerActions;

namespace SchedulerScenario;

public class SchedulerWorkflow
{
    private static ILogger<SchedulerWorkflow> _logger;
    private static SchedulerWrapper _schedulerWrapper;
    private static IAmazonCloudFormation _amazonCloudFormation;
    private static string _snsTopic;
    private static string _eventBusArn;
    private static string _roleArn;

    public static async Task Main(string[] args)
    {
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureLogging(logging =>
                logging.AddFilter("System", LogLevel.Debug)
                    .AddFilter<DebugLoggerProvider>("Microsoft", LogLevel.Information)
                    .AddFilter<ConsoleLoggerProvider>("Microsoft", LogLevel.Trace))
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonScheduler>()
                    .AddTransient<SchedulerWrapper>()
            )
            .Build();

        _logger = LoggerFactory.Create(builder => { builder.AddConsole(); })
            .CreateLogger<SchedulerWorkflow>();

        _schedulerWrapper = host.Services.GetRequiredService<SchedulerWrapper>();

        Console.WriteLine("Welcome to the Amazon EventBridge Scheduler Workflow!");
        Console.WriteLine(new string('-', 80));

        await PrepareApplication();
        await CreateOneTimeSchedule();
        await CreateRecurringSchedule();
        await Cleanup();

        Console.WriteLine("EventBridge Scheduler workflow completed.");
    }

    /// <summary>
    /// Prepares the application by creating the necessary resources.
    /// </summary>
    /// <returns>True if the application was prepared successfully.</returns>
    public static async Task<bool> PrepareApplication()
    {
        _logger.LogInformation("Preparing the application...");
        var success = true;
        try
        {
            // Prompt the user for an email address to use for the subscription
            Console.Write("Please enter an email address to use for the subscription: ");
            var emailAddress = Console.ReadLine();

            // Prompt the user for a name for the CloudFormation stack
            Console.Write("Please enter a name for the CloudFormation stack: ");
            var stackName = Console.ReadLine();

            // Deploy the CloudFormation stack
            success = await DeployCloudFormationStack(stackName, emailAddress);

            // Create a schedule group for all workflow schedules
            success = success && await _schedulerWrapper.CreateScheduleGroupAsync("workflow-schedules");

            _logger.LogInformation("Application preparation complete.");
            return success;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "An error occurred while preparing the application.");
            return false;
        }
    }

    /// <summary>
    /// Deploys the CloudFormation stack with the necessary resources.
    /// </summary>
    /// <param name="stackName">The name of the CloudFormation stack.</param>
    /// <param name="email">The email to use for the subscription.</param>
    /// <returns>True if the stack was deployed successfully.</returns>
    private static async Task<bool> DeployCloudFormationStack(string stackName, string email)
    {
        _logger.LogInformation($"Deploying CloudFormation stack: {stackName}");

        try
        {
            var request = new CreateStackRequest
            {
                StackName = stackName,
                TemplateBody = await File.ReadAllTextAsync("resources/cfn_template.yaml"),
                Parameters = new List<Parameter>()
                {
                    new Parameter
                    {
                        ParameterKey = "Email",
                        ParameterValue = email
                    }
                },
                Capabilities = { Capability.CAPABILITY_NAMED_IAM }
            };

            var response = await _amazonCloudFormation.CreateStackAsync(request);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                _logger.LogInformation($"CloudFormation stack creation started: {stackName}");

                // Wait for the stack to be in CREATE_COMPLETE state
                bool stackCreated = await WaitForStackCompletion(response.StackId);

                if (stackCreated)
                {
                    // Retrieve the output values
                    await GetStackOutputs(response.StackId);
                    return true;
                }
                else
                {
                    _logger.LogError($"CloudFormation stack creation failed: {stackName}");
                    return false;
                }
            }
            else
            {
                _logger.LogError($"Failed to create CloudFormation stack: {stackName}");
                return false;
            }
        }
        catch (AlreadyExistsException)
        {
            _logger.LogWarning($"CloudFormation stack '{stackName}' already exists. Please provide a unique name.");
            return await DeployCloudFormationStack(GetUniqueStackName(stackName), email);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, $"An error occurred while deploying the CloudFormation stack: {stackName}");
            return false;
        }
    }

    /// <summary>
    /// Waits for the CloudFormation stack to be in the CREATE_COMPLETE state.
    /// </summary>
    /// <param name="client">The CloudFormation client.</param>
    /// <param name="stackId">The ID of the CloudFormation stack.</param>
    /// <returns>True if the stack was created successfully.</returns>
    private static async Task<bool> WaitForStackCompletion(string stackId)
    {
        int retryCount = 0;
        const int maxRetries = 60;
        const int retryDelay = 10000; // 10 seconds

        while (retryCount < maxRetries)
        {
            var describeStacksRequest = new DescribeStacksRequest
            {
                StackName = stackId
            };

            var describeStacksResponse = await _amazonCloudFormation.DescribeStacksAsync(describeStacksRequest);

            if (describeStacksResponse.Stacks.Count > 0 &&
                describeStacksResponse.Stacks[0].StackStatus == StackStatus.CREATE_COMPLETE)
            {
                _logger.LogInformation($"CloudFormation stack creation complete: {stackId}");
                return true;
            }

            _logger.LogInformation($"Waiting for CloudFormation stack creation to complete: {stackId}");
            await Task.Delay(retryDelay);
            retryCount++;
        }

        _logger.LogError($"Timed out waiting for CloudFormation stack creation to complete: {stackId}");
        return false;
    }

    /// <summary>
    /// Retrieves the output values from the CloudFormation stack.
    /// </summary>
    /// <param name="client">The CloudFormation client.</param>
    /// <param name="stackId">The ID of the CloudFormation stack.</param>
    private static async Task GetStackOutputs(string stackId)
    {
        var describeStacksRequest = new DescribeStacksRequest
        {
            StackName = stackId
        };

        var describeStacksResponse = await _amazonCloudFormation.DescribeStacksAsync(describeStacksRequest);

        if (describeStacksResponse.Stacks.Count > 0)
        {
            var stack = describeStacksResponse.Stacks[0];
            _snsTopic = GetStackOutputValue(stack, "SNSTopicName");
            _eventBusArn = GetStackOutputValue(stack, "EventBusArn");
            _roleArn = GetStackOutputValue(stack, "RoleARN");
        }
        else
        {
            _logger.LogError($"Failed to retrieve CloudFormation stack outputs: {stackId}");
        }
    }

    private static string GetStackOutputValue(Stack stack, string outputKey)
    {
        var output = stack.Outputs.Find(o => o.OutputKey == outputKey);
        return output?.OutputValue;
    }

    private static string GetUniqueStackName(string baseName)
    {
        return $"{baseName}-{Guid.NewGuid().ToString().Substring(0, 8)}";
    }


    /// <summary>
    /// Creates a new schedule group.
    /// </summary>
    /// <param name="groupName">The name of the schedule group.</param>
    private static async Task CreateScheduleGroup(string groupName)
    {
        await _schedulerWrapper.CreateScheduleGroupAsync(groupName);
    }

    /// <summary>
    /// Creates a one-time schedule to send an initial event on the new EventBus.
    /// </summary>
    /// <returns>True if the one-time schedule was created successfully.</returns>
    public static async Task<bool> CreateOneTimeSchedule()
    {
        // Create a one-time schedule with a flexible time window
        // Set the schedule to delete after completion
        // Print the URL for the user to view logs

        return true;
    }

    /// <summary>
    /// Creates a recurring schedule to send events X times per Y hours.
    /// </summary>
    /// <returns>True if the recurring schedule was created successfully.</returns>
    public static async Task<bool> CreateRecurringSchedule()
    {
        // Prompt the user for how many times per hour and for how many hours
        // Create the recurring schedule
        // Print the URL for the user to view logs
        // Delete the schedule when the user is finished

        return true;
    }

    /// <summary>
    /// Cleans up the resources created during the workflow.
    /// </summary>
    /// <returns>True if the cleanup was successful.</returns>
    public static async Task<bool> Cleanup()
    {
        // Prompt the user to confirm cleanup
        // Delete the schedule group
        // Destroy the CloudFormation stack and wait for it to be removed

        return true;
    }
}
