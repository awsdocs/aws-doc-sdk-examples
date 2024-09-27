// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Basics;
using Microsoft.Extensions.Logging;
using Moq;

namespace EC2Tests;
/// <summary>
/// Integration tests for the Amazon Elastic Compute Cloud (Amazon EC2)
/// Basics scenario.
/// </summary>
public class EC2WrapperTests
{
    private readonly IConfiguration _configuration;
    private AmazonEC2Client _client;
    private EC2Wrapper _ec2Wrapper;
    private SsmWrapper _ssmWrapper;

    /// <summary>
    /// Verifies the scenario with an integration test. No errors should be logged.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task TestScenario()
    {
        // Arrange.
        EC2Basics.isInteractive = false;
        var loggerScenarioMock = new Mock<ILogger<EC2Basics>>();
        var loggerWrapperMock = new Mock<ILogger<EC2Wrapper>>();

        _client = new AmazonEC2Client();

        _ec2Wrapper = new EC2Wrapper(_client, loggerWrapperMock.Object);
        _ssmWrapper = new SsmWrapper(new AmazonSimpleSystemsManagementClient());

        EC2Basics._ec2Wrapper = _ec2Wrapper;
        EC2Basics._ssmWrapper = _ssmWrapper;
        EC2Basics._logger = loggerScenarioMock.Object;

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
        await EC2Basics.Main(new string[] { "" });

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