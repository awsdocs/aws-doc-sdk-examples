# Amazon Location code examples for the SDK for JavaScript (v3)

## Overview

Shows how to use the AWS SDK for JavaScript (v3) to work with Amazon Location Service.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javascriptv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon Location](hello.js#L4) (`ListGeofencesPaginator`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenarios/location-service-basics.js)


### Single actions

Code excerpts that show you how to call individual service functions.

- [BatchUpdateDevicePosition](actions/batch-update-device-position.js#L4)
- [CalculateRoute](actions/calculate-distance-async.js#L4)
- [CreateGeofenceCollection](actions/create-geofence-collection.js#L4)
- [CreateMap](actions/create-map.js#L4)
- [CreateRouteCalculator](actions/create-route-calculator.js#L4)
- [CreateTracker](actions/create-tracker.js#L4)
- [DeleteGeofenceCollection](actions/delete-geofence-collection.js#L4)
- [DeleteMap](actions/delete-map.js#L4)
- [DeleteRouteCalculator](actions/delete-route-calculator.js#L4)
- [DeleteTracker](actions/delete-tracker.js#L4)
- [GetDevicePosition](actions/get-device-position.js#L4)
- [PutGeofence](actions/put-geofence.js#L4)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

**Run a single action**

```bash
node ./actions/<fileName>
```

**Run a scenario**

Most scenarios can be run with the following command:
```bash
node ./scenarios/<fileName>
```

**Run with options**

Some actions and scenarios can be run with options from the command line:
```bash
node ./scenarios/<fileName> --option1 --option2
```
[util.parseArgs](https://nodejs.org/api/util.html#utilparseargsconfig) is used to configure
these options. For the specific options available to each script, see the `parseArgs` usage
for that file.

<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon Location

This example shows you how to get started using Amazon Location.

```bash
node ./hello.js
```

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
in the `javascriptv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Location Developer Guide](https://docs.aws.amazon.com/location/latest/developerguide/what-is.html)
- [Amazon Location API Reference](https://docs.aws.amazon.com/location/latest/APIReference/Welcome.html)
- [SDK for JavaScript (v3) Amazon Location reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/client/location/)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
