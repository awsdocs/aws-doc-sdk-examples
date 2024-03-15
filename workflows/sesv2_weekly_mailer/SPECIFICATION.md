# SES v2 Coupon Newsletter Workflow Specification

Use the Amazon Simple Email Service (SES) v2 API to manage a subscription list for a weekly newsletter.

## Prepare the Application

1. Create an email identity.

   - Retrieve the verified email address from the `VERIFIED_EMAIL_ADDRESS` environment variable.
   - Operation: **CreateEmailIdentity**
     - `EmailIdentity`: Value of the `VERIFIED_EMAIL_ADDRESS` environment variable.

2. Create a contact list with the name `weekly-coupons-newsletter`.
   - Operation: **CreateContactList**
     - `ContactListName`: `weekly-coupons-newsletter`

## Gather Subscriber Email Addresses

1. Prompt the user to enter 3 to 5 email addresses for subscribing to the newsletter.
   - For testing purposes, suggest using a single email address with plus addressing (e.g., `user+1@example.com`, `user+2@example.com`, etc.).
2. For each email address provided:
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
   - The email should include an [Unsubscribe](#) link, using the url `{{amazonSESUnsubscribeUrl}}`.
   - Operation: **SendEmail**
     - `Destination`:
       - `ToAddresses`: One email address from the `ListContacts` response (each email address must get a unique `SendEmail` call for tracking and unsubscribe purposes).
     - `Content`:
       - `Template`:
         - `TemplateName`: `weekly-coupons`
         - `TemplateData`: JSON string representing an object with one key, `coupons`, which is an array of coupon items. Each coupon entry in the array should have one key, `details`, with the details of the coupon. See `sample_coupons.json`.
     - `FromEmailAddress`: (Use the verified email address from step 1)
     - `ListManagementOptions`:
       - `ContactListName`: `weekly-coupons-newsletter`

For more information on using templates with SES v2, refer to the [Amazon SES Developer Guide](https://docs.aws.amazon.com/ses/latest/dg/send-personalized-email-api.html).

## Monitor and Review

1. [Monitor your sending activity](https://docs.aws.amazon.com/ses/latest/dg/monitor-sending-activity.html) using the [SES Homepage](https://us-east-1.console.aws.amazon.com/ses/home#/account) in the AWS console.

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
