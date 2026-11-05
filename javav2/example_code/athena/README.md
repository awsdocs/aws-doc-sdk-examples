# Athena code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon Athena.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Athena _

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

- [Hello Athena](src/main/java/com/example/athena/HelloAthena.java#L22) (`ListWorkGroups`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn Athena basics](src/main/java/com/example/athena/basics/AthenaScenario.java)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateNamedQuery](src/main/java/com/example/athena/basics/AthenaActions.java#L176)
- [CreatePreparedStatement](src/main/java/com/example/athena/basics/AthenaActions.java#L215)
- [CreateWorkGroup](src/main/java/com/example/athena/basics/AthenaActions.java#L109)
- [DeleteNamedQuery](src/main/java/com/example/athena/basics/AthenaActions.java#L408)
- [DeleteWorkGroup](src/main/java/com/example/athena/basics/AthenaActions.java#L436)
- [GetQueryExecution](src/main/java/com/example/athena/basics/AthenaActions.java#L289)
- [GetQueryResults](src/main/java/com/example/athena/basics/AthenaActions.java#L336)
- [GetWorkGroup](src/main/java/com/example/athena/basics/AthenaActions.java#L148)
- [ListQueryExecutions](src/main/java/com/example/athena/basics/AthenaActions.java#L372)
- [StartQueryExecution](src/main/java/com/example/athena/basics/AthenaActions.java#L250)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Athena

This example shows you how to get started using Athena.


#### Learn Athena basics

This example shows you how to learn Athena basics.

- Create an Athena workgroup.
- Verify the workgroup configuration.
- Create a database and table using DDL queries.
- Run SELECT queries and retrieve results.
- Create, list, and delete named queries.
- List query executions.
- Clean up all resources.

<!--custom.basic_prereqs.athena_Scenario.start-->
<!--custom.basic_prereqs.athena_Scenario.end-->


<!--custom.basics.athena_Scenario.start-->
<!--custom.basics.athena_Scenario.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Athena User Guide](https://docs.aws.amazon.com/athena/latest/ug/what-is.html)
- [Athena API Reference](https://docs.aws.amazon.com/athena/latest/APIReference/Welcome.html)
- [SDK for Java 2.x Athena reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/athena/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
