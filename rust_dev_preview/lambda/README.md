# AWS SDK for Rust code examples for Lambda

## Purpose

These examples demonstrate how to perform several AWS Lambda (Lambda) operations using the alpha version of the AWS SDK for Rust.

Use Lambda to run code without provisioning or managing servers.

## Code examples

- [Set the Java runtime](src/bin/change-java-runtime.rs) (ListFunctions, UpdateFunctionConfiguration)
- [Invoke a function](src/bin/invoke-function.rs) (Invoke)
- [Show function runtimes and ARNs](src/bin/list-all-function-runtimes.rs) (ListFunctions)
- [List function ARNs](src/bin/list-functions.rs) (ListFunctions)

## ⚠ Important

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

## Running the code examples

### Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

### change-java-runtime

If a Lambda function uses a Java runtime, this example sets its runtime to Corretto.

`cargo run --bin change-java-runtime -- -a ARN [-r REGION] [-v]`

- _ARN_ is the ARN of the function.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the **AWS_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- **-v** displays additional information.

### invoke-function

This example invokes a Lambda function.

`cargo run --bin invoke-function -- -a ARN [-r REGION] [-v]`

- _ARN_ is the ARN of the function to invoke.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the **AWS_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- **-v** displays additional information.

### list-all-function-runtimes

This example lists the ARNs and runtimes of all Lambda functions in all Regions.

`cargo run --bin list-all-function-runtimes -- [-r REGION] [-v]`

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the **AWS_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- **-v** displays additional information.

### list-functions

This example lists your AWS Lambda functions.

`cargo run --bin list-functions -- [-r REGION] [-v]`

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the **AWS_REGION** environment variable.
  If the environment variable is not set, defaults to **us-west-2**.
- **-v** displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference Guide](https://awslabs.github.io/aws-sdk-rust/aws_sdk_config/index.html)

## Contributing

To propose a new code example to the AWS documentation team,
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md).
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
