# Amazon SESv2 Email Attachments Scenario

## Overview

This example shows how to use AWS SDKs to send emails with attachments using Amazon Simple Email Service v2 (Amazon SESv2). The scenario demonstrates the attachment support in the SESv2 `SendEmail` and `SendBulkEmail` APIs, which enables customers to include file attachments directly in simple and templated email messages without constructing raw MIME messages. SES handles the MIME construction automatically.

[Working with email attachments in SES](https://docs.aws.amazon.com/ses/latest/dg/attachments.html) describes how to attach files such as PDFs, images, and documents to emails sent through Amazon SES.

This scenario demonstrates the following steps and tasks:

1. Set up a verified email identity and create an email template.
2. Send a simple email with a file attachment.
   - SES automatically constructs the MIME message with the attachment.
3. Send a simple email with an inline image.
   - Uses `INLINE` content disposition and `cid:` references to render images in the HTML body.
4. Send bulk templated emails with attachments to multiple recipients.
   - Uses `SendBulkEmail` with personalized template data per recipient.
5. Clean up resources (delete the template and optionally the email identity).

### Resources

- A verified email identity (email address or domain) in Amazon SES is required to send emails. The scenario will verify or create a sender email identity.
- An email template is created during the scenario to demonstrate attachments with templated email content via `SendBulkEmail`.
- No additional AWS infrastructure (such as CloudFormation stacks) is required.

### API actions used

- [SendEmail](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_SendEmail.html)
- [SendBulkEmail](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_SendBulkEmail.html)
- [CreateEmailIdentity](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_CreateEmailIdentity.html)
- [CreateEmailTemplate](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_CreateEmailTemplate.html)
- [GetEmailIdentity](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_GetEmailIdentity.html)
- [DeleteEmailTemplate](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_DeleteEmailTemplate.html)
- [DeleteEmailIdentity](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_DeleteEmailIdentity.html)

## Implementations

This example is implemented in the following languages:

- [Python](../../../python/example_code/sesv2/attachments_scenario/README.md)

## Additional reading

- [What is Amazon SES?](https://docs.aws.amazon.com/ses/latest/DeveloperGuide/Welcome.html)
- [Working with email attachments in SES](https://docs.aws.amazon.com/ses/latest/dg/attachments.html)
- [Moving out of the Amazon SES sandbox](https://docs.aws.amazon.com/ses/latest/dg/request-production-access.html)
- [Attachment object structure](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_Attachment.html)
- [Unsupported attachment types](https://docs.aws.amazon.com/ses/latest/dg/mime-types-appendix.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
