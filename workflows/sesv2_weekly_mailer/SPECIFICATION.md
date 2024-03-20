# SES v2 Coupon Newsletter Workflow Specification

Use the Amazon Simple Email Service (SES) v2 API to manage a subscription list for a weekly newsletter.

## Prepare the Application

1. Create an email identity.
   - Request a `verified email` address from the user. This will be used as the `from` address, and the user will need to click a verification link in their email before continuing to part 3.
   - Operation: **CreateEmailIdentity**
     - `EmailIdentity`: Value of the `verified email` given by the user.
2. Create a contact list with the name `weekly-coupons-newsletter`.
   - Operation: **CreateContactList**
     - `ContactListName`: `weekly-coupons-newsletter`

## Gather Subscriber Email Addresses

1. Prompt the user to enter a base email address for subscribing to the newsletter.
   - For testing purposes, this workflow uses a single email address with [subaddress extensions](https://www.rfc-editor.org/rfc/rfc5233.html) (e.g., `user+ses-weekly-newsletter-1@example.com`, `user+ses-weekly-newsletter-2@example.com`, etc., also known as [plus addressing](https://en.wikipedia.org/wiki/Email_address#:~:text=For%20example%2C%20the%20address%20joeuser,sorting%2C%20and%20for%20spam%20control.)).
   - Create 3 variants of this email address as `{user email}+ses-weekly-newsletter-{i}@{user domain}`.
   - `{user-email}` is the portion up to the first `@` (0x40, dec 64). The `{user domain}` is everything after the first `@`.
2. For each email address created:
   1. Create a new contact with the provided email address in the `weekly-coupons-newsletter` contact list.
      - Operation: **CreateContact**
        - `ContactListName`: `weekly-coupons-newsletter`
        - `EmailAddress`: The email address provided by the user.
   2. Send a welcome email to the new contact using the content from the `welcome.html` file.
      - Operation: **SendEmail**
        - `FromEmailAddress`: Retrieve the value from the `VERIFIED_EMAIL_ADDRESS` environment variable.
        - `Destination.ToAddresses`: The email address provided by the user.
        - `Content.Simple.Subject.Data`: "Welcome to the Weekly Coupons Newsletter"
        - `Content.Simple.Body.Text.Data`: Read the content from the `welcome.txt` file.
        - `Content.Simple.Body.Html.Data`: Read the content from the `welcome.html` file.

## Send the Coupon Newsletter

1. Create an email template named `weekly-coupons` with the following content:
   - Subject: `Weekly Coupons Newsletter`
   - HTML Content: Available in the `coupon-newsletter.html` file.
   - Text Content: Available in the `coupon-newsletter.txt` file.
   - The emails should include an [Unsubscribe](#) link, using the url `{{amazonSESUnsubscribeUrl}}`.
   - Operation: **CreateEmailTemplate**
     - `TemplateName`: `weekly-coupons`
     - `TemplateContent`:
       - `Subject`: `Weekly Coupons Newsletter`
       - `HtmlPart`: Read from the `coupon-newsletter.html` file
       - `TextPart`: Read from the `coupon-newsletter.txt` file
2. Retrieve the list of contacts from the `weekly-coupons-newsletter` contact list.
   - Operation: **ListContacts**
     - `ContactListName`: `weekly-coupons-newsletter`
3. Send an email using the `weekly-coupons` template to each contact in the list.
   - The email should include the following coupon items:
     1. 20% off on all electronics
     2. Buy one, get one free on books
     3. 15% off on home appliances
     4. Free shipping on orders over $50
     5. 25% off on outdoor gear
     6. 10% off on groceries
   - Operation: **SendEmail**
     - `Destination`:
       - `ToAddresses`: One email address from the `ListContacts` response (each email address must get a unique `SendEmail` call for tracking and unsubscribe purposes).
     - `Content`:
       - `Template`:
         - `TemplateName`: `weekly-coupons`
         - `TemplateData`: JSON string representing an object with one key, `coupons`, which is an array of coupon items. Each coupon entry in the array should have one key, `details`, with the details of the coupon. See `sample_coupons.json`.
     - `FromEmailAddress`: (Use the verified email address from step 1)
     - `ListManagementOptions`:
       - `ContactListName`: `weekly-coupons-newsletter` to correctly populate Unsubscribe headers and the `{{amazonSESUnsubscribeUrl}}` value.

For more information on using templates with SES v2, refer to the [Amazon SES Developer Guide](https://docs.aws.amazon.com/ses/latest/dg/send-personalized-email-api.html).

## Monitor and Review

1. [Monitor your sending activity](https://docs.aws.amazon.com/ses/latest/dg/monitor-sending-activity.html) using the [SES Homepage](https://console.aws.amazon.com/ses/home#/account) in the AWS console.

## Clean up

1. Delete the contact list. This operation also deletes all contacts in the list, without needing separate calls.
   - Operation: **DeleteContactList**
     - `ContactListName`: `weekly-coupons-newsletter`
2. Delete the template.
   - Operation: **DeleteEmailTemplate**
     - `TemplateName`: `weekly-coupons`
3. Delete the email identity (optional). Ask the user before performing this step, as they may not want to re-verify the email identity.
   - Operation: **DeleteEmailIdentity**
     - `EmailIdentity`: Value of the `verified email` given by the user in part 1.

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
