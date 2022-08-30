// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.SimpleEmail;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;

namespace SESActions;

/// <summary>
/// Use the AWS SDK for .NET with Amazon Simple Email Service (Amazon SES)
/// to verify and manage email and domain identities. This example
/// was created using the AWS SDK for .NET version 3.7 and .NET Core 6.0.
/// </summary>
public static class SESActionExamples
{
    private static readonly string sepBar = new('-', 80);
    private static SESWrapper sesWrapper = null!;

    public static async Task Main(string[] args)
    {
        // Set up dependency injection for the Amazon SES service.
        using var host = Host.CreateDefaultBuilder(args)
            .ConfigureServices((_, services) =>
                services.AddAWSService<IAmazonSimpleEmailService>()
                    .AddTransient<SESWrapper>()
            )
            .Build();

        sesWrapper = host.Services.GetRequiredService<SESWrapper>();

        Console.WriteLine(sepBar);
        Console.WriteLine(
            "Welcome to the Amazon Simple Email Service (Amazon SES) examples!");
        Console.WriteLine(sepBar);
        Console.WriteLine(
            "Note: If your account is in the Amazon SES sandbox, all emails must be verified.");

        await ListIdentitiesExample();

        await VerifyEmailExample();

        await GetIdentityStatusExample();

        await SendEmailExample();

        await CreateEmailTemplateExample();

        await ListEmailTemplatesExample();

        await SendTemplateEmailExample();

        await DeleteEmailTemplateExample();

        await GetSendQuotaExample();

        await DeleteIdentityExample();
    }

    /// <summary>
    /// Run the wrapper method for listing identities.
    /// </summary>
    /// <returns>Async task.</returns>
    public static async Task ListIdentitiesExample()
    {
        Console.WriteLine(sepBar);
        Console.WriteLine("List identities.");
        Console.WriteLine("Email identities for the current account:");

        var identities = await sesWrapper.ListIdentitiesAsync(IdentityType.EmailAddress);

        identities.ForEach(Console.WriteLine);
        Console.WriteLine(sepBar);
    }

    /// <summary>
    /// Run the wrapper method to get identity status.
    /// </summary>
    /// <returns>Async task.</returns>
    public static async Task GetIdentityStatusExample()
    {
        Console.WriteLine(sepBar);
        Console.WriteLine("Get the status of an email identity.");
        Console.WriteLine("Enter an email address to check verification status:");
        var email = Console.ReadLine()!;
        if (!string.IsNullOrWhiteSpace(email))
        {
            var identityStatus = await sesWrapper.GetIdentityStatusAsync(email);
            Console.WriteLine($"Identity status for email {email}: {identityStatus}");
        }

        Console.WriteLine(sepBar);
    }

    /// <summary>
    /// Run the wrapper method for verifying email.
    /// </summary>
    /// <returns>Async task.</returns>
    public static async Task VerifyEmailExample()
    {
        Console.WriteLine(sepBar);
        Console.WriteLine("Verify an email identity.");
        Console.WriteLine("Enter an email address to verify with Amazon SES:");
        var email = Console.ReadLine()!;
        if (!string.IsNullOrWhiteSpace(email))
        {
            var success = await sesWrapper.VerifyEmailIdentityAsync(email);

            Console.WriteLine(
                success
                    ? "Verification started. Follow email instructions to complete identity verification."
                    : "Unable to begin identity verification.");
        }

        Console.WriteLine(sepBar);
    }

    /// <summary>
    /// Run the wrapper method to delete an identity.
    /// </summary>
    /// <returns>Async task.</returns>
    public static async Task DeleteIdentityExample()
    {
        Console.WriteLine(sepBar);
        Console.WriteLine("Delete an email identity.");
        Console.WriteLine("Enter an email address to delete:");
        var email = Console.ReadLine()!;
        if (!string.IsNullOrWhiteSpace(email))
        {
            var success = await sesWrapper.DeleteIdentityAsync(email);

            Console.WriteLine(
                success
                    ? $"Identity deleted for email {email}"
                    : "Unable to delete identity.");
        }

        Console.WriteLine(sepBar);
    }

    /// <summary>
    /// Run the wrapper method for sending email.
    /// </summary>
    /// <returns>Async task.</returns>
    public static async Task SendEmailExample()
    {
        Console.WriteLine(sepBar);
        Console.WriteLine("Send an email.");
        // For sandbox accounts, all addresses must already be verified. For production accounts, only the sender must be verified.
        Console.WriteLine("Enter email addresses for recipients:");
        var toAddresses = Console.ReadLine()!
            .Split(',', StringSplitOptions.RemoveEmptyEntries).ToList();
        Console.WriteLine("Enter email addresses for cc recipients (optional):");
        var ccAddresses = Console.ReadLine()!
            .Split(',', StringSplitOptions.RemoveEmptyEntries).ToList();
        Console.WriteLine("Enter email addresses for bcc recipients (optional):");
        var bccAddresses = Console.ReadLine()!
            .Split(',', StringSplitOptions.RemoveEmptyEntries).ToList();

        // The sender email must already be verified. See VerifyIdentityExample for more information.
        Console.WriteLine("Enter email address for sender. Must be a verified address:");
        var senderAddress = Console.ReadLine()!;

        var bodyHtml =
            "This is some HTML body text. It can include links <a href=\"https://docs.aws.amazon.com/ses/?id=docs_gateway\"> "
            + "like this</a> and other HTML elements.";
        var bodyText =
            "This is some plain body text.";
        var subject = "Sample email from Amazon Simple Email Service";

        var messageId = await sesWrapper.SendEmailAsync(toAddresses, ccAddresses,
            bccAddresses, bodyHtml, bodyText,
            subject, senderAddress);

        Console.WriteLine($"Message {messageId} sent.");
        Console.WriteLine(sepBar);
    }

    /// <summary>
    /// Run the wrapper method to get the send quota.
    /// </summary>
    /// <returns>Async task.</returns>
    public static async Task GetSendQuotaExample()
    {
        Console.WriteLine(sepBar);
        Console.WriteLine("Get Send Quota.");

        var quota = await sesWrapper.GetSendQuotaAsync();

        Console.WriteLine($"Max 24 Hour Send: {quota.Max24HourSend}");
        Console.WriteLine($"Max Send Rate: {quota.MaxSendRate}");
        Console.WriteLine($"Sent Last 24 Hours: {quota.SentLast24Hours}");
        Console.WriteLine(sepBar);
    }

    /// <summary>
    /// Run the wrapper method to list email templates.
    /// </summary>
    /// <returns>Async task.</returns>
    public static async Task ListEmailTemplatesExample()
    {
        Console.WriteLine(sepBar);
        Console.WriteLine("Listing Email Templates.");

        var templates = await sesWrapper.ListEmailTemplatesAsync();

        templates.ForEach(t => Console.WriteLine($"Template {t.Name}, created on " +
                                                 $"{t.CreatedTimestamp.ToShortDateString()} at {t.CreatedTimestamp.ToShortTimeString()}."));
        Console.WriteLine(sepBar);
    }

    /// <summary>
    /// Run the wrapper method to create an email template.
    /// </summary>
    /// <returns>Async task.</returns>
    public static async Task CreateEmailTemplateExample()
    {
        Console.WriteLine(sepBar);
        Console.WriteLine("Create an Email Template");

        var templateName = "Example_template_1";
        var templateSubject = "Example Email using Template";
        var templateText =
            "This is what {{recipient}} sees from {{sender}} if {{recipient}} can't display HTML.";
        var templateHtml =
            "<p><i>This</i> is what {{recipient}} sees from {{sender}} if {{recipient}} <b>can</b> display HTML.</p>";

        var success = await sesWrapper.CreateEmailTemplateAsync(templateName, templateSubject,
            templateText, templateHtml);

        Console.WriteLine(success
            ? $"Template {templateName} created successfully."
            : "Unable to create template.");
        Console.WriteLine(sepBar);
    }

    /// <summary>
    /// Run the wrapper method to send an email using a template
    /// </summary>
    /// <returns>Async task.</returns>
    public static async Task SendTemplateEmailExample()
    {
        Console.WriteLine(sepBar);
        Console.WriteLine("Send an email with template.");

        var templateName = "Example_template_1";

        Console.WriteLine("Enter recipient email:");
        var recipientEmail = Console.ReadLine()!;

        Console.WriteLine("Enter sender email:");
        var senderEmail = Console.ReadLine()!;

        var templateDataObject = new { sender = senderEmail, recipient = recipientEmail };

        var messageId = await sesWrapper.SendTemplateEmailAsync(senderEmail,
            new List<string> { recipientEmail }, templateName, templateDataObject);

        Console.WriteLine($"Message {messageId} sent.");
        Console.WriteLine(sepBar);
    }

    /// <summary>
    /// Run the wrapper method to delete an email template
    /// </summary>
    /// <returns>Async task.</returns>
    public static async Task DeleteEmailTemplateExample()
    {
        Console.WriteLine(sepBar);
        Console.WriteLine("Delete an email template.");

        var templateName = "Example_template_1";

        var success = await sesWrapper.DeleteEmailTemplateAsync(templateName);

        Console.WriteLine(success
            ? $"Template {templateName} deleted successfully."
            : "Unable to delete template.");
        Console.WriteLine(sepBar);
    }
}