// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using System.Globalization;
using Amazon.SimpleEmailV2;
using DynamoDbItemTracker;
using CsvHelper;
using Microsoft.Extensions.Configuration;
namespace ItemTrackerTests;

/// <summary>
/// Tests for ReportService.
/// </summary>
public class ReportServiceTests
{
    private readonly IConfiguration _configuration;
    private readonly ReportService _reportService;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public ReportServiceTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally load local settings.
            .Build();

        _reportService = new ReportService(new AmazonSimpleEmailServiceV2Client(), _configuration);
    }

    [Fact]
    [Order(1)]
    [Trait("Category", "Integration")]
    public async Task VerifyCanSendEmail_ShouldReturnNotNullMessageId()
    {
        var workItems = new List<WorkItem>
        {
            new WorkItem
            {
                Id = "sample1",
                Description = "sample item",
                Guide = "guide",
                Name = "name",
                Status = "complete",
                Archived = false
            }
        };
        var email = _configuration["EmailRecipientAddress"];
        var messageId = await _reportService.SendReport(workItems, email);
        Assert.NotNull(messageId);
    }

    [Fact]
    [Order(2)]
    [Trait("Category", "Unit")]
    public async Task VerifyBuildRawMessage_ShouldReturnStream()
    {
        var workItems = new List<WorkItem>
        {
            new WorkItem
            {
                Id = "sample1",
                Description = "sample item",
                Guide = "guide",
                Name = "name",
                Status = "complete",
                Archived = false
            }
        };

        await using var attachmentStream = new MemoryStream();
        await using var streamWriter = new StreamWriter(attachmentStream);
        await using var csvWriter = new CsvWriter(streamWriter, CultureInfo.InvariantCulture);
        await _reportService.GetCsvStreamFromWorkItems(workItems, attachmentStream, streamWriter, csvWriter);
        await using var messageStream = new MemoryStream();

        var email = _configuration["EmailRecipientAddress"];
        await _reportService.BuildRawMessageWithAttachment(
            email,
            "body",
            "html",
            "subject",
            "attachment.csv",
            attachmentStream,
            messageStream);

        Assert.NotEqual(0, messageStream.Length);
        Assert.True(messageStream.CanRead);
    }

    [Fact]
    [Order(3)]
    [Trait("Category", "Unit")]
    public async Task VerifyBuildCsv_ShouldReturnStream()
    {
        var workItems = new List<WorkItem>
        {
            new WorkItem
            {
                Id = "sample1",
                Description = "sample item",
                Guide = "guide",
                Name = "name",
                Status = "complete",
                Archived = false
            },
            new WorkItem
            {
                Id = "sample2",
                Description = "sample item",
                Guide = "guide",
                Name = "name",
                Status = "complete",
                Archived = false
            }
        };

        await using var attachmentStream = new MemoryStream();
        await using var streamWriter = new StreamWriter(attachmentStream);
        await using var csvWriter = new CsvWriter(streamWriter, CultureInfo.InvariantCulture);
        await _reportService.GetCsvStreamFromWorkItems(workItems, attachmentStream, streamWriter, csvWriter);

        var csvString = _configuration["csvString"];
        Assert.NotEqual(0, attachmentStream.Length);
        Assert.Equal(0, attachmentStream.Position);
        Assert.True(attachmentStream.CanRead);
        Assert.Equal(csvString, System.Text.Encoding.ASCII.GetString(attachmentStream.ToArray()));
    }
}
