# DynamoDB code examples for the SDK for Kotlin

## Overview

Shows how to use the AWS SDK for Kotlin to work with Amazon DynamoDB.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `kotlin` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](src/main/kotlin/com/kotlin/dynamodb/Scenario.kt)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateTable](src/main/kotlin/com/kotlin/dynamodb/CreateTable.kt#L48)
- [DeleteItem](src/main/kotlin/com/kotlin/dynamodb/DeleteItem.kt#L43)
- [DeleteTable](src/main/kotlin/com/kotlin/dynamodb/DeleteTable.kt#L38)
- [GetItem](src/main/kotlin/com/kotlin/dynamodb/GetItem.kt#L43)
- [ListTables](src/main/kotlin/com/kotlin/dynamodb/ListTables.kt#L23)
- [PutItem](src/main/kotlin/com/kotlin/dynamodb/PutItem.kt#L56)
- [Query](src/main/kotlin/com/kotlin/dynamodb/QueryTable.kt#L47)
- [Scan](src/main/kotlin/com/kotlin/dynamodb/DynamoDBScanItems.kt#L38)
- [UpdateItem](src/main/kotlin/com/kotlin/dynamodb/UpdateItem.kt#L50)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Query a table by using batches of PartiQL statements](src/main/kotlin/com/kotlin/dynamodb/ScenarioPartiQLBatch.kt)
- [Query a table using PartiQL](src/main/kotlin/com/kotlin/dynamodb/ScenarioPartiQ.kt)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->


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
in the `kotlin` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [DynamoDB Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html)
- [DynamoDB API Reference](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/Welcome.html)
- [SDK for Kotlin DynamoDB reference](https://sdk.amazonaws.com/kotlin/api/latest/dynamodb/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0