# Amazon Timestream code examples for the SDK for Java

## Overview
This README discusses how to run and test the AWS SDK for Java (v2) code examples for Amazon Timestream.

Amazon Timestream is a fast, scalable, fully managed, purpose-built time series database for IoT and operational applications that makes it easy to store and analyze trillions of time series data points per day.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single action

The following examples use the **TimestreamWriteClient** object:

- [Creating a database](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/timestream/src/main/java/com/timestream/write/CreateDatabase.java) (CreateDatabase command)
- [Creating a database table](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/timestream/src/main/java/com/timestream/write/CreateTable.java) (CreateTable command)
- [Deleting a database](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/timestream/src/main/java/com/timestream/write/DeleteDatabase.java) (DeleteDatabase command)
- [Describing a database](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/timestream/src/main/java/com/timestream/write/DescribeDatabase.java) (DescribeDatabase command)
- [Describing a database table](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/timestream/src/main/java/com/timestream/write/DescribeTable.java) (DescribeTable command)
- [Listing all databases](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/timestream/src/main/java/com/timestream/write/write/ListDatabases.java) (ListDatabases command)
- [Listing all tables](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/timestream/src/main/java/com/timestream/write/ListTables.java) (ListTables command)
- [Updating a database table](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/timestream/src/main/java/com/timestream/write/write/UpdateTable.java) (UpdateTable command)
- [Writing data into a table](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/timestream/src/main/java/com/timestream/write/WriteData.java) (writeRecords command)


## Running the Amazon Timestream Java files

**IMPORTANT**

The Java examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you might incur AWS service charges by running them. For details about the charges you can expect for a given service and operation, see the [AWS Pricing page](https://aws.amazon.com/pricing/).   

Some of these examples perform *destructive* operations on AWS resources, such as deleting a table. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. We recommend creating separate test-only resources when experimenting with these examples.

To run these examples, set up your development environment to use Apache Maven or Gradle to configure and build AWS SDK for Java projects. For more information, 
see [Get started with the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html).


 ## Testing the Amazon Timestream Java files

You can test the Amazon Timestream Java code examples by running a test file named **TimestreamTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can execute the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test is executed, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real AWS resources and might incur charges on your account._

 ### Properties file
Before running the Amazon Timestream JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to execute the JUnit tests. For example, you define a database name used for various tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **dbName** - The name of the database. 

- **newTable** – The name of a table.

### Command line
To execute the JUnit tests from the command line, you can use the following command.

		mvn test

You will see output from the JUnit tests, as shown here.

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running TimestreamTest
	Test 3 passed
	Test 4 passed
	...
	

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
* [Developer Guide - AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html).
* [Amazon Timestream Developer Guide](https://docs.aws.amazon.com/timestream/latest/developerguide/what-is-timestream.html).
* [Interface TimestreamWriteClient](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/timestreamwrite/TimestreamWriteClient.html).	

