# Amazon Cognito Identity Provider code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon Cognito Identity Provider.

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv4` folder.


<!--custom.prerequisites.start-->
These examples also require the following resources:

* An existing Amazon Cognito user pool that is configured to allow self sign-up.
* A client ID to use for authenticating with Amazon Cognito.


To create these resources, run the AWS CloudFormation script in the
[resources/cdk/cognito_scenario_user_pool_with_mfa](../../resources/cdk/cognito_scenario_user_pool_with_mfa)
folder. This script outputs a user pool ID and a client ID that you can use to run
the scenario.
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [AdminGetUser](Actions/CognitoWrapper.cs#L288)
- [AdminInitiateAuth](Actions/CognitoWrapper.cs#L156)
- [AdminRespondToAuthChallenge](Actions/CognitoWrapper.cs#L72)
- [AssociateSoftwareToken](Actions/CognitoWrapper.cs#L133)
- [ConfirmDevice](Actions/CognitoWrapper.cs#L241)
- [ConfirmSignUp](Actions/CognitoWrapper.cs#L213)
- [InitiateAuth](Actions/CognitoWrapper.cs#L184)
- [ListUserPools](Actions/CognitoWrapper.cs#L25)
- [ListUsers](Actions/CognitoWrapper.cs#L46)
- [ResendConfirmationCode](Actions/CognitoWrapper.cs#L264)
- [SignUp](Actions/CognitoWrapper.cs#L311)
- [VerifySoftwareToken](Actions/CognitoWrapper.cs#L111)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Sign up a user with a user pool that requires MFA](Actions/CognitoWrapper.cs)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

For general instructions to run the examples, see the
[README](../README.md#building-and-running-the-code-examples) in the `dotnetv4` folder.

Some projects might include a settings.json file. Before compiling the project,
you can change these values to match your own account and resources. Alternatively,
add a settings.local.json file with your local settings, which will be loaded automatically
when the application runs.

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .csproj file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.


<!--custom.instructions.start-->
<!--custom.instructions.end-->



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


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv4` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Cognito Identity Provider Developer Guide](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-identity-pools.html)
- [Amazon Cognito Identity Provider API Reference](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/Welcome.html)
- [SDK for .NET Amazon Cognito Identity Provider reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/CognitoIdentity/NCognitoIdentity.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0