# DynamoDB code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with Amazon DynamoDB.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello DynamoDB](hello/hello_dynamodb.py#L4) (`ListTables`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [BatchExecuteStatement](partiql/scenario_partiql_batch.py#L44)
- [BatchGetItem](batching/dynamo_batching.py#L64)
- [BatchWriteItem](GettingStarted/scenario_getting_started_movies.py#L144)
- [CreateTable](GettingStarted/scenario_getting_started_movies.py#L80)
- [DeleteItem](GettingStarted/scenario_getting_started_movies.py#L322)
- [DeleteTable](GettingStarted/scenario_getting_started_movies.py#L343)
- [DescribeTable](GettingStarted/scenario_getting_started_movies.py#L50)
- [ExecuteStatement](partiql/scenario_partiql_single.py#L43)
- [GetItem](GettingStarted/scenario_getting_started_movies.py#L203)
- [ListTables](GettingStarted/scenario_getting_started_movies.py#L120)
- [PutItem](GettingStarted/scenario_getting_started_movies.py#L173)
- [Query](GettingStarted/scenario_getting_started_movies.py#L260)
- [Scan](GettingStarted/scenario_getting_started_movies.py#L283)
- [UpdateItem](GettingStarted/scenario_getting_started_movies.py#L228)

### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Accelerate reads with DynamoDB Accelerator](TryDax/01-create-table.py)
- [Get started with tables, items, and queries](GettingStarted/scenario_getting_started_movies.py)
- [Query a table by using batches of PartiQL statements](partiql/scenario_partiql_batch.py)
- [Query a table using PartiQL](partiql/scenario_partiql_single.py)

### Cross-service examples

Sample applications that work across multiple AWS services.

- [Create a REST API to track COVID-19 data](../../cross_service/apigateway_covid-19_tracker)
- [Create a messenger application](../../cross_service/stepfunctions_messenger)
- [Create a web application to track DynamoDB data](../../cross_service/dynamodb_item_tracker)
- [Create a websocket chat application](../../cross_service/apigateway_websocket_chat)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello DynamoDB

This example shows you how to get started using DynamoDB.

```
python hello/hello_dynamodb.py
```


#### Accelerate reads with DynamoDB Accelerator

This example shows you how to do the following:

- Create and write data to a table with both the DynamoDB Accelerator and SDK clients.
- Get, query, and scan the table with both clients and compare their performance.

<!--custom.scenario_prereqs.dynamodb_Usage_DaxDemo.start-->
<!--custom.scenario_prereqs.dynamodb_Usage_DaxDemo.end-->

Start the example by running the following at a command prompt:

```
python TryDax/01-create-table.py
```


<!--custom.scenarios.dynamodb_Usage_DaxDemo.start-->
To run the scripts with the DAX client, you must run them on an Amazon Elastic Compute 
Cloud (Amazon EC2) instance within your virtual private cloud (VPC). This process is 
described in the Python sample application tutorial in the  
[Developing with the DAX Client](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DAX.client.html) 
section of the *Amazon DynamoDB Developer Guide*.

The tutorial shows you how to set up the following additional resources:

- A VPC from Amazon Virtual Private Cloud (Amazon VPC)
- A DAX cluster set up in your VPC 
- An EC2 instance running in your VPC with the following installed:
    - Python 3.7 or later
    - Boto3 1.11.10 or later
    - Amazon DAX Client for Python 1.1.7 or later

On an EC2 instance, run the get item, query, and scan test scripts with the DAX client
by specifying a DAX cluster endpoint as the first positional argument.
To run the query test script with the DAX client, run the following from a command 
prompt window.
Start the example by running the following at a command prompt:

```commandline
python 04-query-test.py YOUR-CLUSTER-NAME.111111.clustercfg.dax.usw2.cache.amazonaws.com:8111
```
<!--custom.scenarios.dynamodb_Usage_DaxDemo.end-->

#### Get started with tables, items, and queries

This example shows you how to do the following:

- Create a table that can hold movie data.
- Put, get, and update a single movie in the table.
- Write movie data to the table from a sample JSON file.
- Query for movies that were released in a given year.
- Scan for movies that were released in a range of years.
- Delete a movie from the table, then delete the table.

<!--custom.scenario_prereqs.dynamodb_Scenario_GettingStartedMovies.start-->
<!--custom.scenario_prereqs.dynamodb_Scenario_GettingStartedMovies.end-->

Start the example by running the following at a command prompt:

```
python GettingStarted/scenario_getting_started_movies.py
```


<!--custom.scenarios.dynamodb_Scenario_GettingStartedMovies.start-->
<!--custom.scenarios.dynamodb_Scenario_GettingStartedMovies.end-->

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
python partiql/scenario_partiql_batch.py
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
python partiql/scenario_partiql_single.py
```


<!--custom.scenarios.dynamodb_Scenario_PartiQLSingle.start-->
<!--custom.scenarios.dynamodb_Scenario_PartiQLSingle.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [DynamoDB Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html)
- [DynamoDB API Reference](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/Welcome.html)
- [SDK for Python DynamoDB reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/dynamodb.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0