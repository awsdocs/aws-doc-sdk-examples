# AWS Lambda code examples for the AWS SDK for JavaScript in Node.js

## Overview

Shows how to use the SDK for JavaScript in Node.js with AWS Lambda to manage and invoke
functions.

AWS Lambda is a serverless, event-driven compute service that lets you run code for virtually any type of application or backend service without provisioning or managing servers.

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a function](./actions/create-function.js)(`CreateFunction`)
- [Delete a function](./actions/delete-function.js)(`DeleteFunction`)
- [Get a function](./actions/get-function.js)(`GetFunction`)
- [Invoke a function](./actions/invoke.js)(`Invoke`)
- [Update function code](./actions/update-function-code.js)(`UpdateFunctionCode`)
- [Update function configuration](./actions/update-function-configuration.js)(`CreateFunction`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- [Get started with functions](./scenarios/basic)

## Run the examples

### Single action

1. Run `yarn`
1. Create a new file in this directory and `import { functionName } from "./actions/action-name.js"`
   where `action-name` is the filename of the action you want to run, and `function-name` is the name of
   the exported function in that file.
1. Call the imported function with its required parameters and log the result.

### Prerequisites

- [Set up AWS SDK for JavaScript](../README.rst)

## Tests

⚠️ Running the tests might result in charges to your AWS account.

1. Run `yarn`
1. Run `yarn test`

## Additional resources

- [AWS Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
- [AWS Lambda API Reference](https://docs.aws.amazon.com/lambda/latest/dg/API_Reference.html)
- [AWS Lambda client - AWS SDK for JavaScript (v3)](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-lambda/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
