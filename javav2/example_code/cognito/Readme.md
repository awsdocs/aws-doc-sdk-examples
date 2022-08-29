#  Amazon Cognito code examples for the SDK for Java

## Overview
This README discusses how to run and test the AWS SDK for Java (V2) examples for Amazon Cognito.

Amazon Cognito is a simple user identity and data synchronization service that helps you securely manage and synchronize app data for your users across their mobile devices.

## ⚠️ Important
* The SDK for Java examples perform AWS operations for the account and AWS Region for which you've specified credentials. Running these examples might incur charges on your account. For details about the charges you can expect for a given service and API operation, see [AWS Pricing](https://aws.amazon.com/pricing/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

The credential provider used in all code examples is ProfileCredentialsProvider. For more information, see [Using credentials](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html).

### Single action

The following examples use the **CognitoIdentityClient** object:

- [Associating an Amazon Cognito identity pool with an identity provider](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/cognito/src/main/java/com/example/cognito/AddLoginProvider.java) (updateIdentityPool command)
- [Creating an Amazon Cognito identity pool](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/cognito/src/main/java/com/example/cognito/CreateIdentityPool.java) (createIdentityPool command)
- [Deleting an existing Amazon Cognito identity pool](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/cognito/src/main/java/com/example/cognito/DeleteIdentityPool.java) (deleteIdentityPool command)
- [Retrieving the client ID from an identity provider](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/cognito/src/main/java/com/example/cognito/GetId.java) (getId command)
- [Retrieving credentials for an identity in an Amazon Cognito identity pool](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/cognito/src/main/java/com/example/cognito/GetIdentityCredentials.java) (getCredentialsForIdentity command)
- [Listing identities that belong to an Amazon Cognito identity pool](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/cognito/src/main/java/com/example/cognito/ListIdentities.java) (listIdentities command)
- [Listing Amazon Cognito identity pools](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/cognito/src/main/java/com/example/cognito/ListIdentityPools.java) (listIdentityPools command)

The following examples use the **CognitoIdentityProviderClient** object:

- [Adding a new user to your user pool](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/cognito/src/main/java/com/example/cognito/CreateUser.java) (adminCreateUser command)
- [Creating a user pool for Amazon Cognito](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/cognito/src/main/java/com/example/cognito/CreateUserPool.java) (createUserPool command)
- [Creating a user pool client for Amazon Cognito](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/cognito/src/main/java/com/example/cognito/CreateUserPoolClient.java) (createUserPoolClient command)
- [Deleting an existing user pool](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/cognito/src/main/java/com/example/cognito/DeleteUserPool.java) (deleteUserPool command)
- [Listing existing user pool clients](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/cognito/src/main/java/com/example/cognito/ListUserPoolClients.java) (listUserPoolClients command)
- [Listing existing users in the specified user pool](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/cognito/src/main/java/com/example/cognito/ListUserPools.java) (listUserPools command)
- [Registering a user in the specified Amazon Cognito user pool](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/cognito/src/main/java/com/example/cognito/SignUpUser.java) (signUp command)

The following example is an Amazon Cognito scenario:

- [Signing up a new user with Amazon Cognito and associate the user with an MFA application for multi-factor authentication](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/cognito/src/main/java/com/example/cognito/CognitoMVP.java) (various commands)

## Running the Amazon Cognito Java files

The credential provider used in all code examples is ProfileCredentialsProvider. For more information, see [Using credentials](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials.html).

**IMPORTANT**

The Java examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a user pool. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

To run these examples, you can setup your development environment to use Apache Maven or Gradle to configure and build AWS SDK for Java projects. For more information, 
see [Get started with the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html). 


 ## Testing the Amazon Cognito files

You can test the Java code examples for Amazon Cognito by running a test file named **AmazonCognitoTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can execute the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test is run, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and may incur charges on your account._

 ### Properties file
Before running the Amazon Cognito JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to execute the JUnit tests. For example, you define an instance name used for various tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **userPoolName** - The name of the user pool that is created.  
- **username** – The user name that is used in the **CreateAdminUser** test.
- **email** - The user email that is used in the **CreateAdminUser** test.
- **clientName** - The client name that is used in the **CreateUserPoolClient** test.  
- **identityPoolName** - The pool name used in the **CreateIdentityPool** test. 
- **confirmationCode** - The confirmation code that is used in the **ConfirmSignUp** test.

### Command line
To execute the JUnit tests from the command line, you can use the following command.

		mvn test

You will see output from the JUnit tests, as shown here.

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running AmazonCognitoTest
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
	
## Additional resources
* [Developer Guide - AWS SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html).
* [Developer Guide - Amazon Cognito](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-identity-pools.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0	
