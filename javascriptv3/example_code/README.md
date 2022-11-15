# AWS SDK for JavaScript (v3) code examples

## Overview

The code examples in this topic show you how to use the AWS SDK for JavaScript (v3) with AWS.

The AWS SDK for JavaScript (v3) provides a JavaScript API for AWS infrastructure services. Using the SDK, you can build applications on top of Amazon S3, Amazon EC2, Amazon DynamoDB, and more.

## Types of code examples

- **Single-service actions** - Code examples that show you how to call individual service functions.

- **Single-service scenarios** - Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- **Cross-service examples** - Sample applications that work across multiple AWS services.

### Find code examples

Single-service actions and scenarios are organized by AWS service in this folder. A README in each folder lists and describes how to run the examples.

Cross-service examples are located in the [_cross-services folder_](./cross-services). A README in each folder describes how to run the example.

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code the least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

### Prerequisites

- Install the latest stable version of Node.js.

- Set up a shared configuration file with your credentials. For more information, see the [AWS SDK for JavaScript (v3) Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/loading-node-credentials-shared.html).

## Tests

**Note**: Running the tests might result in charges to your AWS account.

1. Where tests are provided, navigate to the 'tests' folder located in the root of the service folder.

**Note**: In some cases scenarios and cross-service examples have their own test folder, so navigate to that instead.

2. Run the following:

```
cd javascriptv3/example_code/[service folder name]
npm install
npm test
```

## Additional resources

- [AWS SDK for JavaScript (v3)](https://github.com/aws/aws-sdk-js-v3)
- [AWS SDK for JavaScript (v3) Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/)
- [AWS SDK for JavaScript (v3) API Reference](http://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
