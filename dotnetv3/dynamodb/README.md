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
- [Querying a table using PartiQL](scenarios/PartiQL_Basics/PartiQL_Basics_Scenario)
- [Querying a table using PartiQL Batch methods](scenarios/PartiQL_Basics/PartiQL_Batch_Scenario)

### Single action

- [Creating DynamoDB tables and adding data](CreateTablesLoadDataExample/)
- [Adding an item to a  DynamoDB table](FromSQL/AddItemExample/)
- [Adding multiple items to a DynamoDB table](FromSQL/AddItemsExample/)
- [Creating an index for a  DynamoDB table](FromSQL/CreateIndexExample/)
- [Creating a  DynamoDB table](FromSQL/CreateTableExample/)
- [Deleting an item from a  DynamoDB table](FromSQL/DeleteItemExample/)
- [Deleting items from a  DynamoDB table](FromSQL/DeleteItemsExample/)
- [Deleting a  DynamoDB table](FromSQL/DeleteTableExample/)
- [Scanning a  DynamoDB table](FromSQL/GetLowProductStock/)
- [Querying a  DynamoDB table](FromSQL/GetLowProductStockGSI/)
- [Scanning a  DynamoDB table for orders](FromSQL/GetOrdersExample/)
- [Querying a DynamoDB table for product orders](FromSQL/GetOrdersForProductGSI/)
- [Scanning a  DynamoDB table for orders in a date range](FromSQL/GetOrdersInDateRange/)
- [Querying a DynamoDB table for orders in a date range](FromSQL/GetOrdersInDateRangeGSI/)
- [Listing the items in a DynamoDB table](FromSQL/ListItemsExample/)
- [Listing the  DynamoDB tables for an account](FromSQL/ListTablesExample/)
- [Updating the data model for a DynamoDB table](FromSQL/UpdateItemDataModelExample/)
- [Updating an item in a DynamoDB table](FromSQL/UpdateItemExample/)
- [Adding multiple items to a table using the high-level DynamoDB API](high-level-api/HighLevelBatchWriteItemExample/)
- [Performing CRUD operations using the high-level DynamoDB API](high-level-api/HighLevelItemCRUDExample/)
- [Mapping data using the high-level DynamoDB API](high-level-api/HighLevelMappingArbitraryDataExample/)
- [Performing query and scan operations on a DynamoDB table](high-level-api/HighLevelQueryAndScanExample/)
- [Using the low-level DynamoDB API to get items](low-level-api/LowLevelBatchGet/)
- [Performing a low-level batch write to a DynamoDB table](low-level-api/LowLevelBatchWrite/)
- [Creating a DynamoDB table with a secondary index](low-level-api/LowLevelSecondaryIndexExample/)
- [LowLevelItemBinaryExample](low-level-api/LowLevelItemBinaryExample/)
- [Performing CRUD operations using the low-level DynamoDB API](low-level-api/LowLevelItemCRUDExample/)
- [Using a secondary index](low-level-api/LowLevelLocalSecondaryIndexExample/)
- [Performing a parallel scan of a DynamoDB table using the low-level API](low-level-api/LowLevelParallelScan/)
- [Performing a query of a DynamoDB table using the low-level API](low-level-api/LowLevelQuery/)
- [Performing a scan of a DynamoDB table using the low-level API](low-level-api/LowLevelScan/)
- [Performing CRUD operations using the low-level API](low-level-api/LowLevelTableExample/)
- [Using the mid-level DynamoDB API to query and scan a table](mid-level-api/MidLevelBatchWriteItemExample/)
- [Performing CRUD operations on a DynamoDB table using the mid-level API](mid-level-api/MidlevelItemCRUDExample/)
- [Querying and scanning a DynamoDB table using the mid-level API](mid-level-api/MidLevelQueryAndScanExample/)
- [Scanning a DynamoDB table using the mid-level API](mid-level-api/MidLevelScanOnlyExample/)

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
