---
combined: true
prompt: |
  Provide a .NET implementation for the CreateOneTimeSchedule method in the SchedulerWorkflow class given below. Use the service wrapper methods in SchedulerWrapper for the service calls. Create a schedule that is set to delete after completion.

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
Here's the implementation of the `CreateOneTimeSchedule` method in the `SchedulerWorkflow` class:

```csharp
using System;
using System.Threading.Tasks;
using Amazon.Scheduler.Model;
using Microsoft.Extensions.Logging;

namespace Scenarios
{
    public class SchedulerWorkflow
    {
        private static ILogger<SchedulerWorkflow> _logger;
        private static SchedulerWrapper _schedulerWrapper;
        private static string _eventBusArn;

        /// <summary>
        /// Creates a one-time schedule to send an initial event on the new EventBus.
        /// </summary>
        /// <returns>True if the one-time schedule was created successfully.</returns>
        public static async Task<bool> CreateOneTimeSchedule()
        {
            _logger.LogInformation("Creating a one-time schedule to send an initial event...");

            try
            {
                // Prompt the user for a schedule name
                Console.Write("Enter a name for the one-time schedule: ");
                string scheduleName = Console.ReadLine();

                // Create a one-time schedule with a flexible time window
                await _schedulerWrapper.CreateScheduleAsync(
                    scheduleName,
                    "rate(1 minute)",
                    _eventBusArn,
                    true,
                    true);

                // Set the schedule to delete after completion
                await _schedulerWrapper.DeleteScheduleAfterCompletionAsync(scheduleName);

                // Print the URL for the user to view logs
                string eventBusLogUrl = $"https://console.aws.amazon.com/events/eventbus";
                _logger.LogInformation($"You can view the logs for the initial event at: {eventBusLogUrl}");

                _logger.LogInformation("One-time schedule created successfully.");
                return true;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "An error occurred while creating the one-time schedule.");
                return false;
            }
        }
    }
}
```

Here's a breakdown of the changes:

1. The `CreateOneTimeSchedule` method prompts the user for a name for the one-time schedule.
2. It then calls the `CreateScheduleAsync` method of the `SchedulerWrapper` class, passing the schedule name, schedule expression (to run the schedule once a minute), the EventBridge Bus ARN, a flag to indicate a flexible time window, and a flag to set the schedule to delete after completion.
3. After creating the schedule, it calls the `DeleteScheduleAfterCompletionAsync` method to ensure the schedule is deleted after it runs.
4. The method then prints the URL for the user to view the logs for the initial event on the EventBridge Bus.
5. The method includes appropriate error handling and logging using the `_logger` object.

The `SchedulerWrapper` class should provide the necessary methods for creating and managing schedules, including `CreateScheduleAsync` and `DeleteScheduleAfterCompletionAsync`.