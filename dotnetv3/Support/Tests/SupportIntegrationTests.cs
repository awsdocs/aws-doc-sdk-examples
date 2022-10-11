// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.AWSSupport;
using Amazon.AWSSupport.Model;
using Amazon.Runtime;
using Microsoft.Extensions.Configuration;
using SupportActions;

namespace SupportTests;

/// <summary>
/// Integration tests for the AWS Support examples.
/// </summary>
public class SupportIntegrationTests
{
    private readonly IConfiguration _configuration;
    private readonly SupportWrapper _supportWrapper;
    private static string? _caseId;
    private static string? _attachmentSetId;

    /// <summary>
    /// Constructor for the test class.
    /// </summary>
    public SupportIntegrationTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from .json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally load local settings.
            .Build();

        _supportWrapper = new SupportWrapper(new AmazonAWSSupportClient());
    }

    /// <summary>
    /// Verify a subscription with API access. Should return true.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    public async Task VerifySubscription_ShouldReturnTrue()
    {
        var result = await _supportWrapper.VerifySubscription();

        Assert.True(result);
    }

    /// <summary>
    /// Verify a subscription without API access.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(2)]
    public async Task VerifySubscription_ShouldReturnFalse()
    {
        var supportClient = new AmazonAWSSupportClient(
            new BasicAWSCredentials(
                _configuration["missingSubscriptionAccessKey"],
                _configuration["missingSubscriptionSecret"]));
        var supportWrapperWithoutSubscription = new SupportWrapper(supportClient);
        var result = await supportWrapperWithoutSubscription.VerifySubscription();

        Assert.False(result);
    }

    /// <summary>
    /// Describe the available support services. Should return a list that is not empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    public async Task VerifyDescribeServices_ShouldNotBeEmpty()
    {
        var services = await _supportWrapper.DescribeServices();
        Assert.NotEmpty(services);
    }

    /// <summary>
    /// Describe the available support severity levels. Should return a list that is not empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(4)]
    public async Task VerifyDescribeSeverityLevels_ShouldNotBeEmpty()
    {
        var services = await _supportWrapper.DescribeSeverityLevels();
        Assert.NotEmpty(services);
    }

    /// <summary>
    /// Create a support case. Should return a new case Id.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(5)]
    public async Task VerifyCreateCase_ShouldReturnId()
    {
        var serviceCode = _configuration["serviceCode"];
        var categoryCode = _configuration["categoryCode"];
        var severityCode = _configuration["severityCode"];
        var caseId = await _supportWrapper.CreateCase(
            serviceCode,
            categoryCode,
            severityCode,
            "Example case for integration testing, please ignore.",
            "This is an example support case.");
        _caseId = caseId;
        Assert.False(string.IsNullOrWhiteSpace(caseId));
    }

    /// <summary>
    /// Create a support case without a service code. Should throw an exception.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(6)]
    public async Task VerifyCreateCaseWithInvalidCode_ShouldFail()
    {
        await Assert.ThrowsAsync<AmazonAWSSupportException>(async () =>
        {
            await _supportWrapper.CreateCase(
                "",
                "test",
                "low",
                "Example case for integration testing, please ignore.",
                "This is an example support case.");
        });
    }

    /// <summary>
    /// Add an attachment set. Should return a new attachment Id.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(7)]
    public async Task VerifyAddAttachmentToSet_ShouldReturnId()
    {
        var fileName = _configuration["exampleFileName"];
        // Create the file if it does not already exist.
        if (!File.Exists(fileName))
        {
            await using StreamWriter sw = File.CreateText(fileName);
            await sw.WriteLineAsync(
                "This is a sample file for attachment to a support case.");
        }

        await using var ms = new MemoryStream(await File.ReadAllBytesAsync(fileName));

        var attachmentSetId = await _supportWrapper.AddAttachmentToSet(
            ms,
            fileName);
        _attachmentSetId = attachmentSetId;

        Assert.False(string.IsNullOrWhiteSpace(attachmentSetId));
    }

    /// <summary>
    /// Add a communication. Should return true.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(8)]
    public async Task VerifyAddCommunicationToCase_ShouldReturnId()
    {
        var result = await _supportWrapper.AddCommunicationToCase(
            _caseId!,
            "This is an example communication added to a support case.",
            _attachmentSetId);

        Assert.True(result);
    }

    /// <summary>
    /// Describe the communications of a case. Should return a list that is not empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(9)]
    public async Task VerifyCommunicationForCase_ShouldNotBeEmpty()
    {
        var communications = await _supportWrapper.DescribeCommunications(_caseId!);
        _attachmentSetId = communications.FirstOrDefault()?.AttachmentSet.FirstOrDefault()?
            .AttachmentId;
        Assert.NotEmpty(communications);
    }

    /// <summary>
    /// Describe the attachment set. Should return an attachment with a filename that is not empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(10)]
    public async Task VerifyDescribeAttachment_ShouldNotBeEmpty()
    {
        var attachment = await _supportWrapper.DescribeAttachment(_attachmentSetId!);
        Assert.NotEmpty(attachment.FileName);
    }

    /// <summary>
    /// Describe cases in the support account. Should return a list that is not empty.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(11)]
    public async Task VerifyDescribeCases_ShouldNotBeEmpty()
    {
        // Describe the cases. In case it is empty, try again and allow time for the new case to appear.
        List<CaseDetails> todayCases = null!;
        while (todayCases == null || todayCases.Count == 0)
        {
            Thread.Sleep(1000);
            todayCases = await _supportWrapper.DescribeCases(
                new List<string>(),
                null,
                false,
                true,
                DateTime.Today,
                DateTime.Now);
        }
        Assert.NotEmpty(todayCases);
    }

    /// <summary>
    /// Resolve an active case. Should return a result of resolved
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(12)]
    public async Task VerifyResolveCase_ShouldReturnResolved()
    {
        var result = await _supportWrapper.ResolveCase(_caseId!);

        Assert.Equal("resolved", result);
    }
}