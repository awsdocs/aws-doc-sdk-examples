# AWS IoT Core code examples for the SDK for Java

## Overview
This README discusses how to run and test the AWS SDK for Java (V2) examples for AWS IoT Core.

AWS IoT Core enables secure two-way communication between internet-connected devices and AWS services with device gateway and device SDK capabilities.

## ⚠️ Important
* The SDK for Java examples perform AWS operations for the account and AWS Region for which you've specified credentials. Running these examples might incur charges on your account. For details about the charges you can expect for a given service and API operation, see [AWS Pricing](https://aws.amazon.com/pricing/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

The credential provider used in all code examples is the default credential provider. For more information, see [Using credentials](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html).

### Get started

- [Hello AWS IoT Core](src/main/java/com/example/iot/HelloIoT.java) (`ListThings`)


### Single action

The following examples use the **IotClient** object:

- [Create an AWS IoT thing](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/iot/src/main/java/com/example/iot/IotScenario.java) (`CreateThing`)
- [Create an AWS IoT certificate](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/iot/src/main/java/com/example/iot/IotScenario.java) (`CreateKeysAndCertificate`)
- [Create an AWS IoT rule](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/iot/src/main/java/com/example/iot/IotScenario.java) (`CreateTopicRule`)
- [Delete an AWS IoT certificate](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/iot/src/main/java/com/example/iot/IotScenario.java) (`DeleteCertificate`)
- [Delete an AWS IoT thing](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/iot/src/main/java/com/example/iot/IotScenario.java) (`DeleteThing`)
- [Get information about an endpoint](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/iot/src/main/java/com/example/iot/IotScenario.java) (`DescribeEndpoint`)
- [List your AWS IoT certificates](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/iot/src/main/java/com/example/iot/IotScenario.java) (`ListCertificates`)
- [Query the AWS IoT search index.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/iot/src/main/java/com/example/iot/IotScenario.java) (`SearchIndex`)

The following examples use the **IotClient** object:

- [Get the shadow for the specified thing](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/iot/src/main/java/com/example/iot/IotScenario.java) (`GetThingShadow`)
- [Update the shadow for the specified thing](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/iot/src/main/java/com/example/iot/IotScenario.java) (`UpdateThingShadow`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

* [Perform device management use cases ](src/main/java/com/example/iot/IotScenario.java) 

## Running the AWS IoT Java files

Some of these examples perform *destructive* operations on AWS resources, such as deleting an AWS IoT Thing. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. We recommend creating separate test-only resources when experimenting with these examples.

To run these examples, set up your development environment. For more information, 
see [Get started with the SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html). 


 ## Testing the AWS IoT Java files

You can test the Java code examples for AWS IoT by running a test file named **IoTTests**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from an IDE, such as IntelliJ, or from the command line. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that the Test passed.

	Test 2 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and might incur charges on your account._

 ### Properties file
Before running the JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define a tableId used in the tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **roleARN** - The ARN of an IAM role that has permission to work with AWS IOT.
- **snsAction**  - An ARN of an SNS topic.

## Additional resources
* [Developer Guide - AWS SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html).
* [Developer Guide - AWS IoT Core](https://docs.aws.amazon.com/iot/latest/developerguide/iot-gs.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0