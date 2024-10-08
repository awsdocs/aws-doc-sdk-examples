# Auto Scaling code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon EC2 Auto Scaling.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Auto Scaling](hello/hello_autoscaling.py#L4) (`DescribeAutoScalingGroups`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenario_groups_and_instances.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AttachLoadBalancerTargetGroups](../../cross_service/resilient_service/auto_scaler.py#L599)
- [CreateAutoScalingGroup](action_wrapper.py#L31)
- [DeleteAutoScalingGroup](../../cross_service/resilient_service/auto_scaler.py#L639)
- [DescribeAutoScalingGroups](action_wrapper.py#L167)
- [DescribeAutoScalingInstances](action_wrapper.py#L284)
- [DescribeScalingActivities](action_wrapper.py#L317)
- [DisableMetricsCollection](action_wrapper.py#L399)
- [EnableMetricsCollection](action_wrapper.py#L359)
- [SetDesiredCapacity](action_wrapper.py#L248)
- [TerminateInstanceInAutoScalingGroup](action_wrapper.py#L204)
- [UpdateAutoScalingGroup](action_wrapper.py#L92)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Build and manage a resilient service](../../cross_service/resilient_service/runner.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Auto Scaling

This example shows you how to get started using Auto Scaling.

```
python hello/hello_autoscaling.py
```

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

Start the example by running the following at a command prompt:

```
python scenario_groups_and_instances.py
```


<!--custom.basics.auto-scaling_Scenario_GroupsAndInstances.start-->
<!--custom.basics.auto-scaling_Scenario_GroupsAndInstances.end-->


#### Build and manage a resilient service

This example shows you how to create a load-balanced web service that returns book, movie, and song recommendations. The example shows how the service responds to failures, and how to restructure the service for more resilience when failures occur.

- Use an Amazon EC2 Auto Scaling group to create Amazon Elastic Compute Cloud (Amazon EC2) instances based on a launch template and to keep the number of instances in a specified range.
- Handle and distribute HTTP requests with Elastic Load Balancing.
- Monitor the health of instances in an Auto Scaling group and forward requests only to healthy instances.
- Run a Python web server on each EC2 instance to handle HTTP requests. The web server responds with recommendations and health checks.
- Simulate a recommendation service with an Amazon DynamoDB table.
- Control web server response to requests and health checks by updating AWS Systems Manager parameters.

<!--custom.scenario_prereqs.cross_ResilientService.start-->
<!--custom.scenario_prereqs.cross_ResilientService.end-->

Start the example by running the following at a command prompt:

```
python ../../cross_service/resilient_service/runner.py
```


<!--custom.scenarios.cross_ResilientService.start-->
Complete details and instructions on how to run this example can be found in the
[README](../../cross_service/resilient_service/README.md) for the example.
<!--custom.scenarios.cross_ResilientService.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Auto Scaling User Guide](https://docs.aws.amazon.com/autoscaling/ec2/userguide/what-is-amazon-ec2-auto-scaling.html)
- [Auto Scaling API Reference](https://docs.aws.amazon.com/autoscaling/ec2/APIReference/Welcome.html)
- [SDK for Python Auto Scaling reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/autoscaling.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0