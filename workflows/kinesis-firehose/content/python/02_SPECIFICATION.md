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
```markdown
# Introduction

This specification document describes the implementation details for reliably and efficiently performing PutRecord and PutRecordBatch operations using the AWS Kinesis Firehose service. It addresses batch limits, pricing, and throughput considerations while incorporating robust error handling, logging, pagination, and retry mechanisms. For a high-level overview, please refer to the [README.md](README.md) in the same directory.

# Table of Contents

- [Introduction](#introduction)
- [Architecture](#architecture)
- [User Input](#user-input)
- [Common Resources](#common-resources)
- [Building the Operational Process](#building-the-operational-process)
  - [PutRecord Operation](#putrecord-operation)
  - [PutRecordBatch Operation](#putrecordbatch-operation)
- [Output](#output)
- [Metadata](#metadata)

# Architecture

This example utilizes the following AWS services and resources:

- **AWS Kinesis Firehose**: A fully managed service for delivering real-time streaming data to destinations such as Amazon S3, Amazon Redshift, and more.
- **Amazon S3**: An object storage service that provides scalable and durable storage for data.

The Kinesis Firehose Delivery Stream is configured to deliver data to an S3 bucket. The necessary resources are provisioned using a TypeScript CDK script located in the `infrastructure` folder.

# User Input

Users can provide the following inputs to interact with the example:

- `streamName` (string): The name of the Kinesis Firehose Delivery Stream.
- `bucketName` (string): The name of the S3 bucket where data will be delivered.
- `records` (list of strings): A list of records to be put into the Delivery Stream.

# Common Resources

The `infrastructure` folder contains the following common resources:

- `kinesis-firehose-example.ts`: A TypeScript CDK script that provisions the necessary AWS resources (Kinesis Firehose Delivery Stream and S3 bucket).

# Building the Operational Process

## PutRecord Operation

1. Instantiate the Kinesis Firehose client using the AWS SDK.
2. Create a `PutRecordInput` object with the following parameters:
   - `DeliveryStreamName` (string): The name of the Kinesis Firehose Delivery Stream.
   - `Record` (Binary or Base64-encoded data): The data to be delivered to the Delivery Stream.
3. Call the `putRecord` method of the Kinesis Firehose client with the `PutRecordInput` object.
4. Implement robust error handling, logging, and retry mechanisms with exponential back-off and jitter.

## PutRecordBatch Operation

1. Instantiate the Kinesis Firehose client using the AWS SDK.
2. Create a `PutRecordBatchInput` object with the following parameters:
   - `DeliveryStreamName` (string): The name of the Kinesis Firehose Delivery Stream.
   - `Records` (list of `Record` objects): The list of records to be delivered to the Delivery Stream.
3. Call the `putRecordBatch` method of the Kinesis Firehose client with the `PutRecordBatchInput` object.
4. Implement robust error handling, logging, pagination, and retry mechanisms with exponential back-off and jitter.
5. Check the `FailedPutCount` in the response and handle failed records accordingly.

# Output

The example will output logs and information related to the PutRecord and PutRecordBatch operations, including success and failure cases, retries, and any relevant error messages. The delivered data can be viewed in the configured S3 bucket.

# Metadata

The following metadata table maps actions or scenarios to specific metadata files and keys:

| Action/Scenario | Metadata File | Metadata Key |
| --------------- | ------------- | ------------ |
| PutRecord operation | `metadata.yaml` | `putRecord` |
| PutRecordBatch operation | `metadata.yaml` | `putRecordBatch` |
| Retry mechanism | `metadata.yaml` | `retryMechanism` |
| Error handling | `metadata.yaml` | `errorHandling` |
```