# Amazon Cognito code examples for the SDK for .NET

## Overview
The examples in this section show how to use the AWS SDK for .NET with Amazon
Cognito to sign up users, set users up for multi-factor authentication (MFA),
and sign in to get access tokens.

Amazon Cognito provides authentication, authorization, and user management for
your web and mobile apps. Your users can sign in directly with a user name and
password, or through a third party such as Facebook, Amazon, Google, or Apple.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only
the minimum permissions required to perform the task. For more information, see
[Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see
[AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.
* [Get information about a user](scenarios/Cognito_Basics/Cognito_MVP/CognitoMethods.cs) (`AdminGetUserAsync`)
* [Get a token to associate an MFA application with a user](scenarios/Cognito_Basics/Cognito_MVP/CognitoMethods.cs) (`AssociateSoftwareTokenAsync`)
* [Confirm a user](scenarios/Cognito_Basics/Cognito_MVP/CognitoMethods.cs) (`ConfirmSignUpAsync`)
* [Start authentication with a tracked device](scenarios/Cognito_Basics/Cognito_MVP/CognitoMethods.cs) (`InitiateAuthAsync`)
* [Resend a confirmation code](scenarios/Cognito_Basics/Cognito_MVP/CognitoMethods.cs) (`ResendConfirmationCodeAsync`)
* [Respond to an authentication challenge](scenarios/Cognito_Basics/Cognito_MVP/CognitoMethods.cs) (`RespondToAuthChallengeAsync`)
* [Sign up a user](scenarios/Cognito_Basics/Cognito_MVP/CognitoMethods.cs) (`SignUpAsync`)
* [Verify an MFA application with a user](scenarios/Cognito_Basics/Cognito_MVP/CognitoMethods.cs) (`VerifySoftwareTokenAsync`)

### Scenarios
Code examples that show you how to accomplish a specific task by calling
multiple functions within the same service.

[Sign up a user with a user pool that requires MFA](scenarios/Cognito_Basics/Cognito_MVP/)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
[README](../README.md#Prerequisites) in the dotnetv3 folder.

After the example compiles, you can run it from the command line. To do so,
navigate to the folder that contains the .csproj file and run the following
command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Tests
⚠️ Running the tests might result in charges to your AWS account.

The solution includes a test project. To run the tests, navigate to the folder
that contains the test project and then issue the following command:

```
dotnet test
```

Alternatively, you can open the example solution and use the Visual Studio Test
Runner to run the tests.

## Additional resources
* [Amazon Cognito Developer Guide](https://docs.aws.amazon.com/cognito/latest/developerguide/what-is-amazon-cognito.html)
* [Amazon Cognito API Reference](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/Welcome.html)
* [AWS SDK for .NET Amazon Cognito](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/CognitoIdentity/NCognitoIdentity.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

