# Amazon EC2 Auto Scaling code examples for the AWS SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to create and manage Amazon EC2
Auto Scaling groups and instances.

*Amazon EC2 Auto Scaling automatically scales Amazon EC2 instances, either with scaling 
policies or with scheduled scaling.*

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

* [Create a group](action_wrapper.py)
(`CreateAutoScalingGroup`)
* [Delete a group](action_wrapper.py)
(`DeleteAutoScalingGroup`)
* [Disable metrics collection for a group](action_wrapper.py)
(`DisableMetricsCollection`)
* [Enable metrics collection for a group](action_wrapper.py)
(`EnableMetricsCollection`)
* [Get information about groups](action_wrapper.py)
(`DescribeAutoScalingGroups`)
* [Get information about instances](action_wrapper.py)
(`DescribeAutoScalingInstances`)
* [Get information about scaling activities](action_wrapper.py)
(`DescribeScalingActivities`)
* [Set the desired capacity of a group](action_wrapper.py)
(`SetDesiredCapacity`)
* [Terminate an instance in a group](action_wrapper.py)
(`TerminateInstanceInAutoScalingGroup`)
* [Update a group](action_wrapper.py)
(`UpdateAutoScalingGroup`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple 
functions within the same service.

* [Manage groups and instances](scenario_groups_and_instances.py)

## Running the examples

### Prerequisites

Prerequisites for running the examples for this service can be found in the 
[README](../../README.md#Prerequisites) in the Python folder.

### Groups and instances

This interactive scenario runs at a command prompt and shows you how to use 
EC2 Auto Scaling to do the following:

1. Create an Amazon Elastic Compute Cloud (Amazon EC2) launch template.
2. Create an EC2 Auto Scaling group configured with a launch template and Availability
   Zones.
3. Get information about the group and running instances.
4. Enable Amazon CloudWatch metrics collection on the group.
5. Update the desired capacity of the group and wait for an instance to start.
6. Terminate an instance in the group.
7. List scaling activities that have occurred in response to user requests and capacity
   changes.
8. Get statistics for CloudWatch metrics that have been collected during the example.
9. Stop collecting metrics, terminate all instances, and delete the group.

Start the scenario at a command prompt.

```
python scenario_groups_and_instances.py
```

## Tests

Instructions for running the tests for this service can be found in the
[README](../../README.md#Tests) in the Python folder.


## Additional resources
* [Amazon EC2 Auto Scaling User Guide](https://docs.aws.amazon.com/autoscaling/ec2/userguide/what-is-amazon-ec2-auto-scaling.html)
* [Amazon EC2 Auto Scaling API Reference](https://docs.aws.amazon.com/autoscaling/ec2/APIReference)
* [AWS SDK for Python EC2 Auto Scaling Client](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/autoscaling.html) 

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 

SPDX-License-Identifier: Apache-2.0
