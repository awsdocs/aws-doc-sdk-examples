// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.CloudFormation;
using Amazon.CloudWatchLogs;
using CloudWatchLogsActions;
using CloudWatchLogsScenario;
using Microsoft.Extensions.Logging;
using Moq;

namespace CloudWatchLogsTests;

/// <summary>
/// Integration tests for the CloudWatch Logs Large Query workflow.
/// </summary>
public class LargeQueryWorkflowTests
{
    /// <summary>
    /// Verifies the scenario with an integration test. No errors should be logged.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task TestScenarioIntegration()
    {
        // Arrange
        LargeQueryWorkflow._interactive = false;

        var loggerScenarioMock = new Mock<ILogger<LargeQueryWorkflow>>();
        loggerScenarioMock.Setup(logger => logger.Log(
            It.Is<LogLevel>(logLevel => logLevel == LogLevel.Error),
            It.IsAny<EventId>(),
            It.Is<It.IsAnyType>((@object, @type) => true),
            It.IsAny<Exception>(),
            It.IsAny<Func<It.IsAnyType, Exception?, string>>()));

        // Act
        LargeQueryWorkflow._logger = loggerScenarioMock.Object;
        LargeQueryWorkflow._wrapper = new CloudWatchLogsWrapper(
            new AmazonCloudWatchLogsClient(),
            new Mock<ILogger<CloudWatchLogsWrapper>>().Object);
        LargeQueryWorkflow._amazonCloudFormation = new AmazonCloudFormationClient();

        await LargeQueryWorkflow.RunScenario();

        // Assert no errors logged
        loggerScenarioMock.Verify(logger => logger.Log(
            It.Is<LogLevel>(logLevel => logLevel == LogLevel.Error),
            It.IsAny<EventId>(),
            It.Is<It.IsAnyType>((@object, @type) => true),
            It.IsAny<Exception>(),
            It.IsAny<Func<It.IsAnyType, Exception?, string>>()),
            Times.Never);
    }
}