# Amazon EC2 code examples for the SDK for Java

## Overview
This README discusses how to run and test the Java code examples for Amazon Elastic Compute Cloud (Amazon EC2).

Amazon EC2 provides secure, resizable compute in the cloud, offering the broadest choice of processor, storage, networking, OS, and purchase model.

## ⚠️ Important
* The SDK for Java examples perform AWS operations for the account and AWS Region for which you've specified credentials. Running these examples might incur charges on your account. For details about the charges you can expect for a given service and API operation, see [AWS Pricing](https://aws.amazon.com/pricing/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

The credential provider used in all code examples is ProfileCredentialsProvider. For more information, see [Using credentials](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html).


### Get started

- [Hello service](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/ec2/src/main/java/com/example/ec2/DeleteKeyPair.java) (describeServices command)

### Single action

Code excerpts that show you how to call individual service functions.

- [Allocate an elastic IP address](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/ec2/src/main/java/com/example/ec2/AllocateAddress.java) (allocateAddress command)
- [Create an Amazon EC2 instance](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/ec2/src/main/java/com/example/ec2/CreateInstance.java) (runInstances command)
- [Create an Amazon EC2 key pair](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/ec2/src/main/java/com/example/ec2/CreateKeyPair.java) (createKeyPair command)
- [Create an Amazon EC2 security group](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/ec2/src/main/java/com/example/ec2/CreateSecurityGroup.java) (createSecurityGroup command)


### Scenario 

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- [Get started with Amazon EC2 instances](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/support/src/main/javav2/example_code/ec2/src/main/java/com/example/ec2/EC2Scenario.java) (Multiple commands)

## Run the AWS Support Java file



 ## Testing the Amazon EC2 Java files

You can test the Java code examples for Amazon EC2 by running a test file named **AWSEC2ServiceIntegrationTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test is run, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon EC2 resources and may incur charges on your account._

 ### Properties file
Before running the Amazon EC2 JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define an instance name used for various tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **ami** – An Amazon Machine Image (AMI) value.
- **instanceName** – An instance name. You can obtain this value from the AWS Management Console.
- **keyPair** – A key pair to use. For example, **TestKeyPair**.
- **groupName** – A group name to use. For example, **TestSecGroup**.
- **groupDesc** – A description of the group. For example, **Test Group**.
- **vpcId** – A VPC ID. You can obtain this value from the AWS Management Console.

### Command line
To run the JUnit tests from the command line, you can use the following command.

		mvn test

You will see output from the JUnit tests, as shown here.

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running AWSEC2ServiceIntegrationTest
	Test 1 passed
	Test 2 passed
	...
	Done!
	[INFO] Results:
	[INFO]
	[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
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
