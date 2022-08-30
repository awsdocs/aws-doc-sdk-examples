# Amazon Simple Email Service code examples for the SDK for .NET

## Overview
The examples in this section show to use the AWS SDK for .NET with Amazon Simple Email Service (Amazon SES).

Amazon SES is a reliable, scalable, and cost-effective email service. Digital marketers and application developers can use Amazon SES to send marketing, notification, and transactional emails.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.
* [Create an email template](Actions/SESActionExamples.cs) (CreateTemplate)
* [Delete an email template](Actions/SESActionExamples.cs) (DeleteTemplate)
* [Delete an identity](Actions/SESActionExamples.cs) (DeleteIdentity)
* [Get sending limits](Actions/SESActionExamples.cs) (GetSendQuota)
* [Get the status of an identity](Actions/SESActionExamples.cs) (GetIdentityVerificationAttributes)
* [List email templates](Actions/SESActionExamples.cs) (ListTemplates)
* [List identities](Actions/SESActionExamples.cs) (ListIdentities)
* [Send email](Actions/SESActionExamples.cs) (SendEmail)
* [Send templated email](Actions/SESActionExamples.cs) (SendTemplatedEmail)
* [Verify an email identity](Actions/SESActionExamples.cs) (VerifyEmailIdentity)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
[README](../README.md#Prerequisites) in the dotnetv3 folder.

After the example compiles, you can run it from the command line. To
do this, navigate to the folder that contains the .csproj file, and then
issue the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Tests
⚠️ Running the tests might result in charges to your AWS account.

The solution includes a test project. To run the tests, navigate to the folder that contains the test project and then enter the following:

```
dotnet test
```

Alternatively, you can open the example solution and use the Visual Studio
Test Runner to run the tests.

## Additional resources
* [Amazon Simple Email Service Developer Guide](https://docs.aws.amazon.com/ses/latest/dg/index.html)
* [Amazon Simple Email Service API Reference](https://docs.aws.amazon.com/ses/latest/APIReference/Welcome.html)
* [AWS SDK for .NET Amazon SES](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/SimpleEmail/NSimpleEmail.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

