# DynamoDB code examples for .NET

## Overview

The code examples in this directory demonstrate how to work with Amazon DynamoDB using the AWS SDK for JavaScript version 3 (v3).

Amazon DynamoDB is a key-value and document database that delivers single-digit millisecond performance at any scale. It's a fully managed, multiregion, multimaster, durable database with built-in security, backup and restore, and in-memory caching for internet-scale applications.

## ⚠️ Important

- Running this code might result in charges to your AWS account. 
- We recommend that you grant your code least privilege, or at most the minimum
  permissions required to perform the task. For more information, see
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege). 
- This code is not tested in all AWS Regions. For more information, see 
  [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/).

## Code examples

### Scenario

  [Get started using DynamoDB tables, items, and queries](scenarios/DynamoDB_Basics/DynamoDB_Basics_Scenario/)

  This application uses the basic features of DynamoDB for creating,
  updating, and managing data. The application was created using the AWS
  SDK for .NET version 3.7 and .NET Core 5. The application performs the
  following tasks:

  1. Creates a table with partition: year and sort:title. (`CreateTableAsync`)
  2. Adds a single movie to the table. (`PutItemAsync`)
  3. Adds movies to the table from moviedata.json. (`CreateBatchWrite`, `ExecuteAsync`)
  4. Updates the rating and plot of the movie that was just added. (`UpdateItemAsync`)
  5. Gets a movie using its key (partition + sort). (`GetItemAsync`)
  6. Deletes a movie. (`DeleteItemAsync`)
  7. Uses QueryAsync to return all movies released in a given year. (`QueryAsync`)
  8. Uses ScanAsync to return all movies released within a range of years. (`ScanAsync`)
  9. Finally, it deletes the table that was just created. (`DeleteTableAsync`)

### Single action

- [CreateTablesLoadDataExample](CreateTablesLoadDataExample/) - Creates
  multiple DynamoDB tables and adds items to them. (`CreateTableAsync`,
  `DescribeTableAsync`, `PutItemAsync`, `DeleteTableAsync`)
- [AddItemExample](FromSQL/AddItemExample/) - Adds an item to a DynamoDB table.
  (`PutItemAsync`)
- [AddItemsExample](FromSQL/AddItemsExample/) - Adds multiple items to a
  DynamoDB table. (`BatchWriteItemAsync`)
- [CreateIndexExample](FromSQL/CreateIndexExample/) - Creates a secondary index
  for a DynamoDB table. (`AddIndexAsync`)
- [CreateTableExample](FromSQL/CreateTableExample/) - creates a new DynamoDB
  table. (`CreateTableAsync`)
- [DeleteItemExample](FromSQL/DeleteItemExample/) - Deletes an item from a
  DynamoDB table. (`DeleteItemAsync`)
- [DeleteItemsExample](FromSQL/DeleteItemsExample/) - Uses BatchWriteItemsAsync
  to delete multiple items from a DynamoDB table. (`BatchWriteItemAsync`)
- [DeleteTableExample](FromSQL/DeleteTableExample/) - Deletes a DynamoDB table.
  (`DeleteTableAsync`)
- [GetLowProductStock](FromSQL/GetLowProductStock/) - Retrieves information
  about products in a DynamoDB table that fall below a certain level.
  (`ScanAsync`)
- [GetLowProductStockGSI](FromSQL/GetLowProductStockGSI/) - Retrieves
  information about products in a DynamoDB table that fall below a certain lev
  (`QueryAsync`)
- [GetOrdersExample](FromSQL/GetOrdersExample/) - Gets a list of orders from a
  DynamoDB table. (`ScanAsync`)
- [GetOrdersForProductGSI](FromSQL/GetOrdersForProductGSI/) - Retrieves orders
  from a DynamoDB table. (`QueryAsync`)
- [GetOrdersInDateRange](FromSQL/GetOrdersInDateRange/) - Retrieves orders from
  a DynamoDB table that fall within a date range. (`ScanAsync`)
- [GetOrdersInDateRangeGSI](FromSQL/GetOrdersInDateRangeGSI/) - Retrieves
  orders from a DynamoDB table that fall within a date range. (`QueryAsync`)
- [ListItemsExample](FromSQL/ListItemsExample/) - Lists the items in a DynamoDB
  table. (`GetItemsAsync`)
- [ListTablesExample](FromSQL/ListTablesExample/) - Lists the DynamoDB tables
  associated with an AWS account. (`ListTablesAsync`)
- [UpdateItemDataModelExample](FromSQL/UpdateItemDataModelExample/) Updates the
  data model for items in a DynamoDB table. (`UpdateTableItemAsync`, `LoadAsync`)
- [UpdateItemExample](FromSQL/UpdateItemExample/) - Updates an item in a
  DynamoDB table. (`UpdateItemAsync`)
- [HighLevelBatchWriteItemExample](high-level-api/HighLevelBatchWriteItemExample/) -
  Writes items to a DynamoDB table using batch calls. (`ExecuteAsync`)
- [HighLevelItemCRUDExample](high-level-api/HighLevelItemCRUDExample/) - 
  Performs CRUD (Create, Read, Update, and Delete) operations on a DynamoDB table.
  (`LoadAsync`, `DeleteAsync`, `SaveAsync`)
- [HighLevelMappingArbitraryDataExample](high-level-api/HighLevelMappingArbitraryDataExample/) - 
  Shows how to map arbitrary data to an Amazon DynamoDB table. (`SaveAsync`, `LoadAsync`)
- [HighLevelQueryAndScanExample](high-level-api/HighLevelQueryAndScanExample/) - 
  Performs high level query and scan operations on a DynamoDB table. 
  (`LoadAsync<T>`, `QueryAsync<T>`, `ScanAsync<T>`)
- [LowLevelBatchGet](low-level-api/LowLevelBatchGet/) - Retrieves multiple
  items from a DynamoDB table. (`BatchGetItemAsync`)
- [LowLevelBatchWrite](low-level-api/LowLevelBatchWrite/) - Performs a batch
  write operation on a DynamoDB table. (`BatchWriteItemAsync`)
- [LowLevelGlobalSecondaryIndexExample](low-level-api/LowLevelSecondaryIndexExample/) - 
  Creates and uses a DynamoDB table with a secondary index. (`CreateTableAsync`)
- [LowLevelItemBinaryExample](low-level-api/LowLevelItemBinaryExample/) - 
  Creates and adds items to a DynamoDB table using low-level API  calls.
  (`GetItemAsync`, `GetItemAsync`)
- [LowLevelItemCRUDExample](low-level-api/LowLevelItemCRUDExample/) Performs
  CRUD (Create, Read, Update, and Delete) perations on a DynamoDB table using
  the low-level API. (`PutItemAsync`, `GetItemAsync`, `UpdateItemAsync`, `DeleteItemAsync`)
- [LowLevelLocalSecondaryIndexExample](low-level-api/LowLevelLocalSecondaryIndexExample/) - 
  Uses low-level API calls to work with a DynamoDB table with a secondary index.
  (`CreateTableAsync`, `PutItemAsync`, `QueryAsync`, `DeleteTableAsync`, `DescribeTableAsync`)
- [LowLevelParallelScan](low-level-api/LowLevelParallelScan/) - 
- [LowLevelQuery](low-level-api/LowLevelQuery/) - Queries a DynamoDB table for
  information. (`QueryAsync`)
- [LowLevelScan](low-level-api/LowLevelScan/) - Scans a DynamoDB table for
  information. (`ScanAsync`, )
- [LowLevelTableExample](low-level-api/LowLevelTableExample/)- Performs
  operations on a DynamoDB table using low-level API methods. (`CreateTableAsync`,
  `ListTablesAsync`, `DescribeTableAsync`, `UpdateTableAsync`, `DeleteTableAsync`)
- [MidLevelBatchWriteItemExample](mid-level-api/MidLevelBatchWriteItemExample/) - 
  Adds items to a DynamoDB table in a batch. (`QueryAsync`, `ScanAsync`)
- [MidlevelItemCRUDExample](mid-level-api/MidlevelItemCRUDExample/) Performs
  CRUD (Create, Read, Update, and Delete) operations on a DynamoDB table.
  (`Table.LoadTable`, `PutItemAsync`, `GetItemAsync`, `UpdateItemAsync`, `DeleteItemAsync`)
- [MidLevelQueryAndScanExample](mid-level-api/MidLevelQueryAndScanExample/) - 
  Performs query and scan operations on a DynamoDB table using mid-level
  API commands. (`GetItemAsync`, `Table.Query`, `GetNextSetAsync`)
- [MidLevelScanOnlyExample](mid-level-api/MidLevelScanOnlyExample/) - Shows two
  different ways to scan a DynamoDB table. One method uses a configuration
  object, the other doesn't. (`Table.Scan`)

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

