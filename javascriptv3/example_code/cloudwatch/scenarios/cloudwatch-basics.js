// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[javascript.v3.cloudwatch.Basics.scenario]
import {
  Scenario,
  ScenarioAction,
  ScenarioInput,
  ScenarioOutput,
} from "@aws-doc-sdk-examples/lib/scenario/index.js";
import {
  CloudWatchClient,
  DeleteAlarmMuteRuleCommand,
  DeleteAlarmsCommand,
  DeleteDashboardsCommand,
  DescribeAlarmContributorsCommand,
  GetAlarmMuteRuleCommand,
  GetDashboardCommand,
  GetMetricStatisticsCommand,
  GetOTelEnrichmentCommand,
  ListAlarmMuteRulesCommand,
  ListMetricsCommand,
  PutAlarmMuteRuleCommand,
  PutDashboardCommand,
  PutMetricAlarmCommand,
  StartOTelEnrichmentCommand,
  StopOTelEnrichmentCommand,
} from "@aws-sdk/client-cloudwatch";
import { parseArgs } from "node:util";
import { fileURLToPath } from "node:url";

const DEFAULT_QUERY = "avg by (host) (system_cpu_utilization) > 80";

// Valid evaluation intervals are 10, 20, 30, or any multiple of 60 up to 3600 seconds.
const EVALUATION_INTERVAL = 60;
const PENDING_PERIOD = 300;
const RECOVERY_PERIOD = 120;

/**
 * @typedef {{
 *   client: import('@aws-sdk/client-cloudwatch').CloudWatchClient,
 *   alarmName: string,
 *   dashboardName: string,
 *   muteRuleName: string,
 *   namespaces: [string, number][],
 *   metric: import('@aws-sdk/client-cloudwatch').Metric | undefined,
 *   query: string,
 *   startedEnrichment: boolean,
 *   dashboardCreated: boolean,
 *   deleteResources: boolean,
 * }} State
 */

/**
 * Used repeatedly to have the user press enter.
 * @type {ScenarioInput}
 */
const pressEnter = new ScenarioInput("continue", "Press Enter to continue", {
  type: "confirm",
});

const greet = new ScenarioOutput(
  "greet",
  `Welcome to the Amazon CloudWatch Basics scenario.

CloudWatch now ingests OpenTelemetry metrics natively. This scenario walks through that experience: it turns on OTel enrichment so CloudWatch can correlate incoming OTLP metrics with the resources that produced them, alarms on those metrics with a PromQL query, and shows you which individual series drove the alarm.

A PromQL alarm works differently from a classic metric alarm. Rather than watching one metric and counting breaching periods, it evaluates a query that can match many series at once, and tracks each one separately as a contributor.

Note that sending OTLP metrics to CloudWatch is not an AWS SDK operation. Metrics arrive over the OTLP protocol through the CloudWatch agent, an OpenTelemetry Collector, or an ADOT SDK. Everything this scenario does is configuration and querying around that ingestion path.

Let's get started...`,
  { header: true },
);

// Step 1: List metrics and namespaces. This orients the reader before any configuration
// happens.
const displayListMetrics = new ScenarioOutput(
  "displayListMetrics",
  "1. List metrics and namespaces\n\nBefore configuring anything, let's see what CloudWatch is already collecting in this account by calling ListMetrics.",
);

const sdkListMetrics = new ScenarioAction(
  "sdkListMetrics",
  async (/** @type {State} */ state) => {
    const counts = new Map();
    let metricCount = 0;
    let nextToken;

    do {
      const response = await state.client.send(
        new ListMetricsCommand({ NextToken: nextToken }),
      );
      for (const metric of response.Metrics ?? []) {
        counts.set(metric.Namespace, (counts.get(metric.Namespace) ?? 0) + 1);
        metricCount += 1;
        // Keep the first metric we see so later steps have something to chart.
        if (!state.metric) {
          state.metric = metric;
        }
      }
      nextToken = response.NextToken;
      // This account may have a very large number of metrics, so stop once we have
      // enough to give the reader a sense of what is there.
    } while (nextToken && metricCount < 500);

    state.namespaces = [...counts.entries()].sort((a, b) => b[1] - a[1]);

    console.log(
      `\tFound ${metricCount} metrics across ${state.namespaces.length} namespaces:`,
    );
    for (const [namespace, count] of state.namespaces.slice(0, 10)) {
      console.log(`\t  ${namespace} (${count} metrics)`);
    }
    if (state.namespaces.length === 0) {
      console.log(
        "\tNo metrics found in this account. The statistics and dashboard steps later on need an existing metric, so they will be skipped.",
      );
    }
  },
);

// Step 2: Start OTel enrichment. Enrichment is what makes CloudWatch attach AWS resource
// context to incoming OTLP metrics.
const displayStartEnrichment = new ScenarioOutput(
  "displayStartEnrichment",
  "2. Start OpenTelemetry enrichment\n\nEnrichment is what lets CloudWatch attach AWS resource context to the OTLP metrics you send it. Without it, your metrics arrive as opaque series with no connection to the resources that emitted them.\n\nWe check the current state first, and only start enrichment if it isn't already on.",
);

const sdkStartEnrichment = new ScenarioAction(
  "sdkStartEnrichment",
  async (/** @type {State} */ state) => {
    const { Status } = await state.client.send(
      new GetOTelEnrichmentCommand({}),
    );
    console.log(`\tEnrichment status: ${Status}`);

    if (Status === "Running") {
      console.log(
        "\n\tEnrichment was already running, so we will leave it alone. The cleanup step will not stop it, because other workloads in this account may depend on it.",
      );
      return;
    }

    await state.client.send(new StartOTelEnrichmentCommand({}));
    // Record that *this run* started enrichment, so cleanup only stops what it turned on.
    state.startedEnrichment = true;

    const after = await state.client.send(new GetOTelEnrichmentCommand({}));
    console.log(`\tEnrichment status: ${after.Status}`);
    console.log(
      "\n\tNote: this run started enrichment, so the cleanup step will stop it again.",
    );
  },
);

// Step 3: Explain OTLP ingestion. This step makes no service call; naming the gap
// explicitly is the point.
const displayOtlpIngestion = new ScenarioOutput(
  "displayOtlpIngestion",
  `3. Send OTLP metrics to CloudWatch

This step is not an AWS SDK operation, and that's worth being explicit about. Metrics reach CloudWatch over the OTLP protocol, through the CloudWatch agent, an OpenTelemetry Collector, or an ADOT SDK. There is no PutOTelMetrics API to call.

Point your collector at the CloudWatch metrics endpoint, which follows the pattern
\thttps://monitoring.<region>.amazonaws.com/v1/metrics

The endpoint is HTTP/1.1 only and does not support gRPC, so use an otlphttp exporter rather than otlp. The metrics endpoint signs as "monitoring".`,
);

// Step 4: Create a PromQL alarm.
const displayCreateAlarm = new ScenarioOutput(
  "displayCreateAlarm",
  "4. Create a PromQL alarm\n\nNow we alarm on those metrics. The comparison goes inside the query itself: a PromQL alarm has no separate threshold, comparison operator, statistic, or period.",
);

const inputQuery = new ScenarioInput("query", "Enter a PromQL query:", {
  type: "input",
  default: DEFAULT_QUERY,
});

const sdkCreateAlarm = new ScenarioAction(
  "sdkCreateAlarm",
  async (/** @type {State} */ state) => {
    const query = state.query?.trim() || DEFAULT_QUERY;
    state.query = query;

    // EvaluationCriteria is a union and is mutually exclusive with the classic MetricName
    // and Metrics parameters. When you use it you must also set EvaluationInterval, and
    // you must not set Period, Statistic, Threshold, ComparisonOperator,
    // EvaluationPeriods, DatapointsToAlarm, or TreatMissingData.
    await state.client.send(
      new PutMetricAlarmCommand({
        AlarmName: state.alarmName,
        AlarmDescription:
          "A PromQL alarm created by the AWS SDK for JavaScript Basics scenario.",
        EvaluationCriteria: {
          PromQLCriteria: {
            Query: query,
            PendingPeriod: PENDING_PERIOD,
            RecoveryPeriod: RECOVERY_PERIOD,
          },
        },
        EvaluationInterval: EVALUATION_INTERVAL,
        ActionsEnabled: false,
      }),
    );

    console.log(`\tCreated alarm ${state.alarmName}:`);
    console.log(`\t  query:              ${query}`);
    console.log(`\t  evaluationInterval: ${EVALUATION_INTERVAL} seconds`);
    console.log(`\t  pendingPeriod:      ${PENDING_PERIOD} seconds`);
    console.log(`\t  recoveryPeriod:     ${RECOVERY_PERIOD} seconds`);
    console.log(
      "\n\tA PromQL alarm starts in the OK state rather than INSUFFICIENT_DATA, which is another way it differs from a classic alarm.",
    );
  },
);

// Step 5: Inspect the alarm's contributors. This is the step with no classic-alarm
// equivalent.
const displayContributors = new ScenarioOutput(
  "displayContributors",
  "5. Inspect the alarm's contributors\n\nEach contributor is one series the query matched, identified by its label set. This is how you find out which host is unhealthy rather than only that something is. Classic alarms have no equivalent.",
);

const sdkContributors = new ScenarioAction(
  "sdkContributors",
  async (/** @type {State} */ state) => {
    const contributors = [];
    let nextToken;

    do {
      const response = await state.client.send(
        new DescribeAlarmContributorsCommand({
          AlarmName: state.alarmName,
          NextToken: nextToken,
        }),
      );
      contributors.push(...(response.AlarmContributors ?? []));
      nextToken = response.NextToken;
      // A page can come back empty while still carrying a token, so keep going until the
      // token itself is gone rather than stopping at the first empty page.
    } while (nextToken);

    if (contributors.length === 0) {
      console.log(
        "\tNo contributors yet. The query matched no series, which usually means no OTel metrics with these labels have arrived. Once your collector is sending data, each matching series appears here with its labels and the reason it breached.",
      );
      return;
    }

    console.log(`\tFound ${contributors.length} contributors:`);
    for (const contributor of contributors) {
      const labels = Object.entries(contributor.ContributorAttributes ?? {})
        .sort(([a], [b]) => a.localeCompare(b))
        .map(([key, value]) => `${key}=${value}`)
        .join(", ");
      console.log(`\t  ${contributor.ContributorId}: ${labels}`);
      console.log(`\t    reason: ${contributor.StateReason}`);
    }
  },
);

// Step 6: Get statistics and chart the metric on a dashboard.
const displayDashboard = new ScenarioOutput(
  "displayDashboard",
  "6. Get statistics and chart the metric on a dashboard\n\nStatistics and dashboards are how you see what the alarm is evaluating.",
);

const sdkDashboard = new ScenarioAction(
  "sdkDashboard",
  async (/** @type {State} */ state) => {
    if (!state.metric) {
      console.log(
        "\tSkipping statistics and dashboard because no metrics exist yet.",
      );
      return;
    }

    const metric = state.metric;
    const stats = await state.client.send(
      new GetMetricStatisticsCommand({
        Namespace: metric.Namespace,
        MetricName: metric.MetricName,
        Dimensions: metric.Dimensions,
        StartTime: new Date(Date.now() - 24 * 60 * 60 * 1000),
        EndTime: new Date(),
        Period: 3600,
        Statistics: ["Average", "Maximum"],
      }),
    );

    const datapoints = stats.Datapoints ?? [];
    console.log(
      `\tStatistics for ${metric.Namespace} ${metric.MetricName} over the last day:`,
    );
    console.log(`\t  Datapoints: ${datapoints.length}`);
    for (const datapoint of datapoints.slice(0, 3)) {
      console.log(
        `\t  ${datapoint.Timestamp?.toISOString()} average ${datapoint.Average}, maximum ${datapoint.Maximum}`,
      );
    }

    const response = await state.client.send(
      new PutDashboardCommand({
        DashboardName: state.dashboardName,
        DashboardBody: buildDashboardBody(metric),
      }),
    );
    state.dashboardCreated = true;

    for (const message of response.DashboardValidationMessages ?? []) {
      console.log(`\tDashboard validation message: ${message.Message}`);
    }
    console.log(`\tCreated dashboard ${state.dashboardName}.`);

    const stored = await state.client.send(
      new GetDashboardCommand({ DashboardName: state.dashboardName }),
    );
    console.log(
      `\tRead the dashboard back, ${stored.DashboardBody?.length} characters of widget JSON.`,
    );
  },
);

/**
 * Build a single-widget dashboard body that charts the given metric.
 * @param {import('@aws-sdk/client-cloudwatch').Metric} metric
 * @returns {string} The dashboard body, as JSON.
 */
const buildDashboardBody = (metric) => {
  const metricSpec = [metric.Namespace, metric.MetricName];
  for (const dimension of metric.Dimensions ?? []) {
    metricSpec.push(dimension.Name, dimension.Value);
  }

  return JSON.stringify({
    widgets: [
      {
        type: "text",
        x: 0,
        y: 0,
        width: 24,
        height: 2,
        properties: {
          markdown:
            "This dashboard was created programmatically by an AWS SDK code example.",
        },
      },
      {
        type: "metric",
        x: 0,
        y: 2,
        width: 12,
        height: 6,
        properties: {
          metrics: [metricSpec],
          view: "timeSeries",
          stat: "Average",
          period: 300,
          title: metric.MetricName,
        },
      },
    ],
  });
};

// Step 7: Mute the alarm for a maintenance window.
const displayMuteRule = new ScenarioOutput(
  "displayMuteRule",
  "7. Mute the alarm for a maintenance window\n\nWhile a mute rule is active the targeted alarms keep evaluating and keep changing state, but their actions do not fire. This is the supported way to suppress notifications during planned maintenance, instead of disabling alarm actions and hoping someone remembers to turn them back on.",
);

const sdkMuteRule = new ScenarioAction(
  "sdkMuteRule",
  async (/** @type {State} */ state) => {
    // The expression is a five-field cron expression,
    // cron(Minutes Hours Day-of-month Month Day-of-week). Note that this is five fields,
    // not the six that Amazon EventBridge uses. For a one-time window, use
    // at(yyyy-MM-ddThh:mm), with no seconds. The duration is an ISO 8601 duration from
    // PT1M to P15D, so PT2H rather than 2h.
    const expression = "cron(0 2 * * SUN)";
    const duration = "PT2H";
    const timezone = "America/Los_Angeles";

    await state.client.send(
      new PutAlarmMuteRuleCommand({
        Name: state.muteRuleName,
        Description:
          "A mute rule created by the AWS SDK for JavaScript Basics scenario.",
        Rule: {
          Schedule: {
            Expression: expression,
            Duration: duration,
            Timezone: timezone,
          },
        },
        // Target up to 100 alarms. If MuteTargets is omitted, the rule applies to every
        // alarm in the account.
        MuteTargets: { AlarmNames: [state.alarmName] },
      }),
    );

    console.log(`\tCreated mute rule ${state.muteRuleName}:`);
    console.log(`\t  schedule: ${expression} for ${duration}`);
    console.log(`\t  timezone: ${timezone}`);
    console.log(`\t  targets:  ${state.alarmName}`);
    console.log(
      "\n\tNote the two formats here. The expression is a five-field cron expression, five rather than the six Amazon EventBridge uses. The duration is an ISO 8601 duration, so 'PT2H' and not '2h'.",
    );
    console.log(
      "\n\tAlso note that MuteTargets is set explicitly. If you leave it out, the rule applies to every alarm in the account.",
    );

    const rule = await state.client.send(
      new GetAlarmMuteRuleCommand({ AlarmMuteRuleName: state.muteRuleName }),
    );
    console.log(
      `\tRead the rule back: status ${rule.Status}, mute type ${rule.MuteType}.`,
    );

    const summaries = [];
    let nextToken;
    do {
      const response = await state.client.send(
        new ListAlarmMuteRulesCommand({
          AlarmName: state.alarmName,
          NextToken: nextToken,
        }),
      );
      summaries.push(...(response.AlarmMuteRuleSummaries ?? []));
      nextToken = response.NextToken;
    } while (nextToken);

    console.log(`\tFound ${summaries.length} mute rules targeting this alarm.`);
    // Mute rule summaries carry no name field, only an ARN, so match on the ARN suffix.
    const match = summaries.find(
      (summary) =>
        summary.AlarmMuteRuleArn?.endsWith(`/${state.muteRuleName}`) ||
        summary.AlarmMuteRuleArn?.endsWith(`:${state.muteRuleName}`),
    );
    if (match) {
      console.log(
        `\t  matched by ARN: ${match.AlarmMuteRuleArn} (${match.Status})`,
      );
    }
  },
);

// Step 8: Clean up.
const askToDeleteResources = new ScenarioInput(
  "deleteResources",
  "8. Clean up\n\nDelete the resources this scenario created?",
  { type: "confirm" },
);

const displaySkipCleanUp = new ScenarioOutput(
  "displaySkipCleanUp",
  "\tSkipping cleanup. Note that the alarm, dashboard, and mute rule are still in your account, and enrichment may still be running.",
  { skipWhen: (/** @type {State} */ state) => state.deleteResources },
);

const sdkCleanUp = new ScenarioAction(
  "sdkCleanUp",
  async (/** @type {State} */ state) => {
    // Each deletion is attempted independently so that one failure does not leave the
    // remaining resources behind.
    try {
      await state.client.send(
        new DeleteAlarmMuteRuleCommand({
          AlarmMuteRuleName: state.muteRuleName,
        }),
      );
      console.log(`\tDeleted mute rule ${state.muteRuleName}.`);
    } catch (caught) {
      console.log(`\tCould not delete the mute rule: ${caught.message}`);
    }

    try {
      await state.client.send(
        new DeleteAlarmsCommand({ AlarmNames: [state.alarmName] }),
      );
      console.log(`\tDeleted alarm ${state.alarmName}.`);
    } catch (caught) {
      console.log(`\tCould not delete the alarm: ${caught.message}`);
    }

    if (state.dashboardCreated) {
      try {
        await state.client.send(
          new DeleteDashboardsCommand({
            DashboardNames: [state.dashboardName],
          }),
        );
        console.log(`\tDeleted dashboard ${state.dashboardName}.`);
      } catch (caught) {
        console.log(`\tCould not delete the dashboard: ${caught.message}`);
      }
    }

    if (!state.startedEnrichment) {
      console.log(
        "\tLeft OTel enrichment running, because it was already on before this run.",
      );
      return;
    }

    try {
      await state.client.send(new StopOTelEnrichmentCommand({}));
      console.log("\tStopped OTel enrichment, because this run started it.");
    } catch (caught) {
      console.log(`\tCould not stop OTel enrichment: ${caught.message}`);
    }
  },
  { skipWhen: (/** @type {State} */ state) => !state.deleteResources },
);

const goodbye = new ScenarioOutput(
  "goodbye",
  "This concludes the Amazon CloudWatch Basics scenario.",
);

// Suffix the resource names so repeated runs do not collide.
const suffix = Math.floor(Math.random() * 9000) + 1000;

const myScenario = new Scenario(
  "CloudWatch Basics",
  [
    greet,
    pressEnter,
    displayListMetrics,
    sdkListMetrics,
    pressEnter,
    displayStartEnrichment,
    sdkStartEnrichment,
    pressEnter,
    displayOtlpIngestion,
    pressEnter,
    displayCreateAlarm,
    inputQuery,
    sdkCreateAlarm,
    pressEnter,
    displayContributors,
    sdkContributors,
    pressEnter,
    displayDashboard,
    sdkDashboard,
    pressEnter,
    displayMuteRule,
    sdkMuteRule,
    pressEnter,
    askToDeleteResources,
    displaySkipCleanUp,
    sdkCleanUp,
    goodbye,
  ],
  {
    client: new CloudWatchClient({}),
    alarmName: `doc-example-promql-alarm-${suffix}`,
    dashboardName: `doc-example-dashboard-${suffix}`,
    muteRuleName: `doc-example-mute-rule-${suffix}`,
    namespaces: [],
    metric: undefined,
    startedEnrichment: false,
    dashboardCreated: false,
  },
);

/** @type {{ stepHandlerOptions: StepHandlerOptions }} */
export const main = async (stepHandlerOptions) => {
  await myScenario.run(stepHandlerOptions);
};

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  const { values } = parseArgs({
    options: {
      yes: {
        type: "boolean",
        short: "y",
      },
    },
  });
  main({ confirmAll: values.yes });
}
// snippet-end:[javascript.v3.cloudwatch.Basics.scenario]
