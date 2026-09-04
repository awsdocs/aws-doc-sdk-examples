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

- [DeleteAlarmMuteRule](Actions/CloudWatchOTelWrapper.cs#L288)
- [DeleteAlarms](Actions/CloudWatchWrapper.cs#L402)
- [DeleteAnomalyDetector](Actions/CloudWatchWrapper.cs#L500)
- [DeleteDashboards](Actions/CloudWatchWrapper.cs#L518)
- [DescribeAlarmContributors](Actions/CloudWatchOTelWrapper.cs#L146)
- [DescribeAlarmHistory](Actions/CloudWatchWrapper.cs#L375)
- [DescribeAlarms](Actions/CloudWatchWrapper.cs#L332)
- [DescribeAlarmsForMetric](Actions/CloudWatchWrapper.cs#L355)
- [DescribeAnomalyDetectors](Actions/CloudWatchWrapper.cs#L474)
- [DisableAlarmActions](Actions/CloudWatchWrapper.cs#L420)
- [EnableAlarmActions](Actions/CloudWatchWrapper.cs#L438)
- [GetAlarmMuteRule](Actions/CloudWatchOTelWrapper.cs#L233)
- [GetDashboard](Actions/CloudWatchWrapper.cs#L115)
- [GetMetricData](Actions/CloudWatchWrapper.cs#L226)
- [GetMetricStatistics](Actions/CloudWatchWrapper.cs#L61)
- [GetMetricWidgetImage](Actions/CloudWatchWrapper.cs#L175)
- [GetOTelEnrichment](Actions/CloudWatchOTelWrapper.cs#L62)
- [ListAlarmMuteRules](Actions/CloudWatchOTelWrapper.cs#L253)
- [ListDashboards](Actions/CloudWatchWrapper.cs#L134)
- [ListMetrics](Actions/CloudWatchWrapper.cs#L33)
- [PutAlarmMuteRule](Actions/CloudWatchOTelWrapper.cs#L181)
- [PutAnomalyDetector](Actions/CloudWatchWrapper.cs#L456)
- [PutDashboard](Actions/CloudWatchWrapper.cs#L91)
- [PutMetricAlarm](Actions/CloudWatchOTelWrapper.cs#L94)
- [PutMetricData](Actions/CloudWatchWrapper.cs#L154)
- [StartOTelEnrichment](Actions/CloudWatchOTelWrapper.cs#L41)
- [StopOTelEnrichment](Actions/CloudWatchOTelWrapper.cs#L77)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Send OpenTelemetry metrics and alarm on them with PromQL](Actions/CloudWatchOTelWrapper.cs)


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


#### Send OpenTelemetry metrics and alarm on them with PromQL

This example shows you how to do the following:

- Send OTLP metrics to the CloudWatch metrics endpoint with an OpenTelemetry Collector.
- Start OpenTelemetry enrichment so CloudWatch correlates those metrics with your resources.
- Create an alarm that evaluates a PromQL query across every series the query returns.
- Inspect the individual series, called contributors, that put the alarm in ALARM state.
- Mute the alarm for a maintenance window, then clean up.

<!--custom.scenario_prereqs.cloudwatch_Scenario_OTelMetrics.start-->
<!--custom.scenario_prereqs.cloudwatch_Scenario_OTelMetrics.end-->


<!--custom.scenarios.cloudwatch_Scenario_OTelMetrics.start-->
<!--custom.scenarios.cloudwatch_Scenario_OTelMetrics.end-->

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
