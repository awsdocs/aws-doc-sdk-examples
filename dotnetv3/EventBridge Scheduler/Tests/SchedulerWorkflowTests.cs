// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.CloudFormation;
using Amazon.Scheduler;
using Microsoft.Extensions.Logging;
using Moq;
using SchedulerActions;
using SchedulerScenario;

namespace SchedulerTests;

/// <summary>
/// Test class.
/// </summary>
public class SchedulerWorkflowTests
{
    private SchedulerWrapper _schedulerWrapper = null!;

    /// <summary>
    /// Verifies the scenario with an integration test. No errors should be logged.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task TestWorkflow()
    {
        // Arrange.
        SchedulerWorkflow._interactive = false;
        var loggerWorkflowMock = new Mock<ILogger<SchedulerWorkflow>>();
        var loggerWrapperMock = new Mock<ILogger<SchedulerWrapper>>();

        _schedulerWrapper = new SchedulerWrapper(
            new AmazonSchedulerClient(),
            loggerWrapperMock.Object);

        SchedulerWorkflow._schedulerWrapper = _schedulerWrapper;
        SchedulerWorkflow._amazonCloudFormation = new AmazonCloudFormationClient();
        SchedulerWorkflow._logger = loggerWorkflowMock.Object;

        loggerWorkflowMock.Setup(logger => logger.Log(
            It.Is<LogLevel>(logLevel => logLevel == LogLevel.Error),
            It.IsAny<EventId>(),
            It.Is<It.IsAnyType>((@object, @type) => true),
            It.IsAny<Exception>(),
            It.IsAny<Func<It.IsAnyType, Exception?, string>>()
        ));

        loggerWrapperMock.Setup(logger => logger.Log(
            It.Is<LogLevel>(logLevel => logLevel == LogLevel.Error),
            It.IsAny<EventId>(),
            It.Is<It.IsAnyType>((@object, @type) => true),
            It.IsAny<Exception>(),
            It.IsAny<Func<It.IsAnyType, Exception?, string>>()
        ));

        // Act.
        await SchedulerWorkflow.Main(new string[] { "" });

        // Assert no exceptions or errors logged.
        loggerWorkflowMock.Verify(
            logger => logger.Log(
                It.Is<LogLevel>(logLevel => logLevel == LogLevel.Error),
                It.IsAny<EventId>(),
                It.Is<It.IsAnyType>((@object, @type) => true),
                It.IsAny<Exception>(),
                It.IsAny<Func<It.IsAnyType, Exception?, string>>()),
            Times.Never);

        loggerWrapperMock.Verify(
            logger => logger.Log(
                It.Is<LogLevel>(logLevel => logLevel == LogLevel.Error),
                It.IsAny<EventId>(),
                It.Is<It.IsAnyType>((@object, @type) => true),
                It.IsAny<Exception>(),
                It.IsAny<Func<It.IsAnyType, Exception?, string>>()),
            Times.Never);
    }

}