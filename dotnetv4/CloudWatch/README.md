# CloudWatch code examples for the SDK for .NET (v4)

## Overview

Shows how to use the AWS SDK for .NET (v4) to work with Amazon CloudWatch.

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv4` folder.


<!--custom.prerequisites.start-->
To enable billing metrics and statistics for these examples, make sure to
[enable billing alerts](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/monitor_estimated_charges_with_cloudwatch.html#turning_on_billing_metrics) for your account.
<!--custom.prerequisites.end-->

### Get started

- [Hello CloudWatch](Actions/HelloCloudWatch.cs#L4) (`ListMetrics`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](Scenarios/CloudWatchScenario.cs)


### Single actions

Code excerpts that show you how to call individual service functions.

- [DeleteAlarms](Actions/CloudWatchWrapper.cs#L402)
- [DeleteAnomalyDetector](Actions/CloudWatchWrapper.cs#L500)
- [DeleteDashboards](Actions/CloudWatchWrapper.cs#L518)
- [DescribeAlarmHistory](Actions/CloudWatchWrapper.cs#L375)
- [DescribeAlarms](Actions/CloudWatchWrapper.cs#L332)
- [DescribeAlarmsForMetric](Actions/CloudWatchWrapper.cs#L355)
- [DescribeAnomalyDetectors](Actions/CloudWatchWrapper.cs#L474)
- [DisableAlarmActions](Actions/CloudWatchWrapper.cs#L420)
- [EnableAlarmActions](Actions/CloudWatchWrapper.cs#L438)
- [GetDashboard](Actions/CloudWatchWrapper.cs#L115)
- [GetMetricData](Actions/CloudWatchWrapper.cs#L226)
- [GetMetricStatistics](Actions/CloudWatchWrapper.cs#L61)
- [GetMetricWidgetImage](Actions/CloudWatchWrapper.cs#L175)
- [ListDashboards](Actions/CloudWatchWrapper.cs#L134)
- [ListMetrics](Actions/CloudWatchWrapper.cs#L33)
- [PutAnomalyDetector](Actions/CloudWatchWrapper.cs#L456)
- [PutDashboard](Actions/CloudWatchWrapper.cs#L91)
- [PutMetricAlarm](Actions/CloudWatchWrapper.cs#L271)
- [PutMetricData](Actions/CloudWatchWrapper.cs#L154)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello CloudWatch

This example shows you how to get started using CloudWatch.


#### Learn the basics

This example shows you how to do the following:

- List CloudWatch namespaces and metrics.
- Get statistics for a metric and for estimated billing.
- Create and update a dashboard.
- Create and add data to a metric.
- Create and trigger an alarm, then view alarm history.
- Add an anomaly detector.
- Get a metric image, then clean up resources.

<!--custom.basic_prereqs.cloudwatch_GetStartedMetricsDashboardsAlarms.start-->
<!--custom.basic_prereqs.cloudwatch_GetStartedMetricsDashboardsAlarms.end-->


<!--custom.basics.cloudwatch_GetStartedMetricsDashboardsAlarms.start-->
<!--custom.basics.cloudwatch_GetStartedMetricsDashboardsAlarms.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv4` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [CloudWatch User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/WhatIsCloudWatch.html)
- [CloudWatch API Reference](https://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/Welcome.html)
- [SDK for .NET (v4) CloudWatch reference](https://docs.aws.amazon.com/sdkfornet/v4/apidocs/items/Cloudwatch/NCloudwatch.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
