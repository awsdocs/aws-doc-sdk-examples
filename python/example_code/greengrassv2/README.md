# AWS IoT Greengrass V2 code examples for the SDK for Python (Boto3)

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with AWS IoT Greengrass V2.

<!--custom.overview.start-->
<!--custom.overview.end-->

_AWS IoT Greengrass V2 _

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

- [Hello AWS IoT Greengrass V2](greengrassv2_hello.py#L20) (`ListCoreDevices`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn AWS IoT Greengrass V2 basics](scenarios/greengrassv2_basics_scenario.py)

### Actions
_Actions_ are code excerpts from larger programs and must be run in context. While actions show you how to call individual service functions, you can see actions in context in their related scenarios.

- [CancelDeployment](greengrassv2_wrapper.py#L359)
- [CreateComponentVersion](greengrassv2_wrapper.py#L99)
- [CreateDeployment](greengrassv2_wrapper.py#L255)
- [DeleteComponent](greengrassv2_wrapper.py#L382)
- [DescribeComponent](greengrassv2_wrapper.py#L191)
- [GetComponent](greengrassv2_wrapper.py#L163)
- [GetDeployment](greengrassv2_wrapper.py#L297)
- [ListComponentVersions](greengrassv2_wrapper.py#L134)
- [ListCoreDevices](greengrassv2_wrapper.py#L69)
- [ListDeployments](greengrassv2_wrapper.py#L323)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS IoT Greengrass V2

This example shows you how to get started using AWS IoT Greengrass V2.

```
python greengrassv2_hello.py
```

#### Learn AWS IoT Greengrass V2 basics

This example shows you how to learn core operations of AWS IoT Greengrass V2 using an AWS SDK.

- List Greengrass core devices.
- Create and version custom components from inline recipes.
- List component versions and retrieve component recipes.
- Describe component metadata.
- Deploy components to a thing group with configuration overrides.
- Get deployment details and list deployments.
- Cancel deployments.
- Clean up component versions and resources.

<!--custom.basic_prereqs.greengrassv2_Scenario.start-->
<!--custom.basic_prereqs.greengrassv2_Scenario.end-->

Start the example by running the following at a command prompt:

```
python scenarios/greengrassv2_basics_scenario.py
```


<!--custom.basics.greengrassv2_Scenario.start-->
<!--custom.basics.greengrassv2_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS IoT Greengrass V2 Developer Guide](https://docs.aws.amazon.com/greengrass/v2/developerguide/what-is-iot-greengrass.html)
- [AWS IoT Greengrass V2 API Reference](https://docs.aws.amazon.com/greengrass/v2/APIReference/Welcome.html)
- [SDK for Python (Boto3) AWS IoT Greengrass V2 reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/greengrassv2.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
