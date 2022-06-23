# AWS SDK for Rust code examples for CloudWatch Logs

## Purpose

These examples demonstrate how to perform several Amazon CloudWatch Logs operations using the developer preview version of the AWS SDK for Rust.

Use CloudWatch Logs to monitor, store, and access your log files from Amazon Elastic Compute Cloud instances, AWS CloudTrail, or other sources.

## Code examples

- [Get log events](src/bin/get-log-events.rs) (GetLogEvents)
- [List log groups](src/bin/list-log-groups.rs) (DescribeLogGroups)
- [List log streams](src/bin/list-log-streams.rs) (DescribeLogStreams)

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

### get-log-events

This example lists the events for a log stream in the Region.

`cargo run --bin get-log-events -- -g GROUP -s STREAM [-r REGION] [-v]`

- _GROUP_ is the name of the log group.
- _STREAM_ is the name of the log stream.
- _REGION_ is name of the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### list-log-groups

This example lists your log groups in the Region.

`cargo run --bin list-log-groups -- [-r REGION] [-v]`
 
- _REGION_ is name of the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### list-log-streams

This example lists the log streams for a log group in the Region.

`cargo run --bin list-log-streams -- -g GROUP [-r REGION] [-v]`
 
- _GROUP_ is the name of the log group.
- _REGION_ is name of the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Amazon CloudWatch Logs](https://docs.rs/aws-sdk-cloudwatchlogs)
- [AWS SDK for Rust Developer Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg)

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0