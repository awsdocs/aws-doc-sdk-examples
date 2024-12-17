# DynamoDB code examples for the SDK for Go V2

## Overview

Shows how to use the AWS SDK for Go V2 to work with Amazon DynamoDB.

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `gov2` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenarios/scenario_movie_table.go)


### Single actions

Code excerpts that show you how to call individual service functions.

- [BatchExecuteStatement](actions/partiql.go#L7)
- [BatchWriteItem](actions/table_basics.go#L177)
- [CreateTable](actions/table_basics.go#L54)
- [DeleteItem](actions/table_basics.go#L332)
- [DeleteTable](actions/table_basics.go#L347)
- [DescribeTable](actions/table_basics.go#L31)
- [ExecuteStatement](actions/partiql.go#L7)
- [GetItem](actions/table_basics.go#L221)
- [ListTables](actions/table_basics.go#L99)
- [PutItem](actions/table_basics.go#L121)
- [Query](actions/table_basics.go#L243)
- [Scan](actions/table_basics.go#L285)
- [UpdateItem](actions/table_basics.go#L140)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Query a table by using batches of PartiQL statements](scenarios/scenario_partiql_batch.go)
- [Query a table using PartiQL](scenarios/scenario_partiql_single.go)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->


#### Run a scenario

All scenarios can be run with the `cmd` runner. To get a list of scenarios
and to get help for running a scenario, use the following command:

```
go run ./cmd -h
```
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


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `gov2` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [DynamoDB Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html)
- [DynamoDB API Reference](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/Welcome.html)
- [SDK for Go V2 DynamoDB reference](https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/dynamodb)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0