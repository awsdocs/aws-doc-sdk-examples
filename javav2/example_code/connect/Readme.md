# Amazon Connect Java code examples

## Overview
This README discusses how to run and test the Java code examples for Amazon Connect.

Amazon Connect is an omnichannel cloud contact center. You can set up a contact center in a few steps, add agents who are located anywhere, and start engaging with your customers.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

The credential provider used in all code examples is ProfileCredentialsProvider. For more information, see [Using credentials](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html).

### Single action

The following examples use the **ConnectClient** object:

- [Create an Amazon Connect instance](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/connect/src/main/java/com/example/connect/CreateInstance.java) (createInstance command)
- [Delete an Amazon Connect instance](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/connect/src/main/java/com/example/connect/DeleteInstance.java) (deleteInstance command)
- [Describe the specified contact](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/connect/src/main/java/com/example/connect/DescribeContact.java) (describeContact command)
- [Describe the specified instance](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/connect/src/main/java/com/example/connect/DescribeInstance.java) (describeInstance command)
- [Describe the specified instance attributes](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/connect/src/main/java/com/example/connect/DescribeInstanceAttribute.java) (describeInstanceAttribute command)
- [Describe the specified contact attributes](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/connect/src/main/java/com/example/connect/GetContactAttributes.java) (getContactAttributes command)
- [Get historical metric data from the specified Amazon Connect instance](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/connect/src/main/java/com/example/connect/GetMetricData.java) (getMetricData command)
- [List Amazon Connect instances](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/connect/src/main/java/com/example/connect/ListInstances.java) (listInstances command)
- [List Amazon Connect instance phone numbers](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/connect/src/main/java/com/example/connect/ListPhoneNumbers.java) (listPhoneNumbers command)
- [List Amazon Connect instance users](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/connect/src/main/java/com/example/connect/ListUsers.java) (listUsers command)
- [Search queues in an Amazon Connect instance](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/connect/src/main/java/com/example/connect/SearchQueues.java) (searchQueues command)


## Run the Amazon Connect Java files

**IMPORTANT**

The Java examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you might incur AWS service charges by running them. For details about the charges you can expect for a given service and operation, see the [AWS Pricing page](https://aws.amazon.com/pricing/).

Some of these examples perform *destructive* operations on AWS resources, such as deleting an Amazon Connect instance. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

To run these examples, you can set up your development environment to use Apache Maven or Gradle to configure and build AWS SDK for Java projects. For more information, 
see [Get started with the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html). 


 ## Test the Amazon Connect Java files

You can test the Java code examples for Amazon Connect by running a test file named **ConnectTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and might incur charges on your account._

 ### Properties file
Before running the Amazon Connect JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define an instance name used in the tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **instanceAlias** - The name for your Amazon Connect instance.   
- **contactId** - The id of the contact (for example, 16417918-7b38-470a-a9a2-bfcfa7cxxxxx).
- **existingInstanceId** - The id of an existing Amazon Connect instance (for example, c13bb6fa-3cf4-45a2-a93e-ebeaf7xxxxxx).
- **targetArn** - The Amazon Resource Name (ARN) of the Amazon Connect instance.

## Additional resources
* [Developer Guide - AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html).
* [Amazon Connect Administrator Guide](https://docs.aws.amazon.com/connect/latest/adminguide/what-is-amazon-connect.html).
* [Interface ConnectClient](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/connect/ConnectClient.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

