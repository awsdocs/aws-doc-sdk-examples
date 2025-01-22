---
combined: true
debug:
  engine: bedrock
  finish: end_turn
  id: null
  model: anthropic.claude-3-sonnet-20240229-v1:0
  usage: null
isolated: false
prompt: >
  Write the SPECIFICATION.md for the workflow.


  The specification is independent of any programming language, and should
  enable any programmer competent with programming using any published AWS SDK
  to follow along. It must specify the API calls to make, and it must include
  the parameters to send. It should describe the parameters in a list format.
  Implementations will use the specific SDKs, so it does
  not need to specify URL calls to make, only the API calls and the request
  parameters to include. It must specify the exact environment variable names
  and files to use when referring to runtime data.


  Additional instructions for each section of the specification:


  Prepare the Application:

  - Print a short intro message.

  - The verified email should be provided by the builder in an environment
  variable for the workflow.

  - The contact list should have a hardcoded name relevant to the workflow.

  - The newsletter should use a provided template, but read it in at runtime.


  Gather Subscriber Email Addresses:


  - The application should ask the user for several (3 to 5) email addresses
  interactively on the command line.

  - Suggest that during testing, users use a single email address and provide a
  number of plus addresses.

  - The contents of the welcome email should be provided in the spec, but read
  from an html file at runtime.


  Send the Coupon Newsletter:


  - After gathering the subscriber emails, send a weekly newsletter.

  - Specify six coupon items for the newsletter.

  - Include links to the template reference documentation,
  https://docs.aws.amazon.com/ses/latest/dg/send-personalized-email-api.html


  Monitor and Review:


  - Leave this section vague for now, we will fill it in later.
---

# SES v2 Coupon Newsletter Workflow Specification

Use the Amazon Simple Email Service (SES) v2 API to manage a subscription list for a weekly newsletter.

At the start of the workflow, print this header:

> Welcome to the Amazon SES v2 Coupon Newsletter Workflow!
>
> This workflow will help you:
>
> 1. Prepare a verified email identity and contact list for your newsletter.
> 2. Gather subscriber email addresses and send them a welcome email.
> 3. Send a weekly coupon newsletter to your subscribers using email templates.
> 4. Monitor your sending activity and metrics in the AWS console.
>
> Let's get started!

## Prepare the Application

1. Create an email identity.
   - Request a `verified email` address from the user. This will be used as the `from` address, and the user will need to click a verification link in their email before continuing to part 3.
   - Operation: **CreateEmailIdentity**
     - Parameters:
       - `EmailIdentity`: Value of the `verified email` given by the user.
     - Errors:
       - `AlreadyExistsException`: If the identity already exists, skip this step and proceed with the next operation. This error can be safely ignored.
       - `NotFoundException`: If the identity does not exist, fail the workflow and inform the user that the provided email address is not verified.
       - `LimitExceededException`: If the limit for email identities is exceeded, fail the workflow and inform the user that they have reached the limit for email identities.
2. Create a contact list with the name `weekly-coupons-newsletter`.
   - Operation: **CreateContactList**
     - Parameters:
       - `ContactListName`: `weekly-coupons-newsletter`
     - Errors:
       - `AlreadyExistsException`: If the contact list already exists, skip this step and proceed with the next operation. This error can be safely ignored.
       - `LimitExceededException`: If the limit for contact lists is exceeded, fail the workflow and inform the user that they have reached the limit for contact lists.

## Gather Subscriber Email Addresses

1. Prompt the user to enter a base email address for subscribing to the newsletter.
   - For testing purposes, this workflow uses a single email address with [subaddress extensions](https://www.rfc-editor.org/rfc/rfc5233.html) (e.g., `user+ses-weekly-newsletter-1@example.com`, `user+ses-weekly-newsletter-2@example.com`, etc., also known as [plus addressing](https://en.wikipedia.org/wiki/Email_address#:~:text=For%20example%2C%20the%20address%20joeuser,sorting%2C%20and%20for%20spam%20control.)).
   - Create 3 variants of this email address as `{user email}+ses-weekly-newsletter-{i}@{user domain}`.
   - `{user-email}` is the portion up to the first `@` (0x40, dec 64). The `{user domain}` is everything after the first `@`.
2. For each email address created:
   1. Create a new contact with the provided email address in the `weekly-coupons-newsletter` contact list.
      - Operation: **CreateContact**
        - Parameters:
          - `ContactListName`: `weekly-coupons-newsletter`
          - `EmailAddress`: The email address provided by the user.
        - Errors:
          - `AlreadyExistsException`: If the contact already exists, skip this step for that contact and proceed with the next contact. This error can be safely ignored.
   2. Send a welcome email to the new contact using the content from the `welcome.html` file.
      - Operation: **SendEmail**
        - Parameters:
          - `FromEmailAddress`: Use the `verified_email` address provided in Prepare the Application.
          - `Destination.ToAddresses`: The email address provided by the user.
          - `Content.Simple.Subject.Data`: "Welcome to the Weekly Coupons Newsletter"
          - `Content.Simple.Body.Text.Data`: Read the content from the `welcome.txt` file.
          - `Content.Simple.Body.Html.Data`: Read the content from the `welcome.html` file.
        - Errors:
          - See Errors in `SendEmail` for "Send the Coupon Newsletter"

## Send the Coupon Newsletter

1. Create an email template named `weekly-coupons` with the following content:
   - Subject: `Weekly Coupons Newsletter`
   - HTML Content: Available in the `coupon-newsletter.html` file.
   - Text Content: Available in the `coupon-newsletter.txt` file.
   - The emails should include an [Unsubscribe](#) link, using the url `{{amazonSESUnsubscribeUrl}}`.
   - Operation: **CreateEmailTemplate**
     - Parameters:
       - `TemplateName`: `weekly-coupons`
       - `TemplateContent`:
         - `Subject`: `Weekly Coupons Newsletter`
         - `Html`: Read from the `coupon-newsletter.html` file
         - `Text`: Read from the `coupon-newsletter.txt` file
     - Errors:
       - `AlreadyExistsException`: If the template already exists, skip this step and proceed with the next operation. This error can be safely ignored.
       - `LimitExceededException`: If the limit for email templates is exceeded, fail the workflow and inform the user that they have reached the limit for email templates.
2. Retrieve the list of contacts from the `weekly-coupons-newsletter` contact list.
   - Operation: **ListContacts**
     - Parameters:
       - `ContactListName`: `weekly-coupons-newsletter`
     - Errors:
       - `NotFoundException`: If the contact list does not exist, fail the workflow and inform the user that the contact list is missing.
3. Send an email using the `weekly-coupons` template to each contact in the list.
   - The email should include the following coupon items:
     1. 20% off on all electronics
     2. Buy one, get one free on books
     3. 15% off on home appliances
     4. Free shipping on orders over $50
     5. 25% off on outdoor gear
     6. 10% off on groceries
   - Operation: **SendEmail**
     - Parameters:
       - `Destination`:
         - `ToAddresses`: One email address from the `ListContacts` response (each email address must get a unique `SendEmail` call for tracking and unsubscribe purposes).
       - `Content`:
         - `Template`:
           - `TemplateName`: `weekly-coupons`
           - `TemplateData`: JSON string representing an object with one key, `coupons`, which is an array of coupon items. Each coupon entry in the array should have one key, `details`, with the details of the coupon. See `sample_coupons.json`.
       - `FromEmailAddress`: (Use the verified email address from step 1)
       - `ListManagementOptions`:
         - `ContactListName`: `weekly-coupons-newsletter` to correctly populate Unsubscribe headers and the `{{amazonSESUnsubscribeUrl}}` value.
     - Errors:
       - `AccountSuspendedException`: If the account is suspended, fail the workflow and inform the user that their account is suspended.
       - `MailFromDomainNotVerifiedException`: If the sending domain is not verified, fail the workflow and inform the user that the sending domain is not verified.
       - `MessageRejected`: If the message is rejected due to invalid content, fail the workflow and inform the user that the message content is invalid.
       - `SendingPausedException`: If sending is paused, fail the workflow and inform the user that sending is currently paused for their account.

For more information on using templates with SES v2, refer to the [Amazon SES Developer Guide](https://docs.aws.amazon.com/ses/latest/dg/send-personalized-email-api.html).

## Monitor and Review

1. [Monitor your sending activity](https://docs.aws.amazon.com/ses/latest/dg/monitor-sending-activity.html) using the [SES Homepage](https://console.aws.amazon.com/ses/home#/account) in the AWS console.

## Clean up

1. Delete the contact list. This operation also deletes all contacts in the list, without needing separate calls.
   - Operation: **DeleteContactList**
     - Parameters:
       - `ContactListName`: `weekly-coupons-newsletter`
       - `NotFoundException`: If the contact list does not exist, skip this step and proceed with the next operation. This error can be safely ignored.
     - Errors:

- `NotFoundException`: If the contact list does not exist, skip this step and proceed with the next operation. This error can be safely ignored.

2. Delete the template.
   - Operation: **DeleteEmailTemplate**
     - Parameters:
       - `TemplateName`: `weekly-coupons`
       - `NotFoundException`: If the email template does not exist, skip this step and proceed with the next operation. This error can be safely ignored.
     - Errors:
       - `NotFoundException`: If the email template does not exist, skip this step and proceed with the next operation. This error can be safely ignored.
3. Delete the email identity (optional). Ask the user before performing this step, as they may not want to re-verify the email identity.
   - Operation: **DeleteEmailIdentity**
     - Parameters:
       - `EmailIdentity`: Value of the `verified email` given by the user in part 1.
     - Errors:
       - `NotFoundException`: If the email identity does not exist, skip this step and proceed with the next operation. This error can be safely ignored.

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
