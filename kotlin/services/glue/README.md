# AWS Glue code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with AWS Glue.

<!--custom.overview.start-->
<!--custom.overview.end-->

_AWS Glue is a scalable, serverless data integration service that makes it easy to discover, prepare, and combine data for analytics, machine learning, and application development._

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

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/kotlin/com/kotlin/glue/GlueScenario.kt)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateCrawler](src/main/kotlin/com/kotlin/glue/CreateCrawler.kt#L48)
- [GetCrawler](src/main/kotlin/com/kotlin/glue/GetCrawler.kt#L38)
- [GetDatabase](src/main/kotlin/com/kotlin/glue/GetDatabase.kt#L40)
- [StartCrawler](src/main/kotlin/com/kotlin/glue/StartCrawler.kt#L38)


<!--custom.examples.start-->

### Custom Examples

- [Deleting an AWS Glue crawler](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/kotlin/glue/DeleteCrawler.kt) (DeleteCrawler command)
- [Getting AWS Glue crawlers](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/kotlin/glue/GetCrawlers.kt) (GetCrawlers command)
- [Getting a job run request](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/kotlin/glue/GetJobRun.kt) (GetJobRun command)
- [Getting all AWS Glue jobs](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/kotlin/glue/GetJobs.kt) (GetJobs command)
- [Getting all AWS Glue workflows](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/kotlin/glue/ListWorkflows.kt) (ListWorkflows command)
- [Searching AWS Glue tables based on properties](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/kotlin/glue/SearchTables.kt) (SearchTables command)
- [Stopping an AWS Glue crawler](https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/kotlin/services/glue/src/main/kotlin/com/kotlin/glue/StopCrawler.kt) (StopCrawler command)
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->


#### Learn the basics

This example shows you how to do the following:

- Create a crawler that crawls a public Amazon S3 bucket and generates a database of CSV-formatted metadata.
- List information about databases and tables in your AWS Glue Data Catalog.
- Create a job to extract CSV data from the S3 bucket, transform the data, and load JSON-formatted output into another S3 bucket.
- List information about job runs, view transformed data, and clean up resources.

<!--custom.basic_prereqs.glue_Scenario_GetStartedCrawlersJobs.start-->
<!--custom.basic_prereqs.glue_Scenario_GetStartedCrawlersJobs.end-->


<!--custom.basics.glue_Scenario_GetStartedCrawlersJobs.start-->
<!--custom.basics.glue_Scenario_GetStartedCrawlersJobs.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `kotlin` folder.



<!--custom.tests.start-->

You can test the Kotlin code examples for AWS Glue by running a test file named **GlueTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/kotlin** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from an IDE, such as IntelliJ, or from the command line. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that Test 3 passed.

    Test 3 passed

#### Properties file

Before running the AWS Glue JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define a crawler name used in the tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **IAM** - The Amazon Resource Name (ARN) of the AWS Identity and Access Management (IAM) role that has AWS Glue and Amazon Simple Storage Service (Amazon S3) permissions.
- **s3Path** - The Amazon S3 target that contains data (for example, CSV data).
- **cron** - A cron expression used to specify the schedule (for example, `cron(15 12 * _ ? _)`).
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

#### Command line

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

<!--custom.tests.end-->

## Additional resources

- [AWS Glue Developer Guide](https://docs.aws.amazon.com/glue/latest/dg/what-is-glue.html)
- [AWS Glue API Reference](https://docs.aws.amazon.com/glue/latest/dg/aws-glue-api.html)
- [SDK for Kotlin AWS Glue reference](https://sdk.amazonaws.com/kotlin/api/latest/glue/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0