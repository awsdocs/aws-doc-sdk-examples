/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/

// snippet-sourcedescription:[pinpoint_send_email_message_email_api demonstrates how to send a transactional email message by using the SendEmail operation in the Amazon Pinpoint Email API.]
// snippet-service:[mobiletargeting]
// snippet-keyword:[dotnet]
// snippet-keyword:[.NET]
// snippet-sourcesyntax:[.net]
// snippet-keyword:[Amazon Pinpoint Email API]
// snippet-keyword:[Code Sample]
// snippet-keyword:[SendEmail]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-20]
// snippet-sourceauthor:[AWS]
// snippet-start:[pinpoint.dotnet.pinpoint_send_email_message_email_api.complete]

using System;
using System.Collections.Generic;
using Amazon;
using Amazon.PinpointEmail;
using Amazon.PinpointEmail.Model;

namespace PinpointEmailSDK
{
    class MainClass
    {
        // The AWS Region that you want to use to send the email. For a list of
        // AWS Regions where the Amazon Pinpoint Email API is available, see
        // https://docs.aws.amazon.com/pinpoint-email/latest/APIReference
        static string region = "us-west-2";

        // The "From" address. This address has to be verified in Amazon
        // Pinpoint in the region you're using to send email.
        static string senderAddress = "sender@example.com";

        // The address on the "To" line. If your Amazon Pinpoint account is in
        // the sandbox, this address also has to be verified.
        static string toAddress = "recipient@example.com";

        // CC and BCC addresses. If your account is in the sandbox, these
        // addresses have to be verified.
        static string ccAddress = "cc-recipient@example.com";
        static string bccAddress = "bcc-recipient@example.com";

        // The configuration set that you want to use to send the email.
        static string configSet = "ConfigSet";

        // The subject line of the email.
        static string subject = "Amazon Pinpoint Email API test";

        // The body of the email for recipients whose email clients don't
        // support HTML content.
        static string textBody = "Amazon Pinpoint Email Test (.NET)\r\n"
                               + "This email was sent using the Amazon Pinpoint Email"
                               + "API using the AWS SDK for .NET.";

        // The body of the email for recipients whose email clients support
        // HTML content.
        static string htmlBody = @"<html>
<head></head>
<body>
  <h1>Amazon Pinpoint Email API Test (AWS SDK for .NET)</h1>
  <p>This email was sent using the
    <a href='https://aws.amazon.com/pinpoint/'>Amazon Pinpoint</a> Email API
    using the <a href='https://aws.amazon.com/sdk-for-net/'>
      AWS SDK for .NET</a>.</p>
</body>
</html>";

        // The message tags that you want to apply to the email.
        static MessageTag tag0 = new MessageTag
        {
            Name = "key0",
            Value = "value0"
        };
        static MessageTag tag1 = new MessageTag
        {
            Name = "key1",
            Value = "value1"
        };

        // The character encoding the you want to use for the subject line and
        // message body of the email.
        static string charset = "UTF-8";

        public static void Main(string[] args)
        {
            using (var client = new AmazonPinpointEmailClient(RegionEndpoint.GetBySystemName(region)))
            {
                // Create a request to send the message. The request contains
                // all of the message attributes and content that were defined
                // earlier.
                var sendRequest = new SendEmailRequest
                {
                    FromEmailAddress = senderAddress,

                    // An object that contains all of the destinations that you
                    // want to send the message to. You can send a message to up
                    // to 50 recipients in a single call to the API.
                    Destination = new Destination
                    {
                        // You can include multiple To, CC, and BCC addresses.
                        ToAddresses = new List<string>
                        {
                            toAddress,
                            //additional recipients here
                        },
                        CcAddresses = new List<string>
                        {
                            ccAddress,
                            //additional recipients here
                        },
                        BccAddresses = new List<string>
                        {
                            bccAddress,
                            //additional recipients here
                        }
                    },

                    // The body of the email message.
                    Content = new EmailContent
                    {
                        // Create a new Simple email message. If you need to
                        // include attachments, you should send a RawMessage
                        // instead.
                        Simple = new Message
                        {
                            Subject = new Content
                            {
                                Charset = charset,
                                Data = subject
                            },
                            Body = new Body
                            {
                                // The text-only body of the message.
                                Text = new Content
                                {
                                    Charset = charset,
                                    Data = textBody
                                },
                                // The HTML body of the message.
                                Html = new Content
                                {
                                    Charset = charset,
                                    Data = htmlBody
                                }
                            }
                        }
                    },

                    // The configuration set that you want to use when you send
                    // this message.
                    ConfigurationSetName = configSet,

                    // A list of tags that you want to apply to this message.
                    EmailTags = new List<MessageTag>
                    {
                        tag0,
                        tag1
                    }
                };

                // Send the email, and provide a confirmation message if it's
                // sent successfully.
                try
                {
                    Console.WriteLine("Sending email using the Amazon Pinpoint Email API...");
                    var response = client.SendEmail(sendRequest);
                    Console.WriteLine("Email sent!");
                }

                // If the message can't be sent, return the error message that's
                // provided by the Amazon Pinpoint Email API.
                catch (Exception ex)
                {
                    Console.WriteLine("The email wasn't sent. ");
                    Console.WriteLine("Error message: " + ex.Message);
                }
            }
        }
    }
}

// snippet-end:[pinpoint.dotnet.pinpoint_send_email_message_email_api.complete]
