# CloudWatch Logs code examples for the SDK for JavaScript in Node.js

## Overview

The code examples in this directory demonstrate how to work with Amazon CloudWatch Logs
using the AWS SDK for JavaScript (v3).

You can use Amazon CloudWatch Logs to monitor, store, and access your log files from Amazon Elastic Compute Cloud (Amazon EC2) instances, AWS CloudTrail, Route 53, and other sources.

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a log group](actions/create-log-group.js)
- [Create a subscription filter](actions/put-subscription-filter.js)
- [Delete a log group](actions/delete-log-group.js)
- [Delete a subscription filter](actions/delete-subscription-filter.js)
- [Describe log groups](actions/describe-log-groups.js)
- [Describe existing subscription filters](actions/describe-subscription-filters.js)

## Run the examples

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

### Prerequisites

- [Set up AWS SDK for JavaScript](../README.rst)
- Run `yarn` to install dependencies.

## Instructions

**Run a single action**

1. Run `node ./actions/<fileName>`.
   OR
1. Import `./actions/fileName` into another module.

## Tests

⚠️ Running the tests might result in charges to your AWS account.

### Unit tests

1. Run `yarn`.
1. Run `yarn test`.

### Integration tests

1. Run `yarn`.
1. Run `yarn integration-test`.

## Additional resources

- [Amazon CloudWatch Logs User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/WhatIsCloudWatchLogs.html)
- [Amazon CloudWatch Logs API Reference](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/Welcome.html)
- [CloudWatch Logs Client - AWS SDK for JavaScript (v3)](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-cloudwatch-logs/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
