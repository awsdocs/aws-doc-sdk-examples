# Amazon Location code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon Location Service.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Location lets you easily and securely add maps, places, routes, geofences, and trackers, to your applications._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon Location](src/main/java/com/example/location/HelloLocation.java#L18) (`ListGeofencesPaginator`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/java/com/example/location/scenario/LocationScenario.java)


### Single actions

Code excerpts that show you how to call individual service functions.

- [BatchUpdateDevicePosition](src/main/java/com/example/location/scenario/LocationActions.java#L338)
- [CalculateRoute](src/main/java/com/example/location/scenario/LocationActions.java#L249)
- [CreateGeofenceCollection](src/main/java/com/example/location/scenario/LocationActions.java#L447)
- [CreateKey](src/main/java/com/example/location/scenario/LocationActions.java#L476)
- [CreateMap](src/main/java/com/example/location/scenario/LocationActions.java#L513)
- [CreateRouteCalculator](src/main/java/com/example/location/scenario/LocationActions.java#L283)
- [CreateTracker](src/main/java/com/example/location/scenario/LocationActions.java#L376)
- [DeleteGeofenceCollection](src/main/java/com/example/location/scenario/LocationActions.java#L548)
- [DeleteKey](src/main/java/com/example/location/scenario/LocationActions.java#L577)
- [DeleteMap](src/main/java/com/example/location/scenario/LocationActions.java#L606)
- [DeleteRouteCalculator](src/main/java/com/example/location/scenario/LocationActions.java#L665)
- [DeleteTracker](src/main/java/com/example/location/scenario/LocationActions.java#L634)
- [GetDevicePosition](src/main/java/com/example/location/scenario/LocationActions.java#L310)
- [PutGeofence](src/main/java/com/example/location/scenario/LocationActions.java#L406)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon Location

This example shows you how to get started using Amazon Location.


#### Learn the basics

This example shows you how to do the following:

- Create an Amazon Location map.
- Create an Amazon Location API key.
- Display Map URL.
- Create a geofence collection.
- Store a geofence geometry.
- Create a tracker resource.
- Update the position of a device.
- Retrieve the most recent position update for a specified device.
- Create a route calculator.
- Determine the distance between Seattle and Vancouver.
- Use Amazon Location higher level APIs.
- Delete the Amazon Location Assets.

<!--custom.basic_prereqs.location_Scenario.start-->
<!--custom.basic_prereqs.location_Scenario.end-->


<!--custom.basics.location_Scenario.start-->
<!--custom.basics.location_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Location Developer Guide](https://docs.aws.amazon.com/location/latest/developerguide/what-is.html)
- [Amazon Location API Reference](https://docs.aws.amazon.com/location/latest/APIReference/Welcome.html)
- [SDK for Java 2.x Amazon Location reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/entityresolution/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
