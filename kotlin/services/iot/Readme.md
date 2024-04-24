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


### Single actions

Code excerpts that show you how to call individual service functions.

- [Attach a certificate](src/main/kotlin/com/example/iot/IotScenario.kt#L456) (`AttachThingPrincipal`)
- [Create a certificate](../../../javav2/example_code/iot/src/main/java/com/example/iot/IotScenario.java#L458) (`CreateKeysAndCertificate`)
- [Create a rule](src/main/kotlin/com/example/iot/IotScenario.kt#L322) (`CreateTopicRule`)
- [Create a thing](src/main/kotlin/com/example/iot/IotScenario.kt#L503) (`CreateThing`)
- [Delete a certificate](src/main/kotlin/com/example/iot/IotScenario.kt#L254) (`DeleteCertificate`)
- [Delete a thing](src/main/kotlin/com/example/iot/IotScenario.kt#L241) (`DeleteThing`)
- [Describe a thing](src/main/kotlin/com/example/iot/IotScenario.kt#L470) (`DescribeThing`)
- [Detach a certificate](src/main/kotlin/com/example/iot/IotScenario.kt#L273) (`DetachThingPrincipal`)
- [Get endpoint information](src/main/kotlin/com/example/iot/IotScenario.kt#L379) (`DescribeEndpoint`)
- [List your certificates](src/main/kotlin/com/example/iot/IotScenario.kt#L366) (`ListCertificates`)
- [Query the search index](src/main/kotlin/com/example/iot/IotScenario.kt#L287) (`SearchIndex`)
- [Update a thing](src/main/kotlin/com/example/iot/IotScenario.kt#L412) (`UpdateThing`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Work with device management use cases](src/main/kotlin/com/example/iot/IotScenario.kt)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS IoT

This example shows you how to get started using AWS IoT.



#### Work with device management use cases

This example shows you how to work with AWS IoT device management use cases using AWS IoT SDK


<!--custom.scenario_prereqs.iot_Scenario.start-->
<!--custom.scenario_prereqs.iot_Scenario.end-->


<!--custom.scenarios.iot_Scenario.start-->
<!--custom.scenarios.iot_Scenario.end-->

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