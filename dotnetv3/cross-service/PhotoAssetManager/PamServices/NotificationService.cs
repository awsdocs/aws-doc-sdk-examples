// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.SimpleNotificationService;
using Amazon.SimpleNotificationService.Model;

namespace PamServices;

/// <summary>
/// Service for working with notifications for the photo analyzer
/// </summary>
public class NotificationService
{
    private readonly IAmazonSimpleNotificationService _amazonSns;

    public NotificationService(IAmazonSimpleNotificationService amazonSns)
    {
        _amazonSns = amazonSns;
    }

    /// <summary>
    /// Send a notification to subscribers.
    /// </summary>
    /// <param name="topicArn">The Arn of the topic.</param>
    /// <param name="subject">The message subject.</param>
    /// <param name="message">The message text.</param>
    /// <returns>Async task.</returns>
    public async Task SendNotification(string topicArn, string subject, string message)
    {
        var notificationRequest = new PublishRequest()
        {
            Subject = subject,
            TopicArn = topicArn,
            Message = message
        };

        await _amazonSns.PublishAsync(notificationRequest);
    }
}