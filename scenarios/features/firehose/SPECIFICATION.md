## Spec for Amazon Data Firehose Common Actions Scenario 

Create a robust, production-grade script demonstrating how to use Amazon Data Firehose to put individual records (`PutRecord`) and batches of records (`PutRecordBatch`) to a delivery stream.

## Pre-requisites
1. Complete setup outlined in the [README.md](README.md#setup).
4. **OPTIONAL: Ailly Configured**: Configure [Ailly](https://github.com/davidsouther/ailly) to use the [start.sh](./start.sh) script to generate a new scenario using AI.

## Infra & Data Setup
To implement this specification, you will need to follow the [setup steps](README.md#run-this-scenario) outlined in the README.md. 

## Script Specification

### 1. Setup AWS SDK
- Initialize the AWS SDK in your chosen language.
- Ensure the necessary permissions are set up (IAM roles/policies) to allow interaction with the Firehose service.
- Ensure the setup infra and data has been generated ([see previous step](#infra--data-setup)).

### 2. Define Configuration Parameters
- Define configuration parameters such as `delivery_stream_name`, region, batch size, and logging configurations.

### 3. Required Features
The following two functions will be implemented by the script, in sequence. For example, when the script it run it will use the PutRecord API implementation to process a default of 100 records, then use the BatchPutRecord API implementation to process the remaining 5,450 records, for a grand total of 5,550 records processed.

#### PutRecord API
First, the script will implement a function that puts a single record into the Firehose stream from the provided data file.
- **Input**: Data record (e.g., a JSON object or plain text), and Number of Records to process (default 10).
- **Output**: Response from Firehose service indicating success or failure.
- **Error Handling**: Implement retries with exponential back-off and jitter.

#### PutRecordBatch API
Next, the script will implement a function to put a batch of records into the Firehose stream.
- **Input**: List of data records.
- **Output**: Response from Firehose service indicating success or failure for each record, and Number of Records to batch (default 500).
- **Error Handling**: Implement retries with exponential back-off and jitter. Use third party libraries where appropriate.
- **Pagination**: Handle large batches by splitting them into smaller chunks.
- **Batch Sizing**: Optimize batch size to balance throughput and cost, considering the hard limits (500 records or 4MB per request).

### 4. Logging
- Implement structured logging to capture the success and failure of API calls.
- Log retries, exceptions, batch operations, and any critical information for debugging and monitoring.
- Provide real-time updates on the script's activity.

### 5. Monitoring Metrics
- Check `IncomingBytes` and `IncomingRecords` metrics to ensure there is incoming traffic.
- Monitor `FailedPutCount` for batch operations._
