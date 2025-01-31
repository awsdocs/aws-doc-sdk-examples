# Managed Service for Apache Flink code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Managed Service for Apache Flink.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Managed Service for Apache Flink processes and analyzes streaming data using SQL or Java._

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

- [AddApplicationInput](analytics_application.py#L257)
- [AddApplicationOutput](analytics_application.py#L294)
- [CreateApplication](analytics_application.py#L129)
- [DeleteApplication](analytics_application.py#L158)
- [DescribeApplication](analytics_application.py#L174)
- [DescribeApplicationSnapshot](analytics_application.py#L195)
- [DiscoverInputSchema](analytics_application.py#L226)
- [StartApplication](analytics_application.py#L365)
- [StopApplication](analytics_application.py#L394)
- [UpdateApplication](analytics_application.py#L332)

### Data generator

- [Generate a stream with a referrer](../kinesis/streams/dg_referrer.py)
- [Generate a stream with blood pressure anomalies](../kinesis/streams/dg_anomalyex.py)
- [Generate a stream with data in columns](../kinesis/streams/dg_columnlog.py)
- [Generate a stream with heart rate anomalies](../kinesis/streams/dg_anomaly.py)
- [Generate a stream with hotspots](../kinesis/streams/dg_hotspots.py)
- [Generate a stream with log entries](../kinesis/streams/dg_regexlog.py)
- [Generate a stream with stagger data](../kinesis/streams/dg_stagger.py)
- [Generate a stream with stock ticker data](../kinesis/streams/dg_stockticker.py)
- [Generate a stream with two data types](../kinesis/streams/dg_tworecordtypes.py)
- [Generate a stream with web log data](../kinesis/streams/dg_weblog.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
The action examples in this section are demonstrated as part of a scenario that reads 
data from an input stream, uses SQL code to transform the data, and writes it to an 
output stream. Run the scenario at a command prompt with the following command:

```
python kinesisanalyticsv2_demo.py
``` 
<!--custom.instructions.end-->



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Managed Service for Apache Flink Developer Guide](https://docs.aws.amazon.com/managed-flink/latest/java/what-is.html)
- [Managed Service for Apache Flink API Reference](https://docs.aws.amazon.com/managed-flink/latest/apiv2/Welcome.html)
- [SDK for Python Managed Service for Apache Flink reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/kinesisanalyticsv2.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0