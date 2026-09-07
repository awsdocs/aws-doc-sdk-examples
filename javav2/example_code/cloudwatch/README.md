# CloudWatch code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon CloudWatch.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello CloudWatch](src/main/java/com/example/cloudwatch/HelloService.java#L6) (`ListMetrics`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/java/com/example/cloudwatch/scenario/CloudWatchScenario.java)


### Single actions

Code excerpts that show you how to call individual service functions.

- [DeleteAlarmMuteRule](src/main/java/com/example/cloudwatch/otel/CloudWatchOTelActions.java#L352)
- [DeleteAlarms](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L185)
- [DeleteAnomalyDetector](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L143)
- [DeleteDashboards](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L210)
- [DescribeAlarmContributors](src/main/java/com/example/cloudwatch/otel/CloudWatchOTelActions.java#L184)
- [DescribeAlarmHistory](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L388)
- [DescribeAlarms](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L674)
- [DescribeAlarmsForMetric](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L449)
- [DescribeAnomalyDetectors](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L292)
- [DisableAlarmActions](src/main/java/com/example/cloudwatch/DisableAlarmActions.java#L6)
- [EnableAlarmActions](src/main/java/com/example/cloudwatch/EnableAlarmActions.java#L6)
- [GetAlarmMuteRule](src/main/java/com/example/cloudwatch/otel/CloudWatchOTelActions.java#L287)
- [GetMetricData](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L595)
- [GetMetricStatistics](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L956)
- [GetMetricWidgetImage](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L234)
- [GetOTelEnrichment](src/main/java/com/example/cloudwatch/otel/CloudWatchOTelActions.java#L80)
- [ListAlarmMuteRules](src/main/java/com/example/cloudwatch/otel/CloudWatchOTelActions.java#L314)
- [ListDashboards](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L844)
- [ListMetrics](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L1006)
- [PutAlarmMuteRule](src/main/java/com/example/cloudwatch/otel/CloudWatchOTelActions.java#L231)
- [PutAnomalyDetector](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L341)
- [PutDashboard](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L866)
- [PutMetricAlarm](src/main/java/com/example/cloudwatch/otel/CloudWatchOTelActions.java#L124)
- [PutMetricData](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L526)
- [StartOTelEnrichment](src/main/java/com/example/cloudwatch/otel/CloudWatchOTelActions.java#L55)
- [StopOTelEnrichment](src/main/java/com/example/cloudwatch/otel/CloudWatchOTelActions.java#L103)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Send OpenTelemetry metrics and alarm on them with PromQL](src/main/java/com/example/cloudwatch/otel/CloudWatchOTelActions.java)


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
- Start OpenTelemetry enrichment so CloudWatch correlates incoming OTLP metrics with the resources that produced them.
- See how OTLP metrics reach the CloudWatch metrics endpoint. Metric ingestion over OTLP is not an AWS SDK operation.
- Create an alarm that evaluates a PromQL query.
- Inspect the alarm's contributors, the individual series that the query matched.
- Get statistics for a metric and chart it on a dashboard.
- Mute the alarm for a maintenance window, then clean up.

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


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [CloudWatch User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/WhatIsCloudWatch.html)
- [CloudWatch API Reference](https://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/Welcome.html)
- [SDK for Java 2.x CloudWatch reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/cloudwatch/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
