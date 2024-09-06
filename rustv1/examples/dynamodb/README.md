# DynamoDB code examples for the SDK for Rust

## Overview

Shows how to use the AWS SDK for Rust to work with Amazon DynamoDB.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `rustv1` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateTable](src/scenario/create.rs#L12)
- [DeleteItem](src/scenario/delete.rs#L12)
- [DeleteTable](src/scenario/delete.rs#L36)
- [ListTables](src/scenario/list.rs#L7)
- [PutItem](src/scenario/add.rs#L25)
- [Query](src/scenario/movies/server.rs#L30)
- [Scan](src/scenario/list.rs#L178)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Connect to a local instance](src/bin/list-tables-local.rs)
- [Query a table using PartiQL](src/bin/partiql.rs)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Connect to a local instance

This example shows you how to override an endpoint URL to connect to a local development deployment of DynamoDB and an AWS SDK.


<!--custom.scenario_prereqs.dynamodb_local.start-->
<!--custom.scenario_prereqs.dynamodb_local.end-->


<!--custom.scenarios.dynamodb_local.start-->
<!--custom.scenarios.dynamodb_local.end-->

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
in the `rustv1` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [DynamoDB Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html)
- [DynamoDB API Reference](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/Welcome.html)
- [SDK for Rust DynamoDB reference](https://docs.rs/aws-sdk-dynamodb/latest/aws_sdk_dynamodb/)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0