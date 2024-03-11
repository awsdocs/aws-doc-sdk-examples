# Amazon Redshift code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon Redshift.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Redshift is a fast, fully managed, petabyte-scale data warehouse service that makes it simple and cost-effective to efficiently analyze all your data using your existing business intelligence tools._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javav2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello AWS Redshift](src/main/java/com/example/redshift/HelloRedshift.java) (`describeClustersPaginator`)

### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a cluster](src/main/java/com/example/scenario/RedshiftScenario.java) (`CreateCluster`)
- [Delete a cluster](src/main/java/com/example/scenario/RedshiftScenario.java) (`DeleteCluster`)
- [Describe your clusters](src/main/java/com/example/scenario/RedshiftScenario.java) (`DescribeClusters`)
- [Modify a cluster](src/main/java/com/example/scenario/RedshiftScenario.java) (`ModifyCluster`)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

* [Get started with Amazon Redshift tables, items, and queries using an AWS SDK](src/main/java/com/example/scenario/RedshiftScenario.java)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

Some of these examples perform *destructive* operations on AWS resources, such as deleting an Amazon Redshift cluster. 
**Be very careful** when running an operation that deletes or modifies AWS resources in your account. We recommend creating separate test-only resources when experimenting with these examples.

To run these examples, set up your development environment. For more information,
see [Get started with the SDK for Java](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup.html).

## Testing the Amazon Redshift files

You can test the Java code examples for Amazon Redshift by running a test file named **AmazonRedshiftTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/).

You can run the JUnit tests from an IDE, such as IntelliJ, or from the command line. As each test runs, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that the Test passed.

	Test 2 passed

To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.

**WARNING**: _Running these JUnit tests manipulates real Amazon resources and might incur charges on your account._

### Properties file
Before running the JUnit tests, you must define values in the **config.properties** file located in the **resources** folder. This file contains values that are required to run the JUnit tests. For example, you define a tableId used in the tests. If you do not define all values, the JUnit tests fail.

Define these values to successfully run the JUnit tests:

- **clusterId** - The Id value of an Amazon Redshift cluster.
- **username**  - The user name to use for these tests.
- **userPassword** - The corresponding user password. 
- **jsonFilePath** - The path to the Movies JSON file (you can locate that file in ../../../resources/sample_files/movies.json)

<!--custom.instructions.start-->
<!--custom.instructions.end-->

<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Redshift Management Guide](https://docs.aws.amazon.com/redshift/latest/mgmt/welcome.html)
- [Interface RedshiftClient](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/redshift/RedshiftClient.html)
- [Interface RedshiftDataClient](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/redshiftdata/RedshiftDataClient.html)
- [SDK for Java 2.x Amazon Redshift reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/redshift/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0