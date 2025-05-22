## Overview
This AWS IoT Fleetwise basic scenario demonstrates how to interact with the AWS IoT FleetWise service using an AWS SDK. The scenario covers various operations such as creating a collection of standardized signals, creating a model manifest, creating a vehicle, and so on.

## Key Operations

1. **Create an AWS Fleetwise signal catalog**:
   - This step creates an AWS Fleetwise signal catalog by invoking the `createSignalCatalog`method.

2. **Create an AWS IoT Fleetwise fleet**:
   - This operation creates an AWS Fleetwise fleet by invoking the `createFleet`method.

3. **Create a model manifest**:
   - This operation creates an AWS Fleetwise model manifest by invoking the `createModelManifest`method.

4. **Create a decoder manifest**:
   - This operation creates an AWS Fleetwise decoder manifest by invoking the `createDecoderManifest`method.

5. **Create a vehicle**:
   - This operation creates an AWS Fleetwise vehicle by invoking the `createVehicle`method.

**Note** See the [Engineering specification](SPECIFICATION.md) for a full listing of operations.

## Resources

This Basics scenario does not require any additional AWS resources.

## Implementations

This scenario example will be implemented in the following languages:

- Java
- JavaScript
- Kotlin

## Additional Reading

- [AWS IoT Fleetwise Documentation](https://docs.aws.amazon.com/iot-fleetwise/latest/developerguide/what-is-iotfleetwise.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
