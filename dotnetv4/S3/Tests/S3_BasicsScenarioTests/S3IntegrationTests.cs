// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Microsoft.Extensions.Logging;
using S3_Actions;
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

        // Generate test bucket name and create temp file in test class
        var bucketName = $"s3-basics-test-{Guid.NewGuid():N}";
        var tempFilePath = Path.GetTempFileName();

        try
        {
            // Create test file content
            var testContent = "This is a test file for S3 basics scenario integration test.\nGenerated on: " + DateTime.UtcNow.ToString("yyyy-MM-dd HH:mm:ss UTC");
            await File.WriteAllTextAsync(tempFilePath, testContent);

            // Set the public variables in S3_Basics
            S3_Basics.BucketName = bucketName;
            S3_Basics.TempFilePath = tempFilePath;

            var loggerScenarioMock = new Mock<ILogger<S3_Basics>>();

            loggerScenarioMock.Setup(logger => logger.Log(
                It.Is<Microsoft.Extensions.Logging.LogLevel>(logLevel => logLevel == Microsoft.Extensions.Logging.LogLevel.Error),
                It.IsAny<EventId>(),
                It.Is<It.IsAnyType>((@object, @type) => true),
                It.IsAny<Exception?>(),
                It.IsAny<Func<It.IsAnyType, Exception?, string>>()
            ));

            // Act
            S3_Basics._logger = loggerScenarioMock.Object;

            // Set up the wrapper with real AWS S3 client
            S3_Basics._s3Wrapper = new S3Wrapper(new AmazonS3Client());

            await S3_Basics.RunScenario(S3_Basics._s3Wrapper, S3_Basics._logger);

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
        finally
        {
            // Clean up the temporary file if it still exists
            if (File.Exists(tempFilePath))
            {
                try
                {
                    File.Delete(tempFilePath);
                }
                catch
                {
                    // Ignore cleanup errors in tests
                }
            }

            // Reset the static variables
            S3_Basics.BucketName = null;
            S3_Basics.TempFilePath = null;
        }
    }
}