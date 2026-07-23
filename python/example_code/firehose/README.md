# Data Firehose code examples for the SDK for Python (Boto3)

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon Data Firehose.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Data Firehose is a fully managed service for delivering real-time streaming data to AWS destinations and third-party HTTP endpoints._

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

- [Hello Data Firehose](firehose_hello.py#L16) (`ListDeliveryStreams`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [PutRecord](firehose_wrapper.py#L230)
- [PutRecordBatch](firehose_wrapper.py#L261)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with delivery streams](firehose_wrapper.py)
- [Put records to Data Firehose](scenarios/firehose-put-actions/firehose.py)

### Actions
_Actions_ are code excerpts from larger programs and must be run in context. While actions show you how to call individual service functions, you can see actions in context in their related scenarios.

- [CreateDeliveryStream](firehose_wrapper.py#L39)
- [DeleteDeliveryStream](firehose_wrapper.py#L328)
- [DescribeDeliveryStream](firehose_wrapper.py#L86)
- [ListDeliveryStreams](firehose_wrapper.py#L115)
- [ListTagsForDeliveryStream](firehose_wrapper.py#L176)
- [StartDeliveryStreamEncryption](firehose_wrapper.py#L201)
- [StopDeliveryStreamEncryption](firehose_wrapper.py#L306)
- [TagDeliveryStream](firehose_wrapper.py#L152)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Data Firehose

This example shows you how to get started using Data Firehose.

```
python firehose_hello.py
```


#### Get started with delivery streams

This example shows you how to learn the basics of Amazon Data Firehose by creating and managing delivery streams.

- Create a Firehose delivery stream with an S3 destination.
- Describe and list delivery streams.
- Tag, list tags, and untag a delivery stream.
- Enable and disable server-side encryption.
- Put single and batch records.
- Delete the delivery stream and clean up resources.

<!--custom.scenario_prereqs.firehose_Scenario.start-->
<!--custom.scenario_prereqs.firehose_Scenario.end-->

Start the example by running the following at a command prompt:

```
python scenarios/firehose_basics_scenario.py
```


<!--custom.scenarios.firehose_Scenario.start-->
<!--custom.scenarios.firehose_Scenario.end-->

#### Put records to Data Firehose

This example shows you how to use Data Firehose to process individual and batch records.


<!--custom.scenario_prereqs.firehose_Scenario_PutRecords.start-->
See required [resource and data setup instructions](../../../scenarios/features/firehose/README.md).
<!--custom.scenario_prereqs.firehose_Scenario_PutRecords.end-->

Start the example by running the following at a command prompt:

```
python scenarios/firehose-put-actions/firehose.py
```


<!--custom.scenarios.firehose_Scenario_PutRecords.start-->
<!--custom.scenarios.firehose_Scenario_PutRecords.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Data Firehose User Guide](https://docs.aws.amazon.com/firehose/latest/dev/what-is-this-service.html)
- [Data Firehose API Reference](https://docs.aws.amazon.com/firehose/latest/APIReference/Welcome.html)
- [SDK for Python (Boto3) Data Firehose reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/firehose.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
