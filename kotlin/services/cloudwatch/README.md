# CloudWatch code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with Amazon CloudWatch.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `kotlin` folder.


<!--custom.prerequisites.start-->

To run these examples, you must have the following three JSON files: jsonWidgets.json, CloudDashboard.json, and settings.json. Find these files in this GitHub repository. The CloudWatch scenario depends on these files. In addition, to enable billing metrics and statistics for the scenario example, make sure billing alerts are enabled for your account. For more information, see [Enabling billing alerts](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/monitor_estimated_charges_with_cloudwatch.html#turning_on_billing_metrics).

**Be very careful** when running an operation that deletes or modifies AWS resources in your account. We recommend creating separate test-only resources when experimenting with these examples.

<!--custom.prerequisites.end-->

### Get started

- [Hello CloudWatch](src/main/kotlin/com/kotlin/cloudwatch/HelloService.kt#L11) (`ListMetrics`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt)


### Single actions

Code excerpts that show you how to call individual service functions.

- [DeleteAlarms](src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt#L299)
- [DeleteAnomalyDetector](src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt#L272)
- [DeleteDashboards](src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt#L313)
- [DescribeAlarmHistory](src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt#L413)
- [DescribeAlarms](src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt#L602)
- [DescribeAlarmsForMetric](src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt#L457)
- [DescribeAnomalyDetectors](src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt#L362)
- [DisableAlarmActions](src/main/kotlin/com/kotlin/cloudwatch/DisableAlarmActions.kt#L39)
- [EnableAlarmActions](src/main/kotlin/com/kotlin/cloudwatch/EnableAlarmActions.kt#L38)
- [GetMetricData](src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt#L539)
- [GetMetricStatistics](src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt#L801)
- [GetMetricWidgetImage](src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt#L326)
- [ListDashboards](src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt#L714)
- [ListMetrics](src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt#L842)
- [PutAnomalyDetector](src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt#L386)
- [PutDashboard](src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt#L728)
- [PutMetricAlarm](src/main/kotlin/com/kotlin/cloudwatch/PutMetricAlarm.kt#L44)
- [PutMetricData](src/main/kotlin/com/kotlin/cloudwatch/CloudWatchScenario.kt#L491)


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
in the `kotlin` folder.



<!--custom.tests.start-->

You can test the Kotlin code example for Amazon CloudWatch by running a test file named **CloudWatchTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

To successfully run the tests, define the following values in the test:

- **logGroup** - The name of the log group to use. For example, **testgroup**.
- **alarmName** – The name of the alarm to use. For example, **AlarmFeb**.
- **instanceId** – The ID of the instance to use. Get this value from the AWS Management Console. For example, **ami-04300000000**.
- **streamName** - The name of the stream to use. This value is used to retrieve log events.
- **ruleResource** – The Amazon Resource Name (ARN) of the user who owns the rule. Get this value from the AWS Management Console.
- **filterName** - The name of the filter to use.
- **destinationArn** - The ARN of the destination. This value is used to create subscription filters.
- **roleArn** - The ARN of the user. This value is used to create subscription filters.
- **filterPattern** - The filter pattern. For example, **Error**.
- **myDateSc** - The start date to use to get metric statistics in the scenario test. (For example, 2023-01-11T18:35:24.00Z.)
- **costDateWeekSc** - The start date to use to get AWS billing statistics. (For example, 2023-01-11T18:35:24.00Z.)
- **dashboardNameSc** - The name of the dashboard to create in the scenario test.
- **dashboardJsonSc** - The location of the jsonWidgets file to use to create a dashboard.
- **dashboardAddSc** - The location of the CloudDashboard.json file to use to update a dashboard. (See Readme file.)
- **settingsSc** - The location of the settings.json file from which various values are read. (See Readme file.)
- **metricImageSc** - The location of a BMP file that is used to create a graph.

You can run the JUnit tests from an IDE, such as IntelliJ, or from the command line. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

    Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real AWS resources and might incur charges on your account._

<!--custom.tests.end-->

## Additional resources

- [CloudWatch User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/WhatIsCloudWatch.html)
- [CloudWatch API Reference](https://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/Welcome.html)
- [SDK for Kotlin CloudWatch reference](https://sdk.amazonaws.com/kotlin/api/latest/cloudwatch/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0