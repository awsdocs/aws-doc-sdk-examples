# Data Firehose code examples for the SDK for Python

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

- [Hello Data Firehose](firehose_hello.py#L8) (`ListDeliveryStreams`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Learn the basics](firehose_wrapper.py)

### Actions
_Actions_ are code excerpts from larger programs and must be run in context. While actions show you how to call individual service functions, you can see actions in context in their related scenarios.

- [CreateDeliveryStream](firehose_wrapper.py#L41)
- [DeleteDeliveryStream](firehose_wrapper.py#L386)
- [DescribeDeliveryStream](firehose_wrapper.py#L87)
- [ListDeliveryStreams](firehose_wrapper.py#L345)
- [ListTagsForDeliveryStream](firehose_wrapper.py#L141)
- [PutRecord](firehose_wrapper.py#L200)
- [PutRecordBatch](firehose_wrapper.py#L236)
- [StartDeliveryStreamEncryption](firehose_wrapper.py#L171)
- [TagDeliveryStream](firehose_wrapper.py#L116)
- [UpdateDestination](firehose_wrapper.py#L293)


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


#### Learn the basics

This example shows you how to learn the basics of Data Firehose by creating a delivery stream and managing it.


<!--custom.scenario_prereqs.firehose_Scenario.start-->
<!--custom.scenario_prereqs.firehose_Scenario.end-->

Start the example by running the following at a command prompt:

```
python firehose_wrapper.py
```


<!--custom.scenarios.firehose_Scenario.start-->
<!--custom.scenarios.firehose_Scenario.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Data Firehose User Guide](https://docs.aws.amazon.com/firehose/latest/dev/what-is-this-service.html)
- [Data Firehose API Reference](https://docs.aws.amazon.com/firehose/latest/APIReference/Welcome.html)
- [SDK for Python Data Firehose reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/firehose.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
