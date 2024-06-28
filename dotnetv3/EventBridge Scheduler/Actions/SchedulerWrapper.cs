// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.Scheduler;
using Amazon.Scheduler.Model;
using Microsoft.Extensions.Logging;

namespace SchedulerActions;

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
                GroupName = scheduleGroupName
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
                _logger.LogInformation($"Successfully created schedule '{name}' " +
                                       $"in schedule group '{scheduleGroupName}'.");
                return true;
            }
            else
            {
                _logger.LogError($"Failed to create schedule '{name}' " +
                                 $"in schedule group '{scheduleGroupName}'. HTTP status code: {response.HttpStatusCode}");
                return false;
            }
        }
        catch (ServiceQuotaExceededException ex)
        {
            _logger.LogError($"Failed to create schedule '{name}' " +
                             $"in schedule group '{scheduleGroupName}' due to service quota exceeded. " +
                             $"Please try again later: {ex.Message}.");
            return false;
        }
        catch (Exception ex)
        {
            _logger.LogError($"An error occurred while creating schedule '{name}' " +
                             $"in schedule group '{scheduleGroupName}': {ex.Message}");
            return false;
        }
    }

    /// <summary>
    /// Creates a new schedule group in Amazon EventBridge Scheduler.
    /// </summary>
    /// <param name="name">The name of the schedule group.</param>
    /// <returns>True if the schedule group was created successfully, false otherwise.</returns>
    public async Task<bool> CreateScheduleGroupAsync(string name)
    {
        try
        {
            var request = new CreateScheduleGroupRequest
            {
                Name = name
            };

            var response = await _amazonScheduler.CreateScheduleGroupAsync(request);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                _logger.LogInformation($"Successfully created schedule group '{name}'.");
                return true;
            }
            else
            {
                _logger.LogError($"Failed to create schedule group '{name}'. HTTP status code: {response.HttpStatusCode}");
                return false;
            }
        }
        catch (ConflictException ex)
        {
            _logger.LogError($"Failed to create schedule group '{name}' due to a conflict: {ex.Message}");
            return false;
        }
        catch (Exception ex)
        {
            _logger.LogError($"An error occurred while creating schedule group '{name}': {ex.Message}");
            return false;
        }
    }


    /// <summary>
    /// Deletes an existing schedule from Amazon EventBridge Scheduler.
    /// </summary>
    /// <param name="name">The name of the schedule to delete.</param>
    /// <returns>True if the schedule was deleted successfully, false otherwise.</returns>
    public async Task<bool> DeleteScheduleAsync(string name)
    {
        try
        {
            var request = new DeleteScheduleRequest
            {
                Name = name
            };

            var response = await _amazonScheduler.DeleteScheduleAsync(request);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                _logger.LogInformation($"Successfully deleted schedule with name '{name}'.");
                return true;
            }
            else
            {
                _logger.LogError($"Failed to delete schedule with name '{name}'. HTTP status code: {response.HttpStatusCode}");
                return false;
            }
        }
        catch (ResourceNotFoundException ex)
        {
            _logger.LogError($"Failed to delete schedule with ID '{name}' because the resource was not found: {ex.Message}");
            return false;
        }
        catch (Exception ex)
        {
            _logger.LogError($"An error occurred while deleting schedule with ID '{name}': {ex.Message}");
            return false;
        }
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
            var request = new DeleteScheduleGroupRequest { Name = name };

            var response = await _amazonScheduler.DeleteScheduleGroupAsync(request);

            if (response.HttpStatusCode == System.Net.HttpStatusCode.OK)
            {
                _logger.LogInformation($"Successfully deleted schedule group '{name}'.");
                return true;
            }
            else
            {
                _logger.LogError(
                    $"Failed to delete schedule group '{name}'. HTTP status code: {response.HttpStatusCode}");
                return false;
            }
        }
        catch (ResourceNotFoundException ex)
        {
            _logger.LogError(
                $"Failed to delete schedule group '{name}' because the resource was not found: {ex.Message}");
            return false;
        }
        catch (Exception ex)
        {
            _logger.LogError(
                $"An error occurred while deleting schedule group '{name}': {ex.Message}");
            return false;
        }
    }
}