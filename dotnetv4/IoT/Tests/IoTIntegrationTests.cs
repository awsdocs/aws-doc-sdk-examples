// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.CloudFormation;
using Amazon.IoT;
using Amazon.IotData;
using IoTActions;
using Microsoft.Extensions.Logging;
using Moq;
using Xunit;

namespace IoTTests;

/// <summary>
/// Integration tests for the AWS IoT Basics scenario.
/// </summary>
public class IoTBasicsTests
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
        IoTBasics.IoTBasics.IsInteractive = false;

        var loggerScenarioMock = new Mock<ILogger<IoTBasics.IoTBasics>>();
        var loggerWrapperMock = new Mock<ILogger<IoTWrapper>>();

        loggerScenarioMock.Setup(logger => logger.Log(
            It.Is<Microsoft.Extensions.Logging.LogLevel>(logLevel => logLevel == Microsoft.Extensions.Logging.LogLevel.Error),
            It.IsAny<EventId>(),
            It.Is<It.IsAnyType>((@object, @type) => true),
            It.IsAny<Exception>(),
            It.IsAny<Func<It.IsAnyType, Exception?, string>>()
        ));

        // Act
        IoTBasics.IoTBasics.logger = loggerScenarioMock.Object;

        // Set up the wrapper and CloudFormation client
        IoTBasics.IoTBasics.Wrapper = new IoTWrapper(
            new AmazonIoTClient(),
            new AmazonIotDataClient("https://dummy-iot-endpoint.amazonaws.com/"),
            loggerWrapperMock.Object);

        IoTBasics.IoTBasics.CloudFormationClient = new AmazonCloudFormationClient();

        await IoTBasics.IoTBasics.RunScenarioAsync();

        // Assert no errors logged
        loggerScenarioMock.Verify(
            logger => logger.Log(
                It.Is<Microsoft.Extensions.Logging.LogLevel>(logLevel => logLevel == Microsoft.Extensions.Logging.LogLevel.Error),
                It.IsAny<EventId>(),
                It.Is<It.IsAnyType>((@object, @type) => true),
                It.IsAny<Exception>(),
                It.IsAny<Func<It.IsAnyType, Exception?, string>>()),
            Times.Never);
    }
}