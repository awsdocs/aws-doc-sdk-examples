# Amazon SES v2 Coupon Newsletter Scenario

This scenario demonstrates how to use the Amazon Simple Email Service (SES) v2 to send a coupon newsletter to a list of contacts. It covers the following key steps:

1. **Prepare the Application**

   - Create a verified email identity for the "send/reply" email addresses.
   - Create a contact list to store the newsletter subscribers.

2. **Gather Subscriber Email Addresses**

   - Allow subscribers to sign up for the newsletter by providing their email addresses.
   - Send a welcome email to each new subscriber.

3. **Send the Coupon Newsletter**

   - Create a template for the coupon newsletter.
   - Retrieve the list of contacts (subscribers).
   - Send individual emails with the coupon newsletter template to each subscriber.
   - Include Unsubscribe links and headers to follow bulk email best practices.

4. **Monitor and Review**

   - Review dashboards and metrics in the AWS console for the newsletter campaign.
   - Because this scenario minimizes the number of email addresses necessary by utilizing subaddresses to show the features of SES Contact Lists, it is likely an email provider will mark the messages as Spam. Check your spam folder to ensure they sent.

5. **Clean up**

   - Delete the template.
   - Delete the contact list.
   - Optionally delete the sender verified email identity.

## Prerequisites

Before running this scenario, ensure you have:

- An AWS account with proper permissions to use Amazon SES v2.
- (Optional) A verified email identity (domain or email address) in Amazon SES for the Sending address.
  - This will be created during the scenario if it does not already exist.
- (Optional, if the account is in the SES Sandbox) A verified destination address.
  - This will NOT be created during the scenario.

## AWS Services Used

This scenario uses the following AWS services:

- Amazon Simple Email Service (SES) v2

## SES v2 Actions Used

The scenario covers the following SES v2 API actions:

- [`CreateContact`](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_CreateContact.html)
- [`CreateContactList`](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_CreateContactList.html)
- [`CreateEmailIdentity`](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_CreateEmailIdentity.html)
- [`CreateEmailTemplate`](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_CreateEmailTemplate.html)
- [`DeleteContactList`](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_DeleteContactList.html)
- [`DeleteEmailTemplate`](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_DeleteEmailTemplate.html)
- [`DeleteEmailIdentity`](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_DeleteEmailIdentity.html)
- [`ListContacts`](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_ListContacts.html)
- [`SendEmail`](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_SendEmail.html) (with both Simple and Template formats)

## Implementations

This example is implemented in the following languages:

- [Java](../../../javav2/example_code/ses/README.md)
- [Python](../../../python/example_code/sesv2/README.md)
- [Rust](../../../rustv1/examples/ses/README.md)
- [.NET](../../../dotnetv3/SESv2/README.md)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
