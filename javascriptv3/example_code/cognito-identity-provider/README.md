# Amazon Cognito Identity Provider code examples for the SDK for JavaScript (v3)

## Overview

Shows how to use the AWS SDK for JavaScript (v3) to work with Amazon Cognito Identity Provider.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Cognito Identity Provider handles user authentication and authorization for your web and mobile apps._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javascriptv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon Cognito](hello.js#L6) (`ListUserPools`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Confirm a user](actions/confirm-sign-up.js#L9) (`ConfirmSignUp`)
- [Confirm an MFA device for tracking](actions/confirm-device.js#L9) (`ConfirmDevice`)
- [Get a token to associate an MFA application with a user](actions/associate-software-token.js#L9) (`AssociateSoftwareToken`)
- [Get information about a user](actions/admin-get-user.js#L9) (`AdminGetUser`)
- [List users](actions/list-users.js#L9) (`ListUsers`)
- [Resend a confirmation code](actions/resend-confirmation-code.js#L9) (`ResendConfirmationCode`)
- [Respond to SRP authentication challenges](actions/respond-to-auth-challenge.js#L10) (`RespondToAuthChallenge`)
- [Respond to an authentication challenge](actions/admin-respond-to-auth-challenge.js#L10) (`AdminRespondToAuthChallenge`)
- [Sign up a user](actions/sign-up.js#L9) (`SignUp`)
- [Start authentication](actions/initiate-auth.js#L10) (`InitiateAuth`)
- [Start authentication with administrator credentials](actions/admin-initiate-auth.js#L10) (`AdminInitiateAuth`)
- [Verify an MFA application with a user](actions/verify-software-token.js#L9) (`VerifySoftwareToken`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Sign up a user with a user pool that requires MFA](actions/verify-software-token.js)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

**Run a single action**

```bash
node ./actions/<fileName>
```

**Run a scenario**
Most scenarios can be run with the following command:
```bash
node ./scenarios/<fileName>
```

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon Cognito

This example shows you how to get started using Amazon Cognito.

```bash
node ./hello.js
```


#### Sign up a user with a user pool that requires MFA

This example shows you how to do the following:

- Sign up and confirm a user with a username, password, and email address.
- Set up multi-factor authentication by associating an MFA application with the user.
- Sign in by using a password and an MFA code.

<!--custom.scenario_prereqs.cognito-identity-provider_Scenario_SignUpUserWithMfa.start-->
<!--custom.scenario_prereqs.cognito-identity-provider_Scenario_SignUpUserWithMfa.end-->


<!--custom.scenarios.cognito-identity-provider_Scenario_SignUpUserWithMfa.start-->
<!--custom.scenarios.cognito-identity-provider_Scenario_SignUpUserWithMfa.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javascriptv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Cognito Identity Provider Developer Guide](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-identity-pools.html)
- [Amazon Cognito Identity Provider API Reference](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/Welcome.html)
- [SDK for JavaScript (v3) Amazon Cognito Identity Provider reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/client/cognito-identity-provider)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0