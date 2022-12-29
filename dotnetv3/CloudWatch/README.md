# CloudWatch code examples for the SDK for .NET

## Overview
The code examples in this section show how to use the AWS SDK for .NET with Amazon CloudWatch to manage custom metrics and alarms.

Amazon CloudWatch is a monitoring and observability service built for DevOps engineers, developers, site reliability engineers (SREs), IT managers, and product owners. CloudWatch provides you with data and actionable insights to monitor your applications, respond to system-wide performance changes, and optimize resource utilization.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Get started

* [Hello CloudWatch](Actions/HelloCloudWatch.cs)

### Single actions
Code excerpts that show you how to call individual service functions.

* [Delete alarms](Actions/CloudWatchWrapper.cs) (`DeleteAlarmsAsync`)
* [Delete anomaly detector](Actions/CloudWatchWrapper.cs) (`DeleteAnomalyDetectorAsync`)
* [Delete dashboards](Actions/CloudWatchWrapper.cs) (`DeleteDashboardsAsync`)
* [Describe alarm history](Actions/CloudWatchWrapper.cs) (`DescribeAlarmHistoryAsync`)
* [Describe alarms](Actions/CloudWatchWrapper.cs) (`DescribeAlarmsAsync`)
* [Describe alarms for a metric](Actions/CloudWatchWrapper.cs) (`DescribeAlarmsForMetricAsync`)
* [Describe anomaly detectors](Actions/CloudWatchWrapper.cs) (`DescribeAnomalyDetectorsAsync`)
* [Disable alarm actions](Actions/CloudWatchWrapper.cs) (`DisableAlarmActionsAsync`)
* [Enable alarm actions](Actions/CloudWatchWrapper.cs) (`EnableAlarmActionsAsync`)
* [Get dashboard details](Actions/CloudWatchWrapper.cs) (`GetDashboardAsync`)
* [Get metric data](Actions/CloudWatchWrapper.cs) (`GetMetricDataAsync`)
* [Get metric statistics](Actions/CloudWatchWrapper.cs) (`GetMetricStatisticsAsync`)
* [Get metric data image](Actions/CloudWatchWrapper.cs) (`GetMetricWidgetImageAsync`)
* [List dashboards](Actions/CloudWatchWrapper.cs) (`ListDashboardsAsync`)
* [List metrics](Actions/CloudWatchWrapper.cs) (`ListMetricsAsync`)
* [Create anomaly detector](Actions/CloudWatchWrapper.cs) (`PutAnomalyDetectorAsync`)
* [Create dashboard](Actions/CloudWatchWrapper.cs) (`PutDashboardAsync`)
* [Create an alarm that watches a metric](Actions/CloudWatchWrapper.cs) (`PutMetricAlarmAsync`)
* [Put data into a metric](Actions/CloudWatchWrapper.cs) (`PutMetricDataAsync`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling
multiple functions within the same service.

* [Get started with billing, alarms, and metrics](Scenarios/CloudWatchScenario.cs)

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
* [Amazon Cloudwatch User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/index.html)
* [Amazon CloudWatch API Reference](https://docs.aws.amazon.com/AmazonCloudWatchLogs/latest/APIReference/Welcome.html)
* [AWS SDK for .NET Amazon CloudWatch](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/CloudWatch/NCloudWatch.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
