// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using System.Globalization;
using Amazon.SimpleEmailV2;
using Amazon.SimpleEmailV2.Model;
using CsvHelper;
using MimeKit;

namespace DynamoDbItemTracker;

/// <summary>
/// Class for sending work item reports using Amazon Simple Email Service (Amazon SES).
/// </summary>
public class ReportService
{
    private readonly IAmazonSimpleEmailServiceV2 _amazonSESService;
    private readonly IConfiguration _configuration;

    /// <summary>
    /// Constructor that uses the injected Amazon SES client.
    /// </summary>
    /// <param name="amazonSESService">Amazon SES client.</param>
    /// <param name="configuration">App configuration.</param>
    public ReportService(IAmazonSimpleEmailServiceV2 amazonSESService, IConfiguration configuration)
    {
        _amazonSESService = amazonSESService;
        _configuration = configuration;
    }

    /// <summary>
    /// Send the report to an email address.
    /// Both the sender and recipient must be validated email addresses if this account uses the Amazon SES Sandbox.
    /// </summary>
    /// <param name="workItems">The collection of work items for the report.</param>
    /// <param name="emailAddress">The recipient's email address.</param>
    /// <returns>The messageId as a string.</returns>
    public async Task<string> SendReport(IList<WorkItem> workItems, string emailAddress)
    {
        await using var attachmentStream = new MemoryStream();
        await using var streamWriter = new StreamWriter(attachmentStream);
        await using var csvWriter = new CsvWriter(streamWriter, CultureInfo.InvariantCulture);
        await GetCsvStreamFromWorkItems(workItems, attachmentStream, streamWriter, csvWriter);

        await using var messageStream = new MemoryStream();
        var plainTextBody = GetReportPlainTextBody(workItems);
        var htmlBody = GetReportHtmlBody(workItems);
        var attachmentName = $"activeWorkItems_{DateTime.Now:g}.csv";
        var subject = $"Item Tracker Report: Active Work Items {DateTime.Now:g}";

        await BuildRawMessageWithAttachment(emailAddress, plainTextBody, htmlBody, subject, attachmentName, attachmentStream, messageStream);

        var response = await _amazonSESService.SendEmailAsync(
            new SendEmailRequest
            {
                Destination = new Destination
                {
                    ToAddresses = new List<string> { emailAddress }
                },
                Content = new EmailContent()
                {
                    Raw = new RawMessage()
                    {
                        Data = messageStream
                    }
                },
            });

        return response.MessageId;
    }

    /// <summary>
    /// Build a raw message memory stream with an attachment.
    /// </summary>
    /// <param name="emailAddress">The recipient's email address.</param>
    /// <param name="textBody">The plain text email body.</param>
    /// <param name="htmlBody">The HTML email body.</param>
    /// <param name="subject">The email subject.</param>
    /// <param name="attachmentName">The name for the attachment.</param>
    /// <param name="attachmentStream">The stream for the attachment data.</param>
    /// <param name="messageStream">The stream for the email message.</param>
    /// <returns>Async task.</returns>
    public async Task BuildRawMessageWithAttachment(string emailAddress, string textBody, string htmlBody, string subject, string attachmentName,
        MemoryStream attachmentStream, MemoryStream messageStream)
    {
        // The email with attachment can be created using MimeKit.
        var fromEmailAddress = _configuration["EmailSourceAddress"];

        var message = new MimeMessage();
        var builder = new BodyBuilder()
        {
            TextBody = textBody,
            HtmlBody = htmlBody
        };

        message.From.Add(new MailboxAddress(fromEmailAddress, fromEmailAddress));
        message.To.Add(new MailboxAddress(emailAddress, emailAddress));
        message.Subject = subject;

        await builder.Attachments.AddAsync(attachmentName, attachmentStream);

        message.Body = builder.ToMessageBody();
        await message.WriteToAsync(messageStream);
    }

    /// <summary>
    /// Get the HTML body email for the report.
    /// </summary>
    /// <param name="workItems">The collection of work items in the report.</param>
    /// <returns>The body as a string.</returns>
    private string GetReportHtmlBody(IList<WorkItem> workItems)
    {
        string htmlBody = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">";
        htmlBody +=
            "<HTML><HEAD><META http-equiv=Content-Type content=\"text/html; charset=iso-8859-1\">";
        htmlBody +=
            $"</HEAD><BODY><DIV><FONT color=#0000ff size=6>Item Tracker Active Item Report<BR/><BR/></FONT></DIV>";
        htmlBody +=
            $"<P>This email was sent using Amazon SES from the Item Tracker Example." +
            $" The attached report contains a listing of the {workItems.Count} current active work items.</P>";
        htmlBody += $"<P>This report was generated on {DateTime.Now:g}.</P>";
        htmlBody += "</BODY></HTML>";
        return htmlBody;
    }

    /// <summary>
    /// Get the plain text body email for the report.
    /// </summary>
    /// <param name="workItems">The collection of work items in the report.</param>
    /// <returns>The body as a string.</returns>
    private string GetReportPlainTextBody(IList<WorkItem> workItems)
    {
        var plainTextBody =
            "This email was sent using Amazon SES from the Item Tracker Example." +
            $"\nThe report attached contains a listing of the {workItems.Count} current active work items." +
            $"\nThis report was generated on {DateTime.Now:g}.";
        return plainTextBody;
    }

    /// <summary>
    /// Put the work items into a CSV in a memory stream for emailing as an attachment.
    /// </summary>
    /// <param name="workItems">The work item collection.</param>
    /// <param name="memoryStream">The memory stream to use.</param>
    /// <param name="streamWriter">The stream writer to use.</param>
    /// <param name="csvWriter">The CSV writer to use.</param>
    /// <returns>Async task.</returns>
    public async Task GetCsvStreamFromWorkItems(IList<WorkItem> workItems, MemoryStream memoryStream, StreamWriter streamWriter, CsvWriter csvWriter)
    {
        csvWriter.WriteHeader<WorkItem>();
        await csvWriter.NextRecordAsync();
        await csvWriter.WriteRecordsAsync(workItems);
        await csvWriter.FlushAsync();
        await streamWriter.FlushAsync();
        memoryStream.Position = 0;
    }
}