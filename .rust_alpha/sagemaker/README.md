# AWS SDK for Rust code examples for Amazon SageMaker

## Purpose

These examples demonstrate how to perform several Amazon SageMaker (SageMaker) operations using the alpha version of the AWS SDK for Rust.

SageMaker is a fully managed machine learning service that you can use to quickly and easily build and train machine learning models, and then directly deploy them into a production-ready hosted environment.

## Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

## Running the code

### list-training-jobs

This example lists your SageMaker training jobs in an AWS Region.

`cargo run --bin list-training-jobs -- [-d DEFAULT-REGION] [-v]`

- _DEFAULT-REGION_ is name of the AWS Region, such as __us-east-1__, where the training jobs are located.
  If not supplied, uses the value of the __AWS_DEFAULT_REGION__ or __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### sagemaker-helloworld

This example lists the name, status, and type of your SageMaker instances in an AWS Region..

`cargo run --bin sagemaker-helloworld -- [-d DEFAULT-REGION] [-v]`

- _DEFAULT-REGION_ is name of the AWS Region, such as __us-east-1__, where the instances are located.
  If not supplied, uses the value of the __AWS_DEFAULT_REGION__ or __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### Notes

- We recommend that you grant this code least privilege,
  or at most the minimum permissions required to perform the task.
  For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege)
  in the AWS Identity and Access Management User Guide.
- This code has not been tested in all AWS Regions.
  Some AWS services are available only in specific
  [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
- Running this code might result in charges to your AWS account.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0