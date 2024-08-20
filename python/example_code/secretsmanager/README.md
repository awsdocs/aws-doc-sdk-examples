# Secrets Manager code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with AWS Secrets Manager.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Secrets Manager helps you to securely encrypt, store, and retrieve credentials for your databases and other services._

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

- [BatchGetSecretValue](batch_get_secret_value.py#L17)
- [GetSecretValue](get_secret_value.py#L16)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a lending library REST API](../../cross_service/aurora_rest_lending_library)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
To use these examples, first deploy the secrets to AWS using [this CDK stack](../../../resources/cdk/secrets-manager).

Next, after pulling AWS credentials, run one of the following commands:
* `python3 scenario_get_secret.py`
* `python3 scenario_get_batch_secrets.py`
<!--custom.instructions.end-->



#### Create a lending library REST API

This example shows you how to create a lending library where patrons can borrow and return books by using a REST API backed by an Amazon Aurora database.


<!--custom.scenario_prereqs.cross_AuroraRestLendingLibrary.start-->
<!--custom.scenario_prereqs.cross_AuroraRestLendingLibrary.end-->


<!--custom.scenarios.cross_AuroraRestLendingLibrary.start-->
<!--custom.scenarios.cross_AuroraRestLendingLibrary.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Secrets Manager User Guide](https://docs.aws.amazon.com/secretsmanager/latest/userguide/intro.html)
- [Secrets Manager API Reference](https://docs.aws.amazon.com/secretsmanager/latest/apireference/Welcome.html)
- [SDK for Python Secrets Manager reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/secretsmanager.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0