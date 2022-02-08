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

// snippet-sourcedescription:[pinpoint_send_email_smtp demonstrates how to send a transactional email message by using Amazon Pinpoint SMTP interface.]
// snippet-service:[mobiletargeting]
// snippet-keyword:[dotnet]
// snippet-keyword:[.NET]
// snippet-sourcesyntax:[.net]
// snippet-keyword:[Amazon Pinpoint]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-20]
// snippet-sourceauthor:[AWS]
// snippet-start:[pinpoint.dotnet.pinpoint_send_email_smtp.complete]

using System;
using System.Net;
using System.Net.Mail;

namespace PinpointEmailSMTP
{
    class MainClass
    {
        // If you're using Amazon Pinpoint in a region other than US West (Oregon), 
        // replace email-smtp.us-west-2.amazonaws.com with the Amazon Pinpoint SMTP  
        // endpoint in the appropriate AWS Region.
        static string smtpEndpoint = "email-smtp.us-west-2.amazonaws.com";

        // The port to use when connecting to the SMTP server.
        static int port = 587;

        // Replace sender@example.com with your "From" address. 
        // This address must be verified with Amazon Pinpoint.
        static string senderName = "Mary Major"; 
        static string senderAddress = "sender@example.com";

        // Replace recipient@example.com with a "To" address. If your account 
        // is still in the sandbox, this address must be verified.
        static string toAddress = "recipient@example.com";
    
        // CC and BCC addresses. If your account is in the sandbox, these 
        // addresses have to be verified.
        static string ccAddress = "cc-recipient@example.com"; 
        static string bccAddress = "bcc-recipient@example.com";

        // Replace smtp_username with your Amazon Pinpoint SMTP user name.
        static string smtpUsername = "AKIAIOSFODNN7EXAMPLE";

        // Replace smtp_password with your Amazon Pinpoint SMTP password.
        static string smtpPassword = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";

        // (Optional) the name of a configuration set to use for this message.
        static string configurationSet = "ConfigSet";
        
        // The subject line of the email
        static string subject =
            "Amazon Pinpoint test (SMTP interface accessed using C#)";

        // The body of the email for recipients whose email clients don't 
        // support HTML content.
        static AlternateView textBody = AlternateView.
            CreateAlternateViewFromString("Amazon Pinpoint Email Test (.NET)\r\n"
                    + "This email was sent using the Amazon Pinpoint SMTP "
                    + "interface.", null, "text/plain");

        // The body of the email for recipients whose email clients support
        // HTML content.
        static AlternateView htmlBody = AlternateView.
            CreateAlternateViewFromString("<html><head></head><body>"
                    + "<h1>Amazon Pinpoint SMTP Interface Test</h1><p>This "
                    + "email was sent using the "
                    + "<a href='https://aws.amazon.com/pinpoint/'>Amazon Pinpoint"
                    + "</a> SMTP interface.</p></body></html>", null, "text/html");

        // The message tags that you want to apply to the email.
        static string tag0 = "key0=value0";
        static string tag1 = "key1=value1";

        public static void Main(string[] args)
        {
            // Create a new MailMessage object
            MailMessage message = new MailMessage();
            
            // Add sender and recipient email addresses to the message
            message.From = new MailAddress(senderAddress,senderName);
            message.To.Add(new MailAddress(toAddress));
            message.CC.Add(new MailAddress(ccAddress));
            message.Bcc.Add(new MailAddress(bccAddress));
            
            // Add the subject line, text body, and HTML body to the message
            message.Subject = subject;
            message.AlternateViews.Add(textBody);
            message.AlternateViews.Add(htmlBody);
            
            // Add optional headers for configuration set and message tags to the message
            message.Headers.Add("X-SES-CONFIGURATION-SET", configurationSet);
            message.Headers.Add("X-SES-MESSAGE-TAGS", tag0);
            message.Headers.Add("X-SES-MESSAGE-TAGS", tag1);

            using (var client = new System.Net.Mail.SmtpClient(smtpEndpoint, port))
            {
                // Create a Credentials object for connecting to the SMTP server
                client.Credentials =
                    new NetworkCredential(smtpUsername, smtpPassword);

                client.EnableSsl = true;
                
                // Send the message
                try
                {
                    Console.WriteLine("Attempting to send email...");
                    client.Send(message);
                    Console.WriteLine("Email sent!");
                }
                // Show an error message if the message can't be sent
                catch (Exception ex)
                {
                    Console.WriteLine("The email wasn't sent.");
                    Console.WriteLine("Error message: " + ex.Message);
                }
            }
        }
    }
}

// snippet-end:[pinpoint.dotnet.pinpoint_send_email_smtp.complete]
