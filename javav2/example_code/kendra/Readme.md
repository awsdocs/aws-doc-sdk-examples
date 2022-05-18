# Amazon Kendra code examples for the AWS SDK for Java V2

## Overview
This README discusses how to run and test the Java V2 code examples for Amazon Kendra.

Amazon Kendra is an intelligent search service powered by machine learning (ML).

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single action

The following examples use the **KendraClient** object: 

- [Deleting an Amazon Kendra data source](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/kendra/src/main/java/com/example/kendra/DeleteDataSource.java) (DeleteDataSource command)
- [Deleting an Amazon Kendra index](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/kendra/src/main/java/com/example/kendra/DeleteIndex.java) (DeleteIndex command)
- [Getting statistics about synchronizing Amazon Kendra with a data source](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/dynamodb/src/main/java/com/example/dynamodb/ListDataSourceSyncJobs.java) (ListDataSourceSyncJobs command)
- [Querying an Amazon Kendra index](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/dynamodb/src/main/java/com/example/dynamodb/QueryIndex.java) (Query command)

### Scenario

- [Creating an Amazon Kendra index and data source, and syncing the data source](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/kendra/src/main/java/com/example/kendra/CreateIndexAndDataSourceExample.java) (Multiple commands)

## Running the examples
To run these examples, you can set up your development environment to use Apache Maven or Gradle to configure and build AWS SDK for Java projects. For more information, 
see [Get started with the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html). 

Some of these examples perform *destructive* operations on AWS resources, such as deleting an index. **Be very careful** when running an operation that deletes or modifies AWS resources in your account.

## Tests
⚠️ Running the tests might result in charges to your AWS account.

You can test the Java code examples for Amazon Kendra by running a test file named **KendraTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test is run, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real AWS resources and might incur charges on your account.

 ### Properties file
Before running the Amazon DynamoDB JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define an index name used for various tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **indexName** - The name for the new index.
- **dataSourceName** - The name for the new data source.
- **indexDescription** - The description for the index. 
- **indexRoleArn** – The name of a key to use. For example, **Artist**.
- **s3BucketName** - An Amazon S3 bucket name used as your data source.
- **dataSourceDescription** – The ARN of am IAM role with permission to access the data source.
- **text** – The text used to perform a query operation.
- **dataSourceRoleArn** – The ARN of am IAM role with permission to access the data source.

### Command line
To run the JUnit tests from the command line, you can use the following command.

		mvn test

You will see output from the JUnit tests, as shown here.

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running Kendra Tests
	Running Amazon Kendra   Test 1
	Running Amazon Kendra  Test 2
	...
	Done!
	[INFO] Results:
	[INFO]
	[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
	[INFO]
	INFO] --------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO]--------------------------------------------
	[INFO] Total time:  12.003 s
	[INFO] Finished at: 2020-02-10T14:25:08-05:00
	[INFO] --------------------------------------------

### Unsuccessful tests

If you do not define the correct values in the properties file, your JUnit tests are not successful. You will see an error message such as the following. You need to double-check the values that you set in the properties file and run the tests again.

	[INFO]
	[INFO] --------------------------------------
	[INFO] BUILD FAILURE
	[INFO] --------------------------------------
	[INFO] Total time:  19.038 s
	[INFO] Finished at: 2020-02-10T14:41:51-05:00
	[INFO] ---------------------------------------
	[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.22.1:test (default-test) on project S3J2Project:  There are test failures.
	[ERROR];


## Additional resources
* [Developer guide - AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html).
* [Amazon Kendra Developer Guide](https://docs.aws.amazon.com/kendra/latest/dg/what-is-kendra.html).
* [Interface KendraClient](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/kendra/KendraClient.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0

