# Amazon EC2 code examples for the SDK for Kotlin

## Overview
This README discusses how to run and test the AWS SDK for Kotlin code examples for Amazon Elastic Compute Cloud (Amazon EC2).

Amazon EC2 provides secure, resizable compute in the cloud, offering the broadest choice of processor, storage, networking, OS, and purchase model.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

The credential provider used in all code examples is Shared credentials. For more information, see [Using credentials](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/credential-providers.html).

### Get started

- [Hello Amazon EC2](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/ec2/src/main/kotlin/com/kotlin/ec2/DeleteSecurityGroup.kt) (describeSecurityGroups command)

### Single actions

Code excerpts that show you how to call individual service functions.

- [Allocate an elastic IP address](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/ec2/src/main/kotlin/com/kotlin/ec2/AllocateAddress.kt) (allocateAddress command)
- [Create an Amazon EC2 instance](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/ec2/src/main/kotlin/com/kotlin/ec2/CreateInstance.kt) (runInstances command)
- [Create an Amazon EC2 key pair](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/ec2/src/main/kotlin/com/kotlin/ec2/CreateKeyPair.kt) (createKeyPair command)
- [Create an Amazon EC2 security group](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/ec2/src/main/kotlin/com/kotlin/ec2/CreateSecurityGroup.kt) (createSecurityGroup command)
- [Delete an Amazon EC2 key pair](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/ec2/src/main/kotlin/com/kotlin/ec2/DeleteKeyPair.kt) (deleteKeyPair command)
- [Delete an Amazon EC2 security group](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/ec2/src/main/kotlin/com/kotlin/ec2/DeleteSecurityGroup.kt) (deleteSecurityGroup command)
- [Describe an Amazon EC2 account](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/ec2/src/main/kotlin/com/kotlin/ec2/DescribeAccount.kt) (describeAccountAttributes command)
- [Describe elastic IP addresses](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/ec2/src/main/kotlin/com/kotlin/ec2/DescribeAddresses.kt) (describeAddresses command)
- [Describe Amazon EC2 instances](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/ec2/src/main/kotlin/com/kotlin/ec2/DescribeInstances.kt) (describeInstances command)
- [Describe Amazon EC2 instance tags](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/ec2/src/main/kotlin/com/kotlin/ec2/DescribeInstanceTags.kt) (describeTags command)
- [Describe Amazon EC2 key pairs](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/ec2/src/main/kotlin/com/kotlin/ec2/DescribeKeyPairs.kt) (describeKeyPairs command)
- [Describe Amazon EC2 regions and zones](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/ec2/src/main/kotlin/com/kotlin/ec2/DescribeRegionsAndZones.kt) (describeRegions command)
- [Describe Amazon EC2 security groups](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/ec2/src/main/kotlin/com/kotlin/ec2/DescribeSecurityGroups.kt) (describeSecurityGroups command)
- [Describe Amazon EC2 VPCs](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/ec2/src/main/kotlin/com/kotlin/ec2/DescribeVPCs.kt) (describeVpcs command)
- [Find running EC2 instances](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/ec2/src/main/kotlin/com/kotlin/ec2/FindRunningInstances.kt) (monitorInstances command)
- [Terminate Amazon EC2 instances](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/ec2/src/main/kotlin/com/kotlin/ec2/TerminateInstance.kt) (terminateInstances command)

### Scenario 

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- [Get started with Amazon EC2 instances](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/support/src/main/javav2/example_code/ec2/src/main/java/com/example/ec2/EC2Scenario.java) (Multiple commands)

## Run the  Amazon EC2 Kotlin files

**Be very careful** when running an operation that deletes or modifies AWS resources in your account. We recommend creating separate test-only resources when experimenting with these examples.

To run these examples, set up your development environment. For more information, 
see [Get started with the SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/get-started.html). 

 ## Test the Amazon EC2 Kotlin files

You can test the Kotlin code examples for Amazon EC2 by running a test file named **EC2Test**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/kotlin** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test is ran, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

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
- **keyNameSc** - A key pair to use in the scenario test.
- **fileNameSc** - A file name where the key information is written to and used in the scenario test. 
- **groupNameSc** - A group name that is used in the scenario test. 
- **groupDescSc** - A group name description that is used in the scenario test. 
- **vpcIdSc** – A VPC ID that is used in the scenario test.

### Command line
To run the JUnit tests from the command line, you can use the following command.

		mvn test

You will see output from the JUnit tests, as shown here.

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running EC2Test
	Test 1 passed
	Test 2 passed
	...
	Done!
	[INFO] Results:
	[INFO]
	[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
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
	[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.22.1:test (default-test).  There are test failures.
	[ERROR];
	
## Additional resources
* [Developer Guide - AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/get-started.html)
* [Amazon EC2 documentation](https://docs.aws.amazon.com/ec2/index.html)
* [Ec2Client - Kotlin Reference](https://sdk.amazonaws.com/kotlin/api/latest/support/aws.sdk.kotlin.services.support/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
	
