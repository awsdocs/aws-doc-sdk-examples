# Amazon RDS code examples for the SDK for Kotlin

This README discusses how to run and test the AWS SDK for Kotlin code examples for Amazon Relational Database Service (Amazon RDS).

Amazon RDS is a collection of managed services that makes it simple to set up, operate, and scale databases in the cloud.

## ⚠️ Important
* Running this code might result in charges to your AWS account. 
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

The credential provider used in all code examples is Shared credentials. For more information, see [Credential providers](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/credential-providers.html).

### Single actions

Code excerpts that show you how to call individual service functions using the **RdsClient** object: 

- [Create an Amazon RDS instance and wait for it to be in an available state](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin/services/rds/src/main/kotlin/com/kotlin/rds/CreateDBInstance.kt) (createDBInstance command)
- [Create an Amazon RDS snapshot](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin/services/rds/src/main/kotlin/com/kotlin/rds/CreateDBSnapshot.kt) (createDBSnapshot command)
- [Delete an Amazon RDS instance](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin/services/rds/src/main/kotlin/com/kotlin/rds/DeleteDBInstance.kt) (deleteDBInstance command)
- [Retrieve attributes that belong to an Amazon RDS account](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin/services/rds/src/main/kotlin/com/kotlin/rds/DescribeAccountAttributes.kt) (describeAccountAttributes command)
- [Describe an Amazon RDS instance](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin/services/rds/src/main/kotlin/com/kotlin/rds/DescribeDBInstances.kt) (describeDBInstances command)
- [Modify an Amazon RDS instance](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin/services/rds/src/main/kotlin/com/kotlin/rdsModifyDBInstance.kt) (modifyDBInstance command)

### Scenario

Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.

- [Perform various Amazon RDS operations](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin/services/rds/src/main/kotlin/com/kotlin/RDSScenario.kt) (multiple commands)

- [Perform various Amazon RDS operations on Aurora Clusters](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin/services/rds/src/main/kotlin/com/kotlin/AuroraScenario.kt) (multiple commands)

### Cross-service examples

Sample applications that work across multiple AWS services.

- [Create a React and Spring REST application that queries Amazon Aurora Serverless data](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin/usecases/serverless_rds)

## Run the examples

To run these examples, set up your development environment to use Gradle to configure and build AWS SDK for Kotlin projects. For more information, 
see [Get started with the SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/get-started.html).

Some of these examples perform *destructive* operations on AWS resources, such as deleting an Amazon RDS instance. **Be very careful** when running an operation that deletes or modifies AWS resources in your account.

## Test the Amazon RDS Java files

⚠️ Running the tests might result in charges to your AWS account.

You can test the Kotlin code examples for Amazon RDS by running a test file named **RDSTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/kotlin** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

 ### Properties file
Before running the Amazon RDS JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define a **dbInstance** identifier value used in the tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **dbInstanceIdentifier** - The database instance identifier.   
- **dbSnapshotIdentifier** - The snapshot identifier.
- **dbName** - The database name.
- **masterUsername** - The user name.
- **masterUserPassword** - The password that corresponds to the user name.
- **newMasterUserPassword** - The updated password that corresponds to the user name.
- **dbGroupNameSc** - The database group name used in the scenario test.
- **dbParameterGroupFamilySc** - The database parameter group name used in the scenario test (such as mysql8.0).
- **dbInstanceIdentifierSc** - The database instance identifier used in the scenario test.
- **dbNameSc** - The database name used in the scenario test.
- **masterUsernameSc** - The user name used in the scenario test.
- **masterUserPasswordSc** - The password that corresponds to the user name used in the scenario test.
- **dbSnapshotIdentifierSc** - The snapshot identifier used in the scenario test.

### Command line
To run the JUnit tests from the command line, you can use the following command.

		mvn test

You will see output from the JUnit tests, as shown here.

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running Tests
	Test 1 passed
	Test 2 passed
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
	[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.22.1:test (default-test) on project AmazonRedshiftServiceIntegrationTest:  There are test failures.
	[ERROR];
	
## Additional resources
* [Developer Guide - AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/get-started.html).
* [Amazon RDS User Guide](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Welcome.html).
* [Interface RdsClient](https://d2n44ts0t4nz5v.cloudfront.net/api/latest/services/rds/aws.sdk.kotlin.services.rds/-rds-client/index.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0	
