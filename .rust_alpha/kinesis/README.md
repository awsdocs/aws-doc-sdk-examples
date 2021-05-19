# AWS SDK for Rust code examples for Amazon Kinesis

## Purpose

These examples demonstrate how to perform several operations using the alpha version of the AWS SDK for Rust.

## Prerequisites

You must have an AWS account, and have configured your default credentials and AWS Region as described in [https://github.com/awslabs/aws-sdk-rust](https://github.com/awslabs/aws-sdk-rust).

## Running the code

### create-stream

This example creates a Kinesis data stream.

`cargo run -- -n NAME [-d DEFAULT-REGION] [-v]`

- _NAME_ is the name of the stream to create.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ display additional information.  

### delete-stream

This example deletes a Kinesis data stream.

`cargo run -- -n NAME [-d DEFAULT-REGION] [-v]`

- _NAME_ is the name of the stream to delete.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ display additional information.  

### describe-stream

This example displays information about a Kinesis data stream.

`cargo run -- -n NAME [-d DEFAULT-REGION] [-v]`

- _NAME_ is the name of the stream.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ display additional information.  

### list-streams

This example lists your Kinesis data streams.

`cargo run -- [-d DEFAULT-REGION] [-v]`

- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ display additional information.  

### put-record

This example adds a record to a Kinesis data streams.

`cargo run -- -n NAME -k KEY -i INFO [-d DEFAULT-REGION] [-v]`

- _NAME_ is the name of the stream.
- _KEY_ is the name of the partition key for the record.
- _INFO_ is the content of the record.
- _DEFAULT-REGION_ is optional name of a region, such as __us-east-1__.
  If this value is not supplied, the region defaults to __us-west-2__.
- __-v__ display additional information.  











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
