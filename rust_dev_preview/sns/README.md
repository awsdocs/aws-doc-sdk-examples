# AWS SDK for Rust code examples for Amazon SNS

## Purpose

These examples demonstrate how to perform several Amazon Simple Notification Service (Amazon SNS) operations using the developer preview version of the AWS SDK for Rust.

Amazon SNS is a web service that enables applications, end-users, and devices to instantly send and receive notifications from the cloud.

## Code examples

- [Creates a topic](src/bin/create-topic.rs) (CreateTopic)
- [Lists your topics](src/bin/list-topics.rs) (ListTopics)
- [Subscribes an email address and publishes a message to a topic](src/bin/sns-hello-world.rs) (Publish, Subscribe)

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

You must have an AWS account, and have configured your default credentials and AWS Region as described in [Getting started with the AWS SDK for Rust](https://docs.aws.amazon.com/sdk-for-rust/latest/dg/getting-started.html).

### create-topic

This example creates an Amazon SNS topic.

`cargo run --bin create-topic -- -t TOPIC_ARN [-r REGION] [-v]`

- _TOPIC_ARN_ is the ARN of the topic.
- _REGION_ is name of the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### list-topics

This example lists your Amazon SNS topics in the Region.

`cargo run --bin list-topics -- [-r REGION] [-v]`

- _REGION_ is name of the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

### sns-hello-world

This example subscribes an email address and publishes a message to a topic.

`cargo run --bin sns-hello-world -- -e EMAIL_ADDRESS -t TOPIC_ARN [-r REGION] [-v]`

- _EMAIL_ADDRESS_ is the email address to subscribe to a topic.
  The topic to which the address subscribes is the first in the list of topics.
- _TOPIC_ARN_ is the ARN of the topic.
- _REGION_ is name of the Region in which the client is created.
  If not supplied, uses the value of the __AWS_REGION__ environment variable.
  If the environment variable is not set, defaults to __us-west-2__.
- __-v__ displays additional information.

## Resources

- [AWS SDK for Rust repo](https://github.com/awslabs/aws-sdk-rust)
- [AWS SDK for Rust API Reference for Amazon SNS](https://docs.rs/aws-sdk-sns)
- [AWS SDK for Rust Developer Guide](https://docs.aws.amazon.com/sdk-for-rust/latest/dg)

## Contributing

To propose a new code example to the AWS documentation team, 
see [CONTRIBUTING.md](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/CONTRIBUTING.md). 
The team prefers to create code examples that show broad scenarios rather than individual API calls.

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0