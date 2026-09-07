# Amazon CloudWatch Basics Scenario Specification

## Overview

This SDK Basics scenario demonstrates how to interact with Amazon CloudWatch using an AWS SDK, framed around the OpenTelemetry (OTel) experience that CloudWatch now supports natively. The scenario starts OTel enrichment so CloudWatch correlates OTLP metrics with the resources that emitted them, creates an alarm that evaluates a PromQL query across every series the query matches, inspects the individual series (called contributors) that drove the alarm, charts the underlying metric on a dashboard, mutes the alarm for a maintenance window, and finally cleans up every resource it created.

The framing matters. A classic CloudWatch alarm watches one metric and counts breaching periods. A PromQL alarm evaluates a query that can match many series at once, and it tracks each matching series separately as a contributor. That difference changes how you create the alarm, how you interpret its state, and how you find out *which* host or pod is unhealthy rather than only *that* something is. This scenario is built so a reader coming from the classic model sees where the two diverge.

One boundary is important to state up front: **sending OTLP metrics to CloudWatch is not an AWS SDK operation.** Metrics arrive over the OTLP protocol via the CloudWatch agent, an OpenTelemetry Collector, or an ADOT SDK, pointed at the CloudWatch OTLP metrics endpoint. Everything the SDK does in this scenario is configuration and querying *around* that ingestion path. The scenario therefore ships a collector configuration file alongside the SDK code, and the program tells the reader plainly that the ingestion half is not SDK work.

## Resources and User Input

No pre-existing resources are required. The scenario creates and deletes everything it needs.

The scenario takes the following user input:

- **PromQL query** — the query the alarm evaluates. A default is offered, such as `avg by (host) (system_cpu_utilization) > 80`. The default must be a query that returns a value the reader can reason about.
- **Alarm name** — a default with a suffix is offered so repeated runs do not collide.
- **Mute rule name** — likewise defaulted.

The scenario must run to completion in an account with no OTLP metrics flowing. When no metrics have arrived, the PromQL alarm has no contributors, and `DescribeAlarmContributors` returns an empty list. The program must treat that as an expected, explained outcome rather than an error, because most readers will run this before they have a collector configured.

## Hello Amazon CloudWatch

CloudWatch already has a Hello example that lists metrics using `ListMetrics` (metadata key `cloudwatch_Hello`). No new Hello example is required for this scenario, and the existing one should not be changed.

## Scenario Program Flow

The Amazon CloudWatch SDK Basics scenario follows these steps:

1. **List metrics and namespaces**
   - **Description**: Lists the metric namespaces available in the account so the reader can see what CloudWatch is already collecting before any OTel configuration happens. Use the `listMetrics` method with pagination, and display a bounded number of results with the namespaces summarized.
   - **Rationale**: This is the orientation step. It answers "what is already here" and gives the reader a namespace to use in the statistics and dashboard steps later.
   - **Exception Handling**: If a `CloudWatchException` is thrown, display the error message and terminate the program.

2. **Start OpenTelemetry enrichment**
   - **Description**: Starts OTel enrichment for the account with `startOTelEnrichment`, then confirms the state with `getOTelEnrichment`. Enrichment is what makes CloudWatch attach AWS resource context to incoming OTLP metrics, so the metrics become correlatable with the rest of CloudWatch rather than opaque series.
   - **Idempotence**: Enrichment may already be running in the account. The program must call `getOTelEnrichment` first and only call `startOTelEnrichment` if it is not already active. The program must record whether *it* was the one that started enrichment, because the cleanup step must only stop enrichment it turned on. Stopping enrichment that the account already depended on would be a destructive side effect on unrelated workloads.
   - **Exception Handling**: If a `ValidationException` is thrown, display the error message and terminate the program. If enrichment is already active, display the existing state and continue.

3. **Send OTLP metrics to CloudWatch**
   - **Description**: This step is explanatory rather than an SDK call. The program prints the CloudWatch OTLP metrics endpoint for its configured region and points the reader at the collector configuration file shipped with the example, then explains that metric ingestion happens over OTLP through the CloudWatch agent, an OpenTelemetry Collector, or an ADOT SDK — not through the AWS SDK.
   - **Endpoint details**: The metrics endpoint follows the pattern `https://monitoring.{region}.amazonaws.com/v1/metrics`. The CloudWatch OTLP endpoints are **HTTP/1.1 only and do not support gRPC**, so a collector must use the `otlphttp` exporter rather than `otlp`. The metrics endpoint signs as `monitoring` (the logs endpoint signs as `logs` and traces as `xray`), which matters when configuring SigV4 auth.
   - **Rationale**: Without this step a reader would look for a `putOTelMetrics` operation and not find one. Naming the gap explicitly is the point.
   - **Exception Handling**: None; no service call is made.

4. **Create a PromQL alarm**
   - **Description**: Creates an alarm whose evaluation is a PromQL query, using `putMetricAlarm` with the `evaluationCriteria` union set to its PromQL member, plus `evaluationInterval`.
   - **API details that must be reflected in every implementation**:
     - `evaluationCriteria` is a union and is **mutually exclusive** with the classic `metricName` and `metrics` parameters. Setting both is a validation error.
     - When `evaluationCriteria` is used, `evaluationInterval` is **required**. Valid values are 10, 20, 30, or any multiple of 60 up to 3600 seconds.
     - The PromQL criteria carry the `query`, a `pendingPeriod`, and a `recoveryPeriod`, both in seconds. A contributor moves to `ALARM` after breaching continuously for the pending period and returns to `OK` after it stops breaching for the recovery period.
     - There is **no** `threshold`, `comparisonOperator`, `statistic`, `period`, or `evaluationPeriods`. The comparison lives inside the query itself.
     - A PromQL alarm starts in the **`OK`** state, not `INSUFFICIENT_DATA`. Implementations must not tell the reader to wait for `INSUFFICIENT_DATA` to clear.
   - **Exception Handling**: If a `ValidationException` is thrown, display the error message and terminate the program, because a malformed query or an invalid evaluation interval means the rest of the scenario cannot proceed.

5. **Inspect the alarm's contributors**
   - **Description**: Retrieves the series that the alarm's query matched, using `describeAlarmContributors` with full pagination. For each contributor, display its `contributorId`, its label set (`contributorAttributes`), and its `stateReason`.
   - **Rationale**: This is the payoff of the PromQL model and the step with no classic-alarm equivalent. It is how the reader learns which host, service, or pod is breaching.
   - **Pagination requirement**: The paging loop must continue until the next token is empty. **A page can come back empty while still carrying a next token**, so an implementation that stops at the first empty page will silently drop later results. This is a real defect that has already been found and fixed once in this codebase; every implementation must get it right.
   - **Empty result**: The service returns an empty list, not a null or absent field, when nothing matched. When the list is empty, explain that the query matched no series, which usually means no OTLP metrics with those labels have arrived yet.
   - **Exception Handling**: If a `ResourceNotFoundException` is thrown, display the error message and continue. If a `CloudWatchException` is thrown, display the error message and terminate the program.

6. **Get statistics and chart the metric on a dashboard**
   - **Description**: Retrieves aggregated values for a metric with `getMetricStatistics` over a recent time window, then creates a dashboard with `putDashboard` that charts it, and reads the dashboard back with `getDashboard` to confirm the widget JSON round-tripped.
   - **Rationale**: These are the classic operations worth keeping. Statistics and dashboards are how a reader visualizes what the alarm is evaluating, so they support the OTel narrative instead of competing with it.
   - **Exception Handling**: If a `ResourceNotFoundException` is thrown from `getDashboard`, display the error message and continue. If a `CloudWatchException` is thrown, display the error message and terminate the program.

7. **Mute the alarm for a maintenance window**
   - **Description**: Creates an alarm mute rule with `putAlarmMuteRule`, reads it back with `getAlarmMuteRule`, and finds it among the account's rules with `listAlarmMuteRules`.
   - **Rationale**: A mute rule is the supported way to suppress notifications during known maintenance. While a rule is active the targeted alarms keep evaluating and keep changing state, but their configured actions do not fire. This is strictly better than disabling alarm actions and relying on a human to turn them back on, and implementations should say so.
   - **Schedule format details that must be reflected in every implementation** — these are easy to get wrong and were the source of real breakage:
     - `expression` for a recurring window is a **five-field** cron expression, `cron(Minutes Hours Day-of-month Month Day-of-week)`, for example `cron(0 2 * * SUN)`. This is **five fields, not the six Amazon EventBridge uses.** It supports `*`, `-`, and `,`, and English month (`JAN`–`DEC`) and day-of-week (`SUN`–`SAT`) names.
     - `expression` for a one-time window is `at(yyyy-MM-ddThh:mm)`, for example `at(2026-09-05T02:00)`. There are **no seconds** in this format.
     - `duration` is an **ISO 8601 duration**, minimum `PT1M` and maximum `P15D`, for example `PT2H` for two hours or `P2DT12H` for two and a half days. Plain forms such as `2h` are rejected with a validation error. The duration begins when the schedule expression time is reached, and for a recurring schedule it applies to each occurrence.
     - `timezone` takes a standard timezone identifier and defaults to UTC when omitted.
     - `muteTargets` may name up to 100 alarms. **If `muteTargets` is omitted the rule applies to every alarm in the account**, so implementations must always set it in this scenario and should note the consequence of leaving it out.
   - **Matching a rule in the list**: `AlarmMuteRuleSummary` has **no name field**. It carries only the ARN, status, mute type, and last-updated timestamp. To locate the rule the scenario just created, match on the ARN suffix rather than looking for a name.
   - **Exception Handling**: If a `ValidationException` is thrown, display the error message and terminate the program. If a `ResourceNotFoundException` is thrown from `getAlarmMuteRule`, display the error message and continue.

8. **Clean up**
   - **Description**: Deletes everything the scenario created, in dependency order: `deleteAlarmMuteRule`, then `deleteAlarms`, then `deleteDashboards`, then `stopOTelEnrichment` — the last one **only if this run started enrichment**.
   - **Requirement**: Cleanup must be prompted but resilient. Each deletion is attempted independently so that one failure does not leave the remaining resources behind, and a `ResourceNotFoundException` during cleanup is treated as already-deleted rather than an error.
   - **Exception Handling**: Display the error message for any failed deletion and continue to the next resource.

## Cross-language API naming notes

Seven SDKs implement this scenario and they do not spell these operations the same way. Implementers should expect the following, all of which are confirmed against the live service and the SDK models:

**The `OTel` acronym splits in generated snake_case.** Code generators that split on capital boundaries turn `OTelEnrichment` into `o_tel_enrichment`. The affected surfaces:

| SDK / tool | Spelling |
|---|---|
| Python (boto3) | `start_o_tel_enrichment`, `get_o_tel_enrichment`, `stop_o_tel_enrichment` |
| Ruby | `start_o_tel_enrichment` |
| AWS CLI | `get-o-tel-enrichment` |

**The PromQL criteria shape is cased differently per SDK.** Note that Kotlin is the lone outlier, using a lowercase `l` in `Ql`:

| SDK | Spelling |
|---|---|
| Python, JavaScript | `PromQLCriteria` |
| Java | `promQLCriteria()`, `AlarmPromQLCriteria` |
| .NET | `PromQLCriteria`, `AlarmPromQLCriteria` |
| C++ | `SetPromQLCriteria`, `AlarmPromQLCriteria` |
| Ruby | `prom_ql_criteria` |
| **Kotlin** | **`EvaluationCriteria.PromQlCriteria`, `AlarmPromQlCriteria`** |

Implementations should carry a short comment at the Kotlin usage site so a reader comparing languages is not thrown by the difference.

## Program execution

The following shows the output of the program in the console.

```
--------------------------------------------------------------------------------
Welcome to the Amazon CloudWatch Basics scenario.

CloudWatch now ingests OpenTelemetry metrics natively. This scenario walks through
that experience: it turns on OTel enrichment so CloudWatch can correlate incoming
OTLP metrics with the resources that produced them, alarms on those metrics with a
PromQL query, and shows you which individual series drove the alarm.

A PromQL alarm works differently from a classic metric alarm. Rather than watching one
metric and counting breaching periods, it evaluates a query that can match many series
at once, and tracks each one separately as a contributor.

Let's get started...

Press <ENTER> to continue:
--------------------------------------------------------------------------------
1. List metrics and namespaces

Before configuring anything, let's see what CloudWatch is already collecting in this
account by calling ListMetrics.

Press <ENTER> to continue:

Found 312 metrics across 9 namespaces:
  AWS/EC2 (84 metrics)
  AWS/Lambda (61 metrics)
  AWS/ApplicationELB (47 metrics)
  AWS/RDS (38 metrics)
  ...

--------------------------------------------------------------------------------
2. Start OpenTelemetry enrichment

Enrichment is what lets CloudWatch attach AWS resource context to the OTLP metrics you
send it. Without it, your metrics arrive as opaque series with no connection to the
resources that emitted them.

We check the current state first, and only start enrichment if it isn't already on.

Press <ENTER> to continue:

Enrichment status: NotStarted
Starting OTel enrichment...
Enrichment status: Running

Note: this run started enrichment, so the cleanup step will stop it again. If it had
already been running we would leave it alone, since other workloads may depend on it.

--------------------------------------------------------------------------------
3. Send OTLP metrics to CloudWatch

This step is not an AWS SDK operation, and that's worth being explicit about. Metrics
reach CloudWatch over the OTLP protocol, through the CloudWatch agent, an OpenTelemetry
Collector, or an ADOT SDK -- there is no PutOTelMetrics API to call.

Point your collector at:
  https://monitoring.us-west-2.amazonaws.com/v1/metrics

A ready-to-use collector configuration ships alongside this example in
otlp_collector_config.yaml.

Press <ENTER> to continue:

--------------------------------------------------------------------------------
4. Create a PromQL alarm

Now we alarm on those metrics. The comparison goes inside the query itself: a PromQL
alarm has no separate threshold, comparison operator, statistic, or period.

Enter a PromQL query, or press <ENTER> for the default
[avg by (host) (system_cpu_utilization) > 80]:

Creating alarm 'doc-example-promql-alarm-8842'...
  query:            avg by (host) (system_cpu_utilization) > 80
  evaluationInterval: 60 seconds
  pendingPeriod:      300 seconds
  recoveryPeriod:     120 seconds

Alarm created. Its state is OK.

Note that it starts in OK rather than INSUFFICIENT_DATA, which is another way PromQL
alarms differ from classic ones.

Press <ENTER> to continue:
--------------------------------------------------------------------------------
5. Inspect the alarm's contributors

Each contributor is one series the query matched, identified by its label set. This is
how you find out *which* host is unhealthy rather than only that something is. Classic
alarms have no equivalent.

Press <ENTER> to continue:

No contributors yet. The query matched no series, which usually means no OTel metrics
with these labels have arrived. Once your collector is sending data, this is where each
matching series appears, like this:

  host=web-01: value 91.4 exceeds threshold 80
  host=web-04: value 87.2 exceeds threshold 80

--------------------------------------------------------------------------------
6. Get statistics and chart the metric on a dashboard

Statistics and dashboards are how you see what the alarm is evaluating.

Press <ENTER> to continue:

Statistics for AWS/EC2 CPUUtilization over the last 24 hours:
  Average: 12.7
  Maximum: 68.3
  Datapoints: 24

Created dashboard 'doc-example-dashboard-8842'.
Read the dashboard back; it contains 1 widget.

--------------------------------------------------------------------------------
7. Mute the alarm for a maintenance window

While a mute rule is active the targeted alarms keep evaluating and keep changing
state, but their actions do not fire. This is the supported way to suppress
notifications during planned maintenance, instead of disabling alarm actions and hoping
someone remembers to turn them back on.

Press <ENTER> to continue:

Created mute rule 'doc-example-mute-rule-8842':
  schedule: cron(0 2 * * SUN) for PT2H
  timezone: America/Los_Angeles
  targets:  doc-example-promql-alarm-8842

Note the two formats here. The expression is a five-field cron expression -- five, not
the six EventBridge uses. The duration is an ISO 8601 duration, so 'PT2H', not '2h'.

Read the rule back: status Enabled, mute type Scheduled.
Found the rule among 3 rules in this account.

Note: mute rule summaries carry no name field, only an ARN, so we matched on the ARN.

--------------------------------------------------------------------------------
8. Clean up

Delete the resources this scenario created? (y/n): y

Deleted mute rule doc-example-mute-rule-8842.
Deleted alarm doc-example-promql-alarm-8842.
Deleted dashboard doc-example-dashboard-8842.
Stopped OTel enrichment (this run started it).

--------------------------------------------------------------------------------
This concludes the Amazon CloudWatch Basics scenario.
--------------------------------------------------------------------------------
```

## SOS Tags

The following table describes the metadata used in this SDK Getting Started Scenario.

| action                        | metadata file            | metadata key                                |
|-------------------------------|--------------------------|---------------------------------------------|
| `listMetrics`                 | cloudwatch_metadata.yaml | cloudwatch_ListMetrics                      |
| `startOTelEnrichment`         | cloudwatch_metadata.yaml | cloudwatch_StartOTelEnrichment              |
| `getOTelEnrichment`           | cloudwatch_metadata.yaml | cloudwatch_GetOTelEnrichment                |
| `putMetricAlarm`              | cloudwatch_metadata.yaml | cloudwatch_PutMetricAlarm                   |
| `describeAlarmContributors`   | cloudwatch_metadata.yaml | cloudwatch_DescribeAlarmContributors        |
| `getMetricStatistics`         | cloudwatch_metadata.yaml | cloudwatch_GetMetricStatistics              |
| `putDashboard`                | cloudwatch_metadata.yaml | cloudwatch_PutDashboard                     |
| `getDashboard`                | cloudwatch_metadata.yaml | cloudwatch_GetDashboard                     |
| `putAlarmMuteRule`            | cloudwatch_metadata.yaml | cloudwatch_PutAlarmMuteRule                 |
| `getAlarmMuteRule`            | cloudwatch_metadata.yaml | cloudwatch_GetAlarmMuteRule                 |
| `listAlarmMuteRules`          | cloudwatch_metadata.yaml | cloudwatch_ListAlarmMuteRules               |
| `deleteAlarmMuteRule`         | cloudwatch_metadata.yaml | cloudwatch_DeleteAlarmMuteRule              |
| `deleteAlarms`                | cloudwatch_metadata.yaml | cloudwatch_DeleteAlarms                     |
| `deleteDashboards`            | cloudwatch_metadata.yaml | cloudwatch_DeleteDashboards                 |
| `stopOTelEnrichment`          | cloudwatch_metadata.yaml | cloudwatch_StopOTelEnrichment               |
| `scenario`                    | cloudwatch_metadata.yaml | cloudwatch_GetStartedMetricsDashboardsAlarms |

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
