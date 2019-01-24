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

// snippet-sourcedescription:[pinpoint_send_email_message_api demonstrates how to send a transactional email message by using the SendMessages operation in the Amazon Pinpoint API.]
// snippet-service:[mobiletargeting]
// snippet-keyword:[dotnet]
// snippet-keyword:[.NET]
// snippet-keyword:[Amazon Pinpoint]
// snippet-keyword:[Code Sample]
// snippet-keyword:[SendMessages]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-20]
// snippet-sourceauthor:[AWS]
// snippet-start:[pinpoint.dotnet.pinpoint_send_email_message_api.complete]

using System;
using System.Collections.Generic;
using Amazon;
using Amazon.Pinpoint;
using Amazon.Pinpoint.Model;

namespace PinpointEmailSendMessageAPI
{
    class MainClass
    {
        // The AWS Region that you want to use to send the email. For a list of
        // AWS Regions where the Amazon Pinpoint API is available, see 
        // https://docs.aws.amazon.com/pinpoint/latest/apireference/
        static string region = "us-west-2";

        // The "From" address. This address has to be verified in Amazon Pinpoint 
        // in the region you're using to send email.
        static string senderAddress = "sender@example.com";

        // The address on the "To" line. If your Amazon Pinpoint account is in
        // the sandbox, this address also has to be verified. 
        static string toAddress = "recipient@example.com";

        // The Amazon Pinpoint project/application ID to use when you send this message.
        // Make sure that the SMS channel is enabled for the project or application
        // that you choose.
        static string appId = "ce796be37f32f178af652b26eexample";

        // The subject line of the email.
        static string subject = "Amazon Pinpoint Email test";

        // The body of the email for recipients whose email clients don't 
        // support HTML content.
        static string textBody = @"Amazon Pinpoint Email Test (.NET)
---------------------------------
This email was sent using the Amazon Pinpoint API using the AWS SDK for .NET.";

        // The body of the email for recipients whose email clients support
        // HTML content.
        static string htmlBody = @"<html>
<head></head>
<body>
  <h1>Amazon Pinpoint Email Test (AWS SDK for .NET)</h1>
  <p>This email was sent using the 
    <a href='https://aws.amazon.com/pinpoint/'>Amazon Pinpoint</a> API 
    using the <a href='https://aws.amazon.com/sdk-for-net/'>
      AWS SDK for .NET</a>.</p>
</body>
</html>";

        // The character encoding the you want to use for the subject line and
        // message body of the email.
        static string charset = "UTF-8";

        public static void Main(string[] args)
        {
            using (var client = new AmazonPinpointClient(RegionEndpoint.GetBySystemName(region)))
            {
                var sendRequest = new SendMessagesRequest
                {
                    ApplicationId = appId,
                    MessageRequest = new MessageRequest
                    {
                        Addresses = new Dictionary<string, AddressConfiguration>
                        {
                            {
                                toAddress,
                                new AddressConfiguration
                                {
                                    ChannelType = "EMAIL"
                                }
                            }
                        },
                        MessageConfiguration = new DirectMessageConfiguration
                        {
                            EmailMessage = new EmailMessage
                            {
                                FromAddress = senderAddress,
                                SimpleEmail = new SimpleEmail
                                {
                                    HtmlPart = new SimpleEmailPart
                                    {
                                        Charset = charset,
                                        Data = htmlBody
                                    },
                                    TextPart = new SimpleEmailPart
                                    {
                                        Charset = charset,
                                        Data = textBody
                                    },
                                    Subject = new SimpleEmailPart
                                    {
                                        Charset = charset,
                                        Data = subject
                                    }
                                }
                            }
                        }
                    }
                };
                try
                {
                    Console.WriteLine("Sending message...");
                    SendMessagesResponse response = client.SendMessages(sendRequest);
                    Console.WriteLine("Message sent!");
                }
                catch (Exception ex)
                {
                    Console.WriteLine("The message wasn't sent. Error message: " + ex.Message);
                }
            }
        }
    }
}

// snippet-end:[pinpoint.dotnet.pinpoint_send_email_message_api.complete]
