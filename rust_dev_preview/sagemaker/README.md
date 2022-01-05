# AWS SDK for Rust code examples for Amazon SageMaker

## Purpose

These examples demonstrate how to perform several Amazon SageMaker (SageMaker) operations using the developer preview version of the AWS SDK for Rust.

SageMaker is a fully managed machine learning service that you can use to quickly and easily build and train machine learning models, and then directly deploy them into a production-ready hosted environment.

## Code examples

- [List your training jobs](src/bin/list-training-jobs.rs) (ListTrainingJobs)
- [Lists your notebook instances](src/bin/sagemaker-helloworld.rs) (ListNotebookInstances)
 
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

### list-training-jobs

This example lists your SageMaker training jobs in the Region.

`cargo run --bin list-training-jobs -- [-r REGION] [-v]`

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### sagemaker-helloworld

This example lists the name, status, and type of your SageMaker instances in the Region.

`cargo run --bin sagemaker-helloworld -- [-r REGION] [-v]`

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Amazon SageMaker](https://docs.rs/aws-sdk-sagemaker)
- [AWS SDK for Rust API Reference Guide](https://awslabs.github.io/aws-sdk-rust/aws_sdk_config/index.html) 

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0