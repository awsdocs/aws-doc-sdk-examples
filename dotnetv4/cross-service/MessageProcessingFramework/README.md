# Get started with the AWS Message Processing Framework for .NET

## Overview

This example shows you how to get started with the AWS Message Processing Framework for .NET. The tutorial creates a web application that allows the user to publish an Amazon SQS message and a command-line application that receives the message. For the complete tutorial along with prerequisites, setup, and run instructions, see the [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/msg-proc-fw-get-started.html).

## Prerequisites

- Follow the main [README](../../README.md#Prerequisites) in the `dotnetv3` folder
- To set up your development environment see [Get started with the AWS SDK for .NET](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/net-dg-setup.html).

### Important

- Running this code might result in charges to your AWS account.
  Be sure to delete all the resources you create while going through this tutorial so that you won't be charged.
- Running the tests might result in charges to your AWS account.
- This code has not been tested in all AWS Regions. Some AWS services are available only in specific Regions. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).

### .NET Implementation Details
 - If you're using AWS IAM Identity Center for authentication, be sure to also add the `AWSSDK.SSO` and `AWSSDK.SSOOIDC` NuGet packages to the projects.