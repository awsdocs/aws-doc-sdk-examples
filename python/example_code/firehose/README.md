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

### Single actions

Code excerpts that show you how to call individual service functions.

- [PutRecord](scenarios/firehose-put-actions/firehose.py#L33)
- [PutRecordBatch](scenarios/firehose-put-actions/firehose.py#L33)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Put records to Data Firehose](scenarios/firehose-put-actions/firehose.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



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
- [SDK for Python Data Firehose reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/kinesis-firehose.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0