# CloudWatch Logs Large Query - Technical Specification

## Overview

This feature scenario demonstrates how to perform large-scale queries on Amazon CloudWatch Logs using recursive binary search to retrieve more than the 10,000 result limit. 

**Important**: This is a complete, self-contained scenario that handles all setup and cleanup automatically. The scenario includes:

1. Deploying CloudFormation resources (log group and stream)
2. Generating and ingesting 50,000 sample log entries
3. Performing recursive queries to retrieve all logs using binary search
4. Cleaning up all resources

For an introduction, see the [README.md](README.md).

---

## Table of Contents

- [API Actions Used](#api-actions-used)
- [Resources](#resources)
- [Variables](#variables)
- [Building the queries](#building-the-queries)
- [Example Structure](#example-structure)
- [Output Format](#output-format)
- [Errors](#errors)
- [Metadata](#metadata)

---

## API Actions Used

This scenario uses the following CloudWatch Logs API actions:

- `StartQuery` - Initiates a CloudWatch Logs Insights query
- `GetQueryResults` - Retrieves results from a query, polling until complete

---

## Resources

### CloudFormation Template

**Location**: `scenarios/features/cloudwatch_logs_large_query/resources/stack.yaml`

**Resources Created**:
- CloudWatch Logs Log Group: `/workflows/cloudwatch-logs/large-query`
- CloudWatch Logs Log Stream: `stream1`

### Helper files
These files are for reference only. New versions of this example should create and upload logs as part of the scenario.

- [put-log-events](resources/put-log-events.sh) is a bash script that ingests log data and uploads it to CloudWatch.
- [make-log-files.sh](resources/make-log-files.sh) is a bash script that creates log data. **Five minutes of logs, starting at the time of execution, will be created. Wait at least five minutes after running this script before attempting to query.**
---

## Variables

| Variable Name | Description | Type | Default |
|--------------|-------------|------|---------|
| `stackName` | CloudFormation stack name | String | "CloudWatchLargeQueryStack" |
| `queryStartDate` | Query start timestamp | Long/Integer | From script output | 
| `queryEndDate` | Query end timestamp | Long/Integer | From script output | 
| `queryLimit` | Maximum results per query | Integer | 10000 |
| `logGroupName` | Log group name (if not using stack) | String | "/workflows/cloudwatch-logs/large-query" |
| `logStreamName` | Log stream name (if not using stack) | String | "stream1" |

---

## Building the queries

### Building and waiting for single query

The query itself is a "CloudWatch Logs Insights query syntax" string. The query must return the `@timestamp` field so follow-up queries can use that information. Here's a sample query string: `fields @timestamp, @message | sort @timestamp asc`. Notice it sorts in ascending order. You can sort in either `asc` or `desc`, but the recursive strategy described later will need to match accordingly.

Queries are jobs. You can start a query with `StartQuery`, but it immediately returns the `queryId`. You must poll a query using `GetQueryResults` until the query has finished. For the purpose of this example, a query has "finished" when `GetQueryResults` has returned a status of one of "Complete", "Failed", "Cancelled", "Timeout", or "Unknown".

`StartQuery` responds with an error if the query's start or end date occurs out of bounds of the log group creation date. The error message starts with "Query's end date and time".

Start the query and wait for it to "finish". Store the `results`. If the count of the results is less than the configured LIMIT, return the results. If the results are greater than or equal to the limit, go to [Recursive queries](#recursive-queries).

---

### Recursive queries

If the result count from the previous step is 10000 (or the configured LIMIT), it is very likely that there are more results. **The example must do a binary search of the remaining logs**. To do this, get the date of the last log (earliest or latest, depending on sort order). Use that date as the start date of a new date range. The end date can remain the same.

Split that date range in half, resulting in two new date ranges. Call your query function twice; once for each new date range.

Concatenate the results of the first query with the results of the two new queries.

The following pseudocode illustrates this.

```pseudocode
func large_query(date_range):
  query_results = get_query_results(date_range)

  if query_results.length < LIMIT
    return query_results
  else
    date_range = [query_results.end, date_range.end]
    d1, d2 = split(date_range)
    return concat(query_results, large_query(d1), large_query(d2))
```


## Example Structure

### Phase 1: Setup

**Purpose**: Deploy resources and generate sample data as part of the scenario

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
   - Prompt user for existing log group name, or enter to use the default name
   - Prompt user for log stream name, or enter to use the default name
   - Prompt user for query start date (ISO 8601 format with milliseconds)
   - Prompt user for query end date (ISO 8601 format with milliseconds)

**Fully Self-Contained Behavior**:
- Automatically deploys stack with default name
- Automatically generates 50,000 sample logs
- Waits 5 minutes for log ingestion
- Uses default values for all configuration


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
5. Display progress for each query executed
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

| Error Code | Error Message Pattern | Handling Strategy |
|------------|----------------------|-------------------|
| `InvalidParameterException` | "Query's end date and time" | Date range is out of bounds; inform user and adjust dates |
| `ResourceNotFoundException` | Log group not found | Verify log group exists; prompt user to run setup |

---

## Metadata

| action / scenario | metadata file                 | metadata key                      |
| ----------------- | ----------------------------- | --------------------------------- |
| `GetQueryResults` | cloudwatch-logs_metadata.yaml | cloudwatch-logs_GetQueryResults   |
| `StartQuery`      | cloudwatch-logs_metadata.yaml | cloudwatch-logs_StartQuery        |
| `Large Query`     | cloudwatch-logs_metadata.yaml | cloudwatch-logs_Scenario_LargeQuery |
