// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0

using AWS.Messaging;
using Handler;

namespace MessageProcessingFrameworkTests;

public class HandlerTests
{
    /// <summary>
    /// Handle a message. Should return success status.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    public async Task TestHandleMessage()
    {
        // Arrange.
        var handler = new GreetingMessageHandler();

        var message = new Handler.GreetingMessage()
        {
            SenderName = "Sender",
            Greeting = "Hello"
        };

        var envelope = new MessageEnvelope<Handler.GreetingMessage>()
        {
            Message = message
        };

        // Act.
        var response = await handler.HandleAsync(envelope);

        // Assert.
        Assert.True(response.IsSuccess);
    }
}