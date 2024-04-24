---
debug:
  id: null
  model: anthropic.claude-3-sonnet-20240229-v1:0
  usage: null
  finish: end_turn
  engine: bedrock
isolated: false
combined: true
prompt: |
  Write me a SPECIFICATION.md (in GitHub markdown) for a new Workflow Example.

  This document will be informed by the provided Workflow Details, the README, and this template.

  # Introduction:
  Provide a brief description of the new use case, mentioning its relevance and integration with AWS services.
  Link to the corresponding README.md for a high-level overview (same directory).

  # Table of Contents:
  Include a table of contents that outlines the structure of the document, ensuring easy navigation through sections like Architecture, User Input, Common Resources, Building Queries (or equivalent for the use case), Output, and Metadata.

  # Architecture:
  Detail the AWS services and resources involved in the use case.
  Provide a high-level architectural diagram or description that explains how these components interact.

  # User Input:
  Describe the inputs required from users to interact with the example. Include suggested variable names and their purposes.

  # Common Resources:
  List and describe common resources, such as AWS CloudFormation templates or scripts, stored in a resources folder. Provide the purpose and effect of each.

  # Building the operational process:
  Offer a step-by-step guide on the main operational logic of the use case, such as querying, data processing, or resource management.
  Include sample code or pseudocode to illustrate critical parts of the process.

  # Output:
  Explain the expected output, including any logging or results that users should anticipate.
  Provide an example of the output format or content to set proper expectations.

  # Metadata:
  Create a metadata table that links actions or scenarios to specific metadata files and keys, providing a clear mapping for developers.'

  The specification is independent of any programming language, and should enable any programmer competent with programming using any published AWS SDK to follow along. It must specify the API calls to make, and it must include the parameters to send. It should describe the parameters in a list format.  Implementations will use the specific SDKs, so it does not need to specify URL calls to make, only the API calls and the request parameters to include. It must specify the exact environment variable names and files to use when referring to runtime data.
---
# Kinesis Firehose PutRecord and PutRecordBatch Operations

This document provides a specification for using the AWS Kinesis Firehose service to reliably and efficiently ingest data using the PutRecord and PutRecordBatch operations. For a high-level overview and deployment instructions, refer to the [README.md](README.md) in the same directory.

## Table of Contents

- [Introduction](#introduction)
- [Architecture](#architecture)
- [User Input](#user-input)
- [Common Resources](#common-resources)
- [Building the Operational Process](#building-the-operational-process)
  - [PutRecord Operation](#putrecord-operation)
  - [PutRecordBatch Operation](#putrecordbatch-operation)
- [Output](#output)
- [Metadata](#metadata)

## Introduction

This example demonstrates how to use the AWS Kinesis Firehose service for reliable and high-performance data ingestion using the PutRecord and PutRecordBatch operations. It addresses batch sizing considerations, robust error handling, logging, pagination, and exception handling with retry mechanisms featuring exponential back-off with jitter.

## Architecture

The example uses the following AWS services and resources:

- Kinesis Firehose Delivery Stream
- Amazon CloudWatch Logs (for logging)

The Kinesis Firehose Delivery Stream ingests data from various sources and delivers it to destinations like Amazon S3, Amazon Redshift, or Amazon OpenSearch Service. CloudWatch Logs is used for logging and monitoring purposes.

## User Input

Users need to provide the following inputs:

- `deliveryStreamName`: The name of the Kinesis Firehose Delivery Stream.
- `logGroupName`: The name of the CloudWatch Log Group for logging.
- `records`: An array of records to be ingested into the Delivery Stream.

## Common Resources

The `resources` directory contains the following files:

- `cdk.ts`: A TypeScript CDK script to create the necessary AWS resources (Kinesis Firehose Delivery Stream and CloudWatch Log Group).
- `setup.sh`: A bash script to install the required dependencies and build the CDK script.

## Building the Operational Process

### PutRecord Operation

1. Create an instance of the `Firehose` client from the AWS SDK.
2. Call the `putRecord` method on the `Firehose` client, providing the following parameters:
   - `DeliveryStreamName`: The name of the Kinesis Firehose Delivery Stream.
   - `Record`: The record data to be ingested, represented as a `Buffer` or `Uint8Array`.
3. Implement error handling and logging for the `putRecord` operation.
4. Implement a retry mechanism with exponential back-off and jitter for failed `putRecord` attempts.

### PutRecordBatch Operation

1. Create an instance of the `Firehose` client from the AWS SDK.
2. Call the `putRecordBatch` method on the `Firehose` client, providing the following parameters:
   - `DeliveryStreamName`: The name of the Kinesis Firehose Delivery Stream.
   - `Records`: An array of record data to be ingested, represented as `Buffer` or `Uint8Array` objects.
3. Implement error handling and logging for the `putRecordBatch` operation.
4. Check the `FailedPutCount` in the response and handle failed record puts accordingly.
5. Implement a retry mechanism with exponential back-off and jitter for failed `putRecordBatch` attempts.

## Output

The example will log the following information to the CloudWatch Log Group:

- Successful record ingestion
- Failed record ingestion attempts
- Retry attempts for failed operations
- Error messages and stack traces (in case of exceptions)

## Metadata

The following metadata files are included in the `metadata` directory:

| File | Key | Description |
|------|-----|-------------|
| `retry-policy.json` | `retryPolicy` | Configuration for the retry mechanism, including exponential back-off and jitter settings. |
| `logging.json` | `logging` | Configuration for logging, including log level and log format. |
| `batch-sizing.json` | `batchSizing` | Configuration for batch sizing, including maximum batch size and record size limits. |