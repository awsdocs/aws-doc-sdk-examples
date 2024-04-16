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

This document provides a detailed specification for implementing a production-grade ingestion process into Amazon Kinesis Data Firehose. It demonstrates best practices for handling batching, error handling, monitoring, and infrastructure provisioning. For a high-level overview and setup instructions, refer to the [README.md](README.md) in the same directory.

# Table of Contents

- [Introduction](#introduction)
- [Architecture](#architecture)
- [User Input](#user-input)
- [Common Resources](#common-resources)
- [Building the Operational Process](#building-the-operational-process)
  - [Single Record Ingestion](#single-record-ingestion)
  - [Batch Record Ingestion](#batch-record-ingestion)
  - [Error Handling and Retries](#error-handling-and-retries)
  - [Monitoring](#monitoring)
- [Output](#output)
- [Metadata](#metadata)

# Architecture

This example utilizes the following AWS services and resources:

- **Amazon Kinesis Data Firehose**: A fully managed service for automatically loading streaming data into AWS data stores and analytics tools. It serves as the primary ingestion destination for data records.
- **AWS Cloud Development Kit (CDK)**: An open-source software development framework to define cloud infrastructure as code and provision it through AWS CloudFormation. It is used to set up the Kinesis Data Firehose delivery stream and any other necessary resources.

The high-level architecture involves generating data records, which are then ingested into Kinesis Data Firehose using the AWS SDK. The CDK script provisions the required infrastructure, while the ingestion logic handles batching, error handling, retries, and monitoring.

# User Input

Users can provide the following inputs to interact with the example:

- `deliveryStreamName` (string): The name of the Kinesis Data Firehose delivery stream to ingest data into.
- `region` (string): The AWS region where the Kinesis Data Firehose delivery stream is located.
- `maxBatchSize` (number, optional): The maximum number of records to include in a `PutRecordBatch` request. Defaults to 500.
- `maxBatchSizeBytes` (number, optional): The maximum size (in bytes) of a `PutRecordBatch` request. Defaults to 4MB.

# Common Resources

The following common resources are included in the `resources/` folder:

- `cdk.json`: A configuration file for the AWS CDK deployment, specifying the AWS region and other settings.
- `infra-stack.ts`: An AWS CDK script that defines the CloudFormation stack for provisioning the Kinesis Data Firehose delivery stream and any other necessary resources.
- `generate-data.js`: A script for generating sample data records to ingest into Kinesis Data Firehose.

# Building the Operational Process

## Single Record Ingestion

To ingest a single record into Kinesis Data Firehose, follow these steps:

1. Import the required AWS SDK and configure the appropriate region.
2. Create a `putRecordCommand` using the `PutRecordCommand` from the `@aws-sdk/client-firehose` package.
3. Set the required parameters for the `PutRecordCommand`:
   - `DeliveryStreamName`: The name of the Kinesis Data Firehose delivery stream.
   - `Record`: An object containing the data record to ingest, with properties like `Data` (a Buffer or string containing the record data) and `PartitionKey` (for partitioning the data stream, if applicable).
4. Execute the `putRecordCommand` using the appropriate SDK method (e.g., `send` in the AWS SDK for JavaScript).
5. Implement error handling and logging for the response, accounting for potential failures or exceptions.

Example pseudocode:

```javascript
import { FirehoseClient, PutRecordCommand } from '@aws-sdk/client-firehose';

const client = new FirehoseClient({ region: 'us-west-2' });
const command = new PutRecordCommand({
  DeliveryStreamName: 'MyDeliveryStream',
  Record: {
    Data: Buffer.from('Sample data record'),
  },
});

try {
  const response = await client.send(command);
  console.log('Record ingested successfully:', response);
} catch (error) {
  console.error('Error ingesting record:', error);
}
```

## Batch Record Ingestion

To ingest multiple records in a batch using `PutRecordBatch`, follow these steps:

1. Import the required AWS SDK and configure the appropriate region.
2. Create a `putRecordBatchCommand` using the `PutRecordBatchCommand` from the `@aws-sdk/client-firehose` package.
3. Set the required parameters for the `PutRecordBatchCommand`:
   - `DeliveryStreamName`: The name of the Kinesis Data Firehose delivery stream.
   - `Records`: An array of record objects, each containing properties like `Data` (a Buffer or string containing the record data) and `PartitionKey` (for partitioning the data stream, if applicable).
4. Execute the `putRecordBatchCommand` using the appropriate SDK method (e.g., `send` in the AWS SDK for JavaScript).
5. Implement error handling and logging for the response, accounting for potential failures or exceptions. Check the `FailedPutCount` property in the response to handle any failed records in the batch.

Example pseudocode:

```javascript
import { FirehoseClient, PutRecordBatchCommand } from '@aws-sdk/client-firehose';

const client = new FirehoseClient({ region: 'us-west-2' });
const records = [
  { Data: Buffer.from('Record 1') },
  { Data: Buffer.from('Record 2') },
  // ... add more records
];

const command = new PutRecordBatchCommand({
  DeliveryStreamName: 'MyDeliveryStream',
  Records: records,
});

try {
  const response = await client.send(command);
  console.log('Batch ingested successfully:', response);

  if (response.FailedPutCount > 0) {
    console.warn(`${response.FailedPutCount} records failed to ingest.`);
  }
} catch (error) {
  console.error('Error ingesting batch:', error);
}
```

## Error Handling and Retries

To handle errors and implement retries with exponential backoff and jitter, follow these steps:

1. Import the required AWS SDK and configure the appropriate region.
2. Create a utility function to handle retries with exponential backoff and jitter. This function should accept the original operation, the maximum number of retries, and any additional configuration (e.g., base delay, max delay, retry condition).
3. Within the retry function, execute the original operation and handle any exceptions or errors.
4. If the operation fails and the retry condition is met, calculate the delay for the next retry attempt using an exponential backoff strategy with jitter.
5. After the delay, recursively call the retry function with the original operation and updated retry count.
6. If the maximum number of retries is exceeded, throw an appropriate error or handle the failure accordingly.

Example pseudocode:

```javascript
import { FirehoseClient, PutRecordCommand } from '@aws-sdk/client-firehose';

const client = new FirehoseClient({ region: 'us-west-2' });
const command = new PutRecordCommand({
  DeliveryStreamName: 'MyDeliveryStream',
  Record: {
    Data: Buffer.from('Sample data record'),
  },
});

const maxRetries = 5;
const baseDelay = 100; // milliseconds
const maxDelay = 30000; // milliseconds

async function retryWithExponentialBackoff(operation, retryCount = 0) {
  try {
    const response = await operation();
    console.log('Operation succeeded:', response);
  } catch (error) {
    if (retryCount < maxRetries) {
      const delay = Math.min(baseDelay * 2 ** retryCount, maxDelay);
      const jitter = Math.random() * 0.3 * delay;
      const totalDelay = delay + jitter;

      console.warn(`Operation failed, retrying in ${totalDelay} ms...`);
      setTimeout(() => retryWithExponentialBackoff(operation, retryCount + 1), totalDelay);
    } else {
      console.error('Maximum retries exceeded, operation failed:', error);
    }
  }
}

retryWithExponentialBackoff(() => client.send(command));
```

## Monitoring

To monitor the ingestion process and track metrics like `IncomingBytes` and `IncomingRecords`, follow these steps:

1. Import the required AWS SDK and configure the appropriate region.
2. Create a `describeDeliveryStreamCommand` using the `DescribeDeliveryStreamCommand` from the `@aws-sdk/client-firehose` package.
3. Set the required parameters for the `DescribeDeliveryStreamCommand`:
   - `DeliveryStreamName`: The name of the Kinesis Data Firehose delivery stream.
4. Execute the `describeDeliveryStreamCommand` using the appropriate SDK method (e.g., `send` in the AWS SDK for JavaScript).
5. Access the `DeliveryStreamDescription` property in the response, which contains various metrics and statistics about the delivery stream.
6. Log or process the relevant metrics, such as `IncomingBytes` and `IncomingRecords`.

Example pseudocode:

```javascript
import { FirehoseClient, DescribeDeliveryStreamCommand } from '@aws-sdk/client-firehose';

const client = new FirehoseClient({ region: 'us-west-2' });
const command = new DescribeDeliveryStreamCommand({
  DeliveryStreamName: 'MyDeliveryStream',
});

try {
  const response = await client.send(command);
  const { DeliveryStreamDescription } = response;

  console.log('Ingestion metrics:');
  console.log('IncomingBytes:', DeliveryStreamDescription.IncomingBytes);
  console.log('IncomingRecords:', DeliveryStreamDescription.IncomingRecords);
} catch (error) {
  console.error('Error retrieving delivery stream description:', error);
}
```

# Output

The output of this example will consist of log messages and console output, indicating the progress and status of the ingestion process. Successful ingestion will be logged, along with any errors, retries, or relevant metrics.

Example output:

```
Record ingested successfully: {
  RecordId: '...',
  // Other response properties
}
Batch ingested successfully: {
  FailedPutCount: 0,
  // Other response properties
}
Operation failed, retrying in 200 ms...
Operation succeeded: {
  // Response properties
}
Ingestion metrics:
IncomingBytes: 1234
IncomingRecords: 10
```

# Metadata

The following table maps the actions or scenarios in this example to their respective metadata files and keys:

| Action/Scenario | Metadata File | Metadata Key |
|-----------------|----------------|---------------|
| Single Record Ingestion | metadata.yaml | single_record_ingestion |
| Batch Record Ingestion | metadata.yaml | batch_record_ingestion |
| Error Handling and Retries | metadata.yaml | error_handling_retries |
| Monitoring | metadata.yaml | monitoring |
```