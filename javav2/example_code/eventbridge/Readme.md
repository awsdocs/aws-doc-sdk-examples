# Amazon EventBridge code examples for the SDK for Java

## Overview
This README discusses how to run and test the AWS SDK for Java (v2) examples for Amazon EventBridge.

Amazon EventBridge is a serverless service that uses events to connect application components together, making it easier for you to build scalable event-driven applications.

## ⚠️ Important
* Running this code might result in charges to your AWS account. See [AWS Pricing](https://aws.amazon.com/pricing/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

The credential provider used in all code examples is ProfileCredentialsProvider. For more information, see [Using credentials](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html).

### Get started

- [Hello Amazon EventBridge](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/eventbridge/src/main/java/com/example/eventbridge/HelloEventBridge.java) (listEventBuses command)

### Single action

Code excerpts that show you how to call individual service functions.

- [Add a target](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/eventbridge/src/main/java/com/example/eventbridge/EventbridgeMVP.java) (putTargets command)
- [Create an Amazon EventBridge rule](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/eventbridge/src/main/java/com/example/eventbridge/EventbridgeMVP.java) (putRule command)
- [Delete a rule](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/eventbridge/src/main/java/com/example/eventbridge/EventbridgeMVP.java) (deleteRule command)
- [Describe an Amazon EventBridge rule](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/eventbridge/src/main/java/com/example/eventbridge/EventbridgeMVP.java) (describeRule command)
- [Disable an Amazon EventBridge rule](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/eventbridge/src/main/java/com/example/eventbridge/EventbridgeMVP.java) (disableRule command)
- [Enable an Amazon EventBridge rule](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/eventbridge/src/main/java/com/example/eventbridge/EventbridgeMVP.java) (enableRule command)
- [List targets for a rule](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/eventbridge/src/main/java/com/example/eventbridge/EventbridgeMVP.java) (listTargetsByRule command)
- [List Amazon EventBridge rules](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/keyspaces/src/main/java/com/example/keyspace/ScenarioKeyspaces.java) (listRules command)
- [Remove targets from a rule](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/keyspaces/src/main/java/com/example/keyspace/ScenarioKeyspaces.java) (removeTargets command)
- [Send custom events to Amazon EventBridge](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/keyspaces/src/main/java/com/example/keyspace/ScenarioKeyspaces.java) (putEvents command)

### Scenario 

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- [Get started with Amazon EventBridge](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/eventbridge/src/main/java/com/example/eventbridge/EventbridgeMVP.java) (multiple commands)

## Run the examples

### Prerequisites

To run these examples, set up your development environment. For more information, 
see [Get started with the SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html). 

**Be very careful** when running an operation that deletes or modifies AWS resources in your account. We recommend creating separate test-only resources when experimenting with these examples.

 ## Test the examples
 
 ⚠️ Running the tests might result in charges to your AWS account.

You can test the Java code example for Amazon EventBridge by running a test file named **EventBridgeTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

To successfully run the JUnit tests, define the following values in the test:

- **roleName** - The name of the role to create." +
- **bucketName** - The Amazon Simple Storage Service (Amazon S3) bucket name to create." +
- **topicName** - The name of the AWS Simple Notification Service (AWS SNS) topic to create." +
- **eventRuleName** - The Amazon EventBridge rule name to create." ;

You can run the JUnit tests from an IDE, such as IntelliJ, or from the command line. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real AWS resources and might incur charges on your account._

## Additional resources
* [Developer Guide - AWS SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html).
* [User Guide - Amazon EventBridge](https://docs.amazonaws.cn/en_us/eventbridge/latest/userguide/eb-what-is.html).
* [Interface EventBridgeClient](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/eventbridge/EventBridgeClient.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

