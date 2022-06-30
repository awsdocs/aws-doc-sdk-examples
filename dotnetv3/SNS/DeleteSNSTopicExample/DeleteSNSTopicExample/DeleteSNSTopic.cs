// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace DeleteSNSTopicExample
{
    // snippet-start:[SNS.dotnetv3.DeleteSNSTopicExample]
    using System;
    using System.Threading.Tasks;
    using Amazon.SimpleNotificationService;

    /// <summary>
    /// This example deletes an existing Amazon Simple Notification Service
    /// (Amazon SNS) topic. The example was created using the AWS SDK for .NET
    /// version 3.7 and .NET Core 5.0.
    /// </summary>
    public class DeleteSNSTopic
    {
        public static async Task Main()
        {
            string topicArn = "arn:aws:sns:us-east-2:704825161248:ExampleSNSTopic";
            IAmazonSimpleNotificationService client = new AmazonSimpleNotificationServiceClient();

            var response = await client.DeleteTopicAsync(topicArn);
        }
    }

    // snippet-end:[SNS.dotnetv3.DeleteSNSTopicExample]
}
