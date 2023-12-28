# CloudWatch code examples for the SDK for Java 1.x

> NOTE: Examples for this SDK are no longer supported.
> These examples are for historical purposes only, and should not be relied upon.
> Please migrate to the currently supported AWS SDK for this language.

## Overview

Shows how to use the AWS SDK for Java 1.x to work with Amazon CloudWatch.

<!--custom.overview.start-->
<!--custom.overview.end-->

_CloudWatch provides a reliable, scalable, and flexible monitoring solution that you can start using within minutes._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/?aws-products-pricing.sort-by=item.additionalFields.productNameLowercase&aws-products-pricing.sort-order=asc&awsf.Free%20Tier%20Type=*all&awsf.tech-category=*all) and [Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `java` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello CloudWatch](None) (`ListMetrics`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a dashboard](None) (`PutDashboard`)
- [Create a metric alarm](None) (`PutMetricAlarm`)
- [Create an anomaly detector](None) (`PutAnomalyDetector`)
- [Delete alarms](None) (`DeleteAlarms`)
- [Delete an anomaly detector](None) (`DeleteAnomalyDetector`)
- [Delete dashboards](None) (`DeleteDashboards`)
- [Describe alarm history](None) (`DescribeAlarmHistory`)
- [Describe alarms](None) (`DescribeAlarms`)
- [Describe alarms for a metric](None) (`DescribeAlarmsForMetric`)
- [Describe anomaly detectors](None) (`DescribeAnomalyDetectors`)
- [Disable alarm actions](None) (`DisableAlarmActions`)
- [Enable alarm actions](None) (`EnableAlarmActions`)
- [Get a metric data image](None) (`GetMetricWidgetImage`)
- [Get metric data](None) (`GetMetricData`)
- [Get metric statistics](None) (`GetMetricStatistics`)
- [List dashboards](None) (`ListDashboards`)
- [List metrics](None) (`ListMetrics`)
- [Put data into a metric](None) (`PutMetricData`)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello CloudWatch

This example shows you how to get started using CloudWatch.



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `java` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [CloudWatch User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/WhatIsCloudWatch.html)
- [CloudWatch API Reference](https://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/Welcome.html)
- [SDK for Java 1.x CloudWatch reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/cloudwatch/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0