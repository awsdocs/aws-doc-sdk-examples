# Amazon Cognito code examples for the SDK for C++

## Overview

Shows how to use the AWS SDK for C++ with Amazon Cognito to sign up users, set users up for multi-factor authentication (MFA), and sign in to
get access tokens.

*Amazon Cognito provides authentication, authorization, and user management for your web and mobile apps. Your users can sign in directly with a user name and password, or through a third party such as Facebook, Amazon, Google or Apple.*

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

* [Confirm a user](./getting_started_with_user_pools.cpp) (ConfirmSignUp)
* [Delete a user](./getting_started_with_user_pools.cpp) (DeleteUser)
* [Get a token to associate an MFA application with a user](./getting_started_with_user_pools.cpp) (AssociateSoftwareToken)
* [Get information about a user](./getting_started_with_user_pools.cpp) (AdminGetUser)
* [Resend a confirmation code](./getting_started_with_user_pools.cpp) (ResendConfirmationCode)
* [Respond to SRP authentication challenges](./getting_started_with_user_pools.cpp) (RespondToAuthChallenge)
* [Sign up a user](./getting_started_with_user_pools.cpp) (SignUp)
* [Start authentication with a tracked device](./getting_started_with_user_pools.cpp) (InitiateAuth)
* [Verify an MFA application with a user](./getting_started_with_user_pools.cpp) (VerifySoftwareToken)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* [Sign up a user with a user pool that requires MFA](./getting_started_with_user_pools.cpp) (ConfirmSignUp, DeleteUser,
  AssociateSoftwareToken, AdminGetUser, ResendConfirmationCode, RespondToAuthChallenge, SignUp, InitiateAuth, VerifySoftwareToken)

## Run the examples

### Prerequisites
Before using the code examples, first complete the installation and setup steps
for [Getting started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for
C++ Developer Guide.
This section covers how to get and build the SDK, and how to build your own code by using the SDK with a
sample Hello World-style application.

Next, for information on code example structures and how to build and run the examples, see [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html).

#### Running the `Sign up a user with a user pool that requires MFA` scenario.

The Amazon Cognito resources for `Sign up a user with a user pool that requires MFA` can be created by running the AWS CloudFormation
`setup.yaml` script in [resources/cdk/cognito_scenario_user_pool_with_mfa](../../../resources/cdk/cognito_scenario_user_pool_with_mfa/setup.yaml).

For instructions on how to run the script, see the [README](../../../resources/cdk/cognito_scenario_user_pool_with_mfa/README.md).

Optionally, build [Sign up a user with a user pool that requires MFA](./getting_started_with_user_pools.cpp) to use the
[qr-code-generator](https://github.com/nayuki/QR-Code-generator) library. This creates a QR code image for MFA authentication. 
A [conanfile.txt](conanfile.txt) is included with the sample code. You can install the qr-code-generator library by using the [Conan C++ package manager](https://conan.io/).

To install the `qr-code-generator` library using Conan, run the following command from the build directory.

On Linux and Mac.

`conan install <path_to_source_dir> --build=missing`

On Windows.

`conan install <path_to_source_dir> --build=missing -s build_type=Debug`

Next, enable the `USING_CONAN` variable in [CMakeLists.txt](CMakeLists.txt).

`set(USING_CONAN TRUE)  # Setting to true enables generation of a QR code.`

## Tests
⚠️ Running the tests might result in charges to your AWS account.

```sh
   cd <BUILD_DIR>
   cmake <path-to-root-of-this-source-code> -DBUILD_TESTS=ON
   make
   ctest 
```   

## Additional resources

* [Amazon Cognito Developer Guide](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-identity-pools.html)
* [Amazon Cognito API Reference](https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/Welcome.html)
* [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
