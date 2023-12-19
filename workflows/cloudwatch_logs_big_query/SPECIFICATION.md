# CloudWatch Logs big query - technical specification

This document contains the technical specifications for _CloudWatch Logs big query_,
a workflow scenario that showcases AWS services and SDKs. It is primarily intended for the AWS code
examples team to use while developing this example in additional languages.

This document explains the following:

- Deploying AWS resources.
- Adding sample data.
- Setting up a big query.

For an introduction to _Build and manage a resilient service_, see the [README.md](README.md).

---

### Table of contents

- ***

## Architecture

- Amazon CloudWatch Log Group
  - Amazon CloudWatch Log Stream

---

## User actions

This example should run in the console without any user input. Date ranges and results limits should be configurable in the code and sane defaults provided.

---

## Common resources

This example has a set of common resources that are stored in the [resources](resources) folder.

- [stack.yaml](resources/stack.yaml) is an AWS CloudFormation template containing the resources needed to run this example.
- [make-log-files.sh](resources/make-log-files.sh) is a bash script that creates log data.
- [put-log-events](resources/put-log-events.sh) is a bash script that ingests log data and uploads it to CloudWatch.

---

## Building the queries

### Building and waiting for single query
Queries are jobs. You can start a query with `StartQuery`, but it immediately returns the `queryId`. You must poll a query using `GetQueryResults` until the query has finished. For the purpose of this example, a query has "finished" when `GetQueryResults` has returned a status of one of  "Complete", "Failed", "Cancelled", "Timeout", or "Unknown".

`StartQuery` responds with an error if the query's start or end date occurs out of bounds of the log group creation date. The error message starts with "Query's end date and time".

Start the query and wait for it to "finish". Store the `results`. If the count of the results is less than the configured LIMIT, return the results. If the the results are greater than or equal to the limit, go to [Recursive queries](#recursive-queries).

### Recursive queries

```pseudocode
func big_query(date_range):
  query_results = get_query_results(date_range)
  
  if query_results.length < LIMIT
    return query_results
  else
    date_range = [query_results.end, date_range.end]
    d1, d2 = split(date_range)
    return concat(query_results, big_query(d1), big_query(d2))
```

## Example output

```

```

---

## Actions and Metadata

| action            | metadata file                 | metadata key                    |
| ----------------- | ----------------------------- | ------------------------------- |
| `GetQueryResults` | cloudwatch-logs_metadata.yaml | cloudwatch-logs_GetQueryResults |
| `StartQuery`      | cloudwatch-logs_metadata.yaml | cloudwatch-logs_StartQuery      |

---

# Other material

If technical details are not what you seek, try these instead:

- [High-level summary](README.md)
- [Community.aws: How to build and manage a resilient service using AWS SDKs](https://community.aws/posts/build-and-manage-a-resilient-service-using-aws-sdks)
