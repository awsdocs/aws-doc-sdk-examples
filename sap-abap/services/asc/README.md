# Auto Scaling code examples for the SDK for SAP ABAP

## Overview

Shows how to use the AWS SDK for SAP ABAP to work with Amazon EC2 Auto Scaling.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `sap-abap` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateAutoScalingGroup](#awsex#cl_asc_actions.clas.abap#L131)
- [DeleteAutoScalingGroup](#awsex#cl_asc_actions.clas.abap#L207)
- [DescribeAutoScalingGroups](#awsex#cl_asc_actions.clas.abap#L231)
- [DescribeAutoScalingInstances](#awsex#cl_asc_actions.clas.abap#L314)
- [DescribeScalingActivities](#awsex#cl_asc_actions.clas.abap#L335)
- [DisableMetricsCollection](#awsex#cl_asc_actions.clas.abap#L378)
- [EnableMetricsCollection](#awsex#cl_asc_actions.clas.abap#L356)
- [SetDesiredCapacity](#awsex#cl_asc_actions.clas.abap#L292)
- [TerminateInstanceInAutoScalingGroup](#awsex#cl_asc_actions.clas.abap#L267)
- [UpdateAutoScalingGroup](#awsex#cl_asc_actions.clas.abap#L183)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `sap-abap` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Auto Scaling User Guide](https://docs.aws.amazon.com/autoscaling/ec2/userguide/what-is-amazon-ec2-auto-scaling.html)
- [Auto Scaling API Reference](https://docs.aws.amazon.com/autoscaling/ec2/APIReference/Welcome.html)
- [SDK for SAP ABAP Auto Scaling reference](https://docs.aws.amazon.com/sdk-for-sap-abap/v1/api/latest/auto-scaling/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
