# DynamoDB code examples for the SDK for Ruby

## Overview

Shows how to use the AWS SDK for Ruby to work with Amazon DynamoDB.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `ruby` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello DynamoDB](hello/hello_dynamodb.rb#L4) (`ListTables`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scaffold.rb)


### Single actions

Code excerpts that show you how to call individual service functions.

- [BatchExecuteStatement](partiql/partiql_batch.rb#L22)
- [BatchWriteItem](scaffold.rb#L81)
- [CreateTable](scaffold.rb#L53)
- [DeleteItem](basics/dynamodb_basics.rb#L129)
- [DeleteTable](scaffold.rb#L108)
- [DescribeTable](scaffold.rb#L34)
- [ExecuteStatement](partiql/partiql_single.rb#L22)
- [GetItem](basics/dynamodb_basics.rb#L40)
- [ListTables](scaffold.rb#L34)
- [PutItem](basics/dynamodb_basics.rb#L21)
- [Query](basics/dynamodb_basics.rb#L75)
- [Scan](basics/dynamodb_basics.rb#L95)
- [UpdateItem](basics/dynamodb_basics.rb#L55)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Query a table by using batches of PartiQL statements](partiql/scenario_partiql_batch.rb)
- [Query a table using PartiQL](partiql/scenario_partiql_single.rb)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello DynamoDB

This example shows you how to get started using DynamoDB.

```
ruby hello/hello_dynamodb.rb
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

Start the example by running the following at a command prompt:

```
ruby scaffold.rb
```

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

Start the example by running the following at a command prompt:

```
ruby partiql/scenario_partiql_batch.rb
```

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

Start the example by running the following at a command prompt:

```
ruby partiql/scenario_partiql_single.rb
```

<!--custom.scenarios.dynamodb_Scenario_PartiQLSingle.start-->
<!--custom.scenarios.dynamodb_Scenario_PartiQLSingle.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `ruby` folder.



<!--custom.tests.start-->

## Contribute
Code examples thrive on community contribution.

To learn more about the contributing process, see [CONTRIBUTING.md](../../../CONTRIBUTING.md).
<!--custom.tests.end-->

## Additional resources

- [DynamoDB Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html)
- [DynamoDB API Reference](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/Welcome.html)
- [SDK for Ruby DynamoDB reference](https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/Dynamodb.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0