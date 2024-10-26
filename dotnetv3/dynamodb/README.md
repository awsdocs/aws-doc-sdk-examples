# DynamoDB code examples for the SDK for .NET

## Overview

Shows how to use the AWS SDK for .NET to work with Amazon DynamoDB.

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

For prerequisites, see the [README](../README.md#Prerequisites) in the `dotnetv3` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello DynamoDB](scenarios/DynamoDB_Basics/DynamoDB_Actions/HelloDynamoDB.cs#L4) (`ListTables`)


### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](scenarios/DynamoDB_Basics/DynamoDB_Basics_Scenario/DynamoDB_Basics.cs)


### Single actions

Code excerpts that show you how to call individual service functions.

- [BatchExecuteStatement](scenarios/PartiQL_Batch_Scenario/PartiQL_Batch_Scenario/PartiQLBatchMethods.cs#L10)
- [BatchGetItem](low-level-api/LowLevelBatchGet/LowLevelBatchGet.cs#L4)
- [BatchWriteItem](scenarios/DynamoDB_Basics/DynamoDB_Actions/DynamoDbMethods.cs#L202)
- [CreateTable](scenarios/DynamoDB_Basics/DynamoDB_Actions/DynamoDbMethods.cs#L14)
- [DeleteItem](scenarios/DynamoDB_Basics/DynamoDB_Actions/DynamoDbMethods.cs#L262)
- [DeleteTable](scenarios/DynamoDB_Basics/DynamoDB_Actions/DynamoDbMethods.cs#L391)
- [DescribeTable](low-level-api/LowLevelTableExample/LowLevelTableExample.cs#L126)
- [ExecuteStatement](scenarios/PartiQL_Basics_Scenario/PartiQL_Basics_Scenario/PartiQLMethods.cs#L163)
- [GetItem](scenarios/DynamoDB_Basics/DynamoDB_Actions/DynamoDbMethods.cs#L171)
- [ListTables](low-level-api/LowLevelTableExample/LowLevelTableExample.cs#L102)
- [PutItem](scenarios/DynamoDB_Basics/DynamoDB_Actions/DynamoDbMethods.cs#L89)
- [Query](scenarios/DynamoDB_Basics/DynamoDB_Actions/DynamoDbMethods.cs#L297)
- [Scan](scenarios/DynamoDB_Basics/DynamoDB_Actions/DynamoDbMethods.cs#L350)
- [UpdateItem](scenarios/DynamoDB_Basics/DynamoDB_Actions/DynamoDbMethods.cs#L119)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a serverless application to manage photos](../cross-service/PhotoAssetManager)
- [Create a web application to track DynamoDB data](../cross-service/DynamoDbItemTracker)
- [Query a table by using batches of PartiQL statements](scenarios/PartiQL_Batch_Scenario/PartiQL_Batch_Scenario/PartiQLBatch.cs)
- [Query a table using PartiQL](scenarios/PartiQL_Basics_Scenario/PartiQL_Basics_Scenario/PartiQLMethods.cs)
- [Use a document model](mid-level-api/MidlevelItemCRUDExample/MidlevelItemCRUDExample/MidlevelItemCRUD.cs)
- [Use a high-level object persistence model](high-level-api/HighLevelItemCRUDExample/HighLevelItemCRUDExample/HighLevelItemCRUD.cs)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions

For general instructions to run the examples, see the
[README](../README.md#building-and-running-the-code-examples) in the `dotnetv3` folder.

Some projects might include a settings.json file. Before compiling the project,
you can change these values to match your own account and resources. Alternatively,
add a settings.local.json file with your local settings, which will be loaded automatically
when the application runs.

After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .csproj file and run the following command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.


<!--custom.instructions.start-->
Before you compile the .NET application, you can optionally set configuration values
in the settings.json file. Alternatively, add a settings.local.json file with
your local settings, which will be loaded automatically when the application runs.
After the example compiles, you can run it from the command line. To do so, navigate to
the folder that contains the .csproj file and run the following command:
```
dotnet run
```
Alternatively, you can run the example from within your IDE.
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


#### Create a serverless application to manage photos

This example shows you how to create a serverless application that lets users manage photos using labels.


<!--custom.scenario_prereqs.cross_PAM.start-->
<!--custom.scenario_prereqs.cross_PAM.end-->


<!--custom.scenarios.cross_PAM.start-->
<!--custom.scenarios.cross_PAM.end-->

#### Create a web application to track DynamoDB data

This example shows you how to create a web application that tracks work items in an Amazon DynamoDB table and uses Amazon Simple Email Service (Amazon SES) (Amazon SES) to send reports.


<!--custom.scenario_prereqs.cross_DynamoDBDataTracker.start-->
<!--custom.scenario_prereqs.cross_DynamoDBDataTracker.end-->


<!--custom.scenarios.cross_DynamoDBDataTracker.start-->
<!--custom.scenarios.cross_DynamoDBDataTracker.end-->

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

#### Use a document model

This example shows you how to perform Create, Read, Update, and Delete (CRUD) and batch operations using a document model for DynamoDB and an AWS SDK.


<!--custom.scenario_prereqs.dynamodb_MidLevelInterface.start-->
<!--custom.scenario_prereqs.dynamodb_MidLevelInterface.end-->


<!--custom.scenarios.dynamodb_MidLevelInterface.start-->
<!--custom.scenarios.dynamodb_MidLevelInterface.end-->

#### Use a high-level object persistence model

This example shows you how to perform Create, Read, Update, and Delete (CRUD) and batch operations using an object persistence model for DynamoDB and an AWS SDK.


<!--custom.scenario_prereqs.dynamodb_HighLevelInterface.start-->
<!--custom.scenario_prereqs.dynamodb_HighLevelInterface.end-->


<!--custom.scenarios.dynamodb_HighLevelInterface.start-->
<!--custom.scenarios.dynamodb_HighLevelInterface.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../README.md#Tests)
in the `dotnetv3` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [DynamoDB Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html)
- [DynamoDB API Reference](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/Welcome.html)
- [SDK for .NET DynamoDB reference](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/DynamoDBv2/NDynamoDBv2.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0