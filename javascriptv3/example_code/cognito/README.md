# Cognito code examples for the SDK for JavaScript in Node.js

## Overview

Shows how to use the AWS SDK for JavaScript in Node.js with Amazon Cognito to
sign up users, set users up for multi-factor authentication (MFA), and sign in to
get access tokens.

Amazon Cognito user pools let you add registration and sign-in to your apps.

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- [Use a custom authentication flow](./scenarios/lambda-triggers)

## Run the examples

### Prerequisites

- [Set up AWS SDK for JavaScript](../README.rst)

## Tests

⚠️ Running the tests might result in charges to your AWS account.

1. Run `yarn`
1. Run `yarn test`

## Additional resources

- [Amazon Cognito Developer Guide](https://docs.aws.amazon.com/cognito/index.html)
- [Amazon Cognito User Pools API Reference](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/Welcome.html)
- [Amazon Cognito Federated Identities API Reference](https://docs.aws.amazon.com/cognitoidentity/latest/APIReference/Welcome.html)
- [Amazon Cognito Identity client - AWS SDK for JavaScript (v3)](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-cognito-identity/index.html)
- [Amazon Cognito Identity Provider client - AWS SDK for JavaScript (v3)](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-cognito-identity-provider/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
