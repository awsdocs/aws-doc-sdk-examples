# Support code examples for the SDK for JavaScript in Node.js

## Overview

The code examples in this directory demonstrate how to work with AWS Support
using the AWS SDK for JavaScript (v3).

AWS Support offers a range of plans that provide access to tools and expertise that support the success and operational health of your AWS solutions.

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Get started

- [Hello AWS Support](./hello.js)

### Single actions

Code excerpts that show you how to call individual service functions.

- [Add a communication to a case](actions/add-communication-to-case.js)
- [Add an attachment to a set](actions/add-attachments-to-set.js)
- [Create a case](actions/create-case.js)
- [Describe an attachment](actions/describe-attachment.js)
- [Describe cases](actions/describe-cases.js)
- [Describe communications](actions/describe-communications.js)
- [Describe services](actions/describe-services.js)
- [Describe severity levels](actions/describe-severity-levels.js)
- [Resolve case](actions/resolve-case.js)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- [Get started with AWS Support cases](./scenarios/basic.js)

## Run the examples

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

### Prerequisites

- [Set up AWS SDK for JavaScript](../README.rst)
- Run `npm i` to install dependencies.

## Instructions

**Run a single action**

1. Run `node ./actions/<fileName>`.

## Tests

⚠️ Running the tests might result in charges to your AWS account.

### Unit tests

1. Run `npm i`.
1. Run `npm test`.

### Integration tests

1. Run `npm i`.
1. Run `npm run integration-test`.

## Additional resources

- [AWS Support User Guide](https://docs.aws.amazon.com/awssupport/latest/user/getting-started.html)
- [AWS Support API Reference](https://docs.aws.amazon.com/awssupport/latest/APIReference/Welcome.html)
- [Support Client - AWS SDK for JavaScript (v3)](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-support/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
