# AWS IoT code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with AWS IoT.

<!--custom.overview.start-->
<!--custom.overview.end-->

_AWS IoT provides secure, bi-directional communication for Internet-connected devices (such as sensors, actuators, embedded devices, wireless devices, and smart appliances) to connect to the AWS Cloud over MQTT, HTTPS, and LoRaWAN._

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

- [Hello AWS IoT](iot_hello.py#L14) (`listThings`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](iot_wrapper.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AttachThingPrincipal](iot_wrapper.py#L119)
- [CreateKeysAndCertificate](iot_wrapper.py#L94)
- [CreateThing](iot_wrapper.py#L39)
- [CreateTopicRule](iot_wrapper.py#L250)
- [DeleteCertificate](iot_wrapper.py#L224)
- [DeleteThing](iot_wrapper.py#L358)
- [DeleteTopicRule](iot_wrapper.py#L381)
- [DescribeEndpoint](iot_wrapper.py#L145)
- [DetachThingPrincipal](iot_wrapper.py#L198)
- [ListCertificates](iot_wrapper.py#L171)
- [ListThings](iot_wrapper.py#L66)
- [SearchIndex](iot_wrapper.py#L312)
- [UpdateIndexingConfiguration](iot_wrapper.py#L338)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS IoT

This example shows you how to get started using AWS IoT.

```
python iot_hello.py
```

#### Learn the basics

This example shows you how to do the following:

- Create an AWS IoT Thing.
- Generate a device certificate.
- Update an AWS IoT Thing with Attributes.
- Return a unique endpoint.
- List your AWS IoT certificates.
- Update an AWS IoT shadow.
- Write out state information.
- Creates a rule.
- List your rules.
- Search things using the Thing name.
- Delete an AWS IoT Thing.

<!--custom.basic_prereqs.iot_Scenario.start-->
<!--custom.basic_prereqs.iot_Scenario.end-->

Start the example by running the following at a command prompt:

```
python iot_wrapper.py
```


<!--custom.basics.iot_Scenario.start-->
<!--custom.basics.iot_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS IoT Developer Guide](https://docs.aws.amazon.com/iot/latest/developerguide/what-is-aws-iot.html)
- [AWS IoT API Reference](https://docs.aws.amazon.com/iot/latest/apireference/Welcome.html)
- [SDK for Python AWS IoT reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/iot.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
