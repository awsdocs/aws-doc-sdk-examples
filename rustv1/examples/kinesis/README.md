# Kinesis code examples for the SDK for Rust

## Overview

Shows how to use the AWS SDK for Rust to work with Amazon Kinesis.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Kinesis makes it easy to collect, process, and analyze video and data streams in real time._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `rustv1` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a stream](src/bin/create-stream.rs#L28) (`CreateStream`)
- [Delete a stream](src/bin/delete-stream.rs#L28) (`DeleteStream`)
- [Describe a stream](src/bin/describe-stream.rs#L28) (`DescribeStream`)
- [List streams](src/bin/list-streams.rs#L24) (`ListStreams`)
- [Put data into a stream](src/bin/put-record.rs#L37) (`PutRecord`)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `rustv1` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Kinesis Developer Guide](https://docs.aws.amazon.com/streams/latest/dev/introduction.html)
- [Kinesis API Reference](https://docs.aws.amazon.com/kinesis/latest/APIReference/Welcome.html)
- [SDK for Rust Kinesis reference](https://docs.rs/aws-sdk-kinesis/latest/aws_sdk_kinesis/)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0