# CloudWatch Logs large query - Technical specification

This document contains the technical specifications for _CloudWatch Logs large query_,
a feature scenario that showcases AWS services and SDKs. It is primarily intended for the AWS code
examples team to use while developing this example in additional languages.

This document explains the following:

- Deploying AWS resources.
- Adding sample data.
- Setting up a large query.

For an introduction, see the [README.md](README.md).

---

### Table of contents

- [Architecture](#architecture)
- [User input](#user-input)
- [Common resources](#common-resources)
- [Building the queries](#building-the-queries)
- [Output](#output)
- [Metadata](#metadata)

## Architecture

- Amazon CloudWatch Logs group
- Amazon CloudWatch Logs stream

---

## User input

The example should allow the configuration of a query start date, query end date, and results limit. It's up to you to decide how to allow this configuration.

### Suggested variable names

- `QUERY_START_DATE` - The oldest date that will be queried.
- `QUERY_END_DATE` - The newest date that will be queried.
- `QUERY_LIMIT` - The maximum number of results to return. CloudWatch has a maximum of 10,000.

---

## Common resources

This example has a set of common resources that are stored in the [resources](resources) folder.

- [stack.yaml](resources/stack.yaml) is an AWS CloudFormation template containing the resources needed to run this example.
- [make-log-files.sh](resources/make-log-files.sh) is a bash script that creates log data. **Five minutes of logs, starting at the time of execution, will be created. Wait at least five minutes after running this script before attempting to query.**
- [put-log-events](resources/put-log-events.sh) is a bash script that ingests log data and uploads it to CloudWatch.

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

## Output

To illustrate the search, log the date ranges for each query made and the number of logs that were found.

Example:

```
Starting a recursive query...
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

---

## Metadata

| action / scenario | metadata file                 | metadata key                      |
| ----------------- | ----------------------------- | --------------------------------- |
| `GetQueryResults` | cloudwatch-logs_metadata.yaml | cloudwatch-logs_GetQueryResults   |
| `StartQuery`      | cloudwatch-logs_metadata.yaml | cloudwatch-logs_StartQuery        |
| `Large Query`       | cloudwatch-logs_metadata.yaml | cloudwatch-logs_Scenario_LargeQuery |
