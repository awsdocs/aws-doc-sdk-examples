# Identitystore code examples for the SDK for Java (v2)

## Overview
This README discusses how to run and test the AWS SDK for Java (v2) code examples for AWS IdentityStore.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 

This is a Maven application using [AWS Java SDK 2.x](https://github.com/aws/aws-sdk-java-v2) dependencies.

It builds on the basics described in [Get started with the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html).

## Code examples
The following code examples use the **IdentitystoreClient** object. 

- [Creating a Identitystore group](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/identitystore/src/main/java/com/example/identitystore/CreateGroup.java)
    `mvn exec:java -Dexec.mainClass="com.example.identitystore.CreateGroup" -Dexec.args=IDENTITY_STORE_ID GROUP_NAME DESCRIPTION`

- [Adding user to the group](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/identitystore/src/main/java/com/example/identitystore/CreateGroupMembership.java)
    `mvn exec:java -Dexec.mainClass="com.example.identitystore.CreateGroupMembership" -Dexec.args=IDENTITY_STORE_ID GROUP_ID USER_ID`

- [Create a Identitystore user](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/identitystore/src/main/java/com/example/identitystore/CreateUser.java)
    `mvn exec:java -Dexec.mainClass="com.example.identitystore.CreateUser" -Dexec.args=IDENTITY_STORE_ID USER_NAME FIRST_NAME LAST_NAME`

- [Deleting a group from Identitystore](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/identitystore/src/main/java/com/example/identitystore/DeleteGroup.java)
    `mvn exec:java -Dexec.mainClass="com.example.identitystore.DeleteGroup" -Dexec.args=IDENTITY_STORE_ID GROUP_ID`

- [Deleting a user membership from a group](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/identitystore/src/main/java/com/example/identitystore/DeleteGroupMembership.java)
    `mvn exec:java -Dexec.mainClass="com.example.identitystore.DeleteGroupMembership" -Dexec.args=IDENTITY_STORE_ID MEMBERSHIP_ID`

- [Deleting a user from Identitystore](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/identitystore/src/main/java/com/example/identitystore/DeleteUser.java)
    `mvn exec:java -Dexec.mainClass="com.example.identitystore.DeleteUser" -Dexec.args=IDENTITY_STORE_ID USER_ID`

- [Describe group](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/identitystore/src/main/java/com/example/identitystore/DescribeGroup.java)
    `mvn exec:java -Dexec.mainClass="com.example.identitystore.DescribeGroup" -Dexec.args=IDENTITY_STORE_ID GROUP_ID`

- [Describe group membership id](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/identitystore/src/main/java/com/example/identitystore/DescribeGroupMembership.java)
    `mvn exec:java -Dexec.mainClass="com.example.identitystore.DescribeGroupMembership" -Dexec.args=IDENTITY_STORE_ID MEMBERSHIP_ID`
    
- [Describe user](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/identitystore/src/main/java/com/example/identitystore/DescribeUser.java)
    `mvn exec:java -Dexec.mainClass="com.example.identitystore.DescribeUser" -Dexec.args=IDENTITY_STORE_ID USER_ID`

- [Get group id](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/identitystore/src/main/java/com/example/identitystore/GetGroupId.java)
    `mvn exec:java -Dexec.mainClass="com.example.identitystore.GetGroupId" -Dexec.args=IDENTITY_STORE_ID GROUP_ATTRIBUTE_NAME GROUP_ATTRIBUTE_VALUE`

- [Get group membership id](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/identitystore/src/main/java/com/example/identitystore/GetGroupMembershipId.java)
    `mvn exec:java -Dexec.mainClass="com.example.identitystore.GetGroupMembershipId" -Dexec.args=IDENTITY_STORE_ID GROUP_ID USER_ID`

- [Get user id](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/identitystore/src/main/java/com/example/identitystore/GetUserId.java)
    `mvn exec:java -Dexec.mainClass="com.example.identitystore.GetUserId" -Dexec.args=IDENTITY_STORE_ID USER_ATTRIBUTE_NAME USER_ATTRIBUTE_VALUE`

- [Is member in groups](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/identitystore/src/main/java/com/example/identitystore/IsMemberInGroups.java)
    `mvn exec:java -Dexec.mainClass="com.example.identitystore.IsMemberInGroups" -Dexec.args=IDENTITY_STORE_ID USER_ID GROUP_ID1 GROUP_ID2 ...`

- [List group memberships](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/identitystore/src/main/java/com/example/identitystore/ListGroupMemberships.java)
    `mvn exec:java -Dexec.mainClass="com.example.identitystore.ListGroupMemberships" -Dexec.args=IDENTITY_STORE_ID GROUP_ID`

- [List group memberships for member](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/identitystore/src/main/java/com/example/identitystore/ListGroupMembershipsForMember.java)
    `mvn exec:java -Dexec.mainClass="com.example.identitystore.ListGroupMembershipsForMember" -Dexec.args=IDENTITY_STORE_ID USER_ID`

- [List groups](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/identitystore/src/main/java/com/example/identitystore/ListGroups.java)
    `mvn exec:java -Dexec.mainClass="com.example.identitystore.ListGroups" -Dexec.args=IDENTITY_STORE_ID`

- [List users](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/identitystore/src/main/java/com/example/identitystore/ListUsers.java)
    `mvn exec:java -Dexec.mainClass="com.example.identitystore.ListUsers" -Dexec.args=IDENTITY_STORE_ID`

- [Update a group](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/identitystore/src/main/java/com/example/identitystore/UpdateGroup.java)
    `mvn exec:java -Dexec.mainClass="com.example.identitystore.UpdateGroup" -Dexec.args=IDENTITY_STORE_ID GROUP_ID GROUP_ATTRIBUTE_NAME GROUP_ATTRIBUTE_VALUE`

- [Update a user](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/identitystore/src/main/java/com/example/identitystore/UpdateUser.java)
    `mvn exec:java -Dexec.mainClass="com.example.identitystore.UpdateUser" -Dexec.args=IDENTITY_STORE_ID USER_ID USER_ATTRIBUTE_NAME USER_ATTRIBUTE_VALUE`

Note: Replace the arguments with your unique values
               

## Running the IdentityStore code examples for the SDK for Java (v2)   

To run these examples, set up your development environment to use Apache Maven or Gradle to configure and build AWS SDK for Java projects. For more information, 
see [Get started with the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html). 

Some of these examples perform *destructive* operations on AWS resources, such as deleting a user. **Be very careful** when running an operation that deletes or modifies AWS resources in your account.

 ## Testing the AWS Identitystore Java files

You can test the Java code examples for Identitystore by running a test file named **IdentitystoreServiceTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test is run, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and might incur charges on your account._

 ### Properties file
Before running the Identitystore JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define an instance name used for various tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **identitystoreId** 	- Identitystore ID.  
- **groupName** 		- A group name that is used to create a group.  
- **groupDesc** 		- A group description that is used to create a group.  
- **userName**  		- A user name that is used to create a user. 
- **givenName**			- The first name of the user that is used to create a user.
- **familyName**		- The last name os the user that is used to create a user.

### Command line
To run the JUnit tests from the command line, you can use the following command.

		mvn test

You will see output from the JUnit tests, as shown here.

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running IdentitystoreServiceTest
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
	[INFO] Finished at: 2023-02-23T14:25:08-05:00
	[INFO] --------------------------------------------

### Unsuccessful tests

If you don't define the correct values in the properties file, your JUnit tests are not successful. You will see an error message such as the following. You need to double-check the values that you set in the properties file and run the tests again.

	[INFO]
	[INFO] --------------------------------------
	[INFO] BUILD FAILURE
	[INFO] --------------------------------------
	[INFO] Total time:  19.038 s
	[INFO] Finished at: 2023-02-23T14:41:51-05:00
	[INFO] ---------------------------------------
	[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.22.1:test (default-test) on project IdentitystoreJ2Project:  There are test failures.
	[ERROR];

## Additional resources
* [Developer guide - AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html).
* [AWS Identitystore User Guide](https://docs.aws.amazon.com/singlesignon/latest/userguide/what-is.html).
* [Interface IdentitystoreClient](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/identitystore/IdentitystoreClient.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
