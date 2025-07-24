// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace S3Tests;

/// <summary>
/// Integration tests for the Amazon Simple Storage Service (Amazon S3)
/// presigned POST URL scenario.
/// </summary>
public class S3WrapperTests
{
    private AmazonS3Client _client = null!;
    private S3Wrapper _s3Wrapper = null!;

    /// <summary>
    /// Verifies the scenario with an integration test. No errors should be logged.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Trait("Category", "Integration")]
    public async Task TestScenario()
    {
        // Arrange.
        var loggerScenarioMock = new Mock<ILogger<S3Scenarios.CreatePresignedPostBasics>>();
        var loggerWrapperMock = new Mock<ILogger<S3Wrapper>>();
        var uiMethods = new S3Scenarios.UiMethods();
        bool isInteractive = false;

        _client = new AmazonS3Client();
        _s3Wrapper = new S3Wrapper(_client, loggerWrapperMock.Object);
        
        var scenario = new S3Scenarios.CreatePresignedPostBasics(
            _s3Wrapper, 
            loggerScenarioMock.Object, 
            uiMethods, 
            isInteractive);

        // Set up verification for error logging.
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
        await scenario.RunAsync();

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
