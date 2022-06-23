# AWS SDK for Rust code examples for Kinesis

## Purpose

These examples demonstrate how to perform several Amazon Kinesis (Kinesis) operations using the developer preview version of the AWS SDK for Rust.

Kinesis makes it easy to collect, process, and analyze video and data streams in real time.

## Running the code examples

- [Create a data stream](src/bin/create-stream.rs) (CreateStream)
- [Delete a data stream](src/bin/delete-stream.rs) (DeleteStream)
- [Describe a data stream](src/bin/describe-stream.rs) (DescribeStream)
- [List your data streams](src/bin/list-streams.rs) (ListStreams)
- [Add a record to a data stream](src/bin/put-record.rs) (PutRecord)

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

### create-stream

This example creates a Kinesis data stream.

`cargo run --bin create-stream -- -n NAME [-r REGION] [-v]`

- _NAME_ is the name of the stream to create.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ display additional information.  

### delete-stream

This example deletes a Kinesis data stream.

`cargo run --bin delete-stream -- -n NAME [-r REGION] [-v]`

- _NAME_ is the name of the stream to delete.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ display additional information.  

### describe-stream

This example displays information about a Kinesis data stream.

`cargo run --bin describe-stream -- -n NAME [-r REGION] [-v]`

- _NAME_ is the name of the stream.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ display additional information.  

### list-streams

This example lists your Kinesis data streams.

`cargo run --bin list-streams -- [-r REGION] [-v]`

- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ display additional information.  

### put-record

This example adds a record to a Kinesis data streams.

`cargo run --bin put-record -- -n NAME -k KEY -i INFO [-r REGION] [-v]`

- _NAME_ is the name of the stream.
- _KEY_ is the name of the partition key for the record.
- _INFO_ is the content of the record.
- _REGION_ is the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ display additional information.  

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- - [AWS SDK for Rust API Reference for Kinesis](https://docs.rs/aws-sdk-kinesis)
- [AWS SDK for Rust API Reference Guide](https://awslabs.github.io/aws-sdk-rust/aws_sdk_config/index.html) 

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
