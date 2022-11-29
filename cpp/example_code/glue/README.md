# AWS Glue code examples for the AWS SDK for C++

## Overview

Shows how to use the AWS SDK for C++ to manage AWS Glue resources.

*AWS Glue is a serverless data integration service that makes it easier to discover, prepare, move, and integrate data from multiple sources for analytics, machine learning (ML), and application development.*

## ⚠️ Important

* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Scenarios

* [Get started running crawlers and jobs](glue_getting_started_scenario.cpp)

## Run the examples

### Prerequisites

Before using the code examples, first complete the installation and setup steps
of [Getting started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for
C++ Developer Guide.
This section covers how to get and build the SDK, and how to build your own code by using the SDK with a
sample Hello World-style application.

Next, for information on code example structures and how to build and run the examples, see [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html).

Additional steps are required to run the scenario, [get started running
crawlers and jobs](glue_getting_started_scenario.cpp). The scenario depends
on resources which can be created with an AWS Cloud Development Kit (AWS CDK)
script. The script is located at
[aws-doc-sdk-examples/resources/cdk/glue_role_bucket](../../../resources/cdk/glue_role_bucket). See "Running a CDK app" in the [README](../../../resources/cdk/README.md).

Running the CDK scripts will give an output similar to the following.

```sh

Outputs:
doc-example-glue-scenario-stack.BucketName = doc-example-glue-scenario-docexampleglue6e2f12e5-6zbgwfv9hx5k
doc-example-glue-scenario-stack.RoleName = AWSGlueServiceRole-DocExample
Stack ARN:
arn:aws:cloudformation:us-east-1:123456789101:stack/doc-example-glue-scenario-stack/12345789-1234-1234-1234-123456789101

```
_doc-example-glue-scenario-docexampleglue6e2f12e5-6zbgwfv9hx5k_ is the name 
of an Amazon Simple Storage Service (Amazon S3) bucket.

_AWSGlueServiceRole-DocExample_ is an AWS Identity and Access Management 
(IAM) role name. 

These two strings are the inputs required by the scenario.

## Tests

⚠️ Running the tests might result in charges to your AWS account.

```sh
   cd <BUILD_DIR>
   cmake <path-to-root-of-this-source-code> -DBUILD_TESTS=ON
   make
   ctest 
```   

## Additional resources

* [AWS Glue Developer Guide](https://docs.aws.amazon.com/glue/latest/dg/what-is-glue.html)
* [AWS Glue API Reference](https://docs.aws.amazon.com/glue/latest/dg/aws-glue-api.html)
* [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
