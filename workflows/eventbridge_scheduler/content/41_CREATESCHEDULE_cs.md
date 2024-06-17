---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: msg_bdrk_01HTLKqRYcexvYfhTHyDz5ms
  lastRun: 2024-06-14T21:18:57.655Z
  model: claude-3-haiku-20240307
  statistics:
    firstByteLatency: 831
    inputTokenCount: 10770
    invocationLatency: 9362
    outputTokenCount: 1179
prompt: |
  Provide a .NET implementation for the CreateSchedule method in the SchedulerWrapper class. Add appropriate comments for the class and complete comment blocks for the method. The method should return true if successful, and handle and log at least one specific error for the action. Do not require a cancellation token parameter. Include a parameter to set a flexible time window if desired. Use a class-level logger to log output.
---
Here's the implementation of the `CreateScheduleAsync` method in the `SchedulerWrapper` class, with appropriate comments and error handling:

```csharp
using System;
using System.Threading.Tasks;
using Amazon.Scheduler;
using Amazon.Scheduler.Model;
using Microsoft.Extensions.Logging;

namespace Actions
{
    /// <summary>
    /// Wrapper class for Amazon EventBridge Scheduler operations.
    /// </summary>
    public class SchedulerWrapper
    {
        private readonly IAmazonScheduler _amazonScheduler;
        private readonly ILogger<SchedulerWrapper> _logger;

        /// <summary>
        /// Constructor for the SchedulerWrapper class.
        /// </summary>
        /// <param name="amazonScheduler">The injected EventBridge Scheduler client.</param>
        /// <param name="logger">The injected logger.</param>
        public SchedulerWrapper(IAmazonScheduler amazonScheduler, ILogger<SchedulerWrapper> logger)
        {
            _amazonScheduler = amazonScheduler;
            _logger = logger;
        }

        /// <summary>
        /// Creates a new schedule in Amazon EventBridge Scheduler.
        /// </summary>
        /// <param name="name">The name of the schedule.</param>
        /// <param name="scheduleExpression">The schedule expression that defines when the schedule should run.</param>
        /// <param name="scheduleGroupName">The name of the schedule group to which the schedule should be added.</param>
        /// <param name="useFlexibleTimeWindow">Indicates whether to use a flexible time window for the schedule.</param>
        /// <returns>True if the schedule was created successfully, false otherwise.</returns>
        public async Task<bool> CreateScheduleAsync(string name, string scheduleExpression, string scheduleGroupName, bool useFlexibleTimeWindow = false)
        {
            try
            {
                var request = new CreateScheduleRequest
                {
                    Name = name,
                    ScheduleExpression = scheduleExpression,
                    ScheduleGroupName = scheduleGroupName
                };

                if (useFlexibleTimeWindow)
                {
                    request.FlexibleTimeWindow = new FlexibleTimeWindow
                    {
                        Mode = FlexibleTimeWindowMode.FLEXIBLE
                    };
                }

                var response = await _amazonScheduler.CreateScheduleAsync(request);

                if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
                {
                    _logger.LogInformation($"Successfully created schedule '{name}' in schedule group '{scheduleGroupName}'.");
                    return true;
                }
                else
                {
                    _logger.LogError($"Failed to create schedule '{name}' in schedule group '{scheduleGroupName}'. HTTP status code: {response.HttpStatusCode}");
                    return false;
                }
            }
            catch (ServiceQuotaExceededException ex)
            {
                _logger.LogError($"Failed to create schedule '{name}' in schedule group '{scheduleGroupName}' due to service quota exceeded: {ex.Message}");
                return false;
            }
            catch (Exception ex)
            {
                _logger.LogError($"An error occurred while creating schedule '{name}' in schedule group '{scheduleGroupName}': {ex.Message}");
                return false;
            }
        }
    }
}
```

Here's a breakdown of the changes:

1. The class-level `ILogger<SchedulerWrapper>` is injected into the constructor and used throughout the class to log output.
2. The `CreateScheduleAsync` method takes the following parameters:
   - `name`: The name of the schedule.
   - `scheduleExpression`: The schedule expression that defines when the schedule should run.
   - `scheduleGroupName`: The name of the schedule group to which the schedule should be added.
   - `useFlexibleTimeWindow`: (Optional) Indicates whether to use a flexible time window for the schedule.
3. The method creates a `CreateScheduleRequest` object and sets the necessary properties.
4. If `useFlexibleTimeWindow` is `true`, the method adds a `FlexibleTimeWindow` object to the request.
5. The method calls `_amazonScheduler.CreateScheduleAsync` with the request object and handles the response.
6. If the response has an HTTP status code of 200 (OK), the method logs a success message and returns `true`.
7. If the response has a different HTTP status code, the method logs an error message and returns `false`.
8. The method also handles the `ServiceQuotaExceededException` and logs an error message, returning `false` in this case.
9. Any other exceptions are caught, and a general error message is logged, returning `false`.

The `SchedulerWrapper` class now provides a convenient way to create schedules in the EventBridge Scheduler service, with appropriate error handling and logging.