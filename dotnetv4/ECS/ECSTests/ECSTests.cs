// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.ECS;
using ECSActions;
using Microsoft.Extensions.Logging;
using Moq;

namespace ECSTests;

/// <summary>
/// ECS tests.
/// </summary>
public class ECSTests
{
    /// <summary>
    /// Verifies the scenario with an integration test. No errors should be logged.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task TestScenario()
    {
        // Arrange.
        var loggerScenarioMock = new Mock<ILogger<ECSScenario.ECSScenario>>();
        var loggerWrapperMock = new Mock<ILogger<ECSWrapper>>();

        var _ecsWrapper = new ECSWrapper(
            new AmazonECSClient(),
            loggerWrapperMock.Object);

        ECSScenario.ECSScenario._interactive = false;
        ECSScenario.ECSScenario._ecsWrapper = _ecsWrapper;
        ECSScenario.ECSScenario.logger = loggerScenarioMock.Object;

        loggerScenarioMock.Setup(logger => logger.Log(
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
        await ECSScenario.ECSScenario.Main(new string[] { "" });

        // Assert no exceptions or errors logged.
        loggerScenarioMock.Verify(
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