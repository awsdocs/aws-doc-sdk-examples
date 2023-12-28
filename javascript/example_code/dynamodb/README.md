# DynamoDB code examples for the SDK for JavaScript (v2)

> NOTE: Examples for this SDK are no longer supported.
> These examples are for historical purposes only, and should not be relied upon.
> Please migrate to the currently supported AWS SDK for this language.

## Overview

Shows how to use the AWS SDK for JavaScript (v2) to work with Amazon DynamoDB.

<!--custom.overview.start-->
<!--custom.overview.end-->

_DynamoDB is a fully managed NoSQL database service that provides fast and predictable performance with seamless scalability._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/?aws-products-pricing.sort-by=item.additionalFields.productNameLowercase&aws-products-pricing.sort-order=asc&awsf.Free%20Tier%20Type=*all&awsf.tech-category=*all) and [Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc&awsf.Free%20Tier%20Types=*all&awsf.Free%20Tier%20Categories=*all).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `javascript` folder.


<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Get started

- [Hello DynamoDB](None) (`ListTables`)


### Single actions

Code excerpts that show you how to call individual service functions.

- [Create a table](ddb_createtable.js#L28) (`CreateTable`)
- [Delete a table](ddb_deletetable.js#L28) (`DeleteTable`)
- [Delete an item from a table](ddb_deleteitem.js#L28) (`DeleteItem`)
- [Get a batch of items](ddb_batchgetitem.js#L28) (`BatchGetItem`)
- [Get an item from a table](ddb_getitem.js#L28) (`GetItem`)
- [Get information about a table](ddb_describetable.js#L28) (`DescribeTable`)
- [List tables](ddb_listtables.js#L28) (`ListTables`)
- [Put an item in a table](ddb_putitem.js#L28) (`PutItem`)
- [Query a table](ddbdoc_query.js#L28) (`Query`)
- [Run a PartiQL statement](None) (`ExecuteStatement`)
- [Run batches of PartiQL statements](None) (`BatchExecuteStatement`)
- [Scan a table](ddb_scan.js#L28) (`Scan`)
- [Update an item in a table](None) (`UpdateItem`)
- [Write a batch of items](ddb_batchwriteitem.js#L28) (`BatchWriteItem`)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->

#### Hello DynamoDB

This example shows you how to get started using DynamoDB.



### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `javascript` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [DynamoDB Developer Guide](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html)
- [DynamoDB API Reference](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/Welcome.html)
- [SDK for JavaScript (v2) DynamoDB reference](https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/Dynamodb.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0