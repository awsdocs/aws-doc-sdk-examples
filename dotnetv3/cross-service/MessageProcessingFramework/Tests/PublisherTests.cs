// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

using AWS.Messaging;
using Microsoft.AspNetCore.Http;
using Moq;
using Publisher;

namespace MessageProcessingFrameworkTests;

public class PublisherTests
{
    /// <summary>
    /// Test sending a complete message. Should return an OK response.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    public async Task TestPublishMessage()
    {
        // Arrange.
        var mockMessagePublisher = new Mock<IMessagePublisher>();

        // Mock the publish operation.
        mockMessagePublisher.Setup(mp =>
            mp.PublishAsync(
                It.IsAny<GreetingMessage>(), CancellationToken.None)).Returns(Task.CompletedTask);


        var message = new Publisher.GreetingMessage()
        {
            SenderName = "Sender",
            Greeting = "Hello"
        };

        // Act.
        var response = await Program.PostGreeting(message, mockMessagePublisher.Object);

        // Assert.
        Assert.Equal(Results.Ok(), response);
    }

    /// <summary>
    /// Test sending an incomplete message. Should return a bad response.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    public async Task TestPublishIncompleteMessage()
    {
        // Arrange.
        var mockMessagePublisher = new Mock<IMessagePublisher>();

        // Mock the publish operation.
        mockMessagePublisher.Setup(mp =>
            mp.PublishAsync(
                It.IsAny<GreetingMessage>(), CancellationToken.None)).Returns(Task.CompletedTask);

        // Message is missing the sender.
        var message = new Publisher.GreetingMessage()
        {
            Greeting = "Hello"
        };

        // Act.
        var response = await Program.PostGreeting(message, mockMessagePublisher.Object);

        // Assert.
        Assert.Equal(Results.BadRequest(), response);
    }
}