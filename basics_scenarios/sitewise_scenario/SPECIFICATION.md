# AWS IoT SiteWise Service Scenario Specification

## Overview
This SDK Basics scenario demonstrates how to interact with AWS IoT SiteWise using the AWS SDK. It demonstrates various tasks such as creating a SiteWise Asset Model, creating an asset, sending data to the asset, reading data, and so on.  Finally this scenario demonstrates how to clean up resources. Its purpose is to demonstrate how to get up and running with AWS IoT SiteWise and the AWS SDK.

## Resources
This Basics scenario has a depedency on an IAM role that has permissions for this service. We will create this resource using a CloudFormation template so the user does not have to manually create it. 

## Hello Amazon OpenSearch
This program is intended for users not familiar with the AWS IoT SiteWise Service to easily get up and running. The logic is to show use of `
`???.

## Scenario Program Flow
The AWS IoT SiteWise Basics scenario executes the following operations.

1. **Create an AWS SiteWise Asset Model**:
   - Description: This operation creates an AWS SiteWise Asset Model. Invoke the `createAssetModel` method. 
   - Exception Handling: Check to see if a `ResourceAlreadyExistsException` is thrown. If it is thrown, get the asset model id and move on.

2. **Create an AWS IoT SiteWise Asset**:
   - Description: This operation creates an AWS SiteWise asset.
   - The method `createAsset` is called to obtain the asset id.
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end program. 

3. **Retrieve the property ID values**:
   - Description: To send data to an asset, we need to get the property ID values for the Temperature and Humidity properties.
   - The method `listAssetModels()` is called to retrieve asset id values.
   - Exception Handling: Check to see if a `IoTSiteWiseException` is thrown. There are not many other useful exceptions for this specific call. If so, display the message and end the program. 

4. **Send data to an AWS IoT SiteWise Asset**:
   - Description: This operation sends data to an IoT SiteWise Asset.
   - This step uses the method `batchPutAssetPropertyValue()`.
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end program. 

5. **Retrieve the value of the IoT SiteWise Asset property**:
   - Description: This operation gets data from the asset.
   - This step uses the method `getAssetPropertyValue()`.
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end program.  

6. **Create an IoT SiteWise Portal**:
   - Description: This operation is creates a Sitewise portal.
   - The method `createPortal` is called.
   - Exception Handling: Check to see if a `IoTSiteWiseException` is thrown.  

7. **Describe the Portal**:
   - Description: This operation descrines the portal and returns an URL for the portal.
   - The method `describePortal()` is called and retutns URL.
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end program. 

8. **Create an IoTSitewise Gateway**:
   - Description: This operation creates a Sitewise Gateway.
   - The method `createGateway)` is called.
  - Exception Handling: Check to see if a `IoTSiteWiseException` is thrown.  

9. **Describe the Sitewise Gateway**:
   - Description: This operation describes the Gateway.
   - The method `describeGateway()` is called."
   - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end program.

10. **Delete the AWS IoT SiteWise Assets**
  - The `delete()` methods ares called to clean up the resources.
  - Exception Handling: Check to see if a `ResourceNotFoundException` is thrown. If so, display the message and end program. 

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
--------------------------------------------------------------------------------
1. Create an AWS SiteWise Asset Model
 An AWS IoT SiteWise Asset Model is a way to represent the physical assets, such as equipment,
 processes, and systems, that exist in an industrial environment. This model provides a structured and
 hierarchical representation of these assets, allowing users to define the relationships and properties
 of each asset.


Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

The Asset Model MyAssetModel2 already exists. The id of the existing model is 754c6991-9b34-4fd3-85a5-d4261b1324a1. Moving on...

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

Asset created with ID: c11d78da-4356-4f52-8c11-24a02dcf3c5b

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

The Humidity property Id is cfd388c0-b70c-4e25-b656-675d909f7ef7
The Temperature property Id is fac55cfe-6e7f-4c5e-8bea-2d3e333f169d

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

Portal created successfully. Portal ID 391384ff-f439-481d-a6bb-2195fe70418e
The portal Id is 391384ff-f439-481d-a6bb-2195fe70418e

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

Portal URL: https://p-9l9bnfd7.app.iotsitewise.aws

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

The ARN of the gateway is arn:aws:iotsitewise:us-east-1:814548047983:gateway/417f041e-44e8-40e8-93f4-2904f3a8febd
Gateway creation completed successfully. id is 417f041e-44e8-40e8-93f4-2904f3a8febd
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
9. Describe the IoTSitewise Gateway

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Gateway Name: myGateway11
Gateway ARN: arn:aws:iotsitewise:us-east-1:814548047983:gateway/417f041e-44e8-40e8-93f4-2904f3a8febd
Gateway Platform: GatewayPlatform(GreengrassV2=GreengrassV2(CoreDeviceThingName=myThing78))
Gateway Creation Date: 2024-09-13T16:50:54.037Z
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

Portal 391384ff-f439-481d-a6bb-2195fe70418e was deleted successfully.
The Gateway was deleted
Asset deleted successfully.
Lets wait 1 min for the asset to be deleted
00:00Countdown complete!

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

Delete the AWS IoT SiteWise Asset Model
IoT SiteWise error occurred: Error message: Asset model had assets when delete was called (Service: IoTSiteWise, Status Code: 400, Request ID: 6ebc4c3e-82e8-4a8b-a6d7-de0c8ada5a9d), Error code InvalidRequestException

Enter 'c' followed by <ENTER> to continue:
c
Continuing with the program...

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
This concludes the AWS SiteWise Scenario
--------------------------------------------------------------------------------


```

## SOS Tags

The following table describes the metadata used in this Basics Scenario.


| action                         | metadata file                     | metadata key                            |
|--------------------------------|-----------------------------------|---------------------------------------- |
| `createDomain`                 | opensearch_metadata.yaml          | opensearch_CreateDomain                 |
| `deleteDomain            `     | opensearch_metadata.yaml          | opensearch_DeleteDomain                 |
| `listDomainNames  `            | opensearch_metadata.yaml          | opensearch_ListDomainNames              |
| `updateDomainConfig`           | opensearch_metadata.yaml          | opensearch_UpdateDomainConfig           |
| `describeDomain`               | opensearch_metadata.yaml          | opensearch_DescribeDomain               |
| `describeDomainChangeProgress` | opensearch_metadata.yaml          | opensearch_ChangeProgress               |
| `listDomainTags`               | opensearch_metadata.yaml          | opensearch_ListTags                     |
| `scenario`                     | opensearch_metadata.yaml          | opensearch_Scenario                     |

