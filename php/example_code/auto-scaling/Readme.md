# Auto Scaling code examples for the SDK for PHP

## Overview

Shows how to use the AWS SDK for PHP to work with Amazon EC2 Auto Scaling.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `php` folder.


<!--custom.prerequisites.start-->
- You must have an AWS account, and have your default credentials and AWS Region configured as described in
  the [AWS Tools and SDKs Shared Configuration and Credentials Reference Guide]
  (https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- PHP 7.1 or later.
- Composer installed.
<!--custom.prerequisites.end-->

### Get started

- [Hello Auto Scaling](GettingStartedWithAutoScaling.php#L245) (`DescribeAutoScalingGroups`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a group](AutoScalingService.php#L23) (`CreateAutoScalingGroup`)
- [Delete a group](AutoScalingService.php#L62) (`DeleteAutoScalingGroup`)
- [Disable metrics collection for a group](AutoScalingService.php#L177) (`DisableMetricsCollection`)
- [Enable metrics collection for a group](AutoScalingService.php#L167) (`EnableMetricsCollection`)
- [Get information about groups](AutoScalingService.php#L72) (`DescribeAutoScalingGroups`)
- [Get information about instances](AutoScalingService.php#L149) (`DescribeAutoScalingInstances`)
- [Get information about scaling activities](AutoScalingService.php#L158) (`DescribeScalingActivities`)
- [Set the desired capacity of a group](AutoScalingService.php#L139) (`SetDesiredCapacity`)
- [Terminate an instance in a group](AutoScalingService.php#L86) (`TerminateInstanceInAutoScalingGroup`)
- [Update a group](AutoScalingService.php#L43) (`UpdateAutoScalingGroup`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Manage groups and instances](GettingStartedWithAutoScaling.php)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
From the `aws-doc-sdk-examples/php/auto-scaling/` directory:

Install dependencies by using Composer:

```
composer install
```

After your Composer dependencies are installed, you can run the example with the
following command:

```
php Runner.php
```
<!--custom.instructions.end-->

#### Hello Auto Scaling

This example shows you how to get started using Auto Scaling.



#### Manage groups and instances

This example shows you how to do the following:

- Create an Amazon EC2 Auto Scaling group with a launch template and Availability Zones, and get information about running instances.
- Enable Amazon CloudWatch metrics collection.
- Update the group's desired capacity and wait for an instance to start.
- Terminate an instance in the group.
- List scaling activities that occur in response to user requests and capacity changes.
- Get statistics for CloudWatch metrics, then clean up resources.

<!--custom.scenario_prereqs.auto-scaling_Scenario_GroupsAndInstances.start-->
<!--custom.scenario_prereqs.auto-scaling_Scenario_GroupsAndInstances.end-->


<!--custom.scenarios.auto-scaling_Scenario_GroupsAndInstances.start-->
<!--custom.scenarios.auto-scaling_Scenario_GroupsAndInstances.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `php` folder.



<!--custom.tests.start-->
Install dependencies by using Composer:

```
composer install
```
Run the tests with the following command:
```
../vendor/bin/phpunit AutoScalingBasicsTests.php
```

<!--custom.tests.end-->

## Additional resources

- [Auto Scaling User Guide](https://docs.aws.amazon.com/autoscaling/ec2/userguide/what-is-amazon-ec2-auto-scaling.html)
- [Auto Scaling API Reference](https://docs.aws.amazon.com/autoscaling/ec2/APIReference/Welcome.html)
- [SDK for PHP Auto Scaling reference](https://docs.aws.amazon.com/aws-sdk-php/v3/api/namespace-Aws.Auto-scaling.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0