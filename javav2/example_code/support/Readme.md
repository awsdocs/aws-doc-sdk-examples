# AWS Support code examples for the SDK for Java

## Overview
This README discusses how to run and test the AWS SDK for Java (V2) examples for AWS Support.

AWS Support is one-on-one, fast-response support from experienced technical support engineers. The service helps customers use AWS's products and features. With pay-by-the-month pricing and unlimited support cases, customers are freed from long-term commitments.

## ⚠️ Important
* The SDK for Java examples perform AWS operations for the account and AWS Region for which you've specified credentials. Running these examples might incur charges on your account. For details about the charges you can expect for a given service and API operation, see [AWS Pricing](https://aws.amazon.com/pricing/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

The credential provider used in all code examples is ProfileCredentialsProvider. For more information, see [Using credentials](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html).

### Get started

- [Hello service](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/support/src/main/java/com/example/support/HelloSupport.java) (describeServices command)

### Single action

The following examples use the **SupportClient** object:

- [Add communication with the attachment to the support case.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/support/src/main/java/com/example/support/SupportScenario.java) (addCommunicationToCase command)
- [Create an attachment set.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/support/src/main/java/com/example/support/SupportScenario.java) (addAttachmentsToSet command)
- [Create a support case.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/support/src/main/java/com/example/support/SupportScenario.java) (createCase command)
- [Describe the attachment set.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/support/src/main/java/com/example/support/SupportScenario.java) (describeAttachment command)
- [Get and display Support severity levels.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/support/src/main/java/com/example/support/SupportScenario.java) (describeSeverityLevels command)
- [Get open support cases.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/support/src/main/java/com/example/support/SupportScenario.java) (describeCases command)
- [List the communications of the support case.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/support/src/main/java/com/example/support/SupportScenario.java) (describeCommunications command)
- [Resolve the support case.](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/support/src/main/java/com/example/support/SupportScenario.java) (resolveCase command)

### Scenario 

The following Scenario uses the **SupportClient** object:

- [Perform AWS Support operations](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/support/src/main/java/com/example/support/SupportScenario.java) (Multiple commands)

## Running the AWS Support Java file

**Be very careful** when running an operation that deletes or modifies AWS resources in your account. We recommend creating separate test-only resources when experimenting with these examples.

To run these examples, set up your development environment. For more information, 
see [Get started with the SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html). 


 ## Testing the AWS Support Java file

You can test the Java code example for AWS Support by running a test file named **SupportTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from an IDE, such as IntelliJ, or from the command line. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and might incur charges on your account._

 ### Properties file
Before running the JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define a crawler name used in the tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **fileAttachment** - The file can be a simple saved .txt file to use as an email attachment.  

## Additional resources
* [Developer Guide - AWS SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html).
* [Developer Guide - AWS Support](https://docs.aws.amazon.com/awssupport/latest/user/getting-started.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
