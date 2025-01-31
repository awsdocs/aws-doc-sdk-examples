# DynamoDB code examples for the SDK for Java 2.x

## Overview

Shows how to use the AWS SDK for Java 2.x to work with Amazon DynamoDB.

<!--custom.overview.start-->
<!--custom.overview.end-->

_DynamoDB is a fully managed NoSQL database service that provides fast and predictable performance with seamless scalability._

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

- [Hello DynamoDB](src/main/java/com/example/dynamodb/ListTables.java#L6) (`ListTables`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/java/com/example/dynamodb/scenario/Scenario.java)


### Single actions

Code excerpts that show you how to call individual service functions.

- [BatchGetItem](src/main/java/com/example/dynamodb/BatchReadItems.java#L6)
- [BatchWriteItem](src/main/java/com/example/dynamodb/BatchWriteItems.java#L6)
- [CreateTable](src/main/java/com/example/dynamodb/CreateTable.java#L6)
- [DeleteItem](src/main/java/com/example/dynamodb/DeleteItem.java#L6)
- [DeleteTable](src/main/java/com/example/dynamodb/DeleteTable.java#L6)
- [DescribeTable](src/main/java/com/example/dynamodb/DescribeTable.java#L6)
- [GetItem](src/main/java/com/example/dynamodb/GetItem.java#L6)
- [ListTables](src/main/java/com/example/dynamodb/ListTables.java#L6)
- [PutItem](src/main/java/com/example/dynamodb/PutItem.java#L6)
- [Query](src/main/java/com/example/dynamodb/Query.java#L6)
- [Scan](src/main/java/com/example/dynamodb/DynamoDBScanItems.java#L6)
- [UpdateItem](src/main/java/com/example/dynamodb/UpdateItem.java#L6)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Query a table by using batches of PartiQL statements](src/main/java/com/example/dynamodb/scenario/ScenarioPartiQLBatch.java)
- [Query a table using PartiQL](src/main/java/com/example/dynamodb/scenario/ScenarioPartiQ.java)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello DynamoDB

This example shows you how to get started using DynamoDB.


#### Learn the basics

This example shows you how to do the following:

- Create a table that can hold movie data.
- Put, get, and update a single movie in the table.
- Write movie data to the table from a sample JSON file.
- Query for movies that were released in a given year.
- Scan for movies that were released in a range of years.
- Delete a movie from the table, then delete the table.

<!--custom.basic_prereqs.dynamodb_Scenario_GettingStartedMovies.start-->
<!--custom.basic_prereqs.dynamodb_Scenario_GettingStartedMovies.end-->


<!--custom.basics.dynamodb_Scenario_GettingStartedMovies.start-->
<!--custom.basics.dynamodb_Scenario_GettingStartedMovies.end-->


#### Query a table by using batches of PartiQL statements

This example shows you how to do the following:

- Get a batch of items by running multiple SELECT statements.
- Add a batch of items by running multiple INSERT statements.
- Update a batch of items by running multiple UPDATE statements.
- Delete a batch of items by running multiple DELETE statements.

<!--custom.scenario_prereqs.dynamodb_Scenario_PartiQLBatch.start-->
<!--custom.scenario_prereqs.dynamodb_Scenario_PartiQLBatch.end-->


<!--custom.scenarios.dynamodb_Scenario_PartiQLBatch.start-->
<!--custom.scenarios.dynamodb_Scenario_PartiQLBatch.end-->

#### Query a table using PartiQL

This example shows you how to do the following:

- Get an item by running a SELECT statement.
- Add an item by running an INSERT statement.
- Update an item by running an UPDATE statement.
- Delete an item by running a DELETE statement.

<!--custom.scenario_prereqs.dynamodb_Scenario_PartiQLSingle.start-->
<!--custom.scenario_prereqs.dynamodb_Scenario_PartiQLSingle.end-->


<!--custom.scenarios.dynamodb_Scenario_PartiQLSingle.start-->
<!--custom.scenarios.dynamodb_Scenario_PartiQLSingle.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javav2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [DynamoDB Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html)
- [DynamoDB API Reference](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/Welcome.html)
- [SDK for Java 2.x DynamoDB reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/dynamodb/package-summary.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0