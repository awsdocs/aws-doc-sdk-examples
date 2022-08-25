# Amazon SES code examples for the SDK for JavaScript in Node.js

## Overview

_Amazon Simple Email Service (Amazon SES) is a reliable, scalable, and cost-effective email service. Digital marketers and application developers can use Amazon SES to send marketing, notification, and transactional emails._

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a receipt filter](./src/ses_createreceiptfilter.js) (`CreateReceiptFilter`)
- [Create a receipt rule](./src/ses_createreceiptrule.js) (`CreateReceiptRule`)
- [Create a receipt rule set](./src/ses_createreceiptruleset.js) (`CreateReceiptRuleSet`)
- [Create a template](./src/ses_createtemplate.js) (`CreateTemplate`)
- [Delete an identity](./src/ses_deleteidentity.js) (`DeleteIdentity`)
- [Delete a receipt filter](./src/ses_deletereceiptfilter.js) (`DeleteReceiptFilter`)
- [Delete a receipt rule](./src/ses_deletereceiptrule.js) (`DeleteReceiptRule`)
- [Delete a receipt rule set](./src/ses_deletereceiptruleset.js) (`DeleteReceiptRuleSet`)
- [Delete a template](./src/ses_deletetemplate.js) (`DeleteTemplate`)
- [Get a template](./src/ses_gettemplate.js) (`GetTemplate`)
- [List all identities](./src/ses_listidentities.js) (`ListIdentities`)
- [List all receipt filters](./src/ses_listreceiptfilters.js) (`ListReceiptFilters`)
- [List all templates](./src/ses_listtemplates.js) (`ListTemplates`)
- [Send a bulk templated email](./src/ses_sendbulktemplatedemail.js) (`SendBulkTemplatedEmail`)
- [Send an email](./src/ses_sendemail.js) (`SendEmail`)
- [Send a templated email](./src/ses_sendtemplatedemail.js) (`SendTemplatedEmail`)
- [Update a template](./src/ses_updatetemplate.js) (`UpdateTemplate`)
- [Verify a domain identity](./src/ses_verifydomainidentity.js) (`VerifyDomainIdentity`)
- [Verify an email identity](./src/ses_verifyemailidentity.js) (`VerifyEmailIdentity`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- Coming soon.

### Cross-service examples

Sample applications that work across multiple AWS services.

- Coming soon.

## Run the examples

1. `npm i`
1. `node <path/to/example.js>`

### Prerequisites

- [Set up AWS SDK for JavaScript](../README.rst)

## Tests

⚠️ Running the tests might result in charges to your AWS account.

1. `cd ../tests/ses`
1. `npm test`

## Additional resources

- [SES Developer Guide](https://docs.aws.amazon.com/ses/latest/dg/Welcome.html)
- [SES API Reference](https://docs.aws.amazon.com/ses/latest/APIReference/Welcome.html)
- [SES Client - AWS SDK for JavaScript v3](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-ses/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
