# DynamoDB code examples for .NET

## Overview

The code examples in this directory demonstrate how to work with Amazon DynamoDB using the AWS SDK for JavaScript version 3 (v3).

Amazon DynamoDB is a key-value and document database that delivers single-digit millisecond performance at any scale. It's a fully managed, multiregion, multimaster, durable database with built-in security, backup and restore, and in-memory caching for internet-scale applications.

## ⚠️ Important

- Running this code might result in charges to your AWS account.
- We recommend that you grant your code least privilege, or at most the minimum
  permissions required to perform the task. For more information, see
  [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in all AWS Regions. For more information, see
  [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).

## Code examples

### Scenario

- [Get started using DynamoDB tables, items, and queries](scenarios/DynamoDB_Basics/DynamoDB_Basics_Scenario/)
- [Query a table using PartiQL](scenarios/PartiQL_Basics/PartiQL_Basics_Scenario)
- [Query a table using PartiQL Batch methods](scenarios/PartiQL_Basics/PartiQL_Batch_Scenario)

### Single action

- [Create DynamoDB tables and add data](CreateTablesLoadDataExample/) (`CreateTableAsync`,
  `DescribeTableAsync`, `PutItemAsync`, `DeleteTableAsync`)
- [Add an item to a  DynamoDB table](FromSQL/AddItemExample/) (`PutItemAsync`)
- [Add multiple items to a DynamoDB table](FromSQL/AddItemsExample/) (`BatchWriteItemAsync`)
- [Create an index for a  DynamoDB table](FromSQL/CreateIndexExample/) (`AddIndexAsync`)
- [Create a  DynamoDB table](FromSQL/CreateTableExample/) (`CreateTableAsync`)
- [Delete an item from a  DynamoDB table](FromSQL/DeleteItemExample/) (`DeleteItemAsync`)
- [Delete items from a  DynamoDB table](FromSQL/DeleteItemsExample/) (`BatchWriteItemAsync`)
- [Delete a  DynamoDB table](FromSQL/DeleteTableExample/) (`DeleteTableAsync`)
- [Scan a  DynamoDB table](FromSQL/GetLowProductStock/) (`ScanAsync`)
- [Query a  DynamoDB table](FromSQL/GetLowProductStockGSI/) (`QueryAsync`)
- [Scan a  DynamoDB for orders](FromSQL/GetOrdersExample/) (`ScanAsync`)
- [Query a DynamoDB table for product orders](FromSQL/GetOrdersForProductGSI/) (`QueryAsync`)
- [Scan a  DynamoDB table for orders in a date range](FromSQL/GetOrdersInDateRange/) (`ScanAsync`)
- [Query a DynamoDB table for orders in a date range](FromSQL/GetOrdersInDateRangeGSI/) (`QueryAsync`)
- [List the items in a DynamoDB table](FromSQL/ListItemsExample/) (`GetItemsAsync`)
- [List the  DynamoDB tables for an account](FromSQL/ListTablesExample/) (`ListTablesAsync`)
- [Update an data model for a DynamoDB table](FromSQL/UpdateItemDataModelExample/)
  (`UpdateTableItemAsync`, `LoadAsync`)
- [Update an item in a DynamoDB table](FromSQL/UpdateItemExample/) (`UpdateItemAsync`)
- [HighLevelBatchWriteItemExample](high-level-api/HighLevelBatchWriteItemExample/)
  (`ExecuteAsync`)
- [Perform CRUD operations using the high-level DynamoDB API](high-level-api/HighLevelItemCRUDExample/) (`LoadAsync`, `DeleteAsync`, `SaveAsync`)
- [Map data using the high-level DynamoDB API](high-level-api/HighLevelMappingArbitraryDataExample/) (`SaveAsync`, `LoadAsync`)
- [Perform query and scan of a DynamoDB table](high-level-api/HighLevelQueryAndScanExample/) (`LoadAsync<T>`, `QueryAsync<T>`, `ScanAsync<T>`)
- [Use the low-level DynamoDB API to get items](low-level-api/LowLevelBatchGet/) (`BatchGetItemAsync`)
- [Perform a low-level batch write to a DynamoDB table](low-level-api/LowLevelBatchWrite/) (`BatchWriteItemAsync`)
- [Create a DynamoDB table with a secondary index](low-level-api/LowLevelSecondaryIndexExample/) (`CreateTableAsync`)
- [LowLevelItemBinaryExample](low-level-api/LowLevelItemBinaryExample/) (`GetItemAsync`, `GetItemAsync`)
- [LowLevelItemCRUDExample](low-level-api/LowLevelItemCRUDExample/) (`PutItemAsync`, `GetItemAsync`, `UpdateItemAsync`, `DeleteItemAsync`)
- [LowLevelLocalSecondaryIndexExample](low-level-api/LowLevelLocalSecondaryIndexExample/) (`CreateTableAsync`, `PutItemAsync`, `QueryAsync`, `DeleteTableAsync`, `DescribeTableAsync`)
- [Perform a parallel scan of a DynamoDB table using the low-level API](low-level-api/LowLevelParallelScan/) (`ScanAsync`) 
- [Perform a query of a DynamoDB table using the low-level API](low-level-api/LowLevelQuery/) (`QueryAsync`)
- [Perform a scan of a DynamoDB table using the low-level API](low-level-api/LowLevelScan/) (`ScanAsync`)
- [Perform CRUD operations using the low-level API](low-level-api/LowLevelTableExample/) (`CreateTableAsync`,
  `ListTablesAsync`, `DescribeTableAsync`, `UpdateTableAsync`, `DeleteTableAsync`)
- [Use the mid-level DynamoDB API to query and scan a table](mid-level-api/MidLevelBatchWriteItemExample/) (`QueryAsync`, `ScanAsync`)
- [Perform CRUD operations on a DynamoDB table using the mid-level API](mid-level-api/MidlevelItemCRUDExample/) (`Table.LoadTable`, `PutItemAsync`, `GetItemAsync`, `UpdateItemAsync`, `DeleteItemAsync`)
- [Query And Scan a DynamoDB table using the mid-level API](mid-level-api/MidLevelQueryAndScanExample/) (`GetItemAsync`, `Table.Query`, `GetNextSetAsync`)
- [Scan a DynamoDB table using the mid-level API](mid-level-api/MidLevelScanOnlyExample/) (`Table.Scan`)

## Running the Examples

After the example compiles, you can run it from the command line. To do so,
navigating to the folder that contains the .csproj file, and run the following
command:

```
dotnet run
```

Alternatively, you can run the example from within your IDE.

## Resources and documentation

[AWS SDK for .NET Developer Guide](https://docs.aws.amazon.com/sdk-for-net/v3/developer-guide/welcome.html)
[AWS SDK for .NET API Reference Guide](https://docs.aws.amazon.com/sdkfornet/v3/apidocs/index.html)

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
