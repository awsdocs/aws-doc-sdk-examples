# Systems Manager code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with AWS Systems Manager.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Systems Manager organizes, monitors, and automates management tasks on your AWS resources._

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

- [Hello Systems Manager](hello.py#L4) (`listThings`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateDocument](document.py#L32)
- [CreateMaintenanceWindow](maintenance_window.py#L32)
- [CreateOpsItem](ops_item.py#L34)
- [DeleteDocument](document.py#L59)
- [DeleteMaintenanceWindow](maintenance_window.py#L73)
- [DeleteOpsItem](ops_item.py#L73)
- [DescribeOpsItems](ops_item.py#L13)
- [ListCommandInvocations](document.py#L159)
- [SendCommand](document.py#L82)
- [UpdateMaintenanceWindow](maintenance_window.py#L97)
- [UpdateOpsItem](ops_item.py#L126)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with Systems Manager](ssm_getting_started.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Systems Manager

This example shows you how to get started using Systems Manager.

```
python hello.py
```


#### Get started with Systems Manager

This example shows you how to work with Systems Manager maintenance windows, documents, and OpsItems.


<!--custom.scenario_prereqs.ssm_Scenario.start-->
<!--custom.scenario_prereqs.ssm_Scenario.end-->

Start the example by running the following at a command prompt:

```
python ssm_getting_started.py
```


<!--custom.scenarios.ssm_Scenario.start-->
<!--custom.scenarios.ssm_Scenario.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Systems Manager User Guide](https://docs.aws.amazon.com/systems-manager/latest/userguide/what-is-systems-manager.html)
- [Systems Manager API Reference](https://docs.aws.amazon.com/systems-manager/latest/APIReference/Welcome.html)
- [SDK for Python Systems Manager reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/ssm.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0