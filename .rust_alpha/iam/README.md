# AWS SDK for Rust code examples for IAM

AWS Identity and Access Management (IAM) is a web service for securely controlling access to AWS services.

## Purpose

These examples demonstrate how to perform several IAM operations using the alpha version of the AWS SDK for Rust.

## Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

## Running the code

### create-role.rs

This example creates an IAM role in the Region.

`cargo run --bin create-role -- -a ACCOUNT-ID -b BUCKET -n NAME -p POLICY-NAME [-r REGION] [-v]`

- _ACCOUNT-ID_ is the ID of an instance to stop.
- _BUCKET_ is the name of the bucket where Config stores information about resources.
- _NAME_ is the name of the role.
- _POLICY-NAME_ is the name of the JSON file containing the policy document.
- _REGION_ the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

If the example succeeds, it displays the ARN of the new role.

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
