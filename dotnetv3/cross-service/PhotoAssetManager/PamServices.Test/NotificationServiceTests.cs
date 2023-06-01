// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Moq;
using Amazon.SimpleNotificationService;
using Amazon.SimpleNotificationService.Model;

namespace PamServices.Test;

/// <summary>
/// Tests for the NotificationService class.
/// </summary>
public class NotificationServiceTests
{
    /// <summary>
    /// Verify sending a notification using a PublishRequest.
    /// </summary>
    [Fact]
    [Trait("Category", "Unit")]
    public async Task DetectLabels_ShouldReturnEmptyCollection()
    {
        // Arrange.
        var mockService = new Mock<IAmazonSimpleNotificationService>();

        var subject = "subject";
        var topicArn = "123456";
        var message = "test message";

        var service = new NotificationService(mockService.Object);

        // Act.
        await service.SendNotification(topicArn, subject, message);

        // Assert.
        mockService.Verify(mn => mn.PublishAsync(
            It.Is<PublishRequest>(r =>
                r.Subject == subject && r.TopicArn == topicArn && r.Message == message), CancellationToken.None), Times.Once);
    }
}