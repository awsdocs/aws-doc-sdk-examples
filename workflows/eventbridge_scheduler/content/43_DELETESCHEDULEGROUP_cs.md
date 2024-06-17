---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: msg_bdrk_011HPWmLs5GzhD16xCepBkDG
  lastRun: 2024-06-14T21:25:47.559Z
  model: claude-3-haiku-20240307
  statistics:
    firstByteLatency: 844
    inputTokenCount: 12923
    invocationLatency: 6330
    outputTokenCount: 823
prompt: |
  Provide a .NET implementation for the DeleteScheduleGroup method in the SchedulerWrapper class. Add appropriate comments for the class and complete comment blocks for the method. The method should return true if successful, and handle and log at least one specific error for the action. Do not require a cancellation token parameter. Use a class-level logger to log output.
---
Here's the implementation of the `DeleteScheduleGroupAsync` method in the `SchedulerWrapper` class:

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
        /// Deletes an existing schedule group from Amazon EventBridge Scheduler.
        /// </summary>
        /// <param name="name">The name of the schedule group to delete.</param>
        /// <returns>True if the schedule group was deleted successfully, false otherwise.</returns>
        public async Task<bool> DeleteScheduleGroupAsync(string name)
        {
            try
            {
                var request = new DeleteScheduleGroupRequest
                {
                    Name = name
                };

                var response = await _amazonScheduler.DeleteScheduleGroupAsync(request);

                if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
                {
                    _logger.LogInformation($"Successfully deleted schedule group '{name}'.");
                    return true;
                }
                else
                {
                    _logger.LogError($"Failed to delete schedule group '{name}'. HTTP status code: {response.HttpStatusCode}");
                    return false;
                }
            }
            catch (ResourceNotFoundException ex)
            {
                _logger.LogError($"Failed to delete schedule group '{name}' because the resource was not found: {ex.Message}");
                return false;
            }
            catch (Exception ex)
            {
                _logger.LogError($"An error occurred while deleting schedule group '{name}': {ex.Message}");
                return false;
            }
        }
    }
}
```

Here's a breakdown of the changes:

1. The `DeleteScheduleGroupAsync` method takes the following parameter:
   - `name`: The name of the schedule group to delete.
2. The method creates a `DeleteScheduleGroupRequest` object and sets the `Name` property.
3. The method calls `_amazonScheduler.DeleteScheduleGroupAsync` with the request object and handles the response.
4. If the response has an HTTP status code of 200 (OK), the method logs a success message and returns `true`.
5. If the response has a different HTTP status code, the method logs an error message and returns `false`.
6. The method also handles the `ResourceNotFoundException` and logs an error message, returning `false` in this case.
7. Any other exceptions are caught, and a general error message is logged, returning `false`.

The `SchedulerWrapper` class now provides a convenient way to delete schedule groups from the EventBridge Scheduler service, with appropriate error handling and logging.