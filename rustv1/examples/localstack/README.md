# AWS SDK for Rust code example for [LocalStack](https://github.com/localstack/localstack)

## Purpose

This example demonstrate how to use LocalStack with the AWS SDK for Rust.

LocalStack is a cloud service emulator that runs in a single container on your computer. 

## Code example

- [Get AWS resource information](src/bin/use-localstack.rs)

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

## Running the code example

### Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

### use-localstack

This example lists your Amazon Simple Storage Service buckets and Amazon Simple Queue Service queues.

`cargo run --bin use-localstack`

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [LocalStack topic in AWS SDK for Rust API Reference Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg/localstack.html)

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
