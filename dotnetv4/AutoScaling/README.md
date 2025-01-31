# Auto Scaling code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon EC2 Auto Scaling.

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv4` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Auto Scaling](Actions/HelloAutoScaling.cs#L4) (`DescribeAutoScalingGroups`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](Scenarios/AutoScalingBasics/AutoScalingBasics.cs)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AttachLoadBalancerTargetGroups](../cross-service/ResilientService/AutoScalerActions/AutoScalerWrapper.cs#L890)
- [CreateAutoScalingGroup](Actions/AutoScalingWrapper.cs#L28)
- [DeleteAutoScalingGroup](../cross-service/ResilientService/AutoScalerActions/AutoScalerWrapper.cs#L693)
- [DescribeAutoScalingGroups](Actions/AutoScalingWrapper.cs#L109)
- [DescribeAutoScalingInstances](Actions/AutoScalingWrapper.cs#L109)
- [DescribeScalingActivities](Actions/AutoScalingWrapper.cs#L86)
- [DisableMetricsCollection](Actions/AutoScalingWrapper.cs#L202)
- [EnableMetricsCollection](Actions/AutoScalingWrapper.cs#L223)
- [SetDesiredCapacity](Actions/AutoScalingWrapper.cs#L249)
- [TerminateInstanceInAutoScalingGroup](Actions/AutoScalingWrapper.cs#L275)
- [UpdateAutoScalingGroup](Actions/AutoScalingWrapper.cs#L306)

<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

For general instructions to run the examples, see the
[README](../README.md#building-and-running-the-code-examples) in the `dotnetv4` folder.

Some projects might include a settings.json file. Before compiling the project,
you can change these values to match your own account and resources. Alternatively,
add a settings.local.json file with your local settings, which will be loaded automatically
when the application runs.

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .csproj file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.


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
##### Configuration settings

This example uses several configuration settings that are stored in `settings.json`
to change the existing values.

Note: The image ID, instance type, and Availability Zone must be available in the same AWS Region as the account that's
used to run the scenario. For information about how to get valid settings, see [Find a Linux AMI](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/finding-an-ami.html).

* `GroupName` - The name to use for the Auto Scaling group.
* `ImageId` - The image ID to use with the Amazon EC2 Auto Scaling template. The AMI-ID column of the Amazon EC2 image locator table.
* `InstanceType` - The instance type to use with the template. The Instance Type column of the Amazon EC2 image locator table.
* `LaunchTemplateName` - The name of the launch template.
* `AvailabilityZone` - The Availability Zone for the launch template. The Zone column of the Amazon EC2 image locator table.
* `ServiceLinkedRoleArn` - The Amazon Resource Name (ARN) of a serivce-linked role that will be used
                             to create the Auto Scaling group.

<!--custom.basics.auto-scaling_Scenario_GroupsAndInstances.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv4` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Auto Scaling User Guide](https://docs.aws.amazon.com/autoscaling/ec2/userguide/what-is-amazon-ec2-auto-scaling.html)
- [Auto Scaling API Reference](https://docs.aws.amazon.com/autoscaling/ec2/APIReference/Welcome.html)
- [SDK for .NET Auto Scaling reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/AutoScaling/NAutoScaling.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0