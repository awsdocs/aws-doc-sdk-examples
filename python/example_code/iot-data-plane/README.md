# AWS IoT data code examples for the SDK for Python (Boto3)

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with AWS IoT data.

<!--custom.overview.start-->
<!--custom.overview.end-->

_AWS IoT data _

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

- [Hello AWS IoT data](iot_data_plane_hello.py#L22) (`GetThingShadow`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn IoT Data Plane core operations](scenarios/iot-data-plane_basics_scenario.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [DeleteThingShadow](iot_data_plane_wrapper.py#L192)
- [GetRetainedMessage](iot_data_plane_wrapper.py#L324)
- [GetThingShadow](../iot/iot_wrapper.py#L428)
- [ListNamedShadowsForThing](iot_data_plane_wrapper.py#L141)
- [ListRetainedMessages](iot_data_plane_wrapper.py#L283)
- [Publish](iot_data_plane_wrapper.py#L236)
- [UpdateThingShadow](../iot/iot_wrapper.py#L401)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS IoT data

This example shows you how to get started using AWS IoT data.

```
python iot_data_plane_hello.py
```

#### Learn IoT Data Plane core operations

This example shows you how to learn core operations of the IoT Data Plane.

- Create and manage classic (unnamed) and named device shadows.
- Publish MQTT messages with the retain flag.
- List and retrieve retained messages.
- Delete shadows and clean up resources.

<!--custom.basic_prereqs.iot-data-plane_Scenario.start-->
<!--custom.basic_prereqs.iot-data-plane_Scenario.end-->

Start the example by running the following at a command prompt:

```
python scenarios/iot-data-plane_basics_scenario.py
```


<!--custom.basics.iot-data-plane_Scenario.start-->
<!--custom.basics.iot-data-plane_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS IoT data Developer Guide](https://docs.aws.amazon.com/iot/latest/developerguide/what-is-aws-iot.html)
- [AWS IoT data API Reference](https://docs.aws.amazon.com/iot/latest/apireference/Welcome.html)
- [SDK for Python (Boto3) AWS IoT data reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/iot-data-plane.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
