# AWS Identity and Access Management Java code examples

This README discusses how to run and test the Java code examples for AWS Identity and Access Management (IAM).

## Running the IAM Java files

**IMPORTANT**

The Java examples perform AWS operations for the account and AWS Region for which you've specified credentials, and you might incur AWS service charges by running them. See the [AWS Pricing page](https://aws.amazon.com/pricing/) for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting a user. **Be very careful** when running an operation that
deletes or modifies AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

You will find these IAM Java examples:

- **AccessKeyLastUsed** - Demonstrates how to display the time that an access key was last used.
- **AttachRolePolicy** - Demonstrates how to attach a policy to an existing AWS IAM role.
- **CreateAccessKey** - Demonstrates how to create an access key for an AWS IAM user.
- **CreateAccountAlias** - Demonstrates how to create an alias for an AWS account.
- **CreatePolicy** - Demonstrates how to create a policy.
- **CreateRole** - Demonstrates how to create an AWS IAM role
- **CreateUser** - Demonstrates how to create an AWS IAM user.
- **DeleteAccessKey** - Demonstrates how to delete an access key from an AWS IAM user.
- **DeleteAccountAlias** - Demonstrates how to delete an alias from an AWS account.
- **DeletePolicy** - Demonstrates how to delete a fixed policy with a provided policy name.
- **DeleteServerCertificate** - Demonstrates how to delete an AWS IAM server certificate.
- **DeleteUser** - Demonstrates how to delete an AWS IAM user.
- **DetachRolePolicy** - Demonstrates how to detach a policy from an AWS IAM role.
- **GetPolicy** - Demonstrates how to get the details for an AWS IAM policy.
- **GetRole** - Demonstrates how to get information about the specified AWS IAM role.
- **GetServerCertificate** - Demonstrates how to get information about an AWS IAM server certificate.
- **IAMScenario** - Demonstrates how to perform various AWS IAM operations.
- **ListAccessKeys**  - Demonstrates how to list access keys associated with an AWS IAM user.
- **ListAccountAliases** - Demonstrates how to list all aliases associated with an AWS account.
- **ListServerCertificates** - Demonstrates how to list all server certificates associated with an AWS account.
- **ListUsers** - Demonstrates how to list all AWS IAM users.
- **UpdateServerCertificate** - Demonstrates how to update the name of an AWS IAM server certificate.
- **UpdateUser** - Demonstrates how to update the name of an AWS IAM user.


**JSON File**

To successfully run the **IAMScenario**, you need a JSON file that contains the information to create a role. Included in this file is the ARN of the IAM user for the trust relationship. The following JSON shows an example. 

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

To run these examples, you can setup your development environment to use Apache Maven or Gradle to configure and build AWS SDK for Java projects. For more information, 
see [Get started with the AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html). 


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
