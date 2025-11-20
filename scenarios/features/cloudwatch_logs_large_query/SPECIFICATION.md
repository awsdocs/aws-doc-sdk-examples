# CloudWatch Logs Large Query - Technical Specification

## Overview

This feature scenario demonstrates how to perform large-scale queries on Amazon CloudWatch Logs using recursive binary search to retrieve more than the 10,000 result limit. 

**Important**: This is a complete, self-contained scenario that handles all setup and cleanup automatically. The scenario includes:

1. Deploying CloudFormation resources (log group and stream)
2. Generating and ingesting 50,000 sample log entries
3. Performing recursive queries to retrieve all logs using binary search
4. Cleaning up all resources

**The scenario must be runnable in both interactive and non-interactive modes** to support:
- Interactive mode: User runs the scenario manually with prompts
- Non-interactive mode: Automated integration tests run the scenario without user input

For an introduction, see the [README.md](README.md).

---

## Table of Contents

- [API Actions Used](#api-actions-used)
- [Resources](#resources)
- [Proposed Example Structure](#proposed-example-structure)
- [Implementation Details](#implementation-details)
- [Output Format](#output-format)
- [Errors](#errors)
- [Metadata](#metadata)

---

## API Actions Used

This scenario uses the following CloudWatch Logs API actions:

- `StartQuery` - Initiates a CloudWatch Logs Insights query
- `GetQueryResults` - Retrieves results from a query, polling until complete

This scenario uses the following CloudFormation API actions:

- `CreateStack` - Deploys the CloudFormation template
- `DescribeStacks` - Checks stack status and retrieves outputs
- `DeleteStack` - Removes the CloudFormation stack

---

## Resources

### CloudFormation Template

**Location**: `scenarios/features/cloudwatch_logs_large_query/resources/stack.yaml`

**Resources Created**:
- CloudWatch Logs Log Group: `/workflows/cloudwatch-logs/large-query`
- CloudWatch Logs Log Stream: `stream1`

**Stack Outputs**: None (resources use fixed names)

### Sample Data Generation Scripts

**Script 1**: `scenarios/features/cloudwatch_logs_large_query/resources/make-log-files.sh`
- Creates 50,000 log entries divided into 5 JSON files (10,000 entries each)
- Generates timestamps spanning 5 minutes from execution time
- Outputs `QUERY_START_DATE` and `QUERY_END_DATE` environment variables
- Creates files: `file1.json`, `file2.json`, `file3.json`, `file4.json`, `file5.json`

**Script 2**: `scenarios/features/cloudwatch_logs_large_query/resources/put-log-events.sh`
- Uploads the generated JSON files to CloudWatch Logs
- Uses AWS CLI `put-log-events` command
- Targets log group: `/workflows/cloudwatch-logs/large-query`
- Targets log stream: `stream1`

**Python Alternative**: `scenarios/features/cloudwatch_logs_large_query/resources/create_logs.py`
- Python script that combines both generation and upload
- Creates 50,000 log entries and uploads them directly
- Returns start and end timestamps for query configuration
- Preferred for cross-platform compatibility

---

## Proposed Example Structure

### Phase 1: Setup

**Purpose**: Deploy resources and generate sample data as part of the scenario

**Interactive Mode Steps**:
1. Welcome message explaining the scenario
2. Prompt user: "Would you like to deploy the CloudFormation stack and generate sample logs? (y/n)"
3. If yes:
   - Prompt for CloudFormation stack name (default: "CloudWatchLargeQueryStack")
   - Deploy CloudFormation stack from `resources/stack.yaml`
   - Wait for stack creation to complete (status: CREATE_COMPLETE)
   - Generate logs directly using CloudWatch Logs API:
     - Create 50,000 log entries with timestamps spanning 5 minutes
     - Upload in batches of 10,000 entries using PutLogEvents
     - Display progress for each batch uploaded
   - Capture start and end timestamps for query configuration
   - Display message: "Sample logs created. Waiting 5 minutes for logs to be fully ingested..."
   - Wait 5 minutes (300 seconds) for log ingestion with countdown display
4. If no:
   - Prompt user for existing log group name
   - Prompt user for log stream name
   - Prompt user for query start date (ISO 8601 format with milliseconds)
   - Prompt user for query end date (ISO 8601 format with milliseconds)

**Non-Interactive Mode Behavior**:
- Automatically deploys stack with default name
- Automatically generates 50,000 sample logs
- Waits 5 minutes for log ingestion
- Uses default values for all configuration

**Variables Set**:
- `stackName` - CloudFormation stack name
- `logGroupName` - Log group name (default: `/workflows/cloudwatch-logs/large-query`)
- `logStreamName` - Log stream name (default: `stream1`)
- `queryStartDate` - Start timestamp for query (seconds since epoch)
- `queryEndDate` - End timestamp for query (seconds since epoch)

### Phase 2: Query Execution

**Purpose**: Demonstrate recursive large query functionality

**Steps**:
1. Display message: "Starting recursive query to retrieve all logs..."
2. Prompt user for query limit (default: 10000, max: 10000)
3. Set query string: `fields @timestamp, @message | sort @timestamp asc`
4. Execute recursive query function with:
   - Log group name
   - Query string
   - Start date
   - End date
   - Limit
5. Display progress for each query executed (see [Output Format](#output-format))
6. Display total execution time
7. Display total logs found
8. Prompt user: "Would you like to see a sample of the logs? (y/n)"
9. If yes, display first 10 log entries with timestamps and messages

### Phase 3: Cleanup

**Purpose**: Remove created resources

**Interactive Mode Steps**:
1. Prompt user: "Would you like to delete the CloudFormation stack and all resources? (y/n)"
2. If yes:
   - Delete CloudFormation stack
   - Wait for stack deletion to complete (status: DELETE_COMPLETE or stack not found)
   - Display message: "Stack deleted successfully"
3. If no:
   - Display message: "Resources will remain. You can delete them later through the AWS Console."
   - Display stack name and log group name for reference

**Non-Interactive Mode Behavior**:
- Automatically deletes the CloudFormation stack
- Waits for deletion to complete
- Ensures cleanup happens even if errors occur during the scenario

---

## Implementation Details

### CloudFormation Stack Deployment

**Deployment**:
```
Stack Name: User-provided or default "CloudWatchLargeQueryStack"
Template: scenarios/features/cloudwatch_logs_large_query/resources/stack.yaml
Capabilities: None required (no IAM resources)
```

**Polling for Completion**:
- Poll `DescribeStacks` every 5-10 seconds
- Success: `StackStatus` = `CREATE_COMPLETE`
- Failure: `StackStatus` = `CREATE_FAILED`, `ROLLBACK_COMPLETE`, or `ROLLBACK_FAILED`
- Timeout: 5 minutes maximum wait time

### Log Generation Execution

**Cross-Platform Considerations**:
- Bash scripts work on Linux, macOS, and Git Bash on Windows
- Python script is preferred for true cross-platform support
- Check for script availability before execution
- Handle script execution errors gracefully

**Capturing Output**:
- Parse stdout for `QUERY_START_DATE` and `QUERY_END_DATE`
- Convert timestamps to appropriate format for SDK
- Store timestamps for query configuration

**Wait Time**:
- CloudWatch Logs requires time to ingest and index logs
- Minimum wait: 5 minutes (300 seconds)
- Display countdown or progress indicator during wait

### Building and Executing Queries

**Query String**:
```
fields @timestamp, @message | sort @timestamp asc
```

**Important**: The query MUST return `@timestamp` field for recursive queries to work.

**StartQuery Parameters**:
- `logGroupName` - The log group to query
- `startTime` - Start of date range (seconds since epoch)
- `endTime` - End of date range (seconds since epoch)
- `queryString` - CloudWatch Logs Insights query syntax
- `limit` - Maximum results (default: 10000, max: 10000)

**GetQueryResults Polling**:
- Poll every 1-2 seconds
- Continue until status is one of: `Complete`, `Failed`, `Cancelled`, `Timeout`, `Unknown`
- Timeout after 60 seconds of polling

**Error Handling**:
- If `StartQuery` returns error starting with "Query's end date and time", the date range is out of bounds
- Handle this by adjusting the date range or informing the user

### Recursive Query Algorithm

**Purpose**: Retrieve more than 10,000 results by splitting date ranges

**Algorithm**:
```
function LargeQuery(startDate, endDate, limit):
    results = ExecuteQuery(startDate, endDate, limit)
    
    if results.count < limit:
        return results
    else:
        // Get timestamp of last result
        lastTimestamp = results[results.count - 1].timestamp
        
        // Calculate midpoint between last result and end date
        midpoint = (lastTimestamp + endDate) / 2
        
        // Query first half
        results1 = LargeQuery(lastTimestamp, midpoint, limit)
        
        // Query second half
        results2 = LargeQuery(midpoint, endDate, limit)
        
        // Combine results
        return Concatenate(results, results1, results2)
```

**Key Points**:
- Use binary search to split remaining date range
- Recursively query each half
- Concatenate all results
- Log each query's date range and result count (see [Output Format](#output-format))

### Stack Deletion

**Deletion**:
```
Stack Name: Same as used during creation
```

**Polling for Completion**:
- Poll `DescribeStacks` every 5-10 seconds
- Success: Stack not found (ValidationError) or `StackStatus` = `DELETE_COMPLETE`
- Failure: `StackStatus` = `DELETE_FAILED`
- If `DELETE_FAILED`, optionally retry with force delete
- Timeout: 5 minutes maximum wait time

---

## Output Format

### Query Progress Output

Display each query execution with the following format:

```
Query date range: <START_ISO8601> to <END_ISO8601>. Found <COUNT> logs.
```

**Example**:
```
Starting recursive query...
Query date range: 2023-12-22T19:08:42.000Z to 2023-12-22T19:13:41.994Z. Found 10000 logs.
Query date range: 2023-12-22T19:09:41.995Z to 2023-12-22T19:11:41.994Z. Found 10000 logs.
Query date range: 2023-12-22T19:11:41.995Z to 2023-12-22T19:13:41.994Z. Found 10000 logs.
Query date range: 2023-12-22T19:10:41.995Z to 2023-12-22T19:11:11.994Z. Found 5000 logs.
Query date range: 2023-12-22T19:11:11.995Z to 2023-12-22T19:11:41.994Z. Found 5000 logs.
Query date range: 2023-12-22T19:12:41.995Z to 2023-12-22T19:13:11.994Z. Found 5000 logs.
Query date range: 2023-12-22T19:13:11.995Z to 2023-12-22T19:13:41.994Z. Found 5000 logs.
Queries finished in 11.253 seconds.
Total logs found: 50000
```

### Summary Output

After all queries complete, display:
- Total execution time (in seconds with 3 decimal places)
- Total number of logs found

### Sample Logs Output

If user chooses to view sample logs, display first 10 entries:

```
Sample logs (first 10 of 50000):
[2023-12-22T19:08:42.000Z] Entry 0
[2023-12-22T19:08:42.006Z] Entry 1
[2023-12-22T19:08:42.012Z] Entry 2
...
```

---

## Errors

### CloudFormation Errors

| Error Code | Error Message Pattern | Handling Strategy |
|------------|----------------------|-------------------|
| `AlreadyExistsException` | Stack already exists | Prompt user for different stack name and retry |
| `ValidationError` | Template validation failed | Display error message and exit setup |
| `InsufficientCapabilitiesException` | Requires capabilities | Should not occur (template has no IAM resources) |

### CloudWatch Logs Errors

| Error Code | Error Message Pattern | Handling Strategy |
|------------|----------------------|-------------------|
| `InvalidParameterException` | "Query's end date and time" | Date range is out of bounds; inform user and adjust dates |
| `ResourceNotFoundException` | Log group not found | Verify log group exists; prompt user to run setup |
| `LimitExceededException` | Too many concurrent queries | Wait and retry after 5 seconds |
| `ServiceUnavailableException` | Service temporarily unavailable | Retry with exponential backoff (max 3 retries) |

### Script Execution Errors

| Error Type | Handling Strategy |
|------------|-------------------|
| Script not found | Display error message; provide manual instructions |
| Script execution failed | Display error output; allow user to retry or skip |
| Permission denied | Suggest making script executable (`chmod +x`) |
| AWS CLI not available | Inform user AWS CLI is required for bash scripts; suggest Python alternative |

---

## User Input Variables

### Required Variables

| Variable Name | Description | Type | Default | Validation |
|--------------|-------------|------|---------|------------|
| `stackName` | CloudFormation stack name | String | "CloudWatchLargeQueryStack" | Must match pattern: `[a-zA-Z][-a-zA-Z0-9]*` |
| `queryStartDate` | Query start timestamp | Long/Integer | From script output | Milliseconds since epoch |
| `queryEndDate` | Query end timestamp | Long/Integer | From script output | Milliseconds since epoch |
| `queryLimit` | Maximum results per query | Integer | 10000 | Min: 1, Max: 10000 |

### Optional Variables

| Variable Name | Description | Type | Default |
|--------------|-------------|------|---------|
| `logGroupName` | Log group name (if not using stack) | String | "/workflows/cloudwatch-logs/large-query" |
| `logStreamName` | Log stream name (if not using stack) | String | "stream1" |

---

## Metadata

| action / scenario | metadata file                 | metadata key                      |
| ----------------- | ----------------------------- | --------------------------------- |
| `GetQueryResults` | cloudwatch-logs_metadata.yaml | cloudwatch-logs_GetQueryResults   |
| `StartQuery`      | cloudwatch-logs_metadata.yaml | cloudwatch-logs_StartQuery        |
| `Large Query`       | cloudwatch-logs_metadata.yaml | cloudwatch-logs_Scenario_LargeQuery |
