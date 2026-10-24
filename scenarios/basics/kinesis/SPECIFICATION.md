# Amazon Kinesis Data Streams Specification

This specification describes a Basics scenario for Amazon Kinesis Data Streams that demonstrates advanced stream management, batch data ingestion, enhanced fan-out consumer registration, shard inspection, and resource tagging. It is intended as a draft for the AWS code examples team to implement in multiple languages.

**IMPORTANT:** This scenario covers operations NOT already present in existing code examples. Existing examples cover: `CreateStream`, `DeleteStream`, `DescribeStream`, `GetRecords`, `PutRecord`. This scenario uses 10 different operations to expand coverage.

### Resources

- A Kinesis data stream (created and deleted within the scenario using the Kinesis client directly).
- No external AWS resources are required. All resources belong to the Kinesis service and are managed via the Kinesis client API.

### Relevant documentation

* [What is Amazon Kinesis Data Streams?](https://docs.aws.amazon.com/streams/latest/dev/introduction.html)
* [Amazon Kinesis Data Streams API Reference](https://docs.aws.amazon.com/kinesis/latest/APIReference/Welcome.html)
* [ListStreams API](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_ListStreams.html)
* [DescribeStreamSummary API](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_DescribeStreamSummary.html)
* [ListShards API](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_ListShards.html)
* [PutRecords API](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_PutRecords.html)
* [GetShardIterator API](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_GetShardIterator.html)
* [AddTagsToStream API](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_AddTagsToStream.html)
* [ListTagsForStream API](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_ListTagsForStream.html)
* [RemoveTagsFromStream API](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_RemoveTagsFromStream.html)
* [RegisterStreamConsumer API](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_RegisterStreamConsumer.html)
* [DeregisterStreamConsumer API](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_DeregisterStreamConsumer.html)
* [Enhanced Fan-Out Using the Kinesis Data Streams API](https://docs.aws.amazon.com/streams/latest/dev/building-enhanced-consumers-api.html)
* [Tagging Your Streams](https://docs.aws.amazon.com/streams/latest/dev/tagging.html)

### API Actions Used

* [ListStreams](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_ListStreams.html) - Lists Kinesis data streams in the account.
* [DescribeStreamSummary](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_DescribeStreamSummary.html) - Provides a summarized description of a stream without the shard list.
* [ListShards](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_ListShards.html) - Lists the shards in a stream with detailed shard information.
* [PutRecords](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_PutRecords.html) - Writes multiple data records into a stream in a single call (batch operation).
* [GetShardIterator](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_GetShardIterator.html) - Gets a shard iterator for reading data records sequentially from a shard.
* [AddTagsToStream](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_AddTagsToStream.html) - Adds or updates tags for a stream.
* [ListTagsForStream](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_ListTagsForStream.html) - Lists the tags for a stream.
* [RemoveTagsFromStream](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_RemoveTagsFromStream.html) - Removes tags from a stream.
* [RegisterStreamConsumer](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_RegisterStreamConsumer.html) - Registers an enhanced fan-out consumer with a stream.
* [DeregisterStreamConsumer](https://docs.aws.amazon.com/kinesis/latest/APIReference/API_DeregisterStreamConsumer.html) - Deregisters a stream consumer.

## Hello Kinesis

The Hello example is a separate, minimal runnable program that verifies connectivity to the Kinesis service.

- Create a Kinesis service client.
- Call `ListStreams` with a limit of 10.
- Display the stream names returned, or a message stating no streams were found.
- Handle errors gracefully (e.g., `LimitExceededException`).

Example output:
```
Hello, Amazon Kinesis! Let's list some of your streams:
  Stream: my-application-stream
  Stream: clickstream-data
Found 2 stream(s).
```

If no streams exist:
```
Hello, Amazon Kinesis! Let's list some of your streams:
No streams found in your account.
```

## Scenario

This scenario demonstrates advanced Kinesis Data Streams management by creating a stream, inspecting its shards, batch-ingesting data, reading records via shard iterators, managing tags for cost allocation, registering an enhanced fan-out consumer, and cleaning up all resources. It forms a coherent real-world workflow that covers stream lifecycle management beyond the basic create/put/get/delete pattern.

### Setup

1. **Create a Kinesis data stream** using `CreateStream` (from existing examples, but necessary as a prerequisite — this call is part of setup, NOT one of the 10 scenario operations). Create a stream with 1 shard and a unique name (e.g., `sdk-example-stream-<timestamp>`).
2. **Wait for the stream to become ACTIVE** by polling `DescribeStreamSummary` until `StreamStatus` is `ACTIVE`. Use a polling loop with a short delay (e.g., 2 seconds between polls, up to 60 seconds). This serves as a waiter pattern.

**Note:** `CreateStream` is used ONLY in setup as a prerequisite. It is NOT counted among the 10 scenario operations since it is already covered in existing examples.

Example output:
```
--------------------------------------------------------------------------------
Welcome to the Amazon Kinesis Data Streams Scenario.
This scenario demonstrates advanced stream management, batch data
ingestion, shard inspection, tagging, and enhanced fan-out consumers.
--------------------------------------------------------------------------------

Setting up resources...
Creating Kinesis stream: sdk-example-stream-1696789012
Waiting for stream to become ACTIVE...
Stream is now ACTIVE.
--------------------------------------------------------------------------------
```

### Step 1: List Streams and Verify Creation

Call `ListStreams` to list all streams in the account and verify the newly created stream is present.

- Call `ListStreams` (optionally with a Limit parameter).
- Iterate through the returned `StreamSummaries` to find the scenario stream.
- Display the list of streams with their status and mode (PROVISIONED or ON_DEMAND).
- Handle pagination if `HasMoreStreams` is true (by setting `ExclusiveStartStreamName`).

**API details:**
- **Request:** `ListStreams(Limit=100)`
- **Response:** `StreamNames` (list of strings), `StreamSummaries` (list with StreamName, StreamARN, StreamStatus, StreamModeDetails), `HasMoreStreams` (boolean)

Example output:
```
Step 1: Listing streams in your account...
  Stream: sdk-example-stream-1696789012 (Status: ACTIVE, Mode: PROVISIONED)
Found 1 stream(s). Our scenario stream is present.
--------------------------------------------------------------------------------
```

### Step 2: Describe Stream Summary

Call `DescribeStreamSummary` to get detailed metadata about the stream without listing all shards.

- Call `DescribeStreamSummary` with the stream name.
- Display the stream ARN, status, retention period, open shard count, encryption type, and monitoring details.
- Save the stream ARN for use in later steps (needed for RegisterStreamConsumer).

**API details:**
- **Request:** `DescribeStreamSummary(StreamName="sdk-example-stream-...")`
- **Response:** `StreamDescriptionSummary` containing: `StreamName`, `StreamARN`, `StreamStatus`, `RetentionPeriodHours` (default 24), `OpenShardCount`, `EncryptionType` (NONE or KMS), `EnhancedMonitoring` (list of shard-level metrics), `StreamCreationTimestamp`, `StreamModeDetails` (PROVISIONED or ON_DEMAND)

Example output:
```
Step 2: Describing stream summary...
  Stream Name: sdk-example-stream-1696789012
  Stream ARN: arn:aws:kinesis:us-east-1:123456789012:stream/sdk-example-stream-1696789012
  Status: ACTIVE
  Retention Period: 24 hours
  Open Shard Count: 1
  Encryption: NONE
  Stream Mode: PROVISIONED
--------------------------------------------------------------------------------
```

### Step 3: List Shards

Call `ListShards` to enumerate the shards in the stream and display detailed shard information.

- Call `ListShards` with the stream name or ARN.
- Display each shard's ID, hash key range (StartingHashKey to EndingHashKey), and sequence number range.
- Save the first shard ID for use in Step 5 (GetShardIterator).
- Handle pagination via `NextToken` if the stream has many shards.

**API details:**
- **Request:** `ListShards(StreamName="sdk-example-stream-...")`
- **Response:** `Shards` (array of Shard objects, each with ShardId, HashKeyRange with StartingHashKey/EndingHashKey, SequenceNumberRange with StartingSequenceNumber/EndingSequenceNumber, ParentShardId, AdjacentParentShardId), `NextToken` (string, for pagination)

Example output:
```
Step 3: Listing shards in the stream...
  Shard ID: shardId-000000000000
    Hash Key Range: 0 - 340282366920938463463374607431768211455
    Starting Sequence Number: 49640108810694405084505378892770476580360842874393...
Found 1 shard(s).
--------------------------------------------------------------------------------
```

### Step 4: Batch Put Records with PutRecords

Call `PutRecords` to write a batch of data records into the stream in a single API call.

- Build an array of 5 records, each with a Data blob and a PartitionKey. Use simulated IoT sensor data as the record payloads:
  - Record 1: `{"sensor_id": "sensor-001", "temperature": 72.5, "timestamp": "2024-01-15T10:00:00Z"}`
  - Record 2: `{"sensor_id": "sensor-002", "temperature": 68.3, "timestamp": "2024-01-15T10:00:01Z"}`
  - Record 3: `{"sensor_id": "sensor-001", "temperature": 73.1, "timestamp": "2024-01-15T10:00:02Z"}`
  - Record 4: `{"sensor_id": "sensor-003", "temperature": 75.8, "timestamp": "2024-01-15T10:00:03Z"}`
  - Record 5: `{"sensor_id": "sensor-002", "temperature": 67.9, "timestamp": "2024-01-15T10:00:04Z"}`
- Use the sensor_id as the PartitionKey so records from the same sensor are routed to the same shard.
- Check the response for `FailedRecordCount`. If any records failed, log a warning with the error details.
- Display the sequence number and shard ID for each successfully written record.

**API details:**
- **Request:** `PutRecords(StreamName="...", Records=[{Data=<bytes>, PartitionKey="sensor-001"}, ...])`
- **Response:** `FailedRecordCount` (integer), `Records` (array, each with ShardId, SequenceNumber, ErrorCode, ErrorMessage)
- Each PutRecords request can contain up to 500 records, with a total payload limit of 5 MiB.

Example output:
```
Step 4: Putting 5 records into the stream using PutRecords...
  Record 1: ShardId=shardId-000000000000, SequenceNumber=49640108810...
  Record 2: ShardId=shardId-000000000000, SequenceNumber=49640108810...
  Record 3: ShardId=shardId-000000000000, SequenceNumber=49640108810...
  Record 4: ShardId=shardId-000000000000, SequenceNumber=49640108810...
  Record 5: ShardId=shardId-000000000000, SequenceNumber=49640108810...
Successfully put 5 record(s) with 0 failures.
--------------------------------------------------------------------------------
```

### Step 5: Get Shard Iterator and Read Records

Call `GetShardIterator` to obtain a shard iterator, then use it with `GetRecords` (already covered in existing examples) to read back the records.

- Call `GetShardIterator` with the shard ID obtained in Step 3 and `ShardIteratorType` of `TRIM_HORIZON` (to read from the beginning of the shard).
- Display the shard iterator type used and confirm the iterator was obtained.
- Use the returned shard iterator to call `GetRecords` to read back the sensor data.
- Decode and display the record payloads to verify the data written in Step 4.

**Note:** `GetRecords` is from existing examples and is used here only to demonstrate the end-to-end flow. The primary operation being demonstrated is `GetShardIterator`.

**API details for GetShardIterator:**
- **Request:** `GetShardIterator(StreamName="...", ShardId="shardId-000000000000", ShardIteratorType="TRIM_HORIZON")`
- **ShardIteratorType values:** `AT_SEQUENCE_NUMBER`, `AFTER_SEQUENCE_NUMBER`, `TRIM_HORIZON`, `LATEST`, `AT_TIMESTAMP`
- **Response:** `ShardIterator` (string, expires after 5 minutes)

Example output:
```
Step 5: Getting shard iterator (TRIM_HORIZON) and reading records...
  Obtained shard iterator for shardId-000000000000.
  Reading records from the stream...
  Record 1: {"sensor_id": "sensor-001", "temperature": 72.5, ...}
  Record 2: {"sensor_id": "sensor-002", "temperature": 68.3, ...}
  Record 3: {"sensor_id": "sensor-001", "temperature": 73.1, ...}
  Record 4: {"sensor_id": "sensor-003", "temperature": 75.8, ...}
  Record 5: {"sensor_id": "sensor-002", "temperature": 67.9, ...}
Read 5 record(s) from the stream.
--------------------------------------------------------------------------------
```

### Step 6: Add Tags to Stream

Call `AddTagsToStream` to apply resource tags for cost allocation and organization.

- Call `AddTagsToStream` with the stream name and a set of tags:
  - `Project` = `kinesis-demo`
  - `Environment` = `development`
  - `CostCenter` = `12345`
- Confirm the operation succeeded (HTTP 200, empty response body).
- Explain that tags help organize and track costs for Kinesis resources.

**API details:**
- **Request:** `AddTagsToStream(StreamName="...", Tags={"Project": "kinesis-demo", "Environment": "development", "CostCenter": "12345"})`
- **Response:** Empty body on success (HTTP 200)
- Up to 50 tags per stream. Each tag key must be unique.

Example output:
```
Step 6: Adding tags to the stream...
  Added tag: Project = kinesis-demo
  Added tag: Environment = development
  Added tag: CostCenter = 12345
Tags added successfully.
--------------------------------------------------------------------------------
```

### Step 7: List Tags for Stream

Call `ListTagsForStream` to verify the tags applied in Step 6.

- Call `ListTagsForStream` with the stream name.
- Display all returned tags (Key-Value pairs).
- Check the `HasMoreTags` flag for pagination.

**API details:**
- **Request:** `ListTagsForStream(StreamName="...")`
- **Response:** `Tags` (array of Tag objects, each with Key and Value), `HasMoreTags` (boolean)
- Limit of 5 transactions per second per account.

Example output:
```
Step 7: Listing tags for the stream...
  Tag: Project = kinesis-demo
  Tag: Environment = development
  Tag: CostCenter = 12345
Found 3 tag(s). HasMoreTags: false
--------------------------------------------------------------------------------
```

### Step 8: Remove Tags from Stream

Call `RemoveTagsFromStream` to remove one of the tags to demonstrate tag lifecycle management.

- Call `RemoveTagsFromStream` with the stream name and the tag key `CostCenter`.
- Confirm removal succeeded.
- Call `ListTagsForStream` again to verify only 2 tags remain.

**API details:**
- **Request:** `RemoveTagsFromStream(StreamName="...", TagKeys=["CostCenter"])`
- **Response:** Empty body on success (HTTP 200)

Example output:
```
Step 8: Removing the CostCenter tag from the stream...
Tag removed successfully.
Verifying remaining tags...
  Tag: Project = kinesis-demo
  Tag: Environment = development
Confirmed: 2 tag(s) remaining after removal.
--------------------------------------------------------------------------------
```

### Step 9: Register an Enhanced Fan-Out Consumer

Call `RegisterStreamConsumer` to register an enhanced fan-out consumer with the stream.

- Call `RegisterStreamConsumer` with the stream ARN (obtained in Step 2) and a consumer name (e.g., `sdk-example-consumer`).
- Display the consumer name, ARN, status, and creation timestamp.
- The consumer will initially be in `CREATING` status. Poll `DescribeStreamConsumer` (or wait briefly) to confirm the consumer reaches `ACTIVE` status before proceeding.
- Explain that enhanced fan-out provides dedicated 2 MiB/sec throughput per consumer per shard, independent of other consumers.

**API details for RegisterStreamConsumer:**
- **Request:** `RegisterStreamConsumer(StreamARN="arn:aws:kinesis:...", ConsumerName="sdk-example-consumer")`
- **Response:** `Consumer` object with `ConsumerName`, `ConsumerARN`, `ConsumerStatus` (CREATING, ACTIVE, DELETING), `ConsumerCreationTimestamp`
- Up to 20 consumers per stream (50 for On-demand Advantage). Limit of 5 register operations per second.

**API details for DescribeStreamConsumer (used for polling):**
- **Request:** `DescribeStreamConsumer(StreamARN="...", ConsumerName="sdk-example-consumer")`
- **Response:** `ConsumerDescription` with `ConsumerName`, `ConsumerARN`, `ConsumerStatus`, `ConsumerCreationTimestamp`, `StreamARN`

Example output:
```
Step 9: Registering an enhanced fan-out consumer...
  Consumer Name: sdk-example-consumer
  Consumer ARN: arn:aws:kinesis:us-east-1:123456789012:stream/sdk-example-stream-.../consumer/sdk-example-consumer:1696789100
  Status: CREATING
Waiting for consumer to become ACTIVE...
  Consumer status: ACTIVE
Enhanced fan-out consumer registered successfully.
--------------------------------------------------------------------------------
```

### Step 10: Deregister the Enhanced Fan-Out Consumer

Call `DeregisterStreamConsumer` to deregister the consumer created in Step 9.

- Call `DeregisterStreamConsumer` with the consumer ARN obtained from Step 9.
- Confirm the deregistration succeeded.
- Explain that deregistering consumers is important to avoid ongoing enhanced fan-out charges.

**API details:**
- **Request:** `DeregisterStreamConsumer(ConsumerARN="arn:aws:kinesis:...")`
- **Response:** Empty body on success (HTTP 200)
- Limit of 5 deregister operations per second per stream.

Example output:
```
Step 10: Deregistering the enhanced fan-out consumer...
Consumer 'sdk-example-consumer' deregistered successfully.
Enhanced fan-out charges will stop for this consumer.
--------------------------------------------------------------------------------
```

### Cleanup

The cleanup section deletes all resources created during the scenario. It must run even if the scenario fails (use a try/finally pattern).

1. **Deregister the stream consumer** (if still registered) by calling `DeregisterStreamConsumer`. Catch and ignore `ResourceNotFoundException` if the consumer was already deregistered in Step 10.
2. **Delete the Kinesis stream** by calling `DeleteStream` (existing operation, used only for cleanup). Pass `EnforceConsumerDeletion=true` to force delete even if consumers are registered.
3. **Wait for the stream to be fully deleted** by polling `DescribeStreamSummary` until a `ResourceNotFoundException` is raised, confirming deletion.

Example output:
```
--------------------------------------------------------------------------------
Cleaning up resources...
Ensuring consumer is deregistered...
  Consumer already deregistered.
Deleting stream: sdk-example-stream-1696789012
Waiting for stream deletion to complete...
Stream deleted successfully.
--------------------------------------------------------------------------------
Amazon Kinesis Data Streams scenario complete!
```

### Outcome

After running this scenario, the user will have learned how to:
- List and inspect streams and their shards
- Batch-write multiple records efficiently using PutRecords
- Obtain shard iterators for reading records from specific positions
- Manage stream tags for cost allocation and organization
- Register and deregister enhanced fan-out consumers for dedicated throughput
- Properly clean up all Kinesis resources

## Errors

Each action in the scenario has a single most-relevant error that is handled in the code. The error handling follows a try/catch pattern with user-friendly messages.

| Action | Exception | Handling |
|-|-|-|
| `ListStreams` | `LimitExceededException` | Notify the user that the request rate is too high, suggest retrying after a brief delay. |
| `DescribeStreamSummary` | `ResourceNotFoundException` | Notify the user the stream does not exist or was not found, check stream name. |
| `ListShards` | `ResourceNotFoundException` | Notify the user the stream was not found, verify the stream is ACTIVE before calling. |
| `PutRecords` | `InvalidArgumentException` | Notify the user that a parameter is invalid (e.g., data exceeds 1 MiB per record or 5 MiB per request). |
| `GetShardIterator` | `ResourceNotFoundException` | Notify the user the stream or shard was not found, verify shard ID is correct. |
| `AddTagsToStream` | `ResourceNotFoundException` | Notify the user the stream does not exist for tagging. |
| `ListTagsForStream` | `ResourceNotFoundException` | Notify the user the stream was not found when listing tags. |
| `RemoveTagsFromStream` | `ResourceNotFoundException` | Notify the user the stream does not exist for tag removal. |
| `RegisterStreamConsumer` | `ResourceInUseException` | Notify the user that the consumer name already exists or the stream is not available. |
| `DeregisterStreamConsumer` | `ResourceNotFoundException` | Notify the user the consumer was already deregistered or does not exist. |

## Metadata

| action / scenario | metadata file | metadata key |
|-|-|-|
| `ListStreams` | kinesis_metadata.yaml | kinesis_ListStreams |
| `DescribeStreamSummary` | kinesis_metadata.yaml | kinesis_DescribeStreamSummary |
| `ListShards` | kinesis_metadata.yaml | kinesis_ListShards |
| `PutRecords` | kinesis_metadata.yaml | kinesis_PutRecords |
| `GetShardIterator` | kinesis_metadata.yaml | kinesis_GetShardIterator |
| `AddTagsToStream` | kinesis_metadata.yaml | kinesis_AddTagsToStream |
| `ListTagsForStream` | kinesis_metadata.yaml | kinesis_ListTagsForStream |
| `RemoveTagsFromStream` | kinesis_metadata.yaml | kinesis_RemoveTagsFromStream |
| `RegisterStreamConsumer` | kinesis_metadata.yaml | kinesis_RegisterStreamConsumer |
| `DeregisterStreamConsumer` | kinesis_metadata.yaml | kinesis_DeregisterStreamConsumer |
| `Kinesis Hello` | kinesis_metadata.yaml | kinesis_Hello |
| `Kinesis Basics Scenario` | kinesis_metadata.yaml | kinesis_Scenario |
