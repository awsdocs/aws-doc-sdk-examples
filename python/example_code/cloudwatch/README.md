# CloudWatch code examples for the SDK for Python (Boto3)

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon CloudWatch.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [DeleteAlarmMuteRule](cloudwatch_otel.py#L323)
- [DeleteAlarms](cloudwatch_basics.py#L264)
- [DescribeAlarmContributors](cloudwatch_otel.py#L180)
- [DescribeAlarmsForMetric](cloudwatch_basics.py#L216)
- [DisableAlarmActions](cloudwatch_basics.py#L232)
- [EnableAlarmActions](cloudwatch_basics.py#L232)
- [GetAlarmMuteRule](cloudwatch_otel.py#L265)
- [GetMetricStatistics](cloudwatch_basics.py#L123)
- [GetOTelEnrichment](cloudwatch_otel.py#L79)
- [ListAlarmMuteRules](cloudwatch_otel.py#L287)
- [ListMetrics](cloudwatch_basics.py#L37)
- [PutAlarmMuteRule](cloudwatch_otel.py#L216)
- [PutMetricAlarm](cloudwatch_otel.py#L114)
- [PutMetricData](cloudwatch_basics.py#L64)
- [StartOTelEnrichment](cloudwatch_otel.py#L57)
- [StopOTelEnrichment](cloudwatch_otel.py#L98)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Manage custom metrics and alarms](cloudwatch_basics.py)
- [Send OpenTelemetry metrics and alarm on them with PromQL](cloudwatch_otel.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Manage custom metrics and alarms

This example shows you how to do the following:

- Create an alarm to watch a single CloudWatch metric.
- Put data into the metric with <code>PutMetricData</code> and trigger the alarm.
- Get data from the alarm.
- Delete the alarm.

<!--custom.scenario_prereqs.cloudwatch_Usage_MetricsAlarms.start-->
<!--custom.scenario_prereqs.cloudwatch_Usage_MetricsAlarms.end-->

Start the example by running the following at a command prompt:

```
python cloudwatch_basics.py
```


<!--custom.scenarios.cloudwatch_Usage_MetricsAlarms.start-->
<!--custom.scenarios.cloudwatch_Usage_MetricsAlarms.end-->

#### Send OpenTelemetry metrics and alarm on them with PromQL

This example shows you how to do the following:

- Send OTLP metrics to the CloudWatch metrics endpoint with an OpenTelemetry Collector.
- Start OpenTelemetry enrichment so CloudWatch correlates those metrics with your resources.
- Create an alarm that evaluates a PromQL query across every series the query returns.
- Inspect the individual series, called contributors, that put the alarm in ALARM state.
- Mute the alarm for a maintenance window, then clean up.

<!--custom.scenario_prereqs.cloudwatch_Scenario_OTelMetrics.start-->
<!--custom.scenario_prereqs.cloudwatch_Scenario_OTelMetrics.end-->

Start the example by running the following at a command prompt:

```
python cloudwatch_otel.py
```


<!--custom.scenarios.cloudwatch_Scenario_OTelMetrics.start-->
<!--custom.scenarios.cloudwatch_Scenario_OTelMetrics.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [CloudWatch User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/WhatIsCloudWatch.html)
- [CloudWatch API Reference](https://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/Welcome.html)
- [SDK for Python (Boto3) CloudWatch reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/cloudwatch.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
