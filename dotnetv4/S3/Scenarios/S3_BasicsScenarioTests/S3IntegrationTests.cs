// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Microsoft.Extensions.Logging;
using S3_BasicsScenario;

namespace S3_BasicsScenarioTests;

/// <summary>
/// Integration tests for the AWS S3 Basics scenario.
/// </summary>
public class S3IntegrationTests
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
        S3_Basics.IsInteractive = false;

        var loggerScenarioMock = new Mock<ILogger<S3_Basics>>();

        loggerScenarioMock.Setup(logger => logger.Log(
            It.Is<Microsoft.Extensions.Logging.LogLevel>(logLevel => logLevel == Microsoft.Extensions.Logging.LogLevel.Error),
            It.IsAny<EventId>(),
            It.Is<It.IsAnyType>((@object, @type) => true),
            It.IsAny<Exception?>(),
            It.IsAny<Func<It.IsAnyType, Exception?, string>>()
        ));

        // Act
        S3_Basics.logger = loggerScenarioMock.Object;

        // Set up the wrapper with real AWS S3 client
        S3_Basics.Wrapper = new S3_BasicsScenario.S3Bucket(new AmazonS3Client());

        await S3_Basics.RunScenarioAsync();

        // Assert no errors logged
        loggerScenarioMock.Verify(
            logger => logger.Log(
                It.Is<Microsoft.Extensions.Logging.LogLevel>(logLevel => logLevel == Microsoft.Extensions.Logging.LogLevel.Error),
                It.IsAny<EventId>(),
                It.Is<It.IsAnyType>((@object, @type) => true),
                It.IsAny<Exception?>(),
                It.IsAny<Func<It.IsAnyType, Exception?, string>>()),
            Times.Never);
    }
}
