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

- [Hello Amazon Redshift](src/main/java/com/example/redshift/HelloRedshift.java#L6) (`describeClusters`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateCluster](src/main/java/com/example/scenario/RedshiftScenario.java#L498)
- [CreateTable](src/main/java/com/example/scenario/RedshiftScenario.java#L475)
- [DeleteCluster](src/main/java/com/example/scenario/RedshiftScenario.java#L247)
- [DescribeClusters](src/main/java/com/example/scenario/RedshiftScenario.java#L430)
- [DescribeStatement](src/main/java/com/example/scenario/RedshiftScenario.java#L324)
- [GetStatementResult](src/main/java/com/example/scenario/RedshiftScenario.java#L408)
- [Insert](src/main/java/com/example/scenario/RedshiftScenario.java#L265)
- [ModifyCluster](src/main/java/com/example/scenario/RedshiftScenario.java#L356)
- [Query](src/main/java/com/example/scenario/RedshiftScenario.java#L375)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Get started with Amazon Redshift](src/main/java/com/example/scenario/RedshiftScenario.java)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon Redshift

This example shows you how to get started using Amazon Redshift.



#### Get started with Amazon Redshift

This example shows you how to work with Amazon Redshift tables, items, and queries.


<!--custom.scenario_prereqs.redshift_Scenario.start-->
<!--custom.scenario_prereqs.redshift_Scenario.end-->


<!--custom.scenarios.redshift_Scenario.start-->
<!--custom.scenarios.redshift_Scenario.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Amazon Redshift Management Guide](https://docs.aws.amazon.com/redshift/latest/mgmt/welcome.html)
- [Amazon Redshift API Reference](https://docs.aws.amazon.com/redshift/latest/APIReference/Welcome.html)
- [SDK for Java 2.x Amazon Redshift reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/redshift/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0