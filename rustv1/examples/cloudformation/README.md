# CloudFormation code examples for the SDK for Rust

## Overview

Shows how to use the AWS SDK for Rust to work with AWS CloudFormation.

<!--custom.overview.start-->
<!--custom.overview.end-->

_CloudFormation enables you to create and provision AWS infrastructure deployments predictably and repeatedly._

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

<!--custom.examples.start-->
### Single actions
- [Create a CloudFormation stack](src/bin/create-stack.rs) (CreateStack)
- [Delete a CloudFormation stack](src/bin/delete-stack.rs) (DeleteStack)
- [Get CloudFormation stack status](src/bin/describe-stack.rs) (DescribeStacks)
- [Lists your CloudFormation stacks](src/bin/list-stacks.rs) (ListStacks)
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

- [CloudFormation User Guide](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/Welcome.html)
- [CloudFormation API Reference](https://docs.aws.amazon.com/AWSCloudFormation/latest/APIReference/Welcome.html)
- [SDK for Rust CloudFormation reference](https://docs.rs/aws-sdk-cloudformation/latest/aws_sdk_cloudformation/)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0