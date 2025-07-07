# AWS Control Tower code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with AWS Control Tower.

<!--custom.overview.start-->
<!--custom.overview.end-->

_AWS Control Tower enables you to enforce and manage governance rules for security, operations, and compliance at scale across all your organizations and accounts._

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
Before running the example, set up a landing zone in order to run the baseline and control management sections.
Follow the instructions provided by the [quick start](https://docs.aws.amazon.com/controltower/latest/userguide/quick-start.html) guide.
<!--custom.prerequisites.end-->

### Get started

- [Hello AWS Control Tower](hello/hello_controltower.py#L4) (`ListBaselines`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenario_controltower.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [DisableBaseline](controltower_wrapper.py#L392)
- [DisableControl](controltower_wrapper.py#L263)
- [EnableBaseline](controltower_wrapper.py#L69)
- [EnableControl](controltower_wrapper.py#L159)
- [GetControlOperation](controltower_wrapper.py#L209)
- [ListBaselines](controltower_wrapper.py#L39)
- [ListEnabledBaselines](controltower_wrapper.py#L330)
- [ListEnabledControls](controltower_wrapper.py#L431)
- [ListLandingZones](controltower_wrapper.py#L300)
- [ResetEnabledBaseline](controltower_wrapper.py#L358)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS Control Tower

This example shows you how to get started using AWS Control Tower.

```
python hello/hello_controltower.py
```

#### Learn the basics

This example shows you how to do the following:

- List landing zones.
- List, enable, get, reset, and disable baselines.
- List, enable, get, and disable controls.

<!--custom.basic_prereqs.controltower_Scenario.start-->
<!--custom.basic_prereqs.controltower_Scenario.end-->

Start the example by running the following at a command prompt:

```
python scenario_controltower.py
```


<!--custom.basics.controltower_Scenario.start-->
<!--custom.basics.controltower_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS Control Tower User Guide](https://docs.aws.amazon.com/controltower/latest/userguide/what-is-control-tower.html)
- [AWS Control Tower API Reference](https://docs.aws.amazon.com/controltower/latest/APIReference/Welcome.html)
- [SDK for Python AWS Control Tower reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/cognito-idp.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
