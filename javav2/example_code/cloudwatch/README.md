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

- [DeleteAlarms](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L169)
- [DeleteAnomalyDetector](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L127)
- [DeleteDashboards](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L194)
- [DescribeAlarmHistory](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L372)
- [DescribeAlarms](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L658)
- [DescribeAlarmsForMetric](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L433)
- [DescribeAnomalyDetectors](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L276)
- [DisableAlarmActions](src/main/java/com/example/cloudwatch/DisableAlarmActions.java#L6)
- [EnableAlarmActions](src/main/java/com/example/cloudwatch/EnableAlarmActions.java#L6)
- [GetMetricData](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L579)
- [GetMetricStatistics](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L940)
- [GetMetricWidgetImage](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L218)
- [ListDashboards](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L828)
- [ListMetrics](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L990)
- [PutAnomalyDetector](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L325)
- [PutDashboard](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L850)
- [PutMetricAlarm](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L693)
- [PutMetricData](src/main/java/com/example/cloudwatch/scenario/CloudWatchActions.java#L510)


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