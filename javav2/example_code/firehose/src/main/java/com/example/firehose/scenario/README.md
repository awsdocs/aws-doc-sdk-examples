# Amazon Data Firehose Common Actions

## Overview

This example shows how to use AWS SDKs to perform common actions with Amazon Data Firehose, such as putting individual records (`PutRecord`) and batches of records (`PutRecordBatch`) to a delivery stream.

The Data Firehose API has a maximum limit of 500 records or 4MB per request for `PutRecordBatch`. This example demonstrates how to handle scenarios where the number of records exceeds the maximum limit by breaking down the requests into multiple batches.

The following components are used in this example:

- [Amazon Data Firehose](https://docs.aws.amazon.com/firehose/latest/dev/what-is-this-service.html) is the service used to capture, transform, and load streaming data into data lakes, data stores, and analytics services.
- [Amazon S3](https://aws.amazon.com/s3/) is used as the destination for the Data Firehose delivery stream, storing the ingested data.
- [Amazon CloudWatch Logs](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/WhatIsCloudWatchLogs.html) hosts the Firehose metrics used to monitor Firehose performance.

For detailed information on this workflow, see the [firehose/README.md](../../../../../../../../../../scenarios/features/firehose/README.md).

## ⚠ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Run this code

### Prerequisites

To run this example, you need to set up the necessary infrastructure and generate mock data. Follow the steps outlined in the [workflow README](../../../../../../../../../../scenarios/features/firehose/README.md#setup) to create a Data Firehose delivery stream and generate sample data.

### Execution

This Java example will perform the following actions:

1. Initialize the AWS SDK for Java service clients. 
2. Define configuration parameters (delivery stream name, region, batch size, logging settings).
3. Put individual records using the `PutRecord` API.
4. Put batches of records using the `PutRecordBatch` API.
5. Monitor `IncomingBytes` and `IncomingRecords` metrics to ensure there is incoming traffic, and `FailedPutCount` for batch operations.

## Additional reading

- [Data Firehose Developer Guide](https://docs.aws.amazon.com/firehose/latest/dev/what-is-this-service.html)
- [Data Firehose API Reference](https://docs.aws.amazon.com/firehose/latest/APIReference/Welcome.html)
- [SDK for Java Data Firehose reference](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/firehose/FirehoseClient.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0