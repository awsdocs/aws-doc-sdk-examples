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

- [Hello Auto Scaling](GettingStartedWithAutoScaling.php#L252) (`DescribeAutoScalingGroups`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](GettingStartedWithAutoScaling.php)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateAutoScalingGroup](AutoScalingService.php#L22)
- [DeleteAutoScalingGroup](AutoScalingService.php#L61)
- [DescribeAutoScalingGroups](AutoScalingService.php#L71)
- [DescribeAutoScalingInstances](AutoScalingService.php#L148)
- [DescribeScalingActivities](AutoScalingService.php#L157)
- [DisableMetricsCollection](AutoScalingService.php#L176)
- [EnableMetricsCollection](AutoScalingService.php#L166)
- [SetDesiredCapacity](AutoScalingService.php#L138)
- [TerminateInstanceInAutoScalingGroup](AutoScalingService.php#L85)
- [UpdateAutoScalingGroup](AutoScalingService.php#L42)


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