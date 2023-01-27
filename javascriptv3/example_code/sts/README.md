# AWS STS code examples for the SDK for JavaScript (v3)

## Overview

Shows how to use the AWS SDK for JavaScript (v3) with AWS Security Token Service (AWS STS).

AWS provides AWS STS as a web service that enables you to request temporary, limited-privilege credentials for AWS Identity and Access Management (IAM) users or for users you authenticate (federated users).

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

- [Assume a role](./actions//assume-role.js)(AssumeRole)

## Run the examples

### Prerequisites

1. [Set up AWS SDK for JavaScript](../README.md).
1. Run `npm i`.

### Instructions

**Run a single action**

1. Run `node ./actions/<fileName>`.
   OR
1. Import `./actions/fileName` into another module.

## Tests

⚠️ Running the tests might result in charges to your AWS account.

1. Run `npm i`.
1. Run `npm test`.

## Additional resources

- [Amazon CloudWatch User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/WhatIsCloudWatch.html)
- [Amazon CloudWatch API reference](https://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/Welcome.html)
- [Amazon CloudWatch Client - AWS SDK for JavaScript (v3)](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-cloudwatch/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
