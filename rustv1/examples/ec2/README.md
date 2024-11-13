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

### Get started

- [Hello Amazon EC2](src/bin/ec2-helloworld.rs#L22) (`DescribeSecurityGroups`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/getting_started/scenario.rs)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AllocateAddress](src/ec2.rs#L443)
- [AssociateAddress](src/ec2.rs#L465)
- [AuthorizeSecurityGroupIngress](src/ec2.rs#L136)
- [CreateKeyPair](src/ec2.rs#L41)
- [CreateSecurityGroup](src/ec2.rs#L77)
- [CreateTags](src/ec2.rs#L233)
- [DeleteKeyPair](src/getting_started/key_pair.rs#L67)
- [DeleteSecurityGroup](src/ec2.rs#L167)
- [DeleteSnapshot](../ebs/src/bin/delete-snapshot.rs#L26)
- [DescribeImages](src/ec2.rs#L179)
- [DescribeInstanceStatus](src/bin/list-all-instance-events.rs#L22)
- [DescribeInstanceTypes](src/ec2.rs#L198)
- [DescribeInstances](src/ec2.rs#L317)
- [DescribeKeyPairs](src/ec2.rs#L57)
- [DescribeRegions](src/bin/describe-regions.rs#L22)
- [DescribeSecurityGroups](src/bin/ec2-helloworld.rs#L22)
- [DescribeSnapshots](../ebs/src/bin/get-snapshot-state.rs#L27)
- [DisassociateAddress](src/ec2.rs#L482)
- [RebootInstances](src/getting_started/instance.rs#L86)
- [ReleaseAddress](src/ec2.rs#L454)
- [RunInstances](src/ec2.rs#L233)
- [StartInstances](src/ec2.rs#L340)
- [StopInstances](src/ec2.rs#L356)
- [TerminateInstances](src/ec2.rs#L410)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon EC2

This example shows you how to get started using Amazon EC2.


#### Learn the basics

This example shows you how to do the following:

- Create a key pair and security group.
- Select an Amazon Machine Image (AMI) and compatible instance type, then create an instance.
- Stop and restart the instance.
- Associate an Elastic IP address with your instance.
- Connect to your instance with SSH, then clean up resources.

<!--custom.basic_prereqs.ec2_Scenario_GetStartedInstances.start-->
<!--custom.basic_prereqs.ec2_Scenario_GetStartedInstances.end-->


<!--custom.basics.ec2_Scenario_GetStartedInstances.start-->
<!--custom.basics.ec2_Scenario_GetStartedInstances.end-->


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