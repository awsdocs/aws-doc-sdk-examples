# Kinesis code examples for the SDK for Python (Boto3)

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Kinesis.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Kinesis makes it easy to collect, process, and analyze video and data streams in real time._

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

### Get started

- [Hello Kinesis](hello/hello_kinesis.py#L16) (`ListStreams`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with streams](kinesis_wrapper.py)
- [Process events with Lambda](kinesis_lambda_handler.py)
- [Report batch item failures](kinesis_lambda_handler.py)

### Actions
_Actions_ are code excerpts from larger programs and must be run in context. While actions show you how to call individual service functions, you can see actions in context in their related scenarios.

- [AddTagsToStream](kinesis_wrapper.py#L266)
- [CreateStream](kinesis_wrapper.py#L39)
- [DeleteStream](kinesis_wrapper.py#L306)
- [DescribeStream](kinesis_wrapper.py#L63)
- [GetRecords](kinesis_wrapper.py#L238)
- [GetShardIterator](kinesis_wrapper.py#L205)
- [ListShards](kinesis_wrapper.py#L177)
- [ListTagsForStream](kinesis_wrapper.py#L285)
- [PutRecord](kinesis_wrapper.py#L88)
- [PutRecords](kinesis_wrapper.py#L129)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
The action examples in this section are demonstrated as part of an Amazon Managed Service 
for Apache Flink scenario that reads data from an input stream, uses SQL code to transform 
the data, and writes it to an output stream. Run the scenario at a command prompt in 
the `kinesis-analytics-v2` folder with the following command:

```
python kinesisanalyticsv2_demo.py
``` 
<!--custom.instructions.end-->

#### Hello Kinesis

This example shows you how to get started using Kinesis.

```
python hello/hello_kinesis.py
```


#### Get started with streams

This example shows you how to learn the basics of Kinesis by creating a stream, producing and consuming records, and managing stream resources.


<!--custom.scenario_prereqs.kinesis_Scenario.start-->
<!--custom.scenario_prereqs.kinesis_Scenario.end-->

Start the example by running the following at a command prompt:

```
python kinesis_wrapper.py
```


<!--custom.scenarios.kinesis_Scenario.start-->
<!--custom.scenarios.kinesis_Scenario.end-->

#### Process events with Lambda

This example shows you how to process Kinesis events with a Lambda function.


<!--custom.scenario_prereqs.kinesis_serverless_Lambda.start-->
<!--custom.scenario_prereqs.kinesis_serverless_Lambda.end-->

Start the example by running the following at a command prompt:

```
python kinesis_lambda_handler.py
```


<!--custom.scenarios.kinesis_serverless_Lambda.start-->
<!--custom.scenarios.kinesis_serverless_Lambda.end-->

#### Report batch item failures

This example shows you how to report batch item failures for Kinesis with a Lambda function.


<!--custom.scenario_prereqs.kinesis_serverless_Lambda_batch_item_failures.start-->
<!--custom.scenario_prereqs.kinesis_serverless_Lambda_batch_item_failures.end-->

Start the example by running the following at a command prompt:

```
python kinesis_lambda_handler.py
```


<!--custom.scenarios.kinesis_serverless_Lambda_batch_item_failures.start-->
<!--custom.scenarios.kinesis_serverless_Lambda_batch_item_failures.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Kinesis Developer Guide](https://docs.aws.amazon.com/streams/latest/dev/introduction.html)
- [Kinesis API Reference](https://docs.aws.amazon.com/kinesis/latest/APIReference/Welcome.html)
- [SDK for Python (Boto3) Kinesis reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/kinesis.html)

<!--custom.resources.start-->
* [SDK for Python Kinesis Data Analytics reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/kinesisanalyticsv2.html)
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
