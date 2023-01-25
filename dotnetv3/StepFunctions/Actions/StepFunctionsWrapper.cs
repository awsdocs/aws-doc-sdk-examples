// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

// snippet-start:[StepFunctions.dotnetv3.StepFunctionsWrapper]

namespace StepFunctionsActions;

/// <summary>
/// Wrapper that performs AWS Step Functions actions.
/// </summary>
public class StepFunctionsWrapper
{
    private readonly IAmazonStepFunctions _amazonStepFunctions;

    /// <summary>
    /// The constructor for the StepFunctionsWrapper. Initializes the
    /// client object passed to it.
    /// </summary>
    /// <param name="amazonStepFunctions">An initialized Step Functions client object.</param>
    public StepFunctionsWrapper(IAmazonStepFunctions amazonStepFunctions)
    {
        _amazonStepFunctions = amazonStepFunctions;
    }

    // snippet-start:[StepFunctions.dotnetv3.ListActivities]
    public async Task<List<ActivityListItem>> ListActivities()
    {
        Console.WriteLine("Welcome to Amazon Step Functions. Let's list your Step Functions actions:");
        var request = new ListActivitiesRequest { MaxResults = 10 };
        var activities = new List<ActivityListItem>();

        do
        {
            var response = await _amazonStepFunctions.ListActivitiesAsync(request);

            if (response.NextToken is not null)
            {
                request.NextToken = response.NextToken;
            }

            activities.AddRange(response.Activities);
        }
        while (request.NextToken is not null);

        return activities;
    }

    // snippet-end:[StepFunctions.dotnetv3.ListActivities]

    // snippet-start:[StepFunctions.dotnetv3.CreateActivity]
    /// <summary>
    /// Create a Step Functions activity using the supplied name.
    /// </summary>
    /// <param name="activityName">The name for the new Step Functions activity.</param>
    /// <returns>The Amazon Resource Name (ARN) for the new activity.</returns>
    public async Task<string> CreateActivity(string activityName)
    {
        var response = await _amazonStepFunctions.CreateActivityAsync(new CreateActivityRequest { Name = activityName });
        return response.ActivityArn;
    }

    // snippet-end:[StepFunctions.dotnetv3.CreateActivity]

    // snippet-start:[StepFunctions.dotnetv3.CreateStateMachine]
    /// <summary>
    /// Create a Step Functions state machine.
    /// </summary>
    /// <param name="stateMachineName">Name for the new Step Functions state
    /// machine.</param>
    /// <param name="definition">A JSON string that defines the Step Functions
    /// state machine.</param>
    /// <param name="roleArn">The Amazon Resource Name (ARN) of the role.</param>
    /// <returns></returns>
    public async Task<string> CreateStateMachine(string stateMachineName, string definition, string roleArn)
    {
        var request = new CreateStateMachineRequest
        {
            Name = stateMachineName,
            Definition = definition,
            RoleArn = roleArn
        };

        var response =
            await _amazonStepFunctions.CreateStateMachineAsync(request);
        return response.StateMachineArn;
    }

    // snippet-end:[StepFunctions.dotnetv3.CreateStateMachine]

    // snippet-start:[StepFunctions.dotnetv3.StartExecution]
    /// <summary>
    /// Start execution of an AWS Step Functions state machine.
    /// </summary>
    /// <param name="executionName">The name to use for the execution.</param>
    /// <param name="executionJson">The JSON string to pass for execution.</param>
    /// <param name="stateMachineArn">The Amazon Resource Name (ARN) of the
    /// Step Functions state machine.</param>
    /// <returns></returns>
    public async Task<string> StartExecution(string executionName, string executionJson, string stateMachineArn)
    {
        var executionRequest = new StartExecutionRequest
        {
            Input = executionJson,
            Name = executionName,
            StateMachineArn = stateMachineArn
        };

        var response = await _amazonStepFunctions.StartExecutionAsync(executionRequest);
        return response.ExecutionArn;
    }

    // snippet-end:[StepFunctions.dotnetv3.StartExecution]

    // snippet-start:[StepFunctions.dotnetv3.StopExecution]
    public async Task<bool> StopExecution(string executionArn)
    {
        var response =
            await _amazonStepFunctions.StopExecutionAsync(new StopExecutionRequest { ExecutionArn = executionArn });
        return response.HttpStatusCode == HttpStatusCode.OK;
    }

    // snippet-end:[StepFunctions.dotnetv3.StopExecution]

    // snippet-start:[StepFunctions.dotnetv3.ListExecutions]
    public async Task<List<ExecutionListItem>> ListExecutions(string stateMachineArn)
    {
        var executions = new List<ExecutionListItem>();
        var response = new ListExecutionsResponse();
        var request = new ListExecutionsRequest { StateMachineArn = stateMachineArn };

        do
        {
            response = await _amazonStepFunctions.ListExecutionsAsync(new ListExecutionsRequest
            { StateMachineArn = stateMachineArn });
            executions.AddRange(response.Executions);
            if (response.NextToken is not null)
            {
                request.NextToken = response.NextToken;
            }
        } while (response.NextToken is not null);

        return executions;
    }

    // snippet-end:[StepFunctions.dotnetv3.ListExecutions]

    // snippet-start:[StepFunctions.dotnetv3.DeleteStateMachine]
    public async Task<bool> DeleteStateMachine(string stateMachineArn)
    {
        var response = await _amazonStepFunctions.DeleteStateMachineAsync(new DeleteStateMachineRequest
        { StateMachineArn = stateMachineArn });
        return response.HttpStatusCode == HttpStatusCode.OK;
    }

    // snippet-end:[StepFunctions.dotnetv3.DeleteStateMachine]

    // snippet-start:[StepFunctions.dotnetv3.DeleteActivity]
    public async Task<bool> DeleteActivity(string activityArn)
    {
        var response = await _amazonStepFunctions.DeleteActivityAsync(new DeleteActivityRequest { ActivityArn = activityArn });
        return response.HttpStatusCode == HttpStatusCode.OK;
    }

    // snippet-end:[StepFunctions.dotnetv3.DeleteActivity]
}

// snippet-end:[StepFunctions.dotnetv3.StepFunctionsWrapper]