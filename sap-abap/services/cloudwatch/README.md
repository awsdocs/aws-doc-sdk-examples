# CloudWatch code examples for the SDK for SAP ABAP

## Overview

Shows how to use the AWS SDK for SAP ABAP to work with Amazon CloudWatch.

<!--custom.overview.start-->
<!--custom.overview.end-->

_CloudWatch provides a reliable, scalable, and flexible monitoring solution that you can start using within minutes._

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

- [DeleteAlarms](zcl_aws1_cwt_actions.clas.abap#L60)
- [DescribeAlarms](zcl_aws1_cwt_actions.clas.abap#L81)
- [DisableAlarmActions](zcl_aws1_cwt_actions.clas.abap#L103)
- [EnableAlarmActions](zcl_aws1_cwt_actions.clas.abap#L127)
- [ListMetrics](zcl_aws1_cwt_actions.clas.abap#L151)
- [PutMetricAlarm](zcl_aws1_cwt_actions.clas.abap#L174)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with alarms](zcl_aws1_cwt_scenario.clas.abap)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Get started with alarms

This example shows you how to do the following:

- Create an alarm.
- Disable alarm actions.
- Describe an alarm.
- Delete an alarm.

<!--custom.scenario_prereqs.cloudwatch_Scenario_GettingStarted.start-->
<!--custom.scenario_prereqs.cloudwatch_Scenario_GettingStarted.end-->


<!--custom.scenarios.cloudwatch_Scenario_GettingStarted.start-->
<!--custom.scenarios.cloudwatch_Scenario_GettingStarted.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `sap-abap` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [CloudWatch User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/WhatIsCloudWatch.html)
- [CloudWatch API Reference](https://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/Welcome.html)
- [SDK for SAP ABAP CloudWatch reference](https://docs.aws.amazon.com/sdk-for-sap-abap/v1/api/latest/cwt/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0