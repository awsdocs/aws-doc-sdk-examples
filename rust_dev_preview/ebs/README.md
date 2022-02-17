# AWS SDK for Rust code examples for Amazon EBS

## Purpose

These examples demonstrate how to perform several Amazon Elastic Block Store (Amazon EBS) operations using the developer preview version of the AWS SDK for Rust.

Amazon EBS is a web service that provides block level storage volumes for use with Amazon Elastic Compute Cloud instances.

## Code examples

- [Create snapshot](src/bin/create-snapshot.rs) (CompleteSnapshot, PutSnapshotBlock, StartSnapshot)
- [Delete a snapshot](src/bin/delete-snapshot.rs) (DeleteSnapshot)
- [Get snapshot state](src/bin/get-snapshot-state.rs) (DescribeSnapshots)
- [List snapshots](src/bin/list-snapshots.rs) (DescribeSnapshots)

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

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Amazon EBS](https://docs.rs/aws-sdk-ebs)
- [AWS SDK for Rust API Reference Guide](https://awslabs.github.io/aws-sdk-rust/aws_sdk_config/index.html) 

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0