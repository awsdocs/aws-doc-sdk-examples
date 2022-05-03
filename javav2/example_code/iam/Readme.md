# IAM code examples for the SDK for Java (v2)

## Overview
This README discusses how to run and test the AWS SDK for Java (v2) code examples for AWS Identity and Access Management (IAM).

With IAM, the right people and job roles in your organization (identities) can access the tools they need to do their jobs.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples
The following code examples use the **IamClient** object. 

- [Displaying the time that an access key was last used](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/AccessKeyLastUsed.java) (GetAccessKeyLastUsed command)
- [Attaching a policy to an existing IAM role](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/AttachRolePolicy.java) (ListAttachedRolePolicies command)
- [Creating an access key for an IAM user](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/CreateAccessKey.java) (CreateAccessKey command)
- [Creating an alias for an AWS account](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/CreateAccountAlias.java) (CreateAccountAlias command)
- [Creating an IAM policy](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/CreatePolicy.java) (CreatePolicy command)
- [Creating an IAM role](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/CreateRole.java) (CreateRole command)
- [Creating an IAM user](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/CreateUser.java) (CreateUser command)
- [Deleting an access key from an IAM user](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/DeleteAccessKey.java) (DeleteAccessKey command)
- [Deleting an alias from an AWS account](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/DeleteAccountAlias.java) (DeleteAccountAlias command)
- [Deleting a fixed policy with a provided policy name](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/DeletePolicy.java) (DeletePolicy command)
- [Deleting an IAM server certificate](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/DeleteServerCertificate.java) (DeleteServerCertificate command)
- [Deleting an IAM user](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/DeleteUser.java) (DeleteUser command)
- [Detaching a policy from an IAM role](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/DetachRolePolicy.java) (DetachRolePolicy command)
- [Getting the details for an IAM policy](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/GetPolicy.java) (GetPolicy command)
- [Getting information about the specified IAM role](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/GetRole.java) (GetRole command)
- [Getting information about an IAM server certificate](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/GetServerCertificate.java) (GetServerCertificate command)
- [Performing various IAM operations](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/IAMScenario.java) (Mulitple commands)
- [Listing access keys associated with an IAM user](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/ListAccessKeys.java) (ListAccessKeys commands)
- [Listing all aliases associated with an AWS account](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/ListAccountAliases.java) (ListAccountAliases commands)
- [Listing all server certificates associated with an AWS account](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/ListServerCertificates.java) (ListServerCertificates commands)
- [Listing all IAM users](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/ListUsers.java) (ListUsers commands)
- [Updating an IAM server certificate](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/UpdateServerCertificate.java) (UpdateServerCertificate commands)
- [Updating an IAM user](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code/iam/src/main/java/com/example/iam/UpdateUser.java) (UpdateUser commands)
               
**JSON File**

To successfully run the **IAMScenario**, you need a JSON file that contains the information to create a role. Included in this file is the Amazon Resource Name (ARN) of the IAM user for the trust relationship. The following JSON shows an example. 

    {
     "Version": "2012-10-17",
      "Statement": [
       {
       "Effect": "Allow",
       "Principal": {
         "AWS": "<Enter the IAM User ARN value>"
       },
       "Action": "sts:AssumeRole",
       "Condition": {}
      }
     ]
    }
    
## Running the IAM code examples for the SDK for Java (v2)   

To run these examples, setup your development environment to use Apache Maven or Gradle to configure and build AWS SDK for Java projects. For more information, 
see [Get started with the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html). 

Some of these examples perform *destructive* operations on AWS resources, such as deleting a user. **Be very careful** when running an operation that deletes or modifies AWS resources in your account.

 ## Testing the AWS Identity and Access Management Java files

You can test the Java code examples for IAM by running a test file named **IAMServiceTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test is run, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and might incur charges on your account._

 ### Properties file
Before running the IAM JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define an instance name used for various tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **userName** - A user name that is used to create a user.  
- **policyName** – A policy name that is used to create a policy.
- **roleName** – A role name. You can obtain this value from the AWS Management Console.
- **accountAlias** – A value that is used to create an account alias. For example, **myawsaccount10**.-
- **usernameSc** -  The name of the IAM user to create for the IAMScenario test.
- **policyNameSc** - The name of the policy to create for the IAMScenario test.
- **roleNameSc** - The name of the role to create for the IAMScenario test.
- **roleSessionName** - The name of the session required for the assumeRole operation for the IAMScenario test.
- **fileLocationSc** - The file location of the JSON file for the IAMScenario test. 
- **bucketNameSc** - The name of the Amazon S3 bucket from which objects are read for the IAMScenario test.

### Command line
To run the JUnit tests from the command line, you can use the following command.

		mvn test

You will see output from the JUnit tests, as shown here.

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running IAMServiceTest
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

If you don't define the correct values in the properties file, your JUnit tests are not successful. You will see an error message such as the following. You need to double-check the values that you set in the properties file and run the tests again.

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
* [AWS Identity and Access Management User Guide](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html).
* [Interface IamClient](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/iam/IamClient.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
