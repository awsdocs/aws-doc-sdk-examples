# Amazon code examples for the SDK for C++

## Overview

Demonstrates how to use the AWS SDK for C++ with Amazon Cognito to sign up users, set users up for multi-factor authentication (MFA), and sign in to
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

* [Sign up a user](./getting_started_with_user_pools.cpp)(SignUp)
* [Resend a confirmation code](./getting_started_with_user_pools.cpp)(ResendConfirmationCode)
* [Confirm a user](./getting_started_with_user_pools.cpp)(ConfirmSignUp)
* [Get a token to associate an MFA application with a user](./getting_started_with_user_pools.cpp)(AssociateSoftwareToken)
* [Verify an MFA application with a user](./getting_started_with_user_pools.cpp)(VerifySoftwareToken)
* [Respond to an authentication challenge](./getting_started_with_user_pools.cpp)(RespondToAuthChallenge)
* [Sign up a user](./getting_started_with_user_pools.cpp)(SignUp)
* [Sign up a user](./getting_started_with_user_pools.cpp)(SignUp)
* [Sign up a user](./getting_started_with_user_pools.cpp)(SignUp)

### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.
* [*Title of code example*](*relative link to code example*)
### Cross-service examples
Sample applications that work across multiple AWS services.
* [*Title of code example*](*relative link to code example*)
## Run the examples

### Prerequisites
Before using the code examples, first complete the installation and setup steps
for [Getting started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for
C++ Developer Guide.
This section covers how to get and build the SDK, and how to build your own code by using the SDK with a
sample Hello World-style application.

Next, for information on code example structures and how to build and run the examples, see [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html).

## Tests
⚠️ Running the tests might result in charges to your AWS account.

```sh
   cd <BUILD_DIR>
   cmake <path-to-root-of-this-source-code> -DBUILD_TESTS=ON
   make
   ctest 
```   

## Additional resources
* [*Service developer guide*](*link to developer guide*)
* [*Service API reference guide*](*link to developer guide*)
* [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html)


Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
