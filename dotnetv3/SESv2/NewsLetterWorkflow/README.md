# Amazon SES v2 Coupon Newsletter Workflow

## Overview

This workflow demonstrates how to use the Amazon Simple Email Service (SES) v2 to send a coupon newsletter to a list of contacts. It covers the following key steps:

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
   - Because this workflow minimizes the number of email addresses necessary by utilizing subaddresses to show the features of SES Contact Lists, it is likely an email provider will mark the messages as Spam. Check your spam folder to ensure they sent.

5. **Clean up**

   - Delete the template.
   - Delete the contact list.
   - Optionally delete the sender verified email identity.

## ⚠ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Scenario

### Prerequisites

For general prerequisites, see the [README](../../../README.md) in the `dotnetv3` folder.

### Resources

Before running this scenario, ensure you have:

- An AWS account with proper permissions to use Amazon SES v2.
- (Optional) A verified email identity (domain or email address) in Amazon SES for the Sending address.
   - This will be created during the workflow if it does not already exist.
- (Optional, if the account is in the SES Sandbox) A verified destination address.
   - This will NOT be created during the workflow.

### Instructions

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .sln file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Additional resources

- [Amazon SES v2 API Developer Guide](https://docs.aws.amazon.com/ses/latest/dg/Welcome.html)
- [Amazon SES v2 API API Reference](https://docs.aws.amazon.com/ses/latest/APIReference-V2/Welcome.html)
- [SDK for .NET Amazon SES v2 API reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/Sesv2/NSesv2.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
