# AWS SDK for Rust code examples for CloudFormation

## Purpose

These examples demonstrate how to perform several CloudFormation operations using the alpha version of the AWS SDK for Rust.
AWS CloudFormation (CloudFormation) enables you to use a template file to create and delete a collection of resources together as a single unit (a stack).

## Code examples

- [Create a CloudFormation stack](src/bin/create-stack.rs) (CreateStack)
- [Delete a CloudFormation stack](src/bin/delete-stack.rs) (DeleteStack)
- [Get CloudFormation stack status](src/bin/describe-stack.rs) (DescribeStacks)
- [Lists your CloudFormation stacks](src/bin/list-stacks.rs) (ListStacks)

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

### create-stack

This example creates a CloudFormation stack in the region.

`cargo run --bin create-stack -- -s STACK-NAME -t TEMPLATE-FILE [-d DEFAULT-REGION] [-v]`

- _STACK-NAME_ is name of the stack.
- _TEMPLATE-FILE_ is name of the template file, in either JSON or YAML format.
- _DEFAULT-REGION_ is name of the AWS Region, such as __us-east-1__, where the stacks are located.
  If not supplied, uses the value of the __AWS_DEFAULT_REGION__ or __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### delete-stack

This example deletes a CloudFormation stack in the region.

`cargo run --bin delete-stack -- -s STACK-NAME [-d DEFAULT-REGION] [-v]`

- _STACK-NAME_ is name of the stack.
- _DEFAULT-REGION_ is name of the AWS Region, such as __us-east-1__, where the stacks are located.
  If not supplied, uses the value of the __AWS_DEFAULT_REGION__ or __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### describe-stack

This example retrieves the status of a CloudFormation stack in the region.

`cargo run --bin describe-stack -- -s STACK-NAME [-d DEFAULT-REGION] [-v]`

- _STACK-NAME_ is name of the stack.
  If the stack does not exist, the code panics.
- _DEFAULT-REGION_ is name of the AWS Region, such as __us-east-1__, where the stacks are located.
  If not supplied, uses the value of the __AWS_DEFAULT_REGION__ or __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### list-stacks

This example lists the name and status of your CloudFormation stacks in the region.

`cargo run --bin list-stacks -- [-d DEFAULT-REGION] [-v]`

- _DEFAULT-REGION_ is name of the AWS Region, such as __us-east-1__, where the stacks are located.
  If not supplied, uses the value of the __AWS_DEFAULT_REGION__ or __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for CloudFormation](https://docs.rs/aws-sdk-cloudformation)
- [AWS SDK for Rust Developer Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg)

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
