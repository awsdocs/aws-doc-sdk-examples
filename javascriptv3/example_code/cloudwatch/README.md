# Cloudwatch examples for the AWS SDK for JavaScript (v3)

## Overview

These code examples demonstrate how to work with Amazon CloudWatch
using the AWS SDK for JavaScript (v3).

Amazon CloudWatch provides a reliable, scalable, and flexible monitoring solution that you can start using within minutes.
You no longer need to set up, manage, and scale your own monitoring systems and infrastructure.

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single Actions

- [Create an alarm that watches a metric](actions/putMetricAlarm.js)
- [Delete alarms](actions/deleteAlarms.js)
- [Describe alarms for a metric](actions/describeAlarms.js)
- [Disable alarm actions](actions/disableAlarmActions.js)
- [Enable alarm actions](actions/enableAlarmActions.js)
- [List metrics](actions/listMetrics.js)
- [Put data into a metric](actions/putMetricData.js)

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

## Run the examples

### Prerequisites

1. [Set up AWS SDK for JavaScript](../README.md).
1. Run `yarn`.

### Instructions

**Run a single action**
1. Run `node ./actions/<fileName>`.
OR
1. Import `./actions/fileName` into another module.

## Tests

⚠️ Running the tests might result in charges to your AWS account.

1. Run `yarn`.
1. Run `yarn test`.

## Additional resources

- [Amazon CloudWatch User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/WhatIsCloudWatch.html)
- [Amazon CloudWatch API reference](https://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/Welcome.html)
- [Amazon CloudWatch Client - AWS SDK for JavaScript (v3)](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-cloudwatch/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
