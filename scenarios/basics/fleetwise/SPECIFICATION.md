# AWS IoT Fleetwise Service Scenario Specification

## Overview
This SDK Basics scenario demonstrates how to interact with AWS IoT Fleetwise using an AWS SDK. 
It demonstrates various tasks such as creating a Fleetwise catelog, creating an fleet, 
creating a vehicle, and so on.  Finally this scenario demonstrates how 
to clean up resources. Its purpose is to demonstrate how to get up and running with 
AWS Fleetwise and an AWS SDK.

## Resources
This Basics scenario does not require any additional AWS resources. 

## Hello AWS Fleetwise
This program is intended for users not familiar with the AWS IoT Fleetwise Service to easily get up and running. The program uses a `listSignalCatalogsPaginator` to demonstrate how you can read through catalog information. If there are no catalogs, print out a message to inform the user. 

## Basics Scenario Program Flow
The AWS IoT Fleetwise Basics scenario executes the following operations.

1. **Create an AWS FleetWise collection**:
   - Description: Creates an AWS Fleetwise collection by invoking `createSignalCatalog`.
   - Exception Handling: Check to see if a `ValidationException` is thrown. 
     If it is thrown, if so, display the message and end the program.

2. **Create an IoT Fleetwise fleet**:
   - Description: Creates an AWS Fleetwise fleet by invoking `createFleet`.
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If it is thrown, if so, display the message and end the program.

3. **Create a model manifest**:
   - Description: Creates a model manifest by invoking `listSignalCatalogNodes` to retrieve a list of nodes. This node list is passed to `createModelManifest()`.
   - Exception Handling: Check to see if an `InvalidSignalsException` is thrown. If so, display the message and end the program.

4. **Create a decoder manifest**:
   - Description: Creates a decoder manifest by invoking `createDecoderManifest`.
   - Exception Handling: Check to see if a `DecoderManifestValidationException` is thrown. If so, display the message and end the program.

5. **Check the status of the model manifest**:
   - Description: Checks the status of the model manifest by invoking `updateModelManifest`and `getModelManifest`.
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end the program.

6. **Check the status of the decoder**:
   - Description: Checks the status of the decoder manifest by invoking `updateDecoderManifest`and `getDecoderManifest`.
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end the program.


7. **Create an IoT Thing**:
   - Description: Creates an IoT Thing which is required to create a vehicle by invoking `createThing`.
   - Exception Handling: Check to see if a `ResourceAlreadyExistsException` is thrown. If so, display the message and end the program.

8.  **Create a vehicle**:
   - Description: Creates a vehicle by invoking `createVehicle`.
   - Exception Handling: Check to see if an `ResourceNotFoundException` is thrown. If so, display the message and end the program.

9. **Display vehicle details**:
   - Description: Describes the vehicle by invoking `getVehicle`.
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end the program.

10. **Delete the AWS IoT Fleetwise Assets**:
    - The `delete` methods are called to clean up the resources.
    - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end the program."

### Program execution
The following shows the output of the AWS IoT Fleetwise Basics scenario. 

```
 AWS IoT FleetWise is a managed service that simplifies the
 process of collecting, organizing, and transmitting vehicle
 data to the cloud in near real-time. Designed for automakers
 and fleet operators, it allows you to define vehicle models,
 specify the exact data you want to collect (such as engine
 temperature, speed, or battery status), and send this data to
 AWS for analysis. By using intelligent data collection
 techniques, IoT FleetWise reduces the volume of data
 transmitted by filtering and transforming it at the edge,
 helping to minimize bandwidth usage and costs.

At its core, AWS IoT FleetWise helps organizations build
scalable systems for vehicle data management and analytics,
supporting a wide variety of vehicles and sensor configurations.
You can define signal catalogs and decoder manifests that describe
how raw CAN bus signals are translated into readable data, making
the platform highly flexible and extensible. This allows
manufacturers to optimize vehicle performance, improve safety,
and reduce maintenance costs by gaining real-time visibility
into fleet operations.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
1. Creates a collection of standardized signals that can be reused to create vehicle models
The collection ARN is arn:aws:iotfleetwise:us-east-1:814548047983:signal-catalog/catalog60

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
2. Create a fleet that represents a group of vehicles
Creating an IoT FleetWise fleet allows you to efficiently collect,
organize, and transfer vehicle data to the cloud, enabling real-time
insights into vehicle performance and health.

It helps reduce data costs by allowing you to filter and prioritize
only the most relevant vehicle signals, supporting advanced analytics
and predictive maintenance use cases.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

The fleet Id is fleet60

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
3. Create a model manifest
An AWS IoT FleetWise manifest defines the structure and
relationships of vehicle data. The model manifest specifies
which signals to collect and how they relate to vehicle systems,
while the decoder manifest defines how to decode raw vehicle data
into meaningful signals.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

The manifest ARN is arn:aws:iotfleetwise:us-east-1:814548047983:model-manifest/manifest60

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
4. Create a decoder manifest
A decoder manifest in AWS IoT FleetWise defines how raw vehicle
data (such as CAN signals) should be interpreted and decoded
into meaningful signals. It acts as a translation layer
that maps vehicle-specific protocols to standardized data formats
using decoding rules. This is crucial for extracting usable
data from different vehicle models, even when their data
formats vary.



Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

The decoder manifest ARN is arn:aws:iotfleetwise:us-east-1:814548047983:decoder-manifest/decManifest60

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
5. Check the status of the model manifest
The model manifest must be in an ACTIVE state before it can be used
to create or update a vehicle.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Elapsed: 0s | Status: DRAFT
Elapsed: 1s | Status: DRAFT
Elapsed: 2s | Status: DRAFT
Elapsed: 3s | Status: DRAFT
Elapsed: 4s | Status: DRAFT
Elapsed: 5s | Status: ACTIVE

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
6. Check the status of the decoder
The decoder manifest must be in an ACTIVE state before it can be used
to create or update a vehicle.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

 Elapsed: 0s | Decoder Status: DRAFT
 Elapsed: 1s | Decoder Status: DRAFT
 Elapsed: 2s | Decoder Status: DRAFT
 Elapsed: 3s | Decoder Status: DRAFT
 Elapsed: 4s | Decoder Status: DRAFT
 Elapsed: 5s | Decoder Status: ACTIVE

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
7. Create an IoT Thing
AWS IoT FleetWise expects an existing AWS IoT Thing with the same
name as the vehicle name you are passing to createVehicle method.
Before calling createVehicle(), you must create an AWS IoT Thing
with the same name using the AWS IoT Core service.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

IoT Thing created: vehicle60

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
8. Create a vehicle
Creating a vehicle in AWS IoT FleetWise allows you to digitally
represent and manage a physical vehicle within the AWS ecosystem.
This enables efficient ingestion, transformation, and transmission
of vehicle telemetry data to the cloud for analysis.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Vehicle 'vehicle60' created successfully.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
9. Display vehicle details

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Vehicle Details:
• modelManifestArn : arn:aws:iotfleetwise:us-east-1:814548047983:model-manifest/manifest60
• vehicleName : vehicle60
• creationTime : 2025-04-29T16:00:02.147Z
• lastModificationTime : 2025-04-29T16:00:02.147Z
• decoderManifestArn : arn:aws:iotfleetwise:us-east-1:814548047983:decoder-manifest/decManifest60
• attributes : {}
• arn : arn:aws:iotfleetwise:us-east-1:814548047983:vehicle/vehicle60

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
10. Delete the AWS IoT Fleetwise Assets
Would you like to delete the IoT Fleetwise Assets? (y/n)
y
vehicle60 was successfully deleted
decManifest60 was successfully deleted
manifest60 was successfully deleted
fleet60 was successfully deleted
catalog60 was successfully deleted
--------------------------------------------------------------------------------
Thank you for checking out the AWS IoT Fleetwise Service Use demo. We hope you
learned something new, or got some inspiration for your own apps today.
For more AWS code examples, have a look at:
https://docs.aws.amazon.com/code-library/latest/ug/what-is-code-library.html

--------------------------------------------------------------------------------
```

## SOS Tags

The following table describes the metadata used in this Basics Scenario.


| action                 | metadata file              | metadata key                         |
|------------------------|----------------------------|------------------------------------- |
|`createSignalCatalog`   |iot_fleetwise_metadata.yaml |iotfleetwise_CreateSignalCatalog      |
|`createFleet`           |iot_fleetwise_metadata.yaml |iotfleetwise_CreateFleet              |
|`createModelManifest`   iot_fleetwise_metadata.yaml  |iotfleetwise_CreateModelManifest      |
|`createDecoderManifest` |iot_fleetwise_metadata.yaml |iotfleetwise_CreateDecoderManifest    |
|`updateModelManifest`   |iot_fleetwise_metadata.yaml |iotfleetwise_UpdateModelManifest      |
| `createPortal`         |iot_fleetwise_metadata.yaml |iotfleetwise_CreatePortal             |
|`waitForModelManifest`  |iot_fleetwise_metadata.yaml |iotfleetwise_WaitForModelManifest     |
|`updateDecoderManifest` |iot_fleetwise_metadata.yaml |iotfleetwise_updateDecoder            |
| `describeAssetModel`   |iot_fleetwise_metadata.yaml |iotfleetwise_DescribeAssetModel       |
| `waitForDecoder  `     |iot_fleetwise_metadata.yaml |iotfleetwise_WaitForDecoder           |
| `createVehicle`        |iot_fleetwise_metadata.yaml |iotfleetwise_CreateVehicle            |
| `getVehicle`           |iot_fleetwise_metadata.yaml |iotfleetwise_GetVehicle               |
| `deleteVehicle `       |iot_fleetwise_metadata.yaml |iotfleetwise_DeleteVehicle            |
|`deleteDecoderManifest` |iot_fleetwise_metadata.yaml |iotfleetwise_DeleteDecoder            |
| `deleteFleet `         |iot_fleetwise_metadata.yaml |iotfleetwise_DeleteFleet              |
| `deleteModel`          |iot_fleetwise_metadata.yaml |iotfleetwise_DeleteModel              |
| `deleteSignalCatalog ` |iot_fleetwise_metadata.yaml|iotfleetwise_DeleteSignalCatalog       |
| `scenario`             |iot_fleetwise_metadata.yaml |iotfleetwise_Scenario                 |
| `hello`                |iot_fleetwise_metadata.yaml |iotfleetwise_Hello                    |



