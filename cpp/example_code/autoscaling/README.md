#  Amazon EC2 Auto Scaling code examples for the SDK for C++

## Overview
Shows how to use the AWS SDK for C++ to create and manage Amazon EC2 Auto Scaling groups and instances.

*Amazon EC2 Auto Scaling helps you maintain application availability and lets you automatically add or remove EC2 instances using scaling policies that you define.*

## ⚠️ Important

* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions

Code excerpts that show you how to call individual service functions.

* [Create a group](groups_and_instances_scenario.cpp) 
  (`CreateAutoScalingGroup`)
* [Delete a group](groups_and_instances_scenario.cpp)(`DeleteAutoScalingGroup`)
* [Disable metrics collection for a group](groups_and_instances_scenario.cpp)(`DisableMetricsCollection`)
* [Enable metrics collection for a group](groups_and_instances_scenario.cpp) 
  (`EnableMetricsCollection`)
* [Get information about groups](groups_and_instances_scenario.cpp) 
  (`DescribeAutoScalingGroups`)
* [Get information about instances](groups_and_instances_scenario.cpp) 
  (`DescribeAutoScalingInstances`)
* [Get information about scaling activities](groups_and_instances_scenario.cpp) (`DescribeScalingActivities`)
* [Set the desired capacity of a group](groups_and_instances_scenario.cpp) 
  (`SetDesiredCapacity`)
* [Terminate an instance in a group](groups_and_instances_scenario.cpp) 
  (`TerminateInstanceInAutoScalingGroup`)
* [Update a group](groups_and_instances_scenario.cpp) (`UpdateAutoScalingGroup`)
### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* [Manage groups and instances](groups_and_instances_scenario.cpp)

## Run the examples

### Prerequisites
Before using the code examples, first complete the installation and setup steps
for [Getting started](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html) in the AWS SDK for
C++ Developer Guide.
This section covers how to get and build the SDK, and how to build your own code by using the SDK with a sample Hello World-style application.

Next, for information on code example structures and how to build and run the examples, see [Getting started with the AWS SDK for C++ code examples](https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html).

## Tests
⚠️ Running the tests might result in charges to your AWS account.

```sh
   cd <BUILD_DIR>
   cmake <path-to-root-of-this-source-code> -DBUILD_TESTS=ON
   make
   ctest 
```   

## Additional resources
* [Amazon EC2 Auto Scaling User Guide](https://docs.aws.amazon.com/autoscaling/ec2/userguide/what-is-amazon-ec2-auto-scaling.html)
* [Amazon EC2 Auto Scaling API Reference](https://docs.aws.amazon.com/autoscaling/ec2/APIReference)
* [AWS SDK for C++ Documentation](https://docs.aws.amazon.com/sdk-for-cpp/index.html)


Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
