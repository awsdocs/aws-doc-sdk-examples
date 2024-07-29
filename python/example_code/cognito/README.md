# Amazon Cognito Identity Provider code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Cognito Identity Provider.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon Cognito](hello/hello_cognito.py#L4) (`ListUserPools`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AdminGetUser](cognito_idp_actions.py#L23)
- [AdminInitiateAuth](cognito_idp_actions.py#L187)
- [AdminRespondToAuthChallenge](cognito_idp_actions.py#L292)
- [AssociateSoftwareToken](cognito_idp_actions.py#L241)
- [ConfirmDevice](cognito_idp_actions.py#L342)
- [ConfirmSignUp](cognito_idp_actions.py#L131)
- [InitiateAuth](cognito_idp_actions.py#L407)
- [ListUsers](cognito_idp_actions.py#L164)
- [ResendConfirmationCode](cognito_idp_actions.py#L104)
- [RespondToAuthChallenge](cognito_idp_actions.py#L408)
- [SignUp](cognito_idp_actions.py#L56)
- [VerifySoftwareToken](cognito_idp_actions.py#L265)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Sign up a user with a user pool that requires MFA](cognito_idp_actions.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon Cognito

This example shows you how to get started using Amazon Cognito.

```
python hello/hello_cognito.py
```


#### Sign up a user with a user pool that requires MFA

This example shows you how to do the following:

- Sign up and confirm a user with a username, password, and email address.
- Set up multi-factor authentication by associating an MFA application with the user.
- Sign in by using a password and an MFA code.

<!--custom.scenario_prereqs.cognito-identity-provider_Scenario_SignUpUserWithMfa.start-->
This scenario requires the following resources:

* An existing Amazon Cognito user pool that is configured to allow self sign-up.
* A client ID to use for authenticating with Amazon Cognito.

To create these resources, run the AWS CloudFormation script in the
[resources/cdk/cognito_scenario_user_pool_with_mfa](../../../resources/cdk/cognito_scenario_user_pool_with_mfa)
folder. This script outputs a user pool ID and a client ID that you can use to run
the scenario.
<!--custom.scenario_prereqs.cognito-identity-provider_Scenario_SignUpUserWithMfa.end-->

Start the example by running the following at a command prompt:

```
python cognito_idp_actions.py
```


<!--custom.scenarios.cognito-identity-provider_Scenario_SignUpUserWithMfa.start-->
<!--custom.scenarios.cognito-identity-provider_Scenario_SignUpUserWithMfa.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Cognito Identity Provider Developer Guide](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-identity-pools.html)
- [Amazon Cognito Identity Provider API Reference](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/Welcome.html)
- [SDK for Python Amazon Cognito Identity Provider reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/cognito-idp.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0