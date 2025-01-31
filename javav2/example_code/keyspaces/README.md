# Amazon Keyspaces code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon Keyspaces (for Apache Cassandra).

<!--custom.overview.start-->
<!--custom.overview.end-->

_Amazon Keyspaces is a scalable, highly available, and managed Apache Cassandra-compatible database service._

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

- [Hello Amazon Keyspaces](src/main/java/com/example/keyspace/HelloKeyspaces.java#L6) (`ListKeyspaces`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/java/com/example/keyspace/ScenarioKeyspaces.java)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateKeyspace](src/main/java/com/example/keyspace/ScenarioKeyspaces.java#L611)
- [CreateTable](src/main/java/com/example/keyspace/ScenarioKeyspaces.java#L505)
- [DeleteKeyspace](src/main/java/com/example/keyspace/ScenarioKeyspaces.java#L230)
- [DeleteTable](src/main/java/com/example/keyspace/ScenarioKeyspaces.java#L271)
- [GetKeyspace](src/main/java/com/example/keyspace/ScenarioKeyspaces.java#L593)
- [GetTable](src/main/java/com/example/keyspace/ScenarioKeyspaces.java#L469)
- [ListKeyspaces](src/main/java/com/example/keyspace/ScenarioKeyspaces.java#L574)
- [ListTables](src/main/java/com/example/keyspace/ScenarioKeyspaces.java#L449)
- [RestoreTable](src/main/java/com/example/keyspace/ScenarioKeyspaces.java#L322)
- [UpdateTable](src/main/java/com/example/keyspace/ScenarioKeyspaces.java#L369)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello Amazon Keyspaces

This example shows you how to get started using Amazon Keyspaces.


#### Learn the basics

This example shows you how to do the following:

- Create a keyspace and table. The table schema holds movie data and has point-in-time recovery enabled.
- Connect to the keyspace using a secure TLS connection with SigV4 authentication.
- Query the table. Add, retrieve, and update movie data.
- Update the table. Add a column to track watched movies.
- Restore the table to its previous state and clean up resources.

<!--custom.basic_prereqs.keyspaces_Scenario_GetStartedKeyspaces.start-->
<!--custom.basic_prereqs.keyspaces_Scenario_GetStartedKeyspaces.end-->


<!--custom.basics.keyspaces_Scenario_GetStartedKeyspaces.start-->
<!--custom.basics.keyspaces_Scenario_GetStartedKeyspaces.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->

To successfully run the JUnit tests, define the following values in the test:

- **fileName** - The name of the JSON file that contains movie data. (Get this file from the GitHub repo at resources/sample_file.)
- **keyspaceName** - The name of the keyspace to create.
<!--custom.tests.end-->

## Additional resources

- [Amazon Keyspaces Developer Guide](https://docs.aws.amazon.com/keyspaces/latest/devguide/what-is-keyspaces.html)
- [Amazon Keyspaces API Reference](https://docs.aws.amazon.com/keyspaces/latest/APIReference/Welcome.html)
- [SDK for Java 2.x Amazon Keyspaces reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/keyspaces/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0