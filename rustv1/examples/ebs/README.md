# Amazon EBS code examples for the SDK for Rust

## Overview

Shows how to use the AWS SDK for Rust to work with Amazon Elastic Block Store (Amazon EBS).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon EBS is a web service that provides block level storage volumes for use with Amazon Elastic Compute Cloud instances._

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

- [Create a snapshot](src/bin/create-snapshot.rs#L34) (`StartSnapshot`)
- [Seal and complete a snapshot](src/bin/create-snapshot.rs#L73) (`CompleteSnapshot`)
- [Write a block of data to a snapshot](src/bin/create-snapshot.rs#L49) (`PutSnapshotBlock`)


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

- [Amazon EBS User Guide](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/AmazonEBS.html)
- [Amazon EBS API Reference](https://docs.aws.amazon.com/AWSEC2/latest/APIReference/OperationList-query-ebs.html)
- [SDK for Rust Amazon EBS reference](https://docs.rs/aws-sdk-ebs/latest/aws_sdk_ebs/)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0