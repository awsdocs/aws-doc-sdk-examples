---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: msg_bdrk_015MCyuHEknF6bjZMgCfz5Av
  lastRun: 2024-06-14T21:26:29.657Z
  model: claude-3-haiku-20240307
  statistics:
    firstByteLatency: 845
    inputTokenCount: 13823
    invocationLatency: 5509
    outputTokenCount: 838
prompt: |
  Provide a .NET implementation for the DeleteSchedule method in the SchedulerWrapper class. Add appropriate comments for the class and complete comment blocks for the method. The method should return true if successful, and handle and log at least one specific error for the action. Do not require a cancellation token parameter. Use a class-level logger to log output.
---
Here's the implementation of the `DeleteScheduleAsync` method in the `SchedulerWrapper` class:

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
        /// Deletes an existing schedule from Amazon EventBridge Scheduler.
        /// </summary>
        /// <param name="scheduleId">The ID of the schedule to delete.</param>
        /// <returns>True if the schedule was deleted successfully, false otherwise.</returns>
        public async Task<bool> DeleteScheduleAsync(string scheduleId)
        {
            try
            {
                var request = new DeleteScheduleRequest
                {
                    ScheduleId = scheduleId
                };

                var response = await _amazonScheduler.DeleteScheduleAsync(request);

                if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
                {
                    _logger.LogInformation($"Successfully deleted schedule with ID '{scheduleId}'.");
                    return true;
                }
                else
                {
                    _logger.LogError($"Failed to delete schedule with ID '{scheduleId}'. HTTP status code: {response.HttpStatusCode}");
                    return false;
                }
            }
            catch (ResourceNotFoundException ex)
            {
                _logger.LogError($"Failed to delete schedule with ID '{scheduleId}' because the resource was not found: {ex.Message}");
                return false;
            }
            catch (Exception ex)
            {
                _logger.LogError($"An error occurred while deleting schedule with ID '{scheduleId}': {ex.Message}");
                return false;
            }
        }
    }
}
```

Here's a breakdown of the changes:

1. The `DeleteScheduleAsync` method takes the following parameter:
   - `scheduleId`: The ID of the schedule to delete.
2. The method creates a `DeleteScheduleRequest` object and sets the `ScheduleId` property.
3. The method calls `_amazonScheduler.DeleteScheduleAsync` with the request object and handles the response.
4. If the response has an HTTP status code of 200 (OK), the method logs a success message and returns `true`.
5. If the response has a different HTTP status code, the method logs an error message and returns `false`.
6. The method also handles the `ResourceNotFoundException` and logs an error message, returning `false` in this case.
7. Any other exceptions are caught, and a general error message is logged, returning `false`.

The `SchedulerWrapper` class now provides a convenient way to delete schedules from the EventBridge Scheduler service, with appropriate error handling and logging.