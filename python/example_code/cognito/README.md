# Amazon Cognito code examples for the AWS SDK for Python

## Overview

Shows you how to use the AWS SDK for Python (Boto3) with Amazon Cognito to
sign up users, set users up for multi-factor authentication (MFA), and sign in to
get access tokens.

*Amazon Cognito handles user authentication and authorization for your web and mobile apps.*

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

* [Confirm a user](cognito_idp_actions.py)
(`ConfirmSignUp`)
* [Confirm an MFA device for tracking](cognito_idp_actions.py)
(`ConfirmDevice`)
* [Get a token to associate an MFA application with a user](cognito_idp_actions.py)
(`AssociateSoftwareToken`)
* [Get information about a user](cognito_idp_actions.py)
(`AdminGetUser`)
* [List users](cognito_idp_actions.py)
(`ListUsers`)
* [Resend a confirmation code](cognito_idp_actions.py)
(`ResendConfirmationCode`)
* [Respond to SRP authentication challenges](cognito_idp_actions.py)
(`RespondToAuthChallenge`)
* [Respond to an authentication challenge](cognito_idp_actions.py)
(`AdminRespondToAuthChallenge`)
* [Sign up a user](cognito_idp_actions.py)
(`SignUp`)
* [Start authentication with a tracked device](cognito_idp_actions.py)
(`InitiateAuth`)
* [Start authentication with administrator credentials](cognito_idp_actions.py)
(`AdminInitiateAuth`)
* [Verify an MFA application with a user](cognito_idp_actions.py)
(`VerifySoftwareToken`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple 
functions within the same service.

* [Sign up a user with a user pool that requires MFA](scenario_signup_user_with_mfa.py)

## Running the examples

### Prerequisites

To find prerequisites for running these examples, see the 
[README](../../README.md#Prerequisites) in the Python folder.

### Sign up a user with a user pool that requires MFA

This interactive scenario runs at a command prompt and shows you how to use 
Amazon Cognito to do the following:

1. Sign up a user with a user name, password, and email address.
2. Confirm the user from a code sent in email.
3. Set up multi-factor authentication by associating an MFA application with the user.
4. Sign in by using a password and an MFA code.
5. Register an MFA device to be tracked by Amazon Cognito.
6. Sign in by using a password and information from the tracked device. This avoids the
   need to enter a new MFA code.

Signing in with a tracked device requires the client to respond to authentication
challenges that use the Secure Remote Password (SRP) protocol. This example uses the
`warrant` package to help with SRP calculations. You can learn more about SRP on
[Wikipedia](https://en.wikipedia.org/wiki/Secure_Remote_Password_protocol).

#### Create a user pool and add a client ID

This scenario requires an existing Amazon Cognito user pool that is configured to
allow self sign-up, and a client ID that can be used to authenticate with Amazon
Cognito.

You can create these resources by running the AWS CloudFormation script in the
[resources/cdk/cognito_scenario_user_pool_with_mfa](../../../resources/cdk/cognito_scenario_user_pool_with_mfa)
folder. This script outputs a user pool ID and a client ID that you can use to run
the scenario.

#### Install scenario prerequisites

This scenario uses the `warrant` package to help with SRP
calculations and the `qrcode` package to render a QR code that can be scanned by
an MFA application.

Install these packages by running the following at a command prompt:

```
python -m pip install -r requirements.txt
```

#### Run the scenario

Run the scenario by supplying a user pool ID and a client ID at a command prompt:

```
python scenario_signup_user_with_mfa.py [user_pool_id] [client_id]
```

## Tests

To find instructions for running these tests, see the [README](../../README.md#Tests) 
in the Python folder.

## Additional resources
* [Amazon Cognito Developer Guide](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-identity-pools.html)
* [Amazon Cognito API Reference](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/Welcome.html)
* [AWS SDK for Python Amazon Cognito Identity Provider Client](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/cognito-idp.html) 

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 

SPDX-License-Identifier: Apache-2.0
