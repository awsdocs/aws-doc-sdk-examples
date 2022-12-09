# Lambda code examples for the SDK for C++
## Overview

Shows how to use the AWS SDK for C++ to create, deploy, and invoke
AWS Lambda functions.

*AWS Lambda is a serverless, event-driven compute service that lets you run code for virtually any type of application or backend service without provisioning or managing servers. You can trigger Lambda from over 200 AWS services and software as a service (SaaS) applications, and only pay for what you use.*

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

* [Create a function](get_started_with_functions_scenario.cpp)(CreateFunction)
* [Delete a function](get_started_with_functions_scenario.cpp)(DeleteFunction)
* [Get a function](get_started_with_functions_scenario.cpp)(GetFunction)
* [Invoke a function](get_started_with_functions_scenario.cpp)(Invoke)
* [List functions](get_started_with_functions_scenario.cpp)(ListFunctions)
* [Update function code](get_started_with_functions_scenario.cpp)
  (UpdateFunctionCode)
* [Update function configuration](get_started_with_functions_scenario.cpp)
  (UpdateFunctionConfiguration)
* 
### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.
* 
* [Get started with functions](get_started_with_functions_scenario.cpp) 
  (CreateFunction, GetFunction, ListFunctions, Invoke, UpdateFunctionCode, 
  UpdateFunctionConfiguration, DeleteFunction)

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
* [AWS Lambda Developer Guide](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)
* [AWS Lambda API Reference](https://docs.aws.amazon.com/lambda/latest/dg/API_Reference.html)
* [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html)


Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
