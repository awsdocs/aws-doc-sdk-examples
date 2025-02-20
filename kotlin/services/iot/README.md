# AWS IoT code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with AWS IoT.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `kotlin` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello AWS IoT](src/main/kotlin/com/example/iot/HelloIoT.kt#L6) (`listThings`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/kotlin/com/example/iot/IotScenario.kt)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AttachThingPrincipal](src/main/kotlin/com/example/iot/IotScenario.kt#L476)
- [CreateKeysAndCertificate](src/main/kotlin/com/example/iot/IotScenario.kt#L511)
- [CreateThing](src/main/kotlin/com/example/iot/IotScenario.kt#L528)
- [CreateTopicRule](src/main/kotlin/com/example/iot/IotScenario.kt#L331)
- [DeleteCertificate](src/main/kotlin/com/example/iot/IotScenario.kt#L257)
- [DeleteThing](src/main/kotlin/com/example/iot/IotScenario.kt#L243)
- [DescribeEndpoint](src/main/kotlin/com/example/iot/IotScenario.kt#L397)
- [DescribeThing](src/main/kotlin/com/example/iot/IotScenario.kt#L494)
- [DetachThingPrincipal](src/main/kotlin/com/example/iot/IotScenario.kt#L277)
- [ListCertificates](src/main/kotlin/com/example/iot/IotScenario.kt#L384)
- [SearchIndex](src/main/kotlin/com/example/iot/IotScenario.kt#L295)
- [UpdateThing](src/main/kotlin/com/example/iot/IotScenario.kt#L429)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS IoT

This example shows you how to get started using AWS IoT.


#### Learn the basics

This example shows you how to do the following:

- Create an AWS IoT Thing.
- Generate a device certificate.
- Update an AWS IoT Thing with Attributes.
- Return a unique endpoint.
- List your AWS IoT certificates.
- Create an AWS IoT shadow.
- Write out state information.
- Creates a rule.
- List your rules.
- Search things using the Thing name.
- Delete an AWS IoT Thing.

<!--custom.basic_prereqs.iot_Scenario.start-->
<!--custom.basic_prereqs.iot_Scenario.end-->


<!--custom.basics.iot_Scenario.start-->
<!--custom.basics.iot_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `kotlin` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS IoT Developer Guide](https://docs.aws.amazon.com/iot/latest/developerguide/what-is-aws-iot.html)
- [AWS IoT API Reference](https://docs.aws.amazon.com/iot/latest/apireference/Welcome.html)
- [SDK for Kotlin AWS IoT reference](https://sdk.amazonaws.com/kotlin/api/latest/iot/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
