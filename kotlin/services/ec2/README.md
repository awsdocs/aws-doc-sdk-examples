# Amazon EC2 code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with Amazon Elastic Compute Cloud (Amazon EC2).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon EC2 is a web service that provides resizable computing capacity—literally, servers in Amazon's data centers—that you use to build and host your software systems._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `kotlin` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello Amazon EC2](src/main/kotlin/com/kotlin/ec2/DescribeSecurityGroups.kt#L39) (`DescribeSecurityGroups`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/kotlin/com/kotlin/ec2/EC2Scenario.kt)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AllocateAddress](src/main/kotlin/com/kotlin/ec2/AllocateAddress.kt#L41)
- [AssociateAddress](src/main/kotlin/com/kotlin/ec2/EC2Scenario.kt#L289)
- [AuthorizeSecurityGroupIngress](src/main/kotlin/com/kotlin/ec2/EC2Scenario.kt#L507)
- [CreateKeyPair](src/main/kotlin/com/kotlin/ec2/CreateKeyPair.kt#L38)
- [CreateSecurityGroup](src/main/kotlin/com/kotlin/ec2/CreateSecurityGroup.kt#L45)
- [DeleteKeyPair](src/main/kotlin/com/kotlin/ec2/DeleteKeyPair.kt#L38)
- [DeleteSecurityGroup](src/main/kotlin/com/kotlin/ec2/DeleteSecurityGroup.kt#L37)
- [DescribeInstanceTypes](src/main/kotlin/com/kotlin/ec2/EC2Scenario.kt#L420)
- [DescribeInstances](src/main/kotlin/com/kotlin/ec2/DescribeInstances.kt#L23)
- [DescribeKeyPairs](src/main/kotlin/com/kotlin/ec2/DescribeKeyPairs.kt#L23)
- [DescribeSecurityGroups](src/main/kotlin/com/kotlin/ec2/DescribeSecurityGroups.kt#L39)
- [DisassociateAddress](src/main/kotlin/com/kotlin/ec2/EC2Scenario.kt#L276)
- [ReleaseAddress](src/main/kotlin/com/kotlin/ec2/EC2Scenario.kt#L262)
- [RunInstances](src/main/kotlin/com/kotlin/ec2/CreateInstance.kt#L43)
- [StartInstances](src/main/kotlin/com/kotlin/ec2/EC2Scenario.kt#L318)
- [StopInstances](src/main/kotlin/com/kotlin/ec2/EC2Scenario.kt#L337)
- [TerminateInstances](src/main/kotlin/com/kotlin/ec2/TerminateInstance.kt#L38)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon EC2

This example shows you how to get started using Amazon EC2.


#### Learn the basics

This example shows you how to do the following:

- Create a key pair and security group.
- Select an Amazon Machine Image (AMI) and compatible instance type, then create an instance.
- Stop and restart the instance.
- Associate an Elastic IP address with your instance.
- Connect to your instance with SSH, then clean up resources.

<!--custom.basic_prereqs.ec2_Scenario_GetStartedInstances.start-->
<!--custom.basic_prereqs.ec2_Scenario_GetStartedInstances.end-->


<!--custom.basics.ec2_Scenario_GetStartedInstances.start-->
<!--custom.basics.ec2_Scenario_GetStartedInstances.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `kotlin` folder.



<!--custom.tests.start-->

You can run the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test runs, you can view messages that inform you if the tests succeed or fail. For example, the following message informs you that Test 3 passed.

    Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon EC2 resources and might incur charges on your account._

### Properties file

Before running the Amazon EC2 JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define an instance name used for various tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **ami** – An Amazon Machine Image (AMI) value.
- **instanceName** – An instance name. You can get this value from the AWS Management Console.
- **keyPair** – A key pair to use. For example, **TestKeyPair**.
- **groupName** – A group name to use. For example, **TestSecGroup**.
- **groupDesc** – A description of the group. For example, **Test Group**.
- **vpcId** – A VPC ID. You can obtain this value from the AWS Management Console.
- **keyNameSc** - A key pair to use in the scenario test.
- **fileNameSc** - A file name where the key information is written to and used in the scenario test.
- **groupNameSc** - A group name that is used in the scenario test.
- **groupDescSc** - A group name description that is used in the scenario test.
- **vpcIdSc** – A VPC ID that is used in the scenario test.
- **myIpAddress** – The IP address of your development machine that is used in the scenario test.

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

<!--custom.tests.end-->

## Additional resources

- [Amazon EC2 User Guide](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)
- [Amazon EC2 API Reference](https://docs.aws.amazon.com/AWSEC2/latest/APIReference/Welcome.html)
- [SDK for Kotlin Amazon EC2 reference](https://sdk.amazonaws.com/kotlin/api/latest/ec2/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0