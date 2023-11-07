# AWS SDK for Rust code examples for Global Accelerator

## Purpose

These examples demonstrate how to perform AWS Global Accelerator operations using the developer preview version of the AWS SDK for Rust.

AWS Global Accelerator is a networking service that improves the performance of your users’ traffic by up to 60% using Amazon Web Services’ global network infrastructure.

## Code examples

- [Displays the names and ARNs of your accelerators](src/bin/globalaccelerator-helloworld.rs) (ListAccelerators)

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

### globalaccelerator-helloworld

This example lists your Global Accelerator accelerator names and ARNs.

`cargo run --bin globalaccelerator-helloworld [-v]`

- __-v__ displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Global Accelerator](https://docs.rs/aws-sdk-globalaccelerator)
- [AWS SDK for Rust API Reference Guide](https://awslabs.github.io/aws-sdk-rust/aws_sdk_config/index.html) 

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
