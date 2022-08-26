// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using System.Net;
using System.Text.Json;
using Amazon.SimpleEmail;
using Amazon.SimpleEmail.Model;

namespace SESActions;

/// <summary>
///     Wrapper with examples of usage for Amazon Simple Email Service (Amazon SES).
/// </summary>
public class SESWrapper
{
    private readonly IAmazonSimpleEmailService _amazonSimpleEmailService;

    /// <summary>
    ///     Constructor for the wrapper that uses the injected Amazon SES client.
    /// </summary>
    /// <param name="amazonSimpleEmailService"></param>
    public SESWrapper(IAmazonSimpleEmailService amazonSimpleEmailService)
    {
        _amazonSimpleEmailService = amazonSimpleEmailService;
    }

    // snippet-start:[SES.dotnetv3.ListIdentities]

    /// <summary>
    ///     Get the identities of a specified type for the current account.
    /// </summary>
    /// <returns>The list of identities.</returns>
    public async Task<List<string>> ListIdentities(IdentityType identityType)
    {
        var result = new List<string>();
        try
        {
            var response = await _amazonSimpleEmailService.ListIdentitiesAsync(
                new ListIdentitiesRequest
                {
                    IdentityType = identityType
                });
            result = response.Identities;
        }
        catch (Exception ex)
        {
            Console.WriteLine("ListIdentities failed with exception: " + ex.Message);
        }

        return result;
    }

    // snippet-end:[SES.dotnetv3.ListIdentities]

    // snippet-start:[SES.dotnetv3.GetIdentityStatus]

    /// <summary>
    ///     Get identity verification status for an email.
    /// </summary>
    /// <returns>The verification status of the email.</returns>
    public async Task<VerificationStatus> GetIdentityStatus(string email)
    {
        var result = VerificationStatus.TemporaryFailure;
        try
        {
            var response =
                await _amazonSimpleEmailService.GetIdentityVerificationAttributesAsync(
                    new GetIdentityVerificationAttributesRequest
                    {
                        Identities = new List<string> { email }
                    });

            if (response.VerificationAttributes.ContainsKey(email))
                result = response.VerificationAttributes[email].VerificationStatus;
        }
        catch (Exception ex)
        {
            Console.WriteLine("GetIdentityStatus failed with exception: " + ex.Message);
        }

        return result;
    }

    // snippet-end:[SES.dotnetv3.GetIdentityStatus]

    // snippet-start:[SES.dotnetv3.VerifyEmailIdentity]

    /// <summary>
    ///     Starts verification of an email identity. This function causes an email
    ///     to be sent to the specified email address from Amazon SES. To complete
    ///     verification, follow the instructions in the email.
    /// </summary>
    /// <param name="recipientEmailAddress">Email address to verify.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> VerifyEmailIdentity(string recipientEmailAddress)
    {
        var success = false;
        try
        {
            var response = await _amazonSimpleEmailService.VerifyEmailIdentityAsync(
                new VerifyEmailIdentityRequest
                {
                    EmailAddress = recipientEmailAddress
                });

            success = response.HttpStatusCode == HttpStatusCode.OK;
        }
        catch (Exception ex)
        {
            Console.WriteLine("VerifyEmailIdentity failed with exception: " + ex.Message);
        }

        return success;
    }

    // snippet-end:[SES.dotnetv3.VerifyEmailIdentity]

    // snippet-start:[SES.dotnetv3.DeleteIdentity]

    /// <summary>
    ///     Delete an email identity.
    /// </summary>
    /// <param name="identityEmail">The identity email to delete.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> DeleteIdentity(string identityEmail)
    {
        var success = false;
        try
        {
            var response = await _amazonSimpleEmailService.DeleteIdentityAsync(
                new DeleteIdentityRequest
                {
                    Identity = identityEmail
                });
            success = response.HttpStatusCode == HttpStatusCode.OK;
        }
        catch (Exception ex)
        {
            Console.WriteLine("DeleteIdentity failed with exception: " + ex.Message);
        }

        return success;
    }

    // snippet-end:[SES.dotnetv3.DeleteIdentity]

    // snippet-start:[SES.dotnetv3.SendEmail]

    /// <summary>
    ///     Send an email using Amazon SES.
    /// </summary>
    /// <param name="toAddresses">List of recipients.</param>
    /// <param name="ccAddresses">List of carbon copy recipients.</param>
    /// <param name="bccAddresses">List of blind carbon copy recipients.</param>
    /// <param name="bodyHtml">Body of the email in HTML.</param>
    /// <param name="bodyText">Body of the email in plain text.</param>
    /// <param name="subject">Subject line of the email.</param>
    /// <param name="senderAddress">From address.</param>
    /// <returns>The messageId of the email.</returns>
    public async Task<string> SendEmail(List<string> toAddresses,
        List<string> ccAddresses, List<string> bccAddresses,
        string bodyHtml, string bodyText, string subject, string senderAddress)
    {
        var messageId = "";
        try
        {
            var response = await _amazonSimpleEmailService.SendEmailAsync(
                new SendEmailRequest
                {
                    Destination = new Destination
                    {
                        BccAddresses = bccAddresses,
                        CcAddresses = ccAddresses,
                        ToAddresses = toAddresses
                    },
                    Message = new Message
                    {
                        Body = new Body
                        {
                            Html = new Content
                            {
                                Charset = "UTF-8",
                                Data = bodyHtml
                            },
                            Text = new Content
                            {
                                Charset = "UTF-8",
                                Data = bodyText
                            }
                        },
                        Subject = new Content
                        {
                            Charset = "UTF-8",
                            Data = subject
                        }
                    },
                    Source = senderAddress
                });
            messageId = response.MessageId;
            return messageId;
        }
        catch (Exception ex)
        {
            Console.WriteLine("SendEmail failed with exception: " + ex.Message);
        }

        return messageId;
    }

    // snippet-end:[SES.dotnetv3.SendEmail]

    // snippet-start:[SES.dotnetv3.GetSendQuota]

    /// <summary>
    ///     Get information on the current account's send quota.
    /// </summary>
    /// <returns>The send quota response data.</returns>
    public async Task<GetSendQuotaResponse> GetSendQuota()
    {
        var result = new GetSendQuotaResponse();
        try
        {
            var response = await _amazonSimpleEmailService.GetSendQuotaAsync(
                new GetSendQuotaRequest());
            result = response;
        }
        catch (Exception ex)
        {
            Console.WriteLine("GetSendQuota failed with exception: " + ex.Message);
        }

        return result;
    }

    // snippet-end:[SES.dotnetv3.GetSendQuota]

    // snippet-start:[SES.dotnetv3.ListEmailTemplates]

    /// <summary>
    ///     List email templates for the current account.
    /// </summary>
    /// <returns>A list of template metadata.</returns>
    public async Task<List<TemplateMetadata>> ListEmailTemplates()
    {
        var result = new List<TemplateMetadata>();
        try
        {
            var response = await _amazonSimpleEmailService.ListTemplatesAsync(
                new ListTemplatesRequest());
            result = response.TemplatesMetadata;
        }
        catch (Exception ex)
        {
            Console.WriteLine("ListEmailTemplates failed with exception: " + ex.Message);
        }

        return result;
    }

    // snippet-end:[SES.dotnetv3.ListEmailTemplates]

    // snippet-start:[SES.dotnetv3.CreateEmailTemplate]

    /// <summary>
    ///     Create an email template.
    /// </summary>
    /// <param name="name">Name of the template.</param>
    /// <param name="subject">Email subject.</param>
    /// <param name="text">Email body text.</param>
    /// <param name="html">Email html body text.</param>
    /// <returns>True if success.</returns>
    public async Task<bool> CreateEmailTemplate(string name, string subject, string text,
        string html)
    {
        var success = false;
        try
        {
            var response = await _amazonSimpleEmailService.CreateTemplateAsync(
                new CreateTemplateRequest
                {
                    Template = new Template
                    {
                        TemplateName = name,
                        SubjectPart = subject,
                        TextPart = text,
                        HtmlPart = html
                    }
                });
            success = response.HttpStatusCode == HttpStatusCode.OK;
        }
        catch (Exception ex)
        {
            Console.WriteLine("CreateEmailTemplate failed with exception: " + ex.Message);
        }

        return success;
    }

    // snippet-end:[SES.dotnetv3.CreateEmailTemplate]

    // snippet-start:[SES.dotnetv3.SendTemplateEmail]

    /// <summary>
    ///     Send an email using a template.
    /// </summary>
    /// <param name="sender">Address of the sender.</param>
    /// <param name="recipients">Addresses of the recipients.</param>
    /// <param name="templateName">Name of the email template.</param>
    /// <param name="templateDataObject">Data for the email template.</param>
    /// <returns>The messageId of the email.</returns>
    public async Task<string> SendTemplateEmail(string sender, List<string> recipients,
        string templateName, object templateDataObject)
    {
        var messageId = "";
        try
        {
            // Template data should be serialized JSON from either a class or a dynamic object.
            var templateData = JsonSerializer.Serialize(templateDataObject);

            var response = await _amazonSimpleEmailService.SendTemplatedEmailAsync(
                new SendTemplatedEmailRequest
                {
                    Source = sender,
                    Destination = new Destination
                    {
                        ToAddresses = recipients
                    },
                    Template = templateName,
                    TemplateData = templateData
                });
            messageId = response.MessageId;
        }
        catch (Exception ex)
        {
            Console.WriteLine("SendTemplateEmail failed with exception: " + ex.Message);
        }

        return messageId;
    }

    // snippet-end:[SES.dotnetv3.SendTemplateEmail]

    // snippet-start:[SES.dotnetv3.DeleteEmailTemplate]

    /// <summary>
    ///     Delete an email template.
    /// </summary>
    /// <param name="templateName">Name of the template.</param>
    /// <returns>True if success.</returns>
    public async Task<bool> DeleteEmailTemplate(string templateName)
    {
        var success = false;
        try
        {
            var response = await _amazonSimpleEmailService.DeleteTemplateAsync(
                new DeleteTemplateRequest
                {
                    TemplateName = templateName
                });
            success = response.HttpStatusCode == HttpStatusCode.OK;
        }
        catch (Exception ex)
        {
            Console.WriteLine("DeleteEmailTemplate failed with exception: " + ex.Message);
        }

        return success;
    }

    // snippet-end:[SES.dotnetv3.DeleteEmailTemplate]
}