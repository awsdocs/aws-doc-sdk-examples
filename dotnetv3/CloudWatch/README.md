# Amazon CloudWatch code examples for the SDK for .NET

## Overview
The code examples in this section show how to use the AWS SDK for .NET with Amazon CloudWatch (CloudWatch) to manage custom metrics and alarms.

Amazon CloudWatch is a monitoring and observability service built for DevOps engineers, developers, site reliability engineers (SREs), IT managers, and product owners. CloudWatch provides you with data and actionable insights to monitor your applications, respond to system-wide performance changes, and optimize resource utilization.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

* [Delete alarms](DeleteAlarmsExample/DeleteAlarmsExample/DeleteAlarms.cs) (`DeleteAlarmsAsync`)
* [Describe alarm history](DescribeAlarmHistoriesExample/DescribeAlarmHistoriessExample/DescribeAlarmHistories.cs) (`DescribeAlarmsForMetricAsync`)
* [Disable alarm actions](DisableAlarmActionsExample/DisableAlarmActionsExample/DisableAlarmActions.cs) (`DisableAlarmActionsAsync`)
* [Enable alarm actions](EnableAlarmActionsExample/EnableAlarmActionsExample/EnableAlarmActions.cs) (`EnableAlarmActionsAsync`)
* [Get dashboard details](GetDashboardExample/GetDashboardExample/GetDashboard.cs) (`GetDashboardAsync`)
* [List dashboards](ListDashboardsExample/ListDashboardsExample/ListDashboards.cs) (`ListDashboardsAsync`)
* [List metrics](ListMetricsExample/ListMetricsExample/ListMetrics.cs) (`ListMetricsAsync`)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

The examples in this folder use the default user account. The call to
initialize the Amazon CloudWatch client does not specify the AWS Region. Supply
the AWS Region to match your own as a parameter to the client constructor. For
example:

```
IAmazonCloudWatch cwClient = new AmazonCloudWatchClient(Amazon.RegionEndpoint.USWest2);
```

Once the example has been compiled, you can run it from the command line by
first navigating to the folder that contains the .csproj file, and then
issuing the following command:

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
