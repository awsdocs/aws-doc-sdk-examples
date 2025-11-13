// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.CloudWatchLogs;
using Amazon.CloudWatchLogs.Model;
using CloudWatchLogsActions;
using Microsoft.Extensions.Logging;
using Moq;

namespace CloudWatchLogsTests;

public class LargeQueryWorkflowTests
{
    private readonly Mock<IAmazonCloudWatchLogs> _mockCloudWatchLogs;
    private readonly Mock<ILogger<CloudWatchLogsWrapper>> _mockLogger;
    private readonly CloudWatchLogsWrapper _wrapper;

    public LargeQueryWorkflowTests()
    {
        _mockCloudWatchLogs = new Mock<IAmazonCloudWatchLogs>();
        _mockLogger = new Mock<ILogger<CloudWatchLogsWrapper>>();
        _wrapper = new CloudWatchLogsWrapper(_mockCloudWatchLogs.Object, _mockLogger.Object);
    }

    [Fact]
    public async Task StartQueryAsync_Success_ReturnsQueryId()
    {
        // Arrange
        var expectedQueryId = "test-query-id-123";
        _mockCloudWatchLogs
            .Setup(x => x.StartQueryAsync(It.IsAny<StartQueryRequest>(), default))
            .ReturnsAsync(new StartQueryResponse { QueryId = expectedQueryId });

        // Act
        var result = await _wrapper.StartQueryAsync(
            "/test/log-group",
            "fields @timestamp, @message",
            1000,
            2000,
            10000);

        // Assert
        Assert.Equal(expectedQueryId, result);
    }

    [Fact]
    public async Task StartQueryAsync_InvalidParameter_ReturnsNull()
    {
        // Arrange
        _mockCloudWatchLogs
            .Setup(x => x.StartQueryAsync(It.IsAny<StartQueryRequest>(), default))
            .ThrowsAsync(new InvalidParameterException("Invalid parameter"));

        // Act
        var result = await _wrapper.StartQueryAsync(
            "/test/log-group",
            "fields @timestamp, @message",
            1000,
            2000,
            10000);

        // Assert
        Assert.Null(result);
    }

    [Fact]
    public async Task GetQueryResultsAsync_Success_ReturnsResults()
    {
        // Arrange
        var expectedResponse = new GetQueryResultsResponse
        {
            Status = QueryStatus.Complete,
            Results = new List<List<ResultField>>
            {
                new List<ResultField>
                {
                    new ResultField { Field = "@timestamp", Value = "2023-01-01T00:00:00.000Z" },
                    new ResultField { Field = "@message", Value = "Test message" }
                }
            }
        };

        _mockCloudWatchLogs
            .Setup(x => x.GetQueryResultsAsync(It.IsAny<GetQueryResultsRequest>(), default))
            .ReturnsAsync(expectedResponse);

        // Act
        var result = await _wrapper.GetQueryResultsAsync("test-query-id");

        // Assert
        Assert.NotNull(result);
        Assert.Equal(QueryStatus.Complete, result.Status);
        Assert.Single(result.Results);
    }

    [Fact]
    public async Task PutLogEventsAsync_Success_ReturnsTrue()
    {
        // Arrange
        _mockCloudWatchLogs
            .Setup(x => x.PutLogEventsAsync(It.IsAny<PutLogEventsRequest>(), default))
            .ReturnsAsync(new PutLogEventsResponse());

        var logEvents = new List<InputLogEvent>
        {
            new InputLogEvent
            {
                Timestamp = DateTime.UtcNow,
                Message = "Test log message"
            }
        };

        // Act
        var result = await _wrapper.PutLogEventsAsync("/test/log-group", "test-stream", logEvents);

        // Assert
        Assert.True(result);
    }

    [Fact]
    public async Task PutLogEventsAsync_ResourceNotFound_ReturnsFalse()
    {
        // Arrange
        _mockCloudWatchLogs
            .Setup(x => x.PutLogEventsAsync(It.IsAny<PutLogEventsRequest>(), default))
            .ThrowsAsync(new ResourceNotFoundException("Log group not found"));

        var logEvents = new List<InputLogEvent>
        {
            new InputLogEvent
            {
                Timestamp = DateTime.UtcNow,
                Message = "Test log message"
            }
        };

        // Act
        var result = await _wrapper.PutLogEventsAsync("/test/log-group", "test-stream", logEvents);

        // Assert
        Assert.False(result);
    }
}
