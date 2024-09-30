# AWS IoT SiteWise Service Scenario Specification

## Overview
This SDK Basics scenario demonstrates how to interact with AWS IoT SiteWise using an AWS SDK. 
It demonstrates various tasks such as creating a SiteWise Asset Model, creating an asset, 
sending data to the asset, reading data, and so on.  Finally this scenario demonstrates how 
to clean up resources. Its purpose is to demonstrate how to get up and running with 
AWS IoT SiteWise and an AWS SDK.

## Resources
This Basics scenario requires an IAM role that has permissions to work with AWS IoT 
SiteWise service. The scenario creates this resource using a CloudFormation template.

## Hello AWS IoT SiteWise
This program is intended for users not familiar with the AWS IoT SiteWise Service to easily get up and running. The program uses a `listAssetModelsPaginator` to demonstrate how you can read through Asset Model information.

## Basics Scenario Program Flow
The AWS IoT SiteWise Basics scenario executes the following operations.

1. **Create an AWS SiteWise Asset Model**:
   - Description: This step creates an AWS SiteWise Asset Model by invoking the `createAssetModel` method.
   - Exception Handling: Check to see if a `ResourceAlreadyExistsException` is thrown. 
     If it is thrown, display the asset model ID and move on.

2. **Create an AWS IoT SiteWise Asset**:
   - Description: This operation creates an AWS SiteWise asset.
   - The method `createAsset` is called to obtain the asset ID.
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown if the asset model id provided in the request is invalid. If so, 
   display the message and end the program.

3. **Retrieve the property ID values**:
   - Description: To send data to an asset, we need to get the property ID values for the 
   the model properties. This scenario uses temperature and humidity properties.
   - The method `listAssetModelProperties` is called to retrieve the property ID values.
   - Exception Handling: Check to see if an `IoTSiteWiseException` is thrown. There are not 
   many other useful exceptions for this specific call. If so, display the message and end the program.

4. **Send data to an AWS IoT SiteWise Asset**:
   - Description: This operation sends data to an IoT SiteWise Asset.
   - This step uses the method `batchPutAssetPropertyValue`.
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end the program.

5. **Retrieve the value of the IoT SiteWise Asset property**:
   - Description: This operation gets data from the asset.
   - This step uses the method `getAssetPropertyValue`.
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end the program.

6. **Create an IoT SiteWise Portal**:
   - Description: This operation creates an IoT SiteWise portal.
   - The method `createPortal` is called.
   - Exception Handling: Check to see if an `IoTSiteWiseException` is thrown. If so, display the message and end the program. 

7. **Describe the Portal**:
   - Description: This operation describes the portal and returns a URL for the portal.
   - The method `describePortal` is called and returns the URL.
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end the program.

8.  **Create an IoT SiteWise Gateway**:
   - Description: This operation creates an IoT SiteWise Gateway.
   - The method `createGateway` is called.
   - Exception Handling: Check to see if an `IoTSiteWiseException` is thrown. If so, display the message and end the program.

9. **Describe the IoT SiteWise Gateway**:
   - Description: This operation describes the Gateway.
   - The method `describeGateway` is called.
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end the program.

10. **Delete the AWS IoT SiteWise Assets**:
    - The `delete` methods are called to clean up the resources.
    - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end the program."

### Program execution
The following shows the output of the AWS IoT SiteWise Basics scenario in the console. 

```
AWS IoT SiteWise is a fully managed industrial software-as-a-service (SaaS) that
makes it easy to collect, store, organize, and monitor data from industrial equipment and processes.
It is designed to help industrial and manufacturing organizations collect data from their equipment and
processes, and use that data to make informed decisions about their operations.

One of the key features of AWS IoT SiteWise is its ability to connect to a wide range of industrial
equipment and systems, including programmable logic controllers (PLCs), sensors, and other
industrial devices. It can collect data from these devices and organize it into a unified data model,
making it easier to analyze and gain insights from the data. AWS IoT SiteWise also provides tools for
visualizing the data, setting up alarms and alerts, and generating reports.

Another key feature of AWS IoT SiteWise is its ability to scale to handle large volumes of data.
It can collect and store data from thousands of devices and process millions of data points per second,
making it suitable for large-scale industrial operations. Additionally, AWS IoT SiteWise is designed
to be secure and compliant, with features like role-based access controls, data encryption,
and integration with other AWS services for additional security and compliance features.

Let's get started...


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
Use AWS CloudFormation to create an IAM role that are required for this scenario.
Stack creation requested, ARN is arn:aws:cloudformation:us-east-1:814548047983:stack/RoleSitewise/29f480c0-75fd-11ef-a42e-12cd4e534049
Stack created successfully
--------------------------------------------------------------------------------
1. Create an AWS SiteWise Asset Model
 An AWS IoT SiteWise Asset Model is a way to represent the physical assets, such as equipment,
 processes, and systems, that exist in an industrial environment. This model provides a structured and
 hierarchical representation of these assets, allowing users to define the relationships and properties
 of each asset.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

The Asset Model MyAssetModel already exists. The id of the existing model is ffbc475b-73ad-4eb6-bf28-8728818fa8ef. Moving on...

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
2. Create an AWS IoT SiteWise Asset
 The IoT SiteWise model defines the structure and metadata for your physical assets. Now we
 can use the asset model to create the asset.



Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Asset created with ID: 4d681624-a303-46dd-8830-6189790ae915

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
3. Retrieve the property ID values
 To send data to an asset, we need to get the property ID values for the
 Temperature and Humidity properties.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

The Humidity property Id is feb4aba6-55f9-4b00-b366-27b9d7e5a747
The Temperature property Id is 6cb505aa-6bcc-46f4-a12a-7ca5df8eb028

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
4. Send data to an AWS IoT SiteWise Asset
By sending data to an IoT SiteWise Asset, you can aggregate data from
multiple sources, normalize the data into a standard format, and store it in a
centralized location. This makes it easier to analyze and gain insights from the data.

This example demonstrate how to generate sample data and ingest it into the AWS IoT SiteWise asset.



Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Data sent successfully.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
5. Retrieve the value of the IoT SiteWise Asset property
IoT SiteWise is an AWS service that allows you to collect, process, and analyze industrial data
from connected equipment and sensors. One of the key benefits of reading an IoT SiteWise property
is the ability to gain valuable insights from your industrial data.



Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

The property name is: Temperature property 
The value of this property is 23.5

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

The property name is: Humidity property 
The value of this property is 65.0

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
6. Create an IoT SiteWise Portal
 An IoT SiteWise Portal allows you to aggregate data from multiple industrial sources,
 such as sensors, equipment, and control systems, into a centralized platform.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Portal created successfully. Portal ID 63e65729-b7a1-410a-aa36-94145fe92153
The portal Id is 63e65729-b7a1-410a-aa36-94145fe92153

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
7. Describe the Portal
 In this step, we will describe the step and provide the portal URL.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Portal URL: https://p-fy9qnrqy.app.iotsitewise.aws

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
8. Create an IoTSitewise Gateway
IoTSitewise Gateway serves as the bridge between industrial equipment, sensors, and the
cloud-based IoTSitewise service. It is responsible for securely collecting, processing, and
transmitting data from various industrial assets to the IoTSitewise platform,
enabling real-time monitoring, analysis, and optimization of industrial operations.



Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

The ARN of the gateway is arn:aws:iotsitewise:us-east-1:814548047983:gateway/50320670-1d88-4a7e-9013-1d7e8a3af832
Gateway creation completed successfully. id is 50320670-1d88-4a7e-9013-1d7e8a3af832
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
9. Describe the IoTSitewise Gateway

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Gateway Name: myGateway11
Gateway ARN: arn:aws:iotsitewise:us-east-1:814548047983:gateway/50320670-1d88-4a7e-9013-1d7e8a3af832
Gateway Platform: GatewayPlatform(GreengrassV2=GreengrassV2(CoreDeviceThingName=myThing78))
Gateway Creation Date: 2024-09-18T20:34:13.117Z
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
10. Delete the AWS IoT SiteWise Assets
Before you can delete the Asset Model, you must delete the assets.


Would you like to delete the IoT Sitewise Assets? (y/n)
y
You selected to delete the Sitewise assets.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Portal 63e65729-b7a1-410a-aa36-94145fe92153 was deleted successfully.
An unexpected error occurred: Cannot invoke "java.util.concurrent.CompletableFuture.join()" because "future" is null
Asset deleted successfully.
Lets wait 1 min for the asset to be deleted
01:00The Gateway was deleted successfully
00:00Countdown complete!

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Delete the AWS IoT SiteWise Asset Model
Asset model deleted successfully.

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Delete stack requested ....
Stack deleted successfully.
This concludes the AWS SiteWise Scenario
--------------------------------------------------------------------------------


```

## SOS Tags

The following table describes the metadata used in this Basics Scenario.


| action                         | metadata file                     | metadata key                            |
|--------------------------------|-----------------------------------|---------------------------------------- |
| `describeGateway`              | iot_sitewise_metadata.yaml        | iotsitewise_DescribeGateway             |
| `deleteGateway `               | iot_sitewise_metadata.yaml        | iotsitewise_DeleteGateway               |
| `createGateway `               | iot_sitewise_metadata.yaml        | iotsitewise_CreateGateway               |
| `describePortal`               | iot_sitewise_metadata.yaml        | iotsitewise_DescribePortal              |
| `listAssetModels`              | iot_sitewise_metadata.yaml        | iotsitewise_ListAssetModels             |
| `deletePortal`                 | iot_sitewise_metadata.yaml        | iotsitewise_DeletePortal                |
| `createPortal`                 | iot_sitewise_metadata.yaml        | iotsitewise_CreatePortal                |
| `deleteAssetModel`             | iot_sitewise_metadata.yaml        | iotsitewise_DeleteAssetModel            |
| `deleteAsset`                  | iot_sitewise_metadata.yaml        | iotsitewise_DeleteAsset                 |
| `describeAssetModel`           | iot_sitewise_metadata.yaml        | iotsitewise_DescribeAssetModel          |
| `getAssetPropertyValue`        | iot_sitewise_metadata.yaml        | iotsitewise_GetAssetPropertyValue       |
| `batchPutAssetPropertyValue`   | iot_sitewise_metadata.yaml        | iotsitewise_BatchPutAssetPropertyValue  |
| `createAsset`                  | iot_sitewise_metadata.yaml        | iotsitewise_CreateAsset                 |
| `createAssetModel `            | iot_sitewise_metadata.yaml        | iotsitewise_CreateAssetModel            |
| `scenario`                     | iot_sitewise_metadata.yaml        | iotsitewise_Scenario                    |
| `hello`                        | iot_sitewise_metadata.yaml        | iotsitewise_Hello                       |



