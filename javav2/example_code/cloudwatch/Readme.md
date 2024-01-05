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

- [Hello CloudWatch](src/main/java/com/example/cloudwatch/HelloService.java#L12) (`ListMetrics`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a dashboard](src/main/java/com/example/cloudwatch/CloudWatchScenario.java#L775) (`PutDashboard`)
- [Create a metric alarm](src/main/java/com/example/cloudwatch/CloudWatchScenario.java#L661) (`PutMetricAlarm`)
- [Create an anomaly detector](src/main/java/com/example/cloudwatch/CloudWatchScenario.java#L433) (`PutAnomalyDetector`)
- [Delete alarms](src/main/java/com/example/cloudwatch/DeleteAlarm.java#L10) (`DeleteAlarms`)
- [Delete an anomaly detector](src/main/java/com/example/cloudwatch/CloudWatchScenario.java#L301) (`DeleteAnomalyDetector`)
- [Delete dashboards](src/main/java/com/example/cloudwatch/CloudWatchScenario.java#L349) (`DeleteDashboards`)
- [Describe alarm history](src/main/java/com/example/cloudwatch/CloudWatchScenario.java#L462) (`DescribeAlarmHistory`)
- [Describe alarms](src/main/java/com/example/cloudwatch/CloudWatchScenario.java#L637) (`DescribeAlarms`)
- [Describe alarms for a metric](src/main/java/com/example/cloudwatch/CloudWatchScenario.java#L497) (`DescribeAlarmsForMetric`)
- [Describe anomaly detectors](src/main/java/com/example/cloudwatch/CloudWatchScenario.java#L405) (`DescribeAnomalyDetectors`)
- [Disable alarm actions](src/main/java/com/example/cloudwatch/DisableAlarmActions.java#L10) (`DisableAlarmActions`)
- [Enable alarm actions](src/main/java/com/example/cloudwatch/EnableAlarmActions.java#L10) (`EnableAlarmActions`)
- [Get a metric data image](src/main/java/com/example/cloudwatch/CloudWatchScenario.java#L365) (`GetMetricWidgetImage`)
- [Get metric data](src/main/java/com/example/cloudwatch/CloudWatchScenario.java#L578) (`GetMetricData`)
- [Get metric statistics](src/main/java/com/example/cloudwatch/CloudWatchScenario.java#L844) (`GetMetricStatistics`)
- [List dashboards](src/main/java/com/example/cloudwatch/CloudWatchScenario.java#L757) (`ListDashboards`)
- [List metrics](src/main/java/com/example/cloudwatch/ListMetrics.java#L11) (`ListMetrics`)
- [Put data into a metric](src/main/java/com/example/cloudwatch/CloudWatchScenario.java#L532) (`PutMetricData`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with metrics, dashboards, and alarms](src/main/java/com/example/cloudwatch/CloudWatchScenario.java)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello CloudWatch

This example shows you how to get started using CloudWatch.



#### Get started with metrics, dashboards, and alarms

This example shows you how to do the following:

- List CloudWatch namespaces and metrics.
- Get statistics for a metric and for estimated billing.
- Create and update a dashboard.
- Create and add data to a metric.
- Create and trigger an alarm, then view alarm history.
- Add an anomaly detector.
- Get a metric image, then clean up resources.

<!--custom.scenario_prereqs.cloudwatch_GetStartedMetricsDashboardsAlarms.start-->
<!--custom.scenario_prereqs.cloudwatch_GetStartedMetricsDashboardsAlarms.end-->


<!--custom.scenarios.cloudwatch_GetStartedMetricsDashboardsAlarms.start-->
<!--custom.scenarios.cloudwatch_GetStartedMetricsDashboardsAlarms.end-->

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