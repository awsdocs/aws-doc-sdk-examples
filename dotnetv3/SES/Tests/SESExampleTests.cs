// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.SimpleEmail;
using Microsoft.Extensions.Configuration;
using SESActions;

namespace SESTests;

/// <summary>
/// Amazon SES example integration tests
/// </summary>
public class SesExampleTests
{
    private readonly IConfiguration _configuration;
    private readonly SESWrapper _wrapper;

    public SesExampleTests()
    {
        _configuration = new ConfigurationBuilder()
            .SetBasePath(Directory.GetCurrentDirectory())
            .AddJsonFile("testsettings.json") // Load test settings from json file.
            .AddJsonFile("testsettings.local.json",
                true) // Optionally load local settings.
            .Build();

        _wrapper = new SESWrapper(new AmazonSimpleEmailServiceClient());
    }

    /// <summary>
    /// Verify a valid email. Should return true.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(1)]
    public async Task VerifyIdentity_ValidEmail_ReturnsTrue()
    {
        var verifyEmail = _configuration["ValidEmailAddress"];
        var success = await _wrapper.VerifyEmailIdentityAsync(verifyEmail);
        Assert.True(success, "Could not validate the email address.");
    }

    /// <summary>
    /// Verify an email that isn't valid. Should not return true.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(2)]
    public async Task VerifyIdentity_NotValidEmail_ReturnsFalse()
    {
        var verifyEmail = _configuration["NotValidEmailAddress"];
        var success = await _wrapper.VerifyEmailIdentityAsync(verifyEmail);
        Assert.False(success, "Non-valid email did not fail.");
    }

    /// <summary>
    /// List identities for email type should include the new identity.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(3)]
    public async Task ListIdentity_EmailType_ReturnsWithEmail()
    {
        var verifyEmail = _configuration["ValidEmailAddress"];
        var identities = await _wrapper.ListIdentitiesAsync(IdentityType.EmailAddress);
        Assert.Contains(verifyEmail, identities);
        Assert.NotEmpty(identities);
    }

    /// <summary>
    /// Get identity status for a valid email should return a status.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(4)]
    public async Task GetIdentityStatus_ValidEmail_ReturnsStatus()
    {
        var validEmail = _configuration["ValidEmailAddress"];
        var verificationStatus = await _wrapper.GetIdentityStatusAsync(validEmail);
        Assert.NotEqual(VerificationStatus.TemporaryFailure, verificationStatus);
    }

    /// <summary>
    /// Get identity status for an email that isn't valid should return a temporary error.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(5)]
    public async Task GetIdentityStatus_NotValidEmail_ReturnsNotStarted()
    {
        var notValidEmail = _configuration["NotValidEmailAddress"];
        var verificationStatus = await _wrapper.GetIdentityStatusAsync(notValidEmail);
        Assert.Equal(VerificationStatus.TemporaryFailure, verificationStatus);
    }

    /// <summary>
    /// Send an email with all valid settings should return a messageId.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(6)]
    public async Task SendEmail_ValidSettings_ReturnsMessageId()
    {
        var senderAddress = _configuration["SendEmailSenderAddress"];
        var toAddresses = _configuration["SendEmailToAddresses"].Split(',').ToList();
        var ccAddresses = _configuration["SendEmailCcAddresses"].Split(',').ToList();
        var bccAddresses = _configuration["SendEmailBccAddresses"].Split(',').ToList();
        var messageId = await _wrapper.SendEmailAsync(toAddresses, ccAddresses, bccAddresses,
            "test html", "test body", "test subject", senderAddress);
        Assert.NotEqual("", messageId);
    }

    /// <summary>
    ///  Send an email with a missing sender should return no messageId.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(7)]
    public async Task SendEmail_MissingSender_ReturnsEmptyString()
    {
        var senderAddress = "";
        var toAddresses = _configuration["SendEmailToAddresses"].Split(',').ToList();
        var ccAddresses = _configuration["SendEmailCcAddresses"].Split(',').ToList();
        var bccAddresses = _configuration["SendEmailBccAddresses"].Split(',').ToList();
        var messageId = await _wrapper.SendEmailAsync(toAddresses, ccAddresses, bccAddresses,
            "test html", "test body", "test subject", senderAddress);
        Assert.Equal("", messageId);
    }

    /// <summary>
    /// Send an email with a recipient that isn't valid should return no messageId.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(8)]
    public async Task SendEmail_NotValidRecipient_ReturnsEmptyString()
    {
        var senderAddress = _configuration["SendEmailSenderAddress"];
        var toAddresses =
            _configuration["SendEmailToNotValidAddresses"].Split(',').ToList();
        var ccAddresses = _configuration["SendEmailCcAddresses"].Split(',').ToList();
        var bccAddresses = _configuration["SendEmailBccAddresses"].Split(',').ToList();
        var messageId = await _wrapper.SendEmailAsync(toAddresses, ccAddresses, bccAddresses,
            "test html", "test body", "test subject", senderAddress);
        Assert.Equal("", messageId);
    }

    /// <summary>
    /// Create an email template email with valid settings should return true.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(9)]
    public async Task CreateEmailTemplate_ValidSettings_ReturnsTrue()
    {
        var templateName = _configuration["TemplateName"];
        var templateSubject = _configuration["TemplateSubject"];
        var templateText = _configuration["TemplateText"];
        var templateHtml = _configuration["TemplateHtml"];

        var success = await _wrapper.CreateEmailTemplateAsync(templateName, templateSubject,
            templateText, templateHtml);
        Assert.True(success, "Unable to create template.");
    }

    /// <summary>
    /// Create an email template email with settings that aren't valid. Should return false.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(10)]
    public async Task CreateEmailTemplate_NotValidSettings_ReturnsFalse()
    {
        var success = await _wrapper.CreateEmailTemplateAsync("", "", "", "");
        Assert.False(success, "Template with not valid settings did not fail.");
    }

    /// <summary>
    /// Send a template email with valid settings should return a messageId.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(11)]
    public async Task SendTemplateEmail_ValidSettings_ReturnsMessageId()
    {
        var templateName = _configuration["TemplateName"];
        var senderAddress = _configuration["SendEmailSenderAddress"];
        var toAddresses = _configuration["SendEmailToAddresses"].Split(',').ToList();
        var templateDataObject = new { sender = senderAddress, recipient = toAddresses };

        var messageId = await _wrapper.SendTemplateEmailAsync(senderAddress, toAddresses,
            templateName, templateDataObject);
        Assert.NotEqual("", messageId);
    }

    /// <summary>
    /// Send a template email with settings that aren't valid. Should return no messageId.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(12)]
    public async Task SendTemplateEmail_NotValidSettings_ReturnsEmptyString()
    {
        var senderAddress = _configuration["SendEmailSenderAddress"];
        var toAddresses = _configuration["SendEmailToAddresses"].Split(',').ToList();
        var templateDataObject = new { sender = senderAddress, recipient = toAddresses };

        var messageId = await _wrapper.SendTemplateEmailAsync(senderAddress, toAddresses, "",
            templateDataObject);
        Assert.Equal("", messageId);
    }

    /// <summary>
    /// List email templates should return a list that includes the template.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(13)]
    public async Task ListEmailTemplates_ReturnsWithTemplate()
    {
        var templateName = _configuration["TemplateName"];
        var templateList = await _wrapper.ListEmailTemplatesAsync();
        Assert.Contains(templateName, templateList.Select(t => t.Name));
    }

    /// <summary>
    /// Send a template email with valid settings should return true.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(14)]
    public async Task DeleteEmailTemplate_ValidName_ReturnsTrue()
    {
        var templateName = _configuration["TemplateName"];
        var success = await _wrapper.DeleteEmailTemplateAsync(templateName);
        Assert.True(success, "Unable to delete email template.");
    }

    /// <summary>
    /// Send a template email with settings that aren't valid. Should return false.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(15)]
    public async Task DeleteEmailTemplate_NotValidName_ReturnsFalse()
    {
        var success = await _wrapper.DeleteEmailTemplateAsync("");
        Assert.False(success,
            "Delete template with name that is not valid did not fail.");
    }

    /// <summary>
    ///  List email templates should not include the template after deletion.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(16)]
    public async Task ListEmailTemplates_ReturnsWithoutTemplate()
    {
        var templateName = _configuration["TemplateName"];
        var templateList = await _wrapper.ListEmailTemplatesAsync();
        Assert.DoesNotContain(templateName, templateList.Select(t => t.Name));
    }

    /// <summary>
    /// Delete an identity that exists with a valid name. Should return true.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(17)]
    public async Task DeleteIdentity_ValidEmail_ReturnsTrue()
    {
        var identityEmail = _configuration["ValidEmailAddress"];
        var success = await _wrapper.DeleteIdentityAsync(identityEmail);
        Assert.True(success, "Unable to delete email identity.");
    }

    /// <summary>
    /// Delete an identity that exists with an name that isn't valid. Should return false.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(18)]
    public async Task DeleteIdentity_NotValidEmail_ReturnsFalse()
    {
        var success = await _wrapper.DeleteIdentityAsync("");
        Assert.False(success, "Delete identity without a valid email did not fail.");
    }

    /// <summary>
    /// Get send quota should return not null.
    /// </summary>
    /// <returns>Async task.</returns>
    [Fact]
    [Order(19)]
    public async Task GetSendQuota_ReturnsNotNull()
    {
        var sendQuota = await _wrapper.GetSendQuotaAsync();
        Assert.NotNull(sendQuota);
    }
}