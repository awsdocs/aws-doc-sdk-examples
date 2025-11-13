// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[CloudWatchLogs.dotnetv3.CloudWatchLogsWrapper]
using Amazon.CloudWatchLogs;
using Amazon.CloudWatchLogs.Model;
using Microsoft.Extensions.Logging;

namespace CloudWatchLogsActions;

/// <summary>
/// Wrapper class for Amazon CloudWatch Logs operations.
/// </summary>
public class CloudWatchLogsWrapper
{
    private readonly IAmazonCloudWatchLogs _amazonCloudWatchLogs;
    private readonly ILogger<CloudWatchLogsWrapper> _logger;

    /// <summary>
    /// Constructor for the CloudWatchLogsWrapper class.
    /// </summary>
    /// <param name="amazonCloudWatchLogs">The injected CloudWatch Logs client.</param>
    /// <param name="logger">The injected logger.</param>
    public CloudWatchLogsWrapper(IAmazonCloudWatchLogs amazonCloudWatchLogs, ILogger<CloudWatchLogsWrapper> logger)
    {
        _amazonCloudWatchLogs = amazonCloudWatchLogs;
        _logger = logger;
    }

    // snippet-start:[CloudWatchLogs.dotnetv3.StartQuery]
    /// <summary>
    /// Starts a CloudWatch Logs Insights query.
    /// </summary>
    /// <param name="logGroupName">The name of the log group to query.</param>
    /// <param name="queryString">The CloudWatch Logs Insights query string.</param>
    /// <param name="startTime">The start time for the query (seconds since epoch).</param>
    /// <param name="endTime">The end time for the query (seconds since epoch).</param>
    /// <param name="limit">The maximum number of results to return.</param>
    /// <returns>The query ID if successful, null otherwise.</returns>
    public async Task<string?> StartQueryAsync(
        string logGroupName,
        string queryString,
        long startTime,
        long endTime,
        int limit = 10000)
    {
        try
        {
            var request = new StartQueryRequest
            {
                LogGroupName = logGroupName,
                QueryString = queryString,
                StartTime = startTime,
                EndTime = endTime,
                Limit = limit
            };

            var response = await _amazonCloudWatchLogs.StartQueryAsync(request);
            return response.QueryId;
        }
        catch (InvalidParameterException ex)
        {
            _logger.LogError($"Invalid parameter for query: {ex.Message}");
            return null;
        }
        catch (ResourceNotFoundException ex)
        {
            _logger.LogError($"Log group not found: {ex.Message}");
            return null;
        }
        catch (Exception ex)
        {
            _logger.LogError($"An error occurred while starting query: {ex.Message}");
            return null;
        }
    }
    // snippet-end:[CloudWatchLogs.dotnetv3.StartQuery]

    // snippet-start:[CloudWatchLogs.dotnetv3.GetQueryResults]
    /// <summary>
    /// Gets the results of a CloudWatch Logs Insights query.
    /// </summary>
    /// <param name="queryId">The ID of the query.</param>
    /// <returns>The query results response.</returns>
    public async Task<GetQueryResultsResponse?> GetQueryResultsAsync(string queryId)
    {
        try
        {
            var request = new GetQueryResultsRequest
            {
                QueryId = queryId
            };

            var response = await _amazonCloudWatchLogs.GetQueryResultsAsync(request);
            return response;
        }
        catch (ResourceNotFoundException ex)
        {
            _logger.LogError($"Query not found: {ex.Message}");
            return null;
        }
        catch (Exception ex)
        {
            _logger.LogError($"An error occurred while getting query results: {ex.Message}");
            return null;
        }
    }
    // snippet-end:[CloudWatchLogs.dotnetv3.GetQueryResults]

    // snippet-start:[CloudWatchLogs.dotnetv3.PutLogEvents]
    /// <summary>
    /// Puts log events to a CloudWatch Logs log stream.
    /// </summary>
    /// <param name="logGroupName">The name of the log group.</param>
    /// <param name="logStreamName">The name of the log stream.</param>
    /// <param name="logEvents">The list of log events to put.</param>
    /// <returns>True if successful, false otherwise.</returns>
    public async Task<bool> PutLogEventsAsync(
        string logGroupName,
        string logStreamName,
        List<InputLogEvent> logEvents)
    {
        try
        {
            var request = new PutLogEventsRequest
            {
                LogGroupName = logGroupName,
                LogStreamName = logStreamName,
                LogEvents = logEvents
            };

            await _amazonCloudWatchLogs.PutLogEventsAsync(request);
            return true;
        }
        catch (ResourceNotFoundException ex)
        {
            _logger.LogError($"Log group or stream not found: {ex.Message}");
            return false;
        }
        catch (Exception ex)
        {
            _logger.LogError($"An error occurred while putting log events: {ex.Message}");
            return false;
        }
    }
    // snippet-end:[CloudWatchLogs.dotnetv3.PutLogEvents]
}
// snippet-end:[CloudWatchLogs.dotnetv3.CloudWatchLogsWrapper]
