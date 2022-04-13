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

  [DynamoDB Basics](scenarios/DynamoDB_Basics/DynamoDB_Basics_Scenario/)

  This application uses the basic features of the DynamoDB for creating,
  managing, and managing data. The application was created using the AWS
  SDK for .NET version 3.7 and .NET Core 5. The application the following
  API commands:

  - CreateTableAsync
  - PutItemAsync
  - UpdateItemAsync
  - GetItemAsync
  - CreateBatchWrite
  - DeleteItemAsync
  - Query
  - ScanAsync
  - DeleteTableAsync

### Single action

- [CreateTablesLoadDataExample](CreateTablesLoadDataExample/CreateTablesLoadDataExample/) -
  Creates several tables and adds sample data for use with other examples. (`CreateTableAsync`)
- [AddItemExample](FromSQL/AddItemExample/AddItemExample/)
- [AddItemsExample](FromSQL/AddItemsExample/AddItemsExample/)
- [CreateIndexExample](FromSQL/CreateIndexExample/CreateIndexExample/)
- [CreateTableExample](FromSQL/CreateTableExample/CreateTableExample/)
- [DeleteItemExample](FromSQL/DeleteItemExample/DeleteItemExample/)
- [DeleteItemsExample](FromSQL/DeleteItemsExample/DeleteItemsExample/)
- [DeleteTableExample](FromSQL/DeleteTableExample/DeleteTableExample/)
- [GetLowProductStock](FromSQL/GetLowProductStock/)
- [GetLowProductStockGSI](FromSQL/GetLowProductStockGSI/)
- [GetOrdersExample](FromSQL/GetOrdersExample/GetOrdersExample/)
- [GetOrdersForProductGSI](FromSQL/GetOrdersForProductGSI/)
- [GetOrdersInDateRange](FromSQL/GetOrdersInDateRange/)
- [GetOrdersInDateRangeGSI](FromSQL/GetOrdersInDateRangeGSI/)
- [ListItemsExample](FromSQL/ListItemsExample/ListItemsExample/)
- [ListTablesExample](FromSQL/ListTablesExample/ListTablesExample/)
- [UpdateItemDataModelExample](FromSQL/UpdateItemDataModelExample/UpdateItemDataModelExample/)
- [UpdateItemDataModelExample](FromSQL/UpdateItemDataModelExample/UpdateItemDataModelExample/)
- [UpdateItemExample](FromSQL/UpdateItemExample/UpdateItemExample/)
- [HighLevelBatchWriteItemExample](high-level-api/HighLevelBatchWriteItemExample/HighLevelBatchWriteItemExample/)
- [HighLevelItemCRUDExample](high-level-api/HighLevelItemCRUDExample/HighLevelItemCRUDExample/)
- [HighLevelMappingArbitraryDataExample](high-level-api/HighLevelMappingArbitraryDataExample/HighLevelMappingArbitraryDataExample/)
- [HighLevelQueryAndScanExample](high-level-api/HighLevelQueryAndScanExample/HighLevelQueryAndScanExample/)
- [LowLevelBatchGet](low-level-api/LowLevelBatchGet/)
- [LowLevelBatchWrite](low-level-api/LowLevelBatchWrite/)
- [LowLevelGlobalSecondaryIndexExample](low-level-api/LowLevelGlobalSecondaryIndexExample/)
- [LowLevelItemBinaryExample](low-level-api/LowLevelItemBinaryExample/)
- [LowLevelItemCRUDExample](low-level-api/LowLevelItemCRUDExample/)
- [LowLevelLocalSecondaryIndexExample](low-level-api/LowLevelLocalSecondaryIndexExample/)
- [LowLevelParallelScan](low-level-api/LowLevelParallelScan/)
- [LowLevelQuery](low-level-api/LowLevelQuery/)
- [LowLevelScan](low-level-api/LowLevelScan/)
- [LowLevelTableExample](low-level-api/LowLevelTableExample/)
- [MidLevelBatchWriteItemExample](mid-level-api/MidLevelBatchWriteItemExample/MidLevelBatchWriteItemExample/)
- [MidlevelItemCRUDExample](mid-level-api/MidlevelItemCRUDExample/MidlevelItemCRUDExample/)
- [MidLevelQueryAndScanExample](mid-level-api/MidLevelQueryAndScanExample/MidLevelQueryAndScanExample/)
- [MidLevelScanOnlyExample](mid-level-api/MidLevelScanOnlyExample/MidLevelScanOnlyExample/)


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

