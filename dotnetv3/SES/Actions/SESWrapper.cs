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
    /// <param name="amazonSimpleEmailService">Amazon Simple Email Service</param>
    public SESWrapper(IAmazonSimpleEmailService amazonSimpleEmailService)
    {
        _amazonSimpleEmailService = amazonSimpleEmailService;
    }

    // snippet-start:[SES.dotnetv3.ListIdentitiesAsync]

    /// <summary>
    ///     Get the identities of a specified type for the current account.
    /// </summary>
    /// <param name="identityType">IdentityType to list.</param>
    /// <returns>The list of identities.</returns>
    public async Task<List<string>> ListIdentitiesAsync(IdentityType identityType)
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
            Console.WriteLine("ListIdentitiesAsync failed with exception: " + ex.Message);
        }

        return result;
    }

    // snippet-end:[SES.dotnetv3.ListIdentitiesAsync]

    // snippet-start:[SES.dotnetv3.GetIdentityStatusAsync]

    /// <summary>
    ///     Get identity verification status for an email.
    /// </summary>
    /// <returns>The verification status of the email.</returns>
    public async Task<VerificationStatus> GetIdentityStatusAsync(string email)
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
            Console.WriteLine("GetIdentityStatusAsync failed with exception: " + ex.Message);
        }

        return result;
    }

    // snippet-end:[SES.dotnetv3.GetIdentityStatusAsync]

    // snippet-start:[SES.dotnetv3.VerifyEmailIdentityAsync]

    /// <summary>
    ///     Starts verification of an email identity. This function causes an email
    ///     to be sent to the specified email address from Amazon SES. To complete
    ///     verification, follow the instructions in the email.
    /// </summary>
    /// <param name="recipientEmailAddress">Email address to verify.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> VerifyEmailIdentityAsync(string recipientEmailAddress)
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
            Console.WriteLine("VerifyEmailIdentityAsync failed with exception: " + ex.Message);
        }

        return success;
    }

    // snippet-end:[SES.dotnetv3.VerifyEmailIdentityAsync]

    // snippet-start:[SES.dotnetv3.DeleteIdentityAsync]

    /// <summary>
    ///     Delete an email identity.
    /// </summary>
    /// <param name="identityEmail">The identity email to delete.</param>
    /// <returns>True if successful.</returns>
    public async Task<bool> DeleteIdentityAsync(string identityEmail)
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
            Console.WriteLine("DeleteIdentityAsync failed with exception: " + ex.Message);
        }

        return success;
    }

    // snippet-end:[SES.dotnetv3.DeleteIdentityAsync]

    // snippet-start:[SES.dotnetv3.SendEmailAsync]

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
    public async Task<string> SendEmailAsync(List<string> toAddresses,
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
            Console.WriteLine("SendEmailAsync failed with exception: " + ex.Message);
        }

        return messageId;
    }

    // snippet-end:[SES.dotnetv3.SendEmailAsync]

    // snippet-start:[SES.dotnetv3.GetSendQuotaAsync]

    /// <summary>
    ///     Get information on the current account's send quota.
    /// </summary>
    /// <returns>The send quota response data.</returns>
    public async Task<GetSendQuotaResponse> GetSendQuotaAsync()
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
            Console.WriteLine("GetSendQuotaAsync failed with exception: " + ex.Message);
        }

        return result;
    }

    // snippet-end:[SES.dotnetv3.GetSendQuotaAsync]

    // snippet-start:[SES.dotnetv3.ListEmailTemplatesAsync]

    /// <summary>
    ///     List email templates for the current account.
    /// </summary>
    /// <returns>A list of template metadata.</returns>
    public async Task<List<TemplateMetadata>> ListEmailTemplatesAsync()
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
            Console.WriteLine("ListEmailTemplatesAsync failed with exception: " + ex.Message);
        }

        return result;
    }

    // snippet-end:[SES.dotnetv3.ListEmailTemplatesAsync]

    // snippet-start:[SES.dotnetv3.CreateEmailTemplateAsync]

    /// <summary>
    ///     Create an email template.
    /// </summary>
    /// <param name="name">Name of the template.</param>
    /// <param name="subject">Email subject.</param>
    /// <param name="text">Email body text.</param>
    /// <param name="html">Email html body text.</param>
    /// <returns>True if success.</returns>
    public async Task<bool> CreateEmailTemplateAsync(string name, string subject, string text,
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
            Console.WriteLine("CreateEmailTemplateAsync failed with exception: " + ex.Message);
        }

        return success;
    }

    // snippet-end:[SES.dotnetv3.CreateEmailTemplateAsync]

    // snippet-start:[SES.dotnetv3.SendTemplateEmailAsync]

    /// <summary>
    ///     Send an email using a template.
    /// </summary>
    /// <param name="sender">Address of the sender.</param>
    /// <param name="recipients">Addresses of the recipients.</param>
    /// <param name="templateName">Name of the email template.</param>
    /// <param name="templateDataObject">Data for the email template.</param>
    /// <returns>The messageId of the email.</returns>
    public async Task<string> SendTemplateEmailAsync(string sender, List<string> recipients,
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
            Console.WriteLine("SendTemplateEmailAsync failed with exception: " + ex.Message);
        }

        return messageId;
    }

    // snippet-end:[SES.dotnetv3.SendTemplateEmailAsync]

    // snippet-start:[SES.dotnetv3.DeleteEmailTemplateAsync]

    /// <summary>
    ///     Delete an email template.
    /// </summary>
    /// <param name="templateName">Name of the template.</param>
    /// <returns>True if success.</returns>
    public async Task<bool> DeleteEmailTemplateAsync(string templateName)
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
            Console.WriteLine("DeleteEmailTemplateAsync failed with exception: " + ex.Message);
        }

        return success;
    }

    // snippet-end:[SES.dotnetv3.DeleteEmailTemplateAsync]
}