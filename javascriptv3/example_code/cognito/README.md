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

### Single actions

Code excerpts that show you how to call individual service functions.

- [Sign up a user](./actions/sign-up.js) (SignUp)
- [Get information about a user](./actions/admin-get-user.js) (AdminGetUser)
- [Resend a confirmation code](./actions/resend-confirmation-code.js) (ResendConfirmationCode)
- [Confirm a user](./actions/confirm-sign-up.js) (ConfirmSignUp)
- [List users](./actions/list-users.js) (ListUsers)
- [Start authentication with administrator credentials](./actions/admin-initiate-auth.js) (AdminInitiateAuth)
- [Get a token to associate an MFA application with a user](./actions/associate-software-token.js) (AssociateSoftwareToken)
- [Verify an MFA application with a user](./actions/verify-software-token.js) (VerifySoftwareToken)
- [Respond to an authentication challenge](./actions/admin-respond-to-auth-challenge.js) (AdminRespondToAuthChallenge)
- [Confirm an MFA device for tracking](./actions/confirm-device.js) (ConfirmDevice)
- [Start authentication with a tracked device](./actions/initiate-auth.js) (InitiateAuth)
- [Respond to SRP authentication challenges](./actions/respond-to-auth-challenge.js) (RespondToAuthChallenge)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- [Use a custom authentication flow](./scenarios/lambda-triggers)
- [Sign up a user with a user pool that requires MFA](./scenarios/basic)

## Run the examples

### Prerequisites

- [Set up AWS SDK for JavaScript](../README.rst)
- Run `yarn` to install dependencies.

### Instructions

#### Run a single Action

1. Create a new `.js` file.
2. Import an action into your file. For example: `import { createUserPool } from "./actions/create-user-pool"`
3. Call the imported action in your file.

#### Run a scenario
Choose one of the scenarios above and follow the instructions in
its readme.
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
