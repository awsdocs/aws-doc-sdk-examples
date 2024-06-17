---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: msg_bdrk_015pNYi6bWStpDG95pKQepuH
  lastRun: 2024-06-11T14:56:33.942Z
  model: claude-3-haiku-20240307
  statistics:
    firstByteLatency: 682
    inputTokenCount: 7596
    invocationLatency: 8071
    outputTokenCount: 775
prompt: |
  Describe the architecture and structure of SchedulerWrapper.cs.
  Here is an example wrapper for a different scenario.

  <example>

  using Amazon.MediaConvert;
  using Amazon.MediaConvert.Model;

  namespace MediaConvertActions;

      /// <summary>
      /// Wrapper class for AWS Elemental MediaConvert operations.
      /// </summary>
      public class MediaConvertWrapper
      {
          private readonly IAmazonMediaConvert _amazonMediaConvert;

          /// <summary>
          /// Constructor for the MediaConvert wrapper.
          /// </summary>
          /// <param name="amazonMediaConvert">The injected MediaConvert client.</param>
          public MediaConvertWrapper(IAmazonMediaConvert amazonMediaConvert)
          {
              _amazonMediaConvert = amazonMediaConvert;
          }


          // snippet-start:[MediaConvert.dotnetv3.GetJob]
          /// <summary>
          /// Get the job information for a job by its ID.
          /// </summary>
          /// <param name="jobId">The ID of the job.</param>
          /// <returns>The Job object.</returns>
          public async Task<Job> GetJobById(string jobId)
          {
              var jobResponse = await _amazonMediaConvert.GetJobAsync(
                      new GetJobRequest
                      {
                          Id = jobId
                      });

              return jobResponse.Job;
          }
          // snippet-end:[MediaConvert.dotnetv3.GetJob]
      }
  </example>

  Do not provide any implementation, include a stub for each necessary method. Each method should be async. Do not use request objects as parameters, instead the most likely properties of each request so the request can be created inside the method. For example, use a name to create the schedule, and a parameter to add that schedule to a schedule group.
---
Here's the SchedulerWrapper.cs file with the necessary methods as stubs:

```csharp
using System.Threading;
using System.Threading.Tasks;
using Amazon.Scheduler;
using Amazon.Scheduler.Model;

namespace Actions
{
    public class SchedulerWrapper
    {
        private readonly IAmazonScheduler _amazonScheduler;

        public SchedulerWrapper(IAmazonScheduler amazonScheduler)
        {
            _amazonScheduler = amazonScheduler;
        }

        public async Task CreateScheduleAsync(string name, string scheduleExpression, string scheduleGroupName, CancellationToken cancellationToken)
        {
            // Create a CreateScheduleRequest and call _amazonScheduler.CreateScheduleAsync()
        }

        public async Task CreateScheduleGroupAsync(string name, CancellationToken cancellationToken)
        {
            // Create a CreateScheduleGroupRequest and call _amazonScheduler.CreateScheduleGroupAsync()
        }

        public async Task DeleteScheduleAsync(string scheduleId, CancellationToken cancellationToken)
        {
            // Create a DeleteScheduleRequest and call _amazonScheduler.DeleteScheduleAsync()
        }

        public async Task DeleteScheduleGroupAsync(string scheduleGroupName, CancellationToken cancellationToken)
        {
            // Create a DeleteScheduleGroupRequest and call _amazonScheduler.DeleteScheduleGroupAsync()
        }
    }
}
```

The SchedulerWrapper class is designed to encapsulate the interactions with the Amazon EventBridge Scheduler service, providing a simplified interface for the workflow implementation. Here's a breakdown of the methods:

1. **CreateScheduleAsync**:
   - This method creates a new schedule in the EventBridge Scheduler service.
   - It takes the following parameters:
     - `name`: the name of the schedule
     - `scheduleExpression`: the schedule expression that defines when the schedule should run
     - `scheduleGroupName`: the name of the schedule group to which the schedule should be added
     - `cancellationToken`: a cancellation token to allow cancellation of the asynchronous operation

2. **CreateScheduleGroupAsync**:
   - This method creates a new schedule group in the EventBridge Scheduler service.
   - It takes the following parameters:
     - `name`: the name of the schedule group
     - `cancellationToken`: a cancellation token to allow cancellation of the asynchronous operation

3. **DeleteScheduleAsync**:
   - This method deletes an existing schedule from the EventBridge Scheduler service.
   - It takes the following parameter:
     - `scheduleId`: the ID of the schedule to be deleted
     - `cancellationToken`: a cancellation token to allow cancellation of the asynchronous operation

4. **DeleteScheduleGroupAsync**:
   - This method deletes an existing schedule group from the EventBridge Scheduler service.
   - It takes the following parameter:
     - `scheduleGroupName`: the name of the schedule group to be deleted
     - `cancellationToken`: a cancellation token to allow cancellation of the asynchronous operation

The implementation of each method will create the appropriate request object and call the corresponding async method on the `IAmazonScheduler` interface, passing the necessary parameters and the cancellation token.