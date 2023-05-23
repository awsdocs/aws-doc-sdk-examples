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

    public async Task SendNotification(string topicArn, string message)
    {
        var notificationRequest = new PublishRequest()
        {
            TopicArn = topicArn,
            Message = message
        };

        await _amazonSns.PublishAsync(notificationRequest);
    }
}