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

- [Hello Amazon Cognito Identity Provider](hello.js#L6) (`ListUserPools`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AdminGetUser](actions/admin-get-user.js#L9)
- [AdminInitiateAuth](actions/admin-initiate-auth.js#L10)
- [AdminRespondToAuthChallenge](actions/admin-respond-to-auth-challenge.js#L10)
- [AssociateSoftwareToken](actions/associate-software-token.js#L9)
- [ConfirmDevice](actions/confirm-device.js#L9)
- [ConfirmSignUp](actions/confirm-sign-up.js#L9)
- [DeleteUser](../cross-services/wkflw-pools-triggers/actions/cognito-actions.js#L122)
- [InitiateAuth](actions/initiate-auth.js#L10)
- [ListUsers](actions/list-users.js#L9)
- [ResendConfirmationCode](actions/resend-confirmation-code.js#L9)
- [RespondToAuthChallenge](actions/respond-to-auth-challenge.js#L10)
- [SignUp](actions/sign-up.js#L9)
- [UpdateUserPool](../cross-services/wkflw-pools-triggers/actions/cognito-actions.js#L13)
- [VerifySoftwareToken](actions/verify-software-token.js#L9)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Automatically confirm known users with a Lambda function](../cross-services/wkflw-pools-triggers/index.js)
- [Sign up a user with a user pool that requires MFA](actions/admin-initiate-auth.js)


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

**Run with options**

Some actions and scenarios can be run with options from the command line:
```bash
node ./scenarios/<fileName> --option1 --option2
```
[util.parseArgs](https://nodejs.org/api/util.html#utilparseargsconfig) is used to configure
these options. For the specific options available to each script, see the `parseArgs` usage
for that file.

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon Cognito Identity Provider

This example shows you how to get started using Amazon Cognito Identity Provider.

```bash
node ./hello.js
```


#### Automatically confirm known users with a Lambda function

This example shows you how to automatically confirm known Amazon Cognito users with a Lambda function.

- Configure a user pool to call a Lambda function for the <code>PreSignUp</code> trigger.
- Sign up a user with Amazon Cognito.
- The Lambda function scans a DynamoDB table and automatically confirms known users.
- Sign in as the new user, then clean up resources.

<!--custom.scenario_prereqs.cross_CognitoAutoConfirmUser.start-->
<!--custom.scenario_prereqs.cross_CognitoAutoConfirmUser.end-->


<!--custom.scenarios.cross_CognitoAutoConfirmUser.start-->
<!--custom.scenarios.cross_CognitoAutoConfirmUser.end-->

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
