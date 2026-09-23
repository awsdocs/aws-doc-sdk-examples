# Amazon CloudWatch Basics Scenario

## Overview

This Amazon CloudWatch Basics scenario demonstrates how to interact with CloudWatch using an AWS SDK, framed around the OpenTelemetry (OTel) metrics experience that CloudWatch now supports natively. Rather than leading with custom metrics and single-metric alarms, the scenario leads with the path most new CloudWatch workloads take: OTLP metrics arriving from an OpenTelemetry collector, queried and alarmed on with PromQL.

Here are the high-level steps of the most important CloudWatch operations used in this scenario:

1. **List metrics and namespaces**: The program calls `ListMetrics` to show what CloudWatch is already collecting in the account, orienting the reader before any configuration happens.

2. **Start OpenTelemetry enrichment**: The program checks enrichment state with `GetOTelEnrichment` and starts it with `StartOTelEnrichment` if it isn't already active. Enrichment is what makes CloudWatch attach AWS resource context to incoming OTLP metrics.

3. **Send OTLP metrics**: The program explains that ingestion is *not* an SDK operation — metrics arrive over OTLP via the CloudWatch agent, an OpenTelemetry Collector, or an ADOT SDK — and points at the collector configuration shipped with the example.

4. **Create a PromQL alarm**: The program calls `PutMetricAlarm` with the PromQL evaluation criteria, creating an alarm that evaluates a query across every series it matches instead of watching a single metric.

5. **Inspect the alarm's contributors**: The program calls `DescribeAlarmContributors` to show which individual series drove the alarm. This is the step with no classic-alarm equivalent, and the reason the PromQL model is worth learning.

6. **Get statistics and chart the metric**: The program calls `GetMetricStatistics`, then `PutDashboard` and `GetDashboard` to visualize what the alarm is evaluating.

7. **Mute the alarm for a maintenance window**: The program calls `PutAlarmMuteRule`, `GetAlarmMuteRule`, and `ListAlarmMuteRules`. A mute rule suppresses alarm actions while letting the alarm keep evaluating — the supported alternative to disabling alarm actions.

8. **Clean up**: The program deletes the mute rule, alarm, and dashboard, and stops OTel enrichment *only if this run started it*.

Note: These steps are not the complete program, but summarize the high-level steps.

### Why this scenario is framed around OTel

A classic CloudWatch alarm watches one metric and counts breaching periods. A PromQL alarm evaluates a query that can match many series at once and tracks each one separately as a contributor. That changes how you create the alarm, how you read its state, and how you find out which host or pod is unhealthy rather than only that something is. The scenario is deliberately built so a reader coming from the classic model sees exactly where the two diverge.

### Resources

No pre-existing resources are required. The scenario creates and deletes everything it needs.

The scenario is designed to run to completion in an account with no OTLP metrics flowing. In that case the PromQL alarm simply has no contributors, and the program explains that outcome rather than failing — most readers will run this before they have a collector configured.

## Implementations

This example will be implemented in the following languages:

- Java
- Kotlin
- .NET
- Python
- Ruby
- JavaScript
- C++

## Additional reading

- [Amazon CloudWatch User Guide](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/WhatIsCloudWatch.html)
- [CloudWatch OTLP endpoint](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/CloudWatch-OTLPEndpoint.html)
- [Bearer token authentication for the OTLP endpoints](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/CloudWatch-OTLP-MetricsBearerTokenAuth.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
