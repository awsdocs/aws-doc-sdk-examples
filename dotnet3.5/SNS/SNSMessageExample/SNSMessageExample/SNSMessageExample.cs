// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

using Amazon;
using System;
using System.Threading.Tasks;

namespace SNSMessageExample
{
    class SNSMessageExample
    {
        /// <summary>
        /// This example sends an SMS message using Amazon Simple Notification
        /// Service (Amazon SNS).
        /// </summary>

        // Change the endpoint to match your own region. This is only an example endpoint.
        private static readonly RegionEndpoint _regionEndpoint = RegionEndpoint.USWest2;

        static async Task Main()
        {
            var snsMessage = new SNSMessage(_regionEndpoint);

            string phoneNumber = "18456992258"; // "1xxxyyyzzzz";
            string message = "This is a test message.";

            Console.Write($"Sending: \"{message}\" to {phoneNumber}.");
            await snsMessage.SendTextMessageAsync(phoneNumber, message);
        }
    }
}
