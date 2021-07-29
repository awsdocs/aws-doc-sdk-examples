# AWS SDK for Rust code examples for Amazon EBS

Amazon Elastic Block Store (Amazon EBS) is a web service that provides block level storage volumes for use with Amazon Elastic Compute Cloud instances.

## Purpose

These examples demonstrate how to perform several Amazon EBS operations using the alpha version of the AWS SDK for Rust.

## Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

## Running the code

### create-snapshot

This example creates an Amazon EBS snapshot using generated data.

`cargo run --bin create-snapshot -- -d DESCRIPTION [-r REGION] [-v]`

- _DESCRIPTION_ is the description of the snapshot. 
- _REGION_ is name of the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### delete-snapshot

This example deletes an Amazon EBS snapshot.

`cargo run --bin delete-snapshot -- -s SNAPSHOP-ID [-r REGION] [-v]`

- _SNAPSHOT-ID_ is the ID of the snapshot. 
- _REGION_ is name of the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### get-snapshot-state

This example retrieves the state of an Amazon EBS snapshot using Amazon EC2 API.

`cargo run --bin get-snapshot-state -- -s SNAPSHOP-ID [-r REGION] [-v]`

- _SNAPSHOT-ID_ is the ID of the snapshot. 
- _REGION_ is name of the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### list-snapshots

This example lists the Amazon EBS snapshots in the Region.

`cargo run --bin list-snapshots -- [-r REGION] [-v]`
 
- _REGION_ is name of the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
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