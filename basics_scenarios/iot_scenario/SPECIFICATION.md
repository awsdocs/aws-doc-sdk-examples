# AWS Iot SDK Basic Scenario Technical Specification

## Overview
This example shows how to use AWS SDKs to perform device management use cases using the AWS Iot SDK.

The AWS Iot API provides secure, bi-directional communication between Internet-connected devices (such as sensors, actuators, embedded devices, or smart appliances) and the Amazon Web Services cloud. This example shows some typical use cases such as creating things, creating certifications, applying the certifications to the IoT Thing and so on. 

## Resources
This program requires these AWS resources.

1. **roleARN** - The ARN of an IAM role that has permission to work with AWS IOT.
2. **snsAction** - An ARN of an SNS topic.

## Hello AWS IoT

This program is intended for users not familiar with the Amazon IoT SDK to easily get up and running. The logic is to show use of `listThingsPaginator`.

## Scenario Program Flow

This scenario demonstrates the following key AWS IoT Service operations:

1. **Create an AWS IoT Thing**:
   - Use the `CreateThing` API to create a new AWS IoT Thing.
   - Specify the Thing name and any desired Thing attributes.

2. **Generate a Device Certificate**:
   - Use the `CreateKeysAndCertificate` API to generate a new device certificate.
   - The certificate is used to authenticate the device when connecting to AWS IoT.

3. **Attach the Certificate to the AWS IoT Thing**:
   - Use the `AttachThingPrincipal` API to associate the device certificate with the AWS IoT Thing.
   - This allows the device to authenticate and communicate with the AWS IoT Core service.

4. **Update an AWS IoT Thing with Attributes**:
   - Use the `UpdateThingShadow` API to update the Thing's shadow with new attribute values.
   - The Thing's shadow represents the device's state and properties.

5. **Get an AWS IoT Endpoint**:
   - Use the `DescribeEndpoint` API to retrieve the AWS IoT Core service endpoint.
   - The device uses this endpoint to connect and communicate with AWS IoT.

6. **List Certificates**:
   - Use the `ListCertificates` API to retrieve a list of all certificates associated with the AWS IoT account.

7. **Detach and Delete the Certificate**:
   - Use the `DetachThingPrincipal` API to detach the certificate from the AWS IoT Thing.
   - Use the `DeleteCertificate` API to delete the certificate.

8. **Update the Thing Shadow**:
   - Use the `UpdateThingShadow` API to update the Thing's shadow with new state information.
   - The Thing's shadow represents the device's state and properties.

9. **Write State Information in JSON Format**:
   - The state information is written in JSON format, which is the standard data format used by AWS IoT.

10. **Create an AWS IoT Rule**:
    - Use the `CreateTopicRule` API to create a new AWS IoT Rule.
    - Rules allow you to define actions to be performed based on device data or events.

11. **List AWS IoT Rules**:
    - Use the `ListTopicRules` API to retrieve a list of all AWS IoT Rules.

12. **Search AWS IoT Things**:
    - Use the `SearchThings` API to search for AWS IoT Things based on various criteria, such as Thing name, attributes, or shadow state.

13. **Delete an AWS IoT Thing**:
    - Use the `DeleteThing` API to delete an AWS IoT Thing.

 Note: We have buy off on these operations from IoT SME. 

### Program execution

This scenario does have user interaction. The following shows the output of the program. 

```
--------------------------------------------------------------------------------
Welcome to the AWS IoT example workflow.
This example program demonstrates various interactions with the AWS Internet of Things (IoT) Core service. The program guides you through a series of steps,
including creating an IoT Thing, generating a device certificate, updating the Thing with attributes, and so on.
It utilizes the AWS SDK for Java V2 and incorporates functionalities for creating and managing IoT Things, certificates, rules,
shadows, and performing searches. The program aims to showcase AWS IoT capabilities and provides a comprehensive example for
developers working with AWS IoT in a Java environment.


Press Enter to continue...
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
1. Create an AWS IoT Thing.
An AWS IoT Thing represents a virtual entity in the AWS IoT service that can be associated with a physical device.

Enter Thing name: foo5543
foo5543 was successfully created. The ARN value is arn:aws:iot:us-east-1:814548047983:thing/foo5543
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
2. Generate a device certificate.
A device certificate performs a role in securing the communication between devices (Things) and the AWS IoT platform.

Do you want to create a certificate for foo5543? (y/n)y

Certificate:
-----BEGIN CERTIFICATE-----
MIIDWTCCAkGgAwIBAgIUY3PjIZIcFhCrPuBvH16219CPqD0wDQYJKoZIhvcNAQEL
BQAwTTFLMEkGA1UECwxCQW1hem9uIFdlYiBTZXJ2aWNlcy
-----END CERTIFICATE-----


Certificate ARN:
arn:aws:iot:us-east-1:814548047983:cert/1c9cd9a0f315b58e549e84c38ada37ced24e89047a15ff7ac4abafae9ff6dfc6
Attach the certificate to the AWS IoT Thing.
Certificate attached to Thing successfully.
Thing Details:
Thing Name: foo5543
Thing ARN: arn:aws:iot:us-east-1:814548047983:thing/foo5543
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
3. Update an AWS IoT Thing with Attributes.
 IoT Thing attributes, represented as key-value pairs, offer a pivotal advantage in facilitating efficient data
 management and retrieval within the AWS IoT ecosystem.

Press Enter to continue...
Thing attributes updated successfully.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
4. Return a unique endpoint specific to the Amazon Web Services account.
 An IoT Endpoint refers to a specific URL or Uniform Resource Locator that serves as the entry point for communication between IoT devices and the AWS IoT service.

Press Enter to continue...
Extracted subdomain: a39q2exsoth3da
Full Endpoint URL: https://a39q2exsoth3da-ats.iot.us-east-1.amazonaws.com
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
5. List your AWS IoT certificates
Press Enter to continue...
Cert id: 1c9cd9a0f315b58e549e84c38ada37ced24e89047a15ff7ac4abafae9ff6dfc6
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/1c9cd9a0f315b58e549e84c38ada37ced24e89047a15ff7ac4abafae9ff6dfc6
Cert id: 0d211c9b39060561fb00b052f72f495cc8fc280c2cae805ba8f4a36d76ff5668
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/0d211c9b39060561fb00b052f72f495cc8fc280c2cae805ba8f4a36d76ff5668
Cert id: c0d340f1fa8484075d84b523144369a9a9c7916dee225d2f053ac1f434961fb6
Cert Arn: arn:aws:iot:us-east-1:814548047983:cert/c0d340f1fa8484075d84b523144369a9a9c7916dee225d2f053ac1f434961fb6

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
6. Create an IoT shadow that refers to a digital representation or virtual twin of a physical IoT device
 A Thing Shadow refers to a feature that enables you to create a virtual representation, or "shadow,"
 of a physical device or thing. The Thing Shadow allows you to synchronize and control the state of a device between
 the cloud and the device itself. and the AWS IoT service. For example, you can write and retrieve JSON data from a Thing Shadow.

Press Enter to continue...
Thing Shadow updated successfully.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
7. Write out the state information, in JSON format.
Press Enter to continue...
Received Shadow Data: {"state":{"reported":{"temperature":25,"humidity":50}},"metadata":{"reported":{"temperature":{"timestamp":1707413791},"humidity":{"timestamp":1707413791}}},"version":1,"timestamp":1707413794}
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
8. Creates a rule
Creates a rule that is an administrator-level action.
Any user who has permission to create rules will be able to access data processed by the rule.

Enter Rule name: rule8823
IoT Rule created successfully.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
9. List your rules.
Press Enter to continue...
List of IoT Rules:
Rule Name: rule0099
Rule ARN: arn:aws:iot:us-east-1:814548047983:rule/rule0099
--------------
Rule Name: rule8823
Rule ARN: arn:aws:iot:us-east-1:814548047983:rule/rule8823
--------------
Rule Name: rule444
Rule ARN: arn:aws:iot:us-east-1:814548047983:rule/rule444
--------------
Rule Name: YourRuleName11
Rule ARN: arn:aws:iot:us-east-1:814548047983:rule/YourRuleName11

--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
10. Search things using the Thing name.
Press Enter to continue...
Thing id found using search is abad8003-3abd-4614-bc04-8d0b6211eb9e
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Do you want to detach and delete the certificate for foo5543? (y/n)y
11. You selected to detach amd delete the certificate.
Press Enter to continue...
arn:aws:iot:us-east-1:814548047983:cert/1c9cd9a0f315b58e549e84c38ada37ced24e89047a15ff7ac4abafae9ff6dfc6 was successfully removed from foo5543
arn:aws:iot:us-east-1:814548047983:cert/1c9cd9a0f315b58e549e84c38ada37ced24e89047a15ff7ac4abafae9ff6dfc6 was successfully deleted.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
12. Delete the AWS IoT Thing.
Do you want to delete the IoT Thing? (y/n)y
Deleted Thing foo5543
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
The AWS IoT workflow has successfully completed.
--------------------------------------------------------------------------------
```

## SOS Tags

The following table describes the metadata used in this scenario.


| action                       | metadata file                | metadata key                            |
|------------------------------|------------------------------|---------------------------------------- |
| `describeEndpoint`           | iot_metadata.yaml            | iot_DescribeEndpoint                    |
| `listThings`                 | iot_metadata.yaml            | iot_ListThings                          |
| `listCertificates`           | iot_metadata.yaml            | iot_ListCertificates                    |
| `CreateKeysAndCertificate`   | iot_metadata.yaml            | iot_CreateKeysAndCertificate            |
| `deleteCertificate`          | iot_metadata.yaml            | iot_DeleteCertificate                   |
| `searchIndex`                | iot_metadata.yaml            | iot_SearchIndex                         |
| `deleteThing`                | iot_metadata.yaml            | iot_DeleteThing                         |
| `describeThing`              | iot_metadata.yaml            | iot_DescribeThing                       |
| `attachThingPrincipal`       | iot_metadata.yaml            | iot_AttachThingPrincipal                |
| `detachThingPrincipal`       | iot_metadata.yaml            | iot_DetachThingPrincipal                |
| `updateThing`                | iot_metadata.yaml            | iot_UpdateThing                         |
| `createTopicRule`            | iot_metadata.yaml            | iot_CreateTopicRule                     |
| `createThing`                | iot_metadata.yaml            | iot_CreateThing                         |
| `hello               `       | iot_metadata.yaml            | iot_Hello                               |
| `scenario                    | iot_metadata.yaml            | iot_Scenario                            |
