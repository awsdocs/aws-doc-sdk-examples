---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: msg_bdrk_01Csd9pBpuLTULH2pHMFkjhM
  lastRun: 2024-08-05T19:50:00.724Z
  model: claude-3-haiku-20240307
  statistics:
    firstByteLatency: 3164
    inputTokenCount: 53132
    invocationLatency: 12865
    outputTokenCount: 1357
prompt: |
  Provide a .NET implementation for the Cleanup method in the SchedulerWorkflow class given below. Use the service wrapper methods in SchedulerWrapper for the service calls. Prompt the user to confirm to delete the schedule group, to delete the CloudFormation stack and wait for it to be removed.

  Each method, even helper methods, should include xmldoc comment blocks for summary, parameters, and outputs.

  Use the following instructions for .NET coding standards: {{code.standards}} 

  <example>
      public class SchedulerWorkflow
    {
        private static ILogger<SchedulerWorkflow> _logger;
        private static SchedulerWrapper _schedulerWrapper;

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

            _logger = LoggerFactory.Create(builder =>
            {
                builder.AddConsole();
            }).CreateLogger<SchedulerWorkflow>();

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
            // Prompt the user for an email address and stack name
            // Deploy the CloudFormation stack
            // Store the stack outputs
            // Create a schedule group

            return true;
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
  </example>

  ---
---
Here's the implementation of the `Cleanup` method in the `SchedulerWorkflow` class:

```csharp
using System;
using System.Threading.Tasks;
using Amazon.CloudFormation;
using Amazon.CloudFormation.Model;
using Microsoft.Extensions.Logging;

namespace Scenarios
{
    public class SchedulerWorkflow
    {
        private static ILogger<SchedulerWorkflow> _logger;
        private static SchedulerWrapper _schedulerWrapper;
        private static string _scheduleGroupName;
        private static string _stackName;

        /// <summary>
        /// Cleans up the resources created during the workflow.
        /// </summary>
        /// <returns>True if the cleanup was successful.</returns>
        public static async Task<bool> Cleanup()
        {
            _logger.LogInformation("Cleaning up the resources...");

            try
            {
                // Prompt the user to confirm cleanup
                if (!await PromptUserToConfirmCleanup())
                {
                    _logger.LogInformation("Cleanup skipped.");
                    return true;
                }

                // Delete the schedule group
                await DeleteScheduleGroup();

                // Destroy the CloudFormation stack and wait for it to be removed
                await DeleteCloudFormationStack();

                _logger.LogInformation("Cleanup complete.");
                return true;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "An error occurred while cleaning up the resources.");
                return false;
            }
        }

        private static async Task<bool> PromptUserToConfirmCleanup()
        {
            Console.Write("Do you want to delete all resources created by this workflow? (y/n) ");
            string response = Console.ReadLine();

            return response.ToLowerInvariant() == "y";
        }

        private static async Task DeleteScheduleGroup()
        {
            await _schedulerWrapper.DeleteScheduleGroupAsync(_scheduleGroupName);
            _logger.LogInformation($"Schedule group '{_scheduleGroupName}' deleted.");
        }

        private static async Task DeleteCloudFormationStack()
        {
            var client = new AmazonCloudFormationClient();
            var request = new DeleteStackRequest
            {
                StackName = _stackName
            };

            await client.DeleteStackAsync(request);
            _logger.LogInformation($"CloudFormation stack '{_stackName}' is being deleted. This may take a few minutes.");

            bool stackDeleted = await WaitForStackDeletion(client, _stackName);

            if (stackDeleted)
            {
                _logger.LogInformation($"CloudFormation stack '{_stackName}' has been deleted.");
            }
            else
            {
                _logger.LogError($"Failed to delete CloudFormation stack '{_stackName}'.");
            }
        }

        private static async Task<bool> WaitForStackDeletion(AmazonCloudFormationClient client, string stackName)
        {
            int retryCount = 0;
            const int maxRetries = 60;
            const int retryDelay = 10000; // 10 seconds

            while (retryCount < maxRetries)
            {
                var describeStacksRequest = new DescribeStacksRequest
                {
                    StackName = stackName
                };

                try
                {
                    var describeStacksResponse = await client.DescribeStacksAsync(describeStacksRequest);

                    if (describeStacksResponse.Stacks.Count == 0 || describeStacksResponse.Stacks[0].StackStatus == StackStatus.DELETE_COMPLETE)
                    {
                        return true;
                    }
                }
                catch (AmazonCloudFormationException ex) when (ex.ErrorCode == "ValidationError")
                {
                    // Stack does not exist, so it has been successfully deleted
                    return true;
                }

                _logger.LogInformation($"Waiting for CloudFormation stack '{stackName}' to be deleted...");
                await Task.Delay(retryDelay);
                retryCount++;
            }

            _logger.LogError($"Timed out waiting for CloudFormation stack '{stackName}' to be deleted.");
            return false;
        }
    }
}
```

Here's a breakdown of the changes:

1. The `Cleanup` method first prompts the user to confirm whether they want to delete all the resources created by the workflow.
2. If the user confirms, the method calls the `DeleteScheduleGroup` method to delete the schedule group.
3. After deleting the schedule group, the method calls the `DeleteCloudFormationStack` method to delete the CloudFormation stack.
4. The `DeleteCloudFormationStack` method creates an `AmazonCloudFormationClient` instance and sends a `DeleteStackRequest` to delete the stack.
5. The method then calls the `WaitForStackDeletion` helper method to wait for the stack to be deleted.
6. The `WaitForStackDeletion` method polls the stack status using the `DescribeStacksAsync` method until the stack is in the `DELETE_COMPLETE` state or the maximum number of retries is reached.
7. If the stack is successfully deleted, the method logs a success message. Otherwise, it logs an error message.
8. The `PromptUserToConfirmCleanup` method prompts the user to confirm the cleanup and returns a boolean value indicating whether the user confirmed.

The `SchedulerWrapper` class should provide the necessary methods for deleting schedule groups, such as `DeleteScheduleGroupAsync`.