# DynamoDB code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python to work with Amazon DynamoDB.

<!--custom.overview.start-->
AWS SDK for Python — an async-native, per-service SDK being developed as a successor/complement to Boto3. It is:
  - Async-native — built from the ground up with Python's asyncio support, unlike Boto3 which is synchronous
  - Per-service distribution — instead of one monolithic package, each AWS service is a separate installable package (you only install what you need)
  - Generated from Smithy models — fully typed and code-generated from official AWS service models

  **Note**:  Examples for AWS SDK for Python (Boto3) are in the [python](../python/) directory of this repo.
<!--custom.overview.end-->

_DynamoDB is a fully managed NoSQL database service that provides fast and predictable performance with seamless scalability._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `pythonv1` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
[ToDo: ADD CUSTOM PREREQUISITES]
<!--custom.prerequisites.end-->

### Get started

- [Hello DynamoDB](test.txt#L4) (`ListTables`)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
[TODO: Add customer intructions for running examples]
<!--custom.instructions.end-->

#### Hello DynamoDB

This example shows you how to get started using DynamoDB.

```
python test.txt
```


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `pythonv1` folder.



<!--custom.tests.start-->
[TODO: Add customer instructions for running tests]
<!--custom.tests.end-->

## Additional resources

- [DynamoDB Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html)
- [DynamoDB API Reference](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/Welcome.html)
- [SDK for Python DynamoDB reference](https://docs.aws.amazon.com/sdk-for-python/v1/reference/clients/dynamodb.html)

<!--custom.resources.start-->

[TODO: Add custome resources]
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
