// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

using Amazon.SimpleNotificationService;
using Amazon.SQS;
using SNSActions;
using SQSActions;
using TopicsAndQueuesScenario;

namespace TopicsAndQueuesTests;

/// <summary>
/// Tests for Topics and Queues scenario.
/// </summary>
public class TopicsAndQueuesTests
{
    /// <summary>
    /// Running the scenario with valid setup should return true.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task ScenarioShouldReturnTrue()
    {
        // Arrange.
        TopicsAndQueues.SnsWrapper = new SNSWrapper(
            new AmazonSimpleNotificationServiceClient());
        TopicsAndQueues.SqsWrapper = new SQSWrapper(
            new AmazonSQSClient());
        TopicsAndQueues.UseConsole = false;

        // Act.
        var result = await TopicsAndQueues.RunScenario();

        // Assert.
        Assert.True(result);
    }

    /// <summary>
    /// Running the scenario without valid clients should return false.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(2)]
    [Trait("Category", "Integration")]
    public async Task ScenarioWithBadSetupShouldReturnFalse()
    {
        // Arrange.
        TopicsAndQueues.UseConsole = false;

        // Act.
        var result = await TopicsAndQueues.RunScenario();

        // Assert.
        Assert.False(result);
    }
}