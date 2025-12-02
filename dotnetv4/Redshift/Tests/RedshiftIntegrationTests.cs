// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using System;
using System.Threading.Tasks;
using Amazon.Redshift;
using Amazon.RedshiftDataAPIService;
using Microsoft.Extensions.Logging;
using Moq;
using RedshiftActions;
using Xunit;

namespace RedshiftTests;

/// <summary>
/// Integration tests for the AWS Redshift Basics scenario.
/// </summary>
public class RedshiftBasicsTests
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
        RedshiftBasics.RedshiftBasics.IsInteractive = false;

        var loggerScenarioMock = new Mock<ILogger<RedshiftBasics.RedshiftBasics>>();

        loggerScenarioMock.Setup(logger => logger.Log(
            It.Is<LogLevel>(logLevel => logLevel == LogLevel.Error),
            It.IsAny<EventId>(),
            It.Is<It.IsAnyType>((@object, @type) => true),
            It.IsAny<Exception>(),
            It.IsAny<Func<It.IsAnyType, Exception?, string>>()
        ));

        // Act
        RedshiftBasics.RedshiftBasics.logger = loggerScenarioMock.Object;

        // Act
        RedshiftBasics.RedshiftBasics.Wrapper = new RedshiftWrapper(
            new AmazonRedshiftClient(),
            new AmazonRedshiftDataAPIServiceClient());

        await RedshiftBasics.RedshiftBasics.RunScenarioAsync();

        // Assert no errors logged
        loggerScenarioMock.Verify(
            logger => logger.Log(
                It.Is<LogLevel>(logLevel => logLevel == LogLevel.Error),
                It.IsAny<EventId>(),
                It.Is<It.IsAnyType>((@object, @type) => true),
                It.IsAny<Exception>(),
                It.IsAny<Func<It.IsAnyType, Exception?, string>>()),
            Times.Never);
    }
}