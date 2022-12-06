# DynamoDB code examples for the SDK for .NET

## Overview
The code examples in this section show how to use the AWS SDK for .NET with Amazon DynamoDB.

Amazon DynamoDB is a fully managed, serverless, key-value NoSQL database designed to run high-performance applications at any scale. DynamoDB offers built-in security, continuous backups, automated multi-Region replication, in-memory caching, and data import and export tools.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.

* [Create a table](scenarios/DynamoDB_Basics/DynamoDB_Basics_Scenario/DynamoDB_Basics.cs) (`CreateTableAsync`)
* [Delete a table](scenarios/DynamoDB_Basics/DynamoDB_Basics_Scenario/DynamoDB_Basics.cs) (`DeleteTableAsync`)
* [Delete an item from a table](scenarios/DynamoDB_Basics/DynamoDB_Basics_Scenario/DynamoDB_Basics.cs) (`DeleteItemAsync`)
* [Get a batch of items](low-level-api/LowLevelBatchGet/LowLevelBatchGet.cs) (`BatchGetItemAsync`)
* [Get an item from a table](scenarios/DynamoDB_Basics/DynamoDB_Basics_Scenario/DynamoDB_Basics.cs) (`GetItemAsync`)
* [Get information about a table](low-level-api/LowLevelTableExample/LowLevelTableExample.cs) (`DescribeTableAsync`)
* [List tables](low-level-api/LowLevelTableExample/LowLevelTableExample.cs) (`ListTablesAsync`)
* [Put an item in a table](scenarios/DynamoDB_Basics/DynamoDB_Basics_Scenario/DynamoDB_Basics.cs) (`PutItemAsync`)
* [Query a table](scenarios/DynamoDB_Basics/DynamoDB_Basics_Scenario/DynamoDB_Basics.cs) (`QueryAsync`)
* [Run a PartiQL statement](scenarios/PartiQL_Basics_Scenario/PartiQL_Basics_Scenario/PartiQLMethods.cs) (`ExecuteStatementAsync`)
* [Run batches of PartiQL statements](scenarios/PartiQL_Basics_Scenario/PartiQL_Basics_Scenario/PartiQLMethods.cs) (`BatchExecuteStatementAsync`)
* [Scan a table](scenarios/DynamoDB_Basics/DynamoDB_Basics_Scenario/DynamoDB_Basics.cs) (`ScanAsync`)
* [Update an item in a table](scenarios/DynamoDB_Basics/DynamoDB_Basics_Scenario/DynamoDB_Basics.cs) (`UpdateItemAsync`)
* [Write a batch of items](scenarios/DynamoDB_Basics/DynamoDB_Basics_Scenario/DynamoDB_Basics.cs) (`BatchWriteItemAsync`)

### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.
* [Get started using tables, items, and queries](scenarios/DynamoDB_Basics/DynamoDB_Basics_Scenario/DynamoDB_Basics.cs)
* [Query a table by using batches of PartiQL statements](scenarios/PartiQL_Batch_Scenario/PartiQL_Batch_Scenario/PartiQLBatchMethods.cs)
* [Query a table using PartiQL](scenarios/PartiQL_Basics_Scenario/PartiQL_Basics_Scenario/PartiQLMethods.cs)
* [Use a document model](mid-level-api)
* [Use a high-level object persistence model](high-level-api)

## Run the examples

### Prerequisites
* To find prerequisites for running these examples, see the
  [README](../README.md#Prerequisites) in the dotnetv3 folder.

After the example compiles, you can run it from the command line. To do so,
navigating to the folder that contains the .csproj file, and run the following
command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Additional resources
* [DynamoDB Developer Guide](http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/)
* [DynamoDB API Reference](http://docs.aws.amazon.com/amazondynamodb/latest/APIReference/)
* [AWS SDK for .NET DynamoDB](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/DynamoDBv2/NDynamoDBv2.html)
* [AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0