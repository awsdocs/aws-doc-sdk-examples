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

- [Hello Kinesis](kinesis_hello.py#L20) (`ListStreams`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn Amazon Kinesis basics](scenarios/kinesis_basics_scenario.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateStream](streams/kinesis_stream.py#L46)
- [DeleteStream](streams/kinesis_stream.py#L90)
- [DescribeStream](streams/kinesis_stream.py#L69)
- [DescribeStreamSummary](kinesis_wrapper.py#L286)
- [GetRecords](streams/kinesis_stream.py#L128)
- [ListStreams](kinesis_wrapper.py#L45)
- [PutRecord](streams/kinesis_stream.py#L105)
- [PutRecords](kinesis_wrapper.py#L183)
- [UpdateShardCount](kinesis_wrapper.py#L312)


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
python kinesis_hello.py
```

#### Learn Amazon Kinesis basics

This example shows you how to learn Amazon Kinesis basics.

- List existing data streams.
- Create a provisioned-mode data stream with 2 shards.
- Write a single record and a batch of records.
- Read records back from a shard.
- Describe the stream summary.
- Scale the stream from 2 to 4 shards.
- Delete the stream.

<!--custom.basic_prereqs.kinesis_Scenario.start-->
<!--custom.basic_prereqs.kinesis_Scenario.end-->

Start the example by running the following at a command prompt:

```
python scenarios/kinesis_basics_scenario.py
```


<!--custom.basics.kinesis_Scenario.start-->
<!--custom.basics.kinesis_Scenario.end-->


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
