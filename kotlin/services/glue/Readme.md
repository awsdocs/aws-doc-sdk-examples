# AWS Glue code examples for the SDK for Kotlin

## Overview
This README discusses how to run and test the AWS SDK for Kotlin code examples for AWS Glue.

AWS Glue is a serverless data integration service that makes it easy to discover, prepare, and combine data for analytics, machine learning, and application development.

## ⚠️ Important
* The SDK for Kotlin examples perform AWS operations for the account and AWS Region for which you've specified credentials. Running these examples might incur charges on your account. For details about the charges you can expect for a given service and API operation, see the [AWS Pricing page](https://aws.amazon.com/pricing/).
* Running the tests might result in charges to your AWS account.
*  We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single action

The following examples use the **GlueClient** object:

- [Creating an AWS Glue crawler](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/kotlin/glue/CreateCrawler.kt) (CreateCrawler command)
- [Deleting an AWS Glue crawler](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/kotlin/glue/DeleteCrawler.kt) (DeleteCrawler command)
- [Getting a specific AWS Glue crawler](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/kotlin/glue/GetCrawler.kt) (GetCrawler command)
- [Getting AWS Glue crawlers](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/kotlin/glue/GetCrawlers.kt) (GetCrawlers command)
- [Getting a database](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/kotlin/glue/GetDatabases.kt) (GetDatabase command)
- [Getting a job run request](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/kotlin/glue/GetJobRun.kt) (GetJobRun command)
- [Getting all AWS Glue jobs](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/kotlin/glue/GetJobs.kt) (GetJobs command)
- [Getting all AWS Glue workflows](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/kotlin/glue/ListWorkflows.kt) (ListWorkflows command)
- [Searching AWS Glue tables based on properties](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/kotlin/glue/SearchTables.kt) (SearchTables command)
- [Starting an AWS Glue crawler](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/kotlin/glue/StartCrawler.kt) (StartCrawler command)
- [Stopping an AWS Glue crawler](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/kotlin/glue/StopCrawler.kt) (StopCrawler command)

### Scenario

- [Performing various AWS Glue operations](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/kotlin/glue/GlueScenario.kt) (Multiple commands)

## Running the AWS Glue Kotlin files

Some of these examples perform *destructive* operations on AWS resources, such as deleting a crawler by running the **DeleteCrawler** example. **Be very careful** when running an operation that deletes or modifies AWS resources in your account. We recommend creating separate test-only resources when experimenting with these examples.

To run these examples, set up your development environment to use Gradle. For more information, 
see [Get started with the SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/get-started.html). 


 ## Testing the AWS Glue Kotlin files

You can test the Kotlin code examples for AWS Glue by running a test file named **GlueTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/kotlin** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from an IDE, such as IntelliJ, or from the command line. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

	Test 3 passed

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and might incur charges on your account._

 ### Properties file
Before running the AWS Glue JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define a crawler name used in the tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **IAM** - The Amazon Resource Name (ARN) of the AWS Identity and Access Management (IAM) role that has AWS Glue and Amazon Simple Storage Service (Amazon S3) permissions.   
- **s3Path** - The Amazon S3 target that contains data (for example, CSV data).
- **cron** - A cron expression used to specify the schedule (for example, cron(15 12 * * ? *).
- **crawlerName** - The crawler name used in various tests.
- **existingCrawlerName** - An existing crawler name that is deleted.
- **databaseName** - The name of the database used in the **CreateCrawler** test.
- **existingDatabaseName** - The name of an existing database.
- **tableName** - The name of a database table used in the **GetTable** test.
- **text** - A string used for a text search and used in the **SearchTables** test.
- **jobNameSc** - A Job name used for the Scenario test.
- **s3PathSc** - The Amazon S3 target that contains data used for the Scenario test.
- **dbNameSc** - The name of the database used for the Scenario test.
- **crawlerNameSc** - The crawler name used for the Scenario test.
- **scriptLocationSc** - The Amazon S3 path to a script that runs a job used for the Scenario test. 
- **locationUri** - The location of the database used for the Scenario test. 

**Note:** To set up the CSV data and other requirements needed for the unit tests, follow [Tutorial: Adding an AWS Glue crawler](https://docs.aws.amazon.com/glue/latest/ug/tutorial-add-crawler.html).

### Command line
To run the JUnit tests from the command line, you can use the following command.

		mvn test

You will see output from the JUnit tests, as shown here.

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running GlueTest
	Test 1 passed
	Test 2 passed
	...
	Done!
	[INFO] Results:
	[INFO]
	[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
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
	[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.22.1:test (default-test) on project GlueServiceTest:  There are test failures.
	[ERROR];

## Additional resources
* [Developer Guide - AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/get-started.html).
* [AWS Glue Developer Guide](https://docs.aws.amazon.com/glue/latest/dg/what-is-glue.html).
* [AWS Glue Studio User Guide](https://docs.aws.amazon.com/glue/latest/ug/notebooks-chapter.html).
* [Interface GlueClient](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/glue/GlueClient.html).

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
