# Amazon IoT code examples for the SDK for .NET (v4)

## Overview

Shows how to use the AWS SDK for .NET (v4) to work with AWS IoT Core.

<!--custom.overview.start-->
<!--custom.overview.end-->

_AWS IoT Core is a managed cloud service that lets connected devices easily and securely interact with cloud applications and other devices._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv4` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello AWS IoT](Actions/HelloIoT.cs#L20) (`ListThings`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](Scenarios/IoTBasics.cs)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AttachThingPrincipal](Actions/IoTWrapper.cs#L156)
- [CreateKeysAndCertificate](Actions/IoTWrapper.cs#L84)
- [CreateThing](Actions/IoTWrapper.cs#L34)
- [CreateTopicRule](Actions/IoTWrapper.cs#L336)
- [DeleteCertificate](Actions/IoTWrapper.cs#L556)
- [DeleteThing](Actions/IoTWrapper.cs#L589)
- [DescribeEndpoint](Actions/IoTWrapper.cs#L213)
- [DetachThingPrincipal](Actions/IoTWrapper.cs#L526)
- [GetThingShadow](Actions/IoTWrapper.cs#L312)
- [ListCertificates](Actions/IoTWrapper.cs#L243)
- [ListThings](Actions/IoTWrapper.cs#L614)
- [ListTopicRules](Actions/IoTWrapper.cs#L373)
- [SearchIndex](Actions/IoTWrapper.cs#L402)
- [UpdateThing](Actions/IoTWrapper.cs#L119)
- [UpdateThingShadow](Actions/IoTWrapper.cs#L280)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello AWS IoT

This example shows you how to get started using AWS IoT Core.


#### Learn the basics

This example shows you how to do the following:

- Create an AWS IoT Thing.
- Generate a device certificate.
- Update an AWS IoT Thing with Attributes.
- Return a unique endpoint specific to the Amazon Web Services account.
- List your AWS IoT certificates.
- Create an AWS IoT shadow that refers to a digital representation or virtual twin of a physical IoT device.
- Write out the state information, in JSON format.
- Creates a rule that is an administrator-level action.
- List your rules.
- Search things using the Thing name.
- Clean up resources.

<!--custom.basic_prereqs.iot_Scenario.start-->
<!--custom.basic_prereqs.iot_Scenario.end-->


<!--custom.basics.iot_Scenario.start-->
<!--custom.basics.iot_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv4` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [AWS IoT Core Developer Guide](https://docs.aws.amazon.com/iot/latest/developerguide/what-is-aws-iot.html)
- [AWS IoT Core API Reference](https://docs.aws.amazon.com/iot/latest/apireference/Welcome.html)
- [SDK for .NET (v4) AWS IoT reference](https://docs.aws.amazon.com/sdkfornet/v4/apidocs/items/IoT/NIoT.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
