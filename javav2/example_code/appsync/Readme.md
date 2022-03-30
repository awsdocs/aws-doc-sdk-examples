# AWS AppSync code examples for the AWS SDK for Java

## Overview
This README discusses how to run and test the AWS SDK for Java (V2) examples for AWS AppSync.

AWS AppSync provides a robust, scalable GraphQL interface for application developers to combine data from multiple sources, including Amazon DynamoDB, AWS Lambda, and HTTP APIs.

## ⚠️ Important
* The SDK for Java examples perform AWS operations for the account and AWS Region for which you've specified credentials. Running these examples might incur charges on your account. For details about the charges you can expect for a given service and API operation, see [AWS Pricing](https://aws.amazon.com/pricing/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single action

The following examples use the **AppSyncClient** object:

- [Creating an AWS AppSync key](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/appsync/src/main/java/com/example/appsync/CreateApiKey.java) (CreateApiKey command)
- [Creating an AWS AppSync data source](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/appsync/src/main/java/com/example/appsync/CreateDataSource.java) (CreateDataSource command)
- [Deleting an AWS AppSync key](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/appsync/src/main/java/com/example/appsync/DeleteApiKey.java) (DeleteApiKey command)
- [Deleting an AWS AppSync data source](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/appsync/src/main/java/com/example/appsync/DeleteDataSource.java) (DeleteDataSource command)
- [Getting an AWS AppSync data source](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/appsync/src/main/java/com/example/appsync/GetDataSource.java) (GetDataSource command)
- [Listing AWS AppSync keys](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/appsync/src/main/java/com/example/appsync/ListApiKeys.java) (ListApiKeys command)
- [Listing AWS AppSync APIs](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/appsync/src/main/java/com/example/appsync/ListGraphqlApis.java) (ListGraphqlApis command)

## Running the AWS AppSync Java files

Some of these examples perform *destructive* operations on AWS resources, such as deleting an AWS AppSync data source. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. We recommend creating separate test-only resources when experimenting with these examples.

To run these examples, set up your development environment. For more information, 
see [Get started with the SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html). 


 ## Testing the AWS AppSync Java  Java files

You can test the Java code examples for AWS AppSync Java by running a test file named **AppSyncTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from an IDE, such as IntelliJ, or from the command line. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and might incur charges on your account._

 ### Properties file
Before running the AWS Elastic Beanstalk JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define a crawler name used in the tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **apiId** - The id of the API (You can get this value from the AWS Management Console).  
- **dsName** - The name of the data source. 
- **dsRole** - The AWS Identity and Access Management (IAM) service role for the data source. 
- **tableName** - The name of the Amazon DynamoDB table used as the data source.

## Additional resources
* [Developer Guide - AWS SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html).
* [Developer Guide - AWS AppSync](https://docs.aws.amazon.com/appsync/latest/devguide/what-is-appsync.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

