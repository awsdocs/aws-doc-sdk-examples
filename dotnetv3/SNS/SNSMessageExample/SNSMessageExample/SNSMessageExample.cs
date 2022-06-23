// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

namespace SNSMessageExample
{
    using System;
    using System.Threading.Tasks;
    using Amazon;

    /// <summary>
    /// This example sends an SMS message using Amazon Simple Notification
    /// Service (Amazon SNS).
    /// </summary>
    public class SNSMessageExample
    {
        // snippet-start:[SNS.dotnetv3.SendTextMessage]

        // Change the endpoint to match your own AWS Region. This is only an example endpoint.
        private static readonly RegionEndpoint RegionEndpoint = RegionEndpoint.USWest2;

        public static async Task Main()
        {
            var snsMessage = new SNSMessage(RegionEndpoint);

            string phoneNumber = "1xxxyyyzzzz";
            string message = "This is a test message.";

            Console.Write($"Sending: \"{message}\" to {phoneNumber}.");
            await snsMessage.SendTextMessageAsync(phoneNumber, message);
        }

        // snippet-end:[SNS.dotnetv3.SendTextMessage]
    }
}
