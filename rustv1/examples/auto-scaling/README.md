# Auto Scaling code examples for the SDK for Rust

## Overview

Shows how to use the AWS SDK for Rust to work with Amazon EC2 Auto Scaling.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Auto Scaling automatically scales EC2 instances, either with scaling policies or with scheduled scaling._

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

- [Hello Auto Scaling](src/bin/list-autoscaling-groups.rs#L22) (`DescribeAutoScalingGroups`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](Cargo.toml)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateAutoScalingGroup](src/bin/create-autoscaling-group.rs#L30)
- [DeleteAutoScalingGroup](src/bin/delete-autoscaling-group.rs#L30)
- [DescribeAutoScalingGroups](src/bin/list-autoscaling-groups.rs#L22)
- [DescribeAutoScalingInstances](src/scenario.rs#L532)
- [DescribeScalingActivities](src/scenario.rs#L399)
- [DisableMetricsCollection](src/scenario.rs#L617)
- [EnableMetricsCollection](src/scenario.rs#L294)
- [SetDesiredCapacity](src/scenario.rs#L595)
- [TerminateInstanceInAutoScalingGroup](src/scenario.rs#L456)
- [UpdateAutoScalingGroup](src/bin/update-autoscaling-group.rs#L30)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Auto Scaling

This example shows you how to get started using Auto Scaling.


#### Learn the basics

This example shows you how to do the following:

- Create an Amazon EC2 Auto Scaling group with a launch template and Availability Zones, and get information about running instances.
- Enable Amazon CloudWatch metrics collection.
- Update the group's desired capacity and wait for an instance to start.
- Terminate an instance in the group.
- List scaling activities that occur in response to user requests and capacity changes.
- Get statistics for CloudWatch metrics, then clean up resources.

<!--custom.basic_prereqs.auto-scaling_Scenario_GroupsAndInstances.start-->
<!--custom.basic_prereqs.auto-scaling_Scenario_GroupsAndInstances.end-->


<!--custom.basics.auto-scaling_Scenario_GroupsAndInstances.start-->
<!--custom.basics.auto-scaling_Scenario_GroupsAndInstances.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `rustv1` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Auto Scaling User Guide](https://docs.aws.amazon.com/autoscaling/ec2/userguide/what-is-amazon-ec2-auto-scaling.html)
- [Auto Scaling API Reference](https://docs.aws.amazon.com/autoscaling/ec2/APIReference/Welcome.html)
- [SDK for Rust Auto Scaling reference](https://docs.rs/aws-sdk-auto-scaling/latest/aws_sdk_auto-scaling/)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0