# DynamoDB code examples for the SDK for .NET

## Overview
The code examples in this section show how to use the AWS SDK for .NET with Amazon DynamoDB (DynamoDB)
to request, import, and manage certificates.

Amazon DynamoDB is a fully managed, serverless, key-value NoSQL database designed to run high-performance applications at any scale. DynamoDB offers built-in security, continuous backups, automated multi-Region replication, in-memory caching, and data import and export tools.

## ⚠️ Important
* Running this code might result in charges to your AWS account.
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Single actions
Code excerpts that show you how to call individual service functions.
* [Add multiple items to a table](high-level-api/HighLevelBatchWriteItemExample/HighLevelBatchWriteItemExample/HighLevelBatchWriteItem.cs) (`BatchWriteItemAsync`)
* [Create a table](FromSQL/CreateTableExample/CreateTableExample/CreateTable.cs) (`CreateTableAsync`)
* [Create a table and load data](CreateTablesLoadDataExample/CreateTablesLoadDataExample/CreateTablesLoadData.cs) (`CreateTableAsync`)
* [Create a table with a secondary index](low-level-api/LowLevelSecondaryIndexExample/LowLevelGlobalSecondaryIndexExample.cs) (`AddIndexAsync`)
* [Create an index for a table](FromSQL/CreateIndexExample/CreateIndexExample/CreateIndex.cs) (`AddIndexAsync`)
* [Delete a table](FromSQL/DeleteTableExample/DeleteTableExample/DeleteTable.cs) (`DeleteTableAsync`)
* [Delete an item from a table](FromSQL/DeleteItemExample/DeleteItemExample/DeleteItem.cs) (`DeleteItemAsync`)
* [Delete multiple items from a table](FromSQL/DeleteItemsExample/DeleteItemsExample/DeleteItems.cs) (`BatchWriteItemAsync`)
* [Get an item from a table](scenarios/DynamoDB_Basics/DynamoDB_Basics_Scenario/DynamoDB_Basics.cs) (`GetItemAsync`)
* [List items in a table](FromSQL/ListItemsExample/ListItemsExample/ListItems.cs) (`ListItemsAsync`)
* [List tables](FromSQL/ListTablesExample/ListTablesExample/ListTables.cs) (`ListTablesAsync`)
* [Map data](high-level-api/HighLevelMappingArbitraryDataExample/HighLevelMappingArbitraryDataExample/HighLevelMappingArbitraryData.cs) (`SaveAsync`)
* [Perform a low-level batch write to a table](low-level-api/LowLevelBatchWrite/LowLevelBatchWrite.cs) (`BatchWriteItemAsync`)
* [Perform a parallel scan of a table by using the low-level API](low-level-api/LowLevelParallelScan/LowLevelParallelScan.cs) (`ScanAsync`)
* [Perform binary operations by using the low-level API](low-level-api/LowLevelItemBinaryExample/LowLevelItemBinaryExample.cs) (`PutItemAsync`)
* [Perform CRUD operations](high-level-api/HighLevelItemCRUDExample/HighLevelItemCRUDExample/HighLevelItemCRUD.cs) (`SaveAsync`, `LoadAsync`)
* [Perform CRUD operations by using the low-level API](low-level-api/LowLevelItemCRUDExample/LowLevelItemCRUDExample.cs) (`PutItemAsync`, `GetItemAsync`)
* [Perform CRUD operations on a table by using the mid-level API](mid-level-api/MidlevelItemCRUDExample/MidlevelItemCRUDExample/MidlevelItemCRUD.cs) (`PutItemAsync`, `GetItemAsync`)
* [Perform query and scan operations](high-level-api/HighLevelQueryAndScanExample/HighLevelQueryAndScanExample/HighLevelQueryAndScan.cs) (`QueryAsync`, `ScanAsync`)
* [Perform table operations by using the low-level API](low-level-api/LowLevelTableExample/LowLevelTableExample.cs) (`CreateTableAsync`, `ListTablesAsync`, `DescribeTableAsync`)
* [Put an item in a table](FromSQL/AddItemExample/AddItemExample/AddItem.cs) (`PutItemAsync`)
* [Query a table](FromSQL/GetLowProductStockGSI/GetLowProductStockGSI.cs) (`QueryAsync`)
* [Query a table by using the low-level API](low-level-api/LowLevelQuery/LowLevelQuery.cs) (`QueryAsync`)
* [Query a table for orders](FromSQL/GetOrdersForProductGSI/GetOrdersForProductGSI.cs) (`QueryAsync`)
* [Query a table for orders in a date range](FromSQL/GetOrdersInDateRangeGSI/GetOrdersInDateRangeGSI.cs) (`QueryAsync`)
* [Query and scan a table by using mid-level API](mid-level-api/MidLevelBatchWriteItemExample/MidLevelBatchWriteItemExample/MidLevelBatchWriteItem.cs) (`ExecuteAsync`)
* [Query and scan a table by using the mid-level API](mid-level-api/MidLevelQueryAndScanExample/MidLevelQueryAndScanExample/MidLevelQueryAndScan.cs) (`GetItemAsync`, `GetNextItemAsync`)
* [Scan a table](FromSQL/GetLowProductStock/GetLowProductStock.cs) (`ScanAsync`)
* [Scan a table by using the low-level API](low-level-api/LowLevelScan/LowLevelScan.cs) (`ScanAsync`)
* [Scan a table by using the mid-level API](mid-level-api/MidLevelScanOnlyExample/MidLevelScanOnlyExample/MidLevelScanOnly.cs) (`ScanAsync`)
* [Scan a table for orders](FromSQL/GetOrdersExample/GetOrdersExample/GetOrders.cs) (`ScanAsync`)
* [Scan a table for orders in a date range](FromSQL/GetOrdersInDateRange/GetOrdersInDateRange.cs) (`ScanAsync`)
* [Update the data model for a table](FromSQL/UpdateItemDataModelExample/UpdateItemDataModelExample/UpdateItemDataModel.cs) (`UpdateTableItemAsync`)
* [Update an item in a table](FromSQL/UpdateItemExample/UpdateItemExample/UpdateItem.cs) (`UpdateItemAsync`)
* [Use a secondary index](low-level-api/LowLevelLocalSecondaryIndexExample/LowLevelLocalSecondaryIndexExample.cs) (`CreateTableAsync`)
* [Use the low-level API to get items](low-level-api/LowLevelBatchGet/LowLevelBatchGet.cs) (`BatchGetItemAsync`)
* [Write a batch of items](FromSQL/AddItemsExample/AddItemsExample/AddItems.cs) (`BatchWriteItemAsync`)

### Scenarios
Code examples that show you how to accomplish a specific task by calling multiple functions within the same service.
* [Get started using tables, items, and queries](scenarios/DynamoDB_Basics/DynamoDB_Basics_Scenario/DynamoDB_Basics.cs)
* [Query a table by using batches of PartiQL statements](scenarios/PartiQL_Batch_Scenario/PartiQL_Batch_Scenario/PartiQLBatchMethods.cs)
* [Query a table using PartiQL](scenarios/PartiQL_Basics_Scenario/PartiQL_Basics_Scenario/PartiQLMethods.cs)

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