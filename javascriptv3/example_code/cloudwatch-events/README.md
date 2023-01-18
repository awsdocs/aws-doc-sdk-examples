# CloudWatch Events code examples for the SDK for JavaScript in Node.js

## Overview

The code examples in this directory demonstrate how to work with Amazon CloudWatch Events
using the AWS SDK for JavaScript (v3).

Amazon CloudWatch Events delivers a near real-time stream of system events that describe 
changes in Amazon Web Services (AWS) resources.

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

- [Add an AWS Lambda function target](actions/put-targets.js)(PutTargets)
- [Create a scheduled rule](actions/put-rule.js)(PutRule)
- [Send events](actions/put-events.js)(PutEvents)

## Run the examples

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

### Prerequisites

- [Set up AWS SDK for JavaScript](../README.rst)
- Run `npm i` to install dependencies.

## Instructions

**Run a single action**

1. Run `node ./actions/<fileName>`.
   OR
1. Import `./actions/fileName` into another module.

## Tests

⚠️ Running the tests might result in charges to your AWS account.

### Integration tests

1. Run `npm i`.
1. Run `npm run integration-test`.

## Additional resources

- [Amazon CloudWatch Events User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/events/WhatIsCloudWatchEvents.html)
- [Amazon CloudWatch Logs API Reference](https://docs.aws.amazon.com/eventbridge/latest/APIReference/Welcome.html)
- [CloudWatch Events Client - AWS SDK for JavaScript (v3)](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-cloudwatch-events/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
