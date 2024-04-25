// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[pinpoint.dotnet.pinpoint_send_email_smtp.complete]

using System.Net;
using System.Net.Mail;
using Microsoft.Extensions.Configuration;

namespace PinpointEmailSMTP;

public class SMTPEmailMainClass
{
    private static IConfigurationRoot _configuration = null!;

    public static async Task Main(string[] args)
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("settings.json") // Load test settings from .json file.
            .AddJsonFile("settings.local.json",
                true) // Optionally load local settings.
            .Build();

        try
        {
            SendEmailSmtp();
        }
        catch (Exception ex)
        {
            Console.WriteLine("The email wasn't sent.");
            Console.WriteLine("Error message: " + ex.Message);
        }
    }

    /// <summary>
    /// Send an SMTP email.
    /// </summary>
    private static void SendEmailSmtp()
    {
        // Use the region-specific endpoint configured in settings.json.
        string smtpEndpoint = _configuration["SmtpEndpoint"]!;

        // The port to use when connecting to the SMTP server.
        int port = Convert.ToInt32(_configuration["SmtpPort"]!);

        // Replace sender@example.com with your "From" address. 
        // This address must be verified with Amazon Pinpoint.
        string senderName = _configuration["SenderName"]!;
        string senderAddress = _configuration["SenderAddress"]!;

        // Replace recipient@example.com with a "To" address. If your account 
        // is still in the sandbox, this address must be verified.
        string toAddress = _configuration["ToAddress"]!;

        // CC and BCC addresses. If your account is in the sandbox, these 
        // addresses have to be verified.
        string ccAddress = _configuration["CcAddress"]!;
        string bccAddress = _configuration["BccAddress"]!;

        // Replace smtp_username with your Pinpoint SMTP user name.
        string smtpUsername = _configuration["SmtpUsername"]!;

        // For example purposes only.
        string smtpPswd = _configuration["SmtpPswd"]!;

        // (Optional) the name of a configuration set to use for this message.
        string configurationSet = "ConfigSet";

        // The subject line of the email.
        string subject =
            "Amazon Pinpoint test (SMTP interface accessed using C#)";

        // The body of the email for recipients whose email clients don't 
        // support HTML content.
        AlternateView textBody = AlternateView.CreateAlternateViewFromString(
            "Amazon Pinpoint Email Test (.NET)\r\n"
            + "This email was sent using the Amazon Pinpoint SMTP "
            + "interface.", null, "text/plain");

        // The body of the email for recipients whose email clients support
        // HTML content.
        AlternateView htmlBody = AlternateView.CreateAlternateViewFromString(
            "<html><head></head><body>"
            + "<h1>Amazon Pinpoint SMTP Interface Test</h1><p>This "
            + "email was sent using the "
            + "<a href='https://aws.amazon.com/pinpoint/'>Amazon Pinpoint"
            + "</a> SMTP interface.</p></body></html>", null, "text/html");

        // The message tags that you want to apply to the email.
        string tag0 = "key0=value0";
        string tag1 = "key1=value1";

        // Create a new MailMessage object.
        MailMessage message = new MailMessage();

        // Add sender and recipient email addresses to the message.
        message.From = new MailAddress(senderAddress, senderName);
        message.To.Add(new MailAddress(toAddress));
        message.CC.Add(new MailAddress(ccAddress));
        message.Bcc.Add(new MailAddress(bccAddress));

        // Add the subject line, text body, and HTML body to the message.
        message.Subject = subject;
        message.AlternateViews.Add(textBody);
        message.AlternateViews.Add(htmlBody);

        // Add optional headers for configuration set and message tags to the message.
        message.Headers.Add("X-SES-CONFIGURATION-SET", configurationSet);
        message.Headers.Add("X-SES-MESSAGE-TAGS", tag0);
        message.Headers.Add("X-SES-MESSAGE-TAGS", tag1);

        using var client = new System.Net.Mail.SmtpClient(smtpEndpoint, port);
        // Create a Credentials object for connecting to the SMTP server.
        client.Credentials =
            new NetworkCredential(smtpUsername, smtpPswd);

        client.EnableSsl = true;

        // Send the message.
        client.Send(message);
    }
}

// snippet-end:[pinpoint.dotnet.pinpoint_send_email_smtp.complete]
