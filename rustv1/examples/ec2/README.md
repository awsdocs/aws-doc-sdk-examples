# Amazon EC2 code examples for the SDK for Rust

## Overview

Shows how to use the AWS SDK for Rust to work with Amazon Elastic Compute Cloud (Amazon EC2).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon EC2 is a web service that provides resizable computing capacity—literally, servers in Amazon's data centers—that you use to build and host your software systems._

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

- [DeleteSnapshot](../ebs/src/bin/delete-snapshot.rs#L26)
- [DescribeImages](src/bin/describe-images.rs#L6)
- [DescribeInstanceStatus](src/bin/list-all-instance-events.rs#L22)
- [DescribeInstances](src/bin/describe-instance.rs#L26)
- [DescribeRegions](src/bin/ec2-helloworld.rs#L22)
- [DescribeSnapshots](../ebs/src/bin/get-snapshot-state.rs#L27)
- [RebootInstances](src/bin/reboot-instance.rs#L28)
- [StartInstances](src/bin/start-instance.rs#L29)
- [StopInstances](src/bin/stop-instance.rs#L29)


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

- [Amazon EC2 User Guide](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)
- [Amazon EC2 API Reference](https://docs.aws.amazon.com/AWSEC2/latest/APIReference/Welcome.html)
- [SDK for Rust Amazon EC2 reference](https://docs.rs/aws-sdk-ec2/latest/aws_sdk_ec2/)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0