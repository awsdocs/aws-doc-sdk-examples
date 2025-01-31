# DynamoDB code examples for the SDK for SAP ABAP

## Overview

Shows how to use the AWS SDK for SAP ABAP to work with Amazon DynamoDB.

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

For prerequisites, see the [README](../../README.md#Prerequisites) in the `sap-abap` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](zcl_aws1_dyn_scenario.clas.abap)


### Single actions

Code excerpts that show you how to call individual service functions.

- [CreateTable](zcl_aws1_dyn_actions.clas.abap#L75)
- [DeleteItem](zcl_aws1_dyn_actions.clas.abap#L117)
- [DeleteTable](zcl_aws1_dyn_actions.clas.abap#L141)
- [DescribeTable](zcl_aws1_dyn_actions.clas.abap#L164)
- [GetItem](zcl_aws1_dyn_actions.clas.abap#L189)
- [ListTables](zcl_aws1_dyn_actions.clas.abap#L215)
- [PutItem](zcl_aws1_dyn_actions.clas.abap#L239)
- [Query](zcl_aws1_dyn_actions.clas.abap#L263)
- [Scan](zcl_aws1_dyn_actions.clas.abap#L301)
- [UpdateItem](zcl_aws1_dyn_actions.clas.abap#L337)


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


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `sap-abap` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [DynamoDB Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html)
- [DynamoDB API Reference](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/Welcome.html)
- [SDK for SAP ABAP DynamoDB reference](https://docs.aws.amazon.com/sdk-for-sap-abap/v1/api/latest/dyn/index.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0