// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace ManageTopicSubscriptionExample
{
    using System;
    using System.Threading.Tasks;
    using Amazon.SimpleNotificationService;
    using Amazon.SimpleNotificationService.Model;

    /// <summary>
    /// This example shows how to manage subscriptions for a Simple Notification
    /// Service (Amazon SNS) topic. The code first subscribes and then
    /// unsubscribes from an Amazon SNS topic. The example was created using the AWS
    /// SDK for .NET versionn 3.7 and .NET Core 5.0.
    /// </summary>
    public class ManageTopicSubscription
    {
        public static async Task Main()
        {
            string topicArn = "arn:aws:sns:us-east-2:000000000000:ExampleSNSTopic";

            // Create Amazon SNS client.
            var client = new AmazonSimpleNotificationServiceClient();

            // Subscribe to Amazon SNS topic.
            var response = await TopicSubscribeAsync(client, topicArn);
            string subscriptionArn = response.SubscriptionArn;
            Console.WriteLine($"Subscribed to the topic {topicArn}.");
            Console.WriteLine($"Subscription ARN: {subscriptionArn}");
            Console.WriteLine("Press <Enter> to delete the subscription.");
            _ = Console.ReadLine();

            // Unsubscribe from topic.
            await TopicUnsubscribeAsync(client, subscriptionArn);
            Console.WriteLine("\nUnscribed from topic.");
        }

        // snippet-start:[SNS.dotnetv3.TopicSubscribe]

        /// <summary>
        /// Creates a new subscription to a topic.
        /// </summary>
        /// <param name="client">The initialized Amazon SNS client object, used
        /// to create an Amazon SNS subscription.</param>
        /// <param name="topicArn">The ARN of the topic to subscribe to.</param>
        /// <returns>A SubscribeResponse object which includes the subscription
        /// ARN for the new subscription.</returns>
        public static async Task<SubscribeResponse> TopicSubscribeAsync(
            IAmazonSimpleNotificationService client,
            string topicArn)
        {
            SubscribeRequest request = new SubscribeRequest()
            {
                TopicArn = topicArn,
                ReturnSubscriptionArn = true,
                Protocol = "email",
                Endpoint = "recipient@example.com",
            };

            var response = await client.SubscribeAsync(request);

            return response;
        }

        // snippet-end:[SNS.dotnetv3.TopicSubscribe]

        // snippet-start:[SNS.dotnetv3.UnsubscribeTopic]

        /// <summary>
        /// Given the ARN for an Amazon SNS subscription, this method deletes
        /// the subscription.
        /// </summary>
        /// <param name="client">The initialized Amazon SNS client object, used
        /// to delete an Amazon SNS subscription.</param>
        /// <param name="subscriptionArn">The ARN of the subscription to delete.</param>
        public static async Task TopicUnsubscribeAsync(
            IAmazonSimpleNotificationService client,
            string subscriptionArn)
        {
            var response = await client.UnsubscribeAsync(subscriptionArn);
        }
    }
    // snippet-end:[SNS.dotnetv3.UnsubscribeTopic]
}
