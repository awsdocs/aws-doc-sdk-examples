# AWS IoT FleetWise code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with AWS IoT FleetWise.

<!--custom.overview.start-->
<!--custom.overview.end-->

_AWS IoT FleetWise provides a secure and scalable platform for collecting, storing, and analyzing data from connected vehicles and fleets._

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

- [Hello AWS IoT FleetWise](src/main/java/com/example/fleetwise/HelloFleetwise.kt#L10) (`listSignalCatalogsPaginator`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/java/com/example/fleetwise/scenario/FleetwiseScenario.kt)


### Single actions

Code excerpts that show you how to call individual service functions.

- [createDecoderManifest](src/main/java/com/example/fleetwise/scenario/FleetwiseScenario.kt#L467)
- [createFleet](src/main/java/com/example/fleetwise/scenario/FleetwiseScenario.kt#L640)
- [createModelManifest](src/main/java/com/example/fleetwise/scenario/FleetwiseScenario.kt#L590)
- [createSignalCatalog](src/main/java/com/example/fleetwise/scenario/FleetwiseScenario.kt#L662)
- [createVehicle](src/main/java/com/example/fleetwise/scenario/FleetwiseScenario.kt#L299)
- [deleteDecoderManifest](src/main/java/com/example/fleetwise/scenario/FleetwiseScenario.kt#L454)
- [deleteFleet](src/main/java/com/example/fleetwise/scenario/FleetwiseScenario.kt#L555)
- [deleteModelManifest](src/main/java/com/example/fleetwise/scenario/FleetwiseScenario.kt#L573)
- [deleteSignalCatalog](src/main/java/com/example/fleetwise/scenario/FleetwiseScenario.kt#L538)
- [deleteVehicle](src/main/java/com/example/fleetwise/scenario/FleetwiseScenario.kt#L260)
- [getDecoderManifest](src/main/java/com/example/fleetwise/scenario/FleetwiseScenario.kt#L343)
- [getModelManifest](src/main/java/com/example/fleetwise/scenario/FleetwiseScenario.kt#L389)
- [getVehicle](src/main/java/com/example/fleetwise/scenario/FleetwiseScenario.kt#L273)
- [listSignalCatalogNodes](src/main/java/com/example/fleetwise/scenario/FleetwiseScenario.kt#L620)
- [updateDecoderManifest](src/main/java/com/example/fleetwise/scenario/FleetwiseScenario.kt#L330)
- [updateModelManifest](src/main/java/com/example/fleetwise/scenario/FleetwiseScenario.kt#L435)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS IoT FleetWise

This example shows you how to get started using AWS IoT FleetWise.


#### Learn the basics

This example shows you how to do the following:

- Create a collection of standardized signals.
- Create a fleet that represents a group of vehicles.
- Create a model manifest.
- Create a decoder manifest.
- Check the status of the model manifest.
- Check the status of the decoder.
- Create an IoT Thing.
- Create a vehicle.
- Display vehicle details.
- Delete the AWS IoT FleetWise Assets.

<!--custom.basic_prereqs.iotfleetwise_Scenario.start-->
<!--custom.basic_prereqs.iotfleetwise_Scenario.end-->


<!--custom.basics.iotfleetwise_Scenario.start-->
<!--custom.basics.iotfleetwise_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `kotlin` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS IoT FleetWise Developer Guide](https://docs.aws.amazon.com/iot-fleetwise/latest/developerguide/what-is-fleetwise.html)
- [AWS IoT FleetWise API Reference](https://docs.aws.amazon.com/iot-fleetwise/latest/APIReference/Welcome.html)
- [SDK for Kotlin AWS IoT FleetWise reference](https://sdk.amazonaws.com/kotlin/api/latest/iot/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
