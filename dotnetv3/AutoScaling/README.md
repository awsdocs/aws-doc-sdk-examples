# Amazon EC2 Auto Scaling code examples for the SDK for .NET

## Overview
The code examples in this section show how to use the AWS SDK for .NET with Amazon EC2 Auto Scaling to create and manage Amazon EC2
Auto Scaling groups and instances.

Amazon EC2 Auto Scaling automatically scales EC2 instances, either with scaling
policies or with scheduled scaling.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

* [Create a group](scenarios/AutoScale_Basics/AutoScale_Basics/AutoScaleMethods.cs) (`CreateAutoScalingGroupAsync`)
* [Delete a group](scenarios/AutoScale_Basics/AutoScale_Basics/AutoScaleMethods.cs) (`DeleteAutoScalingGroupAsync`)
* [Disable metrics collection for a group](scenarios/AutoScale_Basics/AutoScale_Basics/AutoScaleMethods.cs) (`DisableMetricsCollectionAsync`)
* [Enable metrics collection for a group](scenarios/AutoScale_Basics/AutoScale_Basics/AutoScaleMethods.cs) (`EnableMetricsCollectionAsync`)
* [Get information about account limits](scenarios/AutoScale_Basics/AutoScale_Basics/AutoScaleMethods.cs) (`DescribeAccountLimitsAsync`)
* [Get information about scaling activities](scenarios/AutoScale_Basics/AutoScale_Basics/AutoScaleMethods.cs) (`DescribeScalingActivitiesAsync`)
* [Get information about instances](scenarios/AutoScale_Basics/AutoScale_Basics/AutoScaleMethods.cs) (`DescribeAutoScalingInstancesAsync`)
* [Get information about groups](scenarios/AutoScale_Basics/AutoScale_Basics/AutoScaleMethods.cs) (`DescribeAutoScalingGroupsAsync`)
* [Set the desired capacity of a group](scenarios/AutoScale_Basics/AutoScale_Basics/AutoScaleMethods.cs) (`SetDesiredCapacityAsync`)
* [Terminate an instance in a group](scenarios/AutoScale_Basics/AutoScale_Basics/AutoScaleMethods.cs) (`TerminateInstanceInAutoScalingGroupAsync`)
* [Update a group](scenarios/AutoScale_Basics/AutoScale_Basics/AutoScaleMethods.cs) (`UpdateAutoScalingGroupAsync`)

### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

* [Auto Scaling basics](scenarios/AutoScale_Basics/AutoScale_Basics/AutoScaleMethods.cs)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

After the example compiles, you can run it from the command line. To do so,
navigate to the folder that contains the .csproj file and run the following
command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Additional resources
* [Amazon EC2 Auto Scaling User Guide](https://docs.aws.amazon.com/autoscaling/ec2/userguide/index.html)
* [Amazon EC2 Auto Scaling API Reference](https://docs.aws.amazon.com/autoscaling/ec2/APIReference/Welcome.html)
* [AWS SDK for .NET Amazon EC2 Auto Scaling](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/AutoScaling/NAutoScaling.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
