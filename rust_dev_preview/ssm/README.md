# AWS SDK for Rust code examples for Systems Manager

## Purpose

These examples demonstrate how to perform several AWS Systems Manager (Systems Manager) operations using the developer preview version of the AWS SDK for Rust.

Use Systems Manager to organize, monitor, and automate management tasks on your AWS resources.

## Code examples

- [Creates a new parameter](src/bin/create-parameter.rs) (PutParameter)
- [Lists your parameters](src/bin/describe-parameters.rs) (DescribeParameters)

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

### create-parameter

This example creates a new Systems Manager parameter in the Region.

`cargo run --bin create-parameter -- -n NAME -p PARAMETER-VALUE -d DESCRIPTION [-r REGION] [-v]`

Where:

- _DESCRIPTION_ is the description of the parameter.
- _PARAMETER-VALUE_ is the value of the parameter.
- _NAME_ is the name of the parameter.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### describe-parameters

This example lists the names of your Systems Manager parameters in the Region.

`cargo run --bin describe-parameters -- [-r REGION] [-v]`

Where:

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Systems Manager](https://docs.rs/aws-sdk-ssm)
- [AWS SDK for Rust API Reference Guide](https://awslabs.github.io/aws-sdk-rust/aws_sdk_config/index.html) 

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0