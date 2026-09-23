// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { beforeEach, describe, expect, it, vi } from "vitest";
import {
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

import {
  DEFAULT_QUERY,
  EVALUATION_INTERVAL,
  PENDING_PERIOD,
  RECOVERY_PERIOD,
  buildDashboardBody,
  myScenario,
  sdkCleanUp,
  sdkContributors,
  sdkCreateAlarm,
  sdkDashboard,
  sdkListMetrics,
  sdkMuteRule,
  sdkStartEnrichment,
} from "../scenarios/cloudwatch-basics.js";

const REGION = "us-west-2";

/**
 * Builds a fake CloudWatch client. Responses are keyed by command name, so a test only
 * has to describe the calls it cares about.
 * @param {Record<string, object | object[] | Error>} responses
 */
const makeClient = (responses = {}) => {
  const sent = [];
  // Commands with an array response return one entry per call, for paginated steps.
  const queues = new Map(
    Object.entries(responses)
      .filter(([, value]) => Array.isArray(value))
      .map(([name, value]) => [name, [...value]]),
  );

  return {
    sent,
    config: { region: () => Promise.resolve(REGION) },
    send: vi.fn((command) => {
      const name = command.constructor.name;
      sent.push(command);

      if (queues.has(name)) {
        const queue = queues.get(name);
        const next = queue.length > 1 ? queue.shift() : queue[0];
        return next instanceof Error
          ? Promise.reject(next)
          : Promise.resolve(next);
      }

      const response = responses[name];
      if (response instanceof Error) {
        return Promise.reject(response);
      }
      return Promise.resolve(response ?? {});
    }),
  };
};

/** Returns every sent command that is an instance of the given command class. */
const commandsOfType = (client, CommandType) =>
  client.sent.filter((command) => command instanceof CommandType);

/** Returns the input of the single sent command of the given type. */
const inputOf = (client, CommandType) => {
  const matches = commandsOfType(client, CommandType);
  expect(matches).toHaveLength(1);
  return matches[0].input;
};

const makeState = (overrides = {}) => ({
  alarmName: "doc-example-promql-alarm-1234",
  dashboardName: "doc-example-dashboard-1234",
  muteRuleName: "doc-example-mute-rule-1234",
  namespaces: [],
  metric: undefined,
  startedEnrichment: false,
  dashboardCreated: false,
  deleteResources: true,
  ...overrides,
});

/** Step handlers read stepHandlerOptions.verbose, so it always has to be passed. */
const handle = (step, state) => step.handle(state, { verbose: false });

describe("CloudWatch Basics scenario", () => {
  beforeEach(() => {
    vi.spyOn(console, "log").mockImplementation(() => {});
  });

  describe("step 1: list metrics and namespaces", () => {
    it("counts metrics by namespace, busiest first", async () => {
      const client = makeClient({
        ListMetricsCommand: {
          Metrics: [
            { Namespace: "AWS/S3", MetricName: "BucketSizeBytes" },
            { Namespace: "AWS/EC2", MetricName: "CPUUtilization" },
            { Namespace: "AWS/EC2", MetricName: "NetworkIn" },
          ],
        },
      });
      const state = makeState({ client });

      await handle(sdkListMetrics, state);

      expect(state.namespaces).toEqual([
        ["AWS/EC2", 2],
        ["AWS/S3", 1],
      ]);
    });

    it("keeps the first metric so later steps have something to chart", async () => {
      const client = makeClient({
        ListMetricsCommand: {
          Metrics: [
            { Namespace: "AWS/EC2", MetricName: "CPUUtilization" },
            { Namespace: "AWS/EC2", MetricName: "NetworkIn" },
          ],
        },
      });
      const state = makeState({ client });

      await handle(sdkListMetrics, state);

      expect(state.metric).toEqual({
        Namespace: "AWS/EC2",
        MetricName: "CPUUtilization",
      });
    });

    it("follows the next token across pages", async () => {
      const client = makeClient({
        ListMetricsCommand: [
          {
            Metrics: [{ Namespace: "AWS/EC2", MetricName: "CPUUtilization" }],
            NextToken: "token-1",
          },
          {
            Metrics: [{ Namespace: "AWS/S3", MetricName: "BucketSizeBytes" }],
          },
        ],
      });
      const state = makeState({ client });

      await handle(sdkListMetrics, state);

      expect(commandsOfType(client, ListMetricsCommand)).toHaveLength(2);
      expect(client.sent[1].input.NextToken).toBe("token-1");
      expect(state.namespaces).toHaveLength(2);
    });

    it("stops paginating once it has seen enough metrics", async () => {
      // Every page carries a token, so only the 500-metric cap can end the loop.
      const page = {
        Metrics: Array.from({ length: 100 }, (_, index) => ({
          Namespace: "AWS/EC2",
          MetricName: `metric-${index}`,
        })),
        NextToken: "more",
      };
      const client = makeClient({ ListMetricsCommand: page });
      const state = makeState({ client });

      await handle(sdkListMetrics, state);

      expect(commandsOfType(client, ListMetricsCommand)).toHaveLength(5);
      expect(state.namespaces).toEqual([["AWS/EC2", 500]]);
    });

    it("leaves the namespace list empty when the account has no metrics", async () => {
      const client = makeClient({ ListMetricsCommand: { Metrics: [] } });
      const state = makeState({ client });

      await handle(sdkListMetrics, state);

      expect(state.namespaces).toEqual([]);
      expect(state.metric).toBeUndefined();
    });
  });

  describe("step 2: start OTel enrichment", () => {
    it("starts enrichment that is not running and records that it did", async () => {
      const client = makeClient({
        GetOTelEnrichmentCommand: { Status: "Stopped" },
      });
      const state = makeState({ client });

      await handle(sdkStartEnrichment, state);

      expect(commandsOfType(client, StartOTelEnrichmentCommand)).toHaveLength(
        1,
      );
      expect(state.startedEnrichment).toBe(true);
      // The status is read again afterward, so the reader sees the new value.
      expect(commandsOfType(client, GetOTelEnrichmentCommand)).toHaveLength(2);
    });

    it("leaves enrichment that is already running alone", async () => {
      const client = makeClient({
        GetOTelEnrichmentCommand: { Status: "Running" },
      });
      const state = makeState({ client });

      await handle(sdkStartEnrichment, state);

      expect(commandsOfType(client, StartOTelEnrichmentCommand)).toHaveLength(
        0,
      );
      expect(state.startedEnrichment).toBe(false);
    });

    it.each(["Stopped", "Starting", "Stopping", undefined])(
      "treats status %s as not on yet",
      async (Status) => {
        const client = makeClient({ GetOTelEnrichmentCommand: { Status } });
        const state = makeState({ client });

        await handle(sdkStartEnrichment, state);

        expect(commandsOfType(client, StartOTelEnrichmentCommand)).toHaveLength(
          1,
        );
        expect(state.startedEnrichment).toBe(true);
      },
    );
  });

  describe("step 4: create the PromQL alarm", () => {
    it("falls back to the default query when the prompt is left empty", async () => {
      const client = makeClient();
      const state = makeState({ client, query: "   " });

      await handle(sdkCreateAlarm, state);

      expect(inputOf(client, PutMetricAlarmCommand).EvaluationCriteria).toEqual(
        {
          PromQLCriteria: {
            Query: DEFAULT_QUERY,
            PendingPeriod: PENDING_PERIOD,
            RecoveryPeriod: RECOVERY_PERIOD,
          },
        },
      );
    });

    it("trims the entered query", async () => {
      const client = makeClient();
      const state = makeState({ client, query: "  up == 0  " });

      await handle(sdkCreateAlarm, state);

      expect(
        inputOf(client, PutMetricAlarmCommand).EvaluationCriteria.PromQLCriteria
          .Query,
      ).toBe("up == 0");
      expect(state.query).toBe("up == 0");
    });

    it("sets none of the parameters that EvaluationCriteria excludes", async () => {
      // EvaluationCriteria is mutually exclusive with the classic alarm parameters.
      // Sending any of them alongside it makes the request fail.
      const client = makeClient();

      await handle(sdkCreateAlarm, makeState({ client }));

      const input = inputOf(client, PutMetricAlarmCommand);
      expect(input.EvaluationInterval).toBe(EVALUATION_INTERVAL);
      for (const excluded of [
        "MetricName",
        "Metrics",
        "Namespace",
        "Period",
        "Statistic",
        "ExtendedStatistic",
        "Threshold",
        "ComparisonOperator",
        "EvaluationPeriods",
        "DatapointsToAlarm",
        "TreatMissingData",
      ]) {
        expect(input).not.toHaveProperty(excluded);
      }
    });
  });

  describe("step 5: inspect contributors", () => {
    it("prints each contributor with its labels sorted", async () => {
      const client = makeClient({
        DescribeAlarmContributorsCommand: {
          AlarmContributors: [
            {
              ContributorId: "contributor-1",
              ContributorAttributes: { host: "web-1", az: "us-west-2a" },
              StateReason: "Threshold crossed.",
            },
          ],
        },
      });

      await handle(sdkContributors, makeState({ client }));

      expect(console.log).toHaveBeenCalledWith(
        "\t  contributor-1: az=us-west-2a, host=web-1",
      );
      expect(console.log).toHaveBeenCalledWith(
        "\t    reason: Threshold crossed.",
      );
    });

    it("keeps paging past an empty page that still carries a token", async () => {
      const client = makeClient({
        DescribeAlarmContributorsCommand: [
          { AlarmContributors: [], NextToken: "token-1" },
          { AlarmContributors: [{ ContributorId: "contributor-1" }] },
        ],
      });

      await handle(sdkContributors, makeState({ client }));

      expect(
        commandsOfType(client, DescribeAlarmContributorsCommand),
      ).toHaveLength(2);
      expect(console.log).toHaveBeenCalledWith("\tFound 1 contributors:");
    });

    it("explains the empty case rather than printing nothing", async () => {
      const client = makeClient({
        DescribeAlarmContributorsCommand: { AlarmContributors: [] },
      });

      await handle(sdkContributors, makeState({ client }));

      expect(console.log).toHaveBeenCalledWith(
        expect.stringContaining("No contributors yet"),
      );
    });
  });

  describe("buildDashboardBody", () => {
    const metricWidget = (body) =>
      JSON.parse(body).widgets.find((widget) => widget.type === "metric");

    it("names its region", () => {
      // A dashboard can chart metrics from several regions, so a metric widget cannot
      // inherit one and has to name it.
      const body = buildDashboardBody(
        { Namespace: "AWS/EC2", MetricName: "CPUUtilization" },
        "eu-central-1",
      );

      expect(metricWidget(body).properties.region).toBe("eu-central-1");
    });

    it("flattens dimensions into the metric spec", () => {
      const body = buildDashboardBody(
        {
          Namespace: "AWS/EC2",
          MetricName: "CPUUtilization",
          Dimensions: [
            { Name: "InstanceId", Value: "i-abc123" },
            { Name: "InstanceType", Value: "t3.micro" },
          ],
        },
        REGION,
      );

      expect(metricWidget(body).properties.metrics).toEqual([
        [
          "AWS/EC2",
          "CPUUtilization",
          "InstanceId",
          "i-abc123",
          "InstanceType",
          "t3.micro",
        ],
      ]);
    });

    it("handles a metric with no dimensions", () => {
      const body = buildDashboardBody(
        { Namespace: "AWS/EC2", MetricName: "CPUUtilization" },
        REGION,
      );

      expect(metricWidget(body).properties.metrics).toEqual([
        ["AWS/EC2", "CPUUtilization"],
      ]);
      expect(metricWidget(body).properties.title).toBe("CPUUtilization");
    });
  });

  describe("step 6: statistics and dashboard", () => {
    const metric = { Namespace: "AWS/EC2", MetricName: "CPUUtilization" };

    it("charts the metric and reads the dashboard back", async () => {
      const client = makeClient({
        GetMetricStatisticsCommand: { Datapoints: [] },
        PutDashboardCommand: { DashboardValidationMessages: [] },
        GetDashboardCommand: { DashboardBody: "{}" },
      });
      const state = makeState({ client, metric });

      await handle(sdkDashboard, state);

      expect(inputOf(client, GetMetricStatisticsCommand).MetricName).toBe(
        "CPUUtilization",
      );
      const body = JSON.parse(
        inputOf(client, PutDashboardCommand).DashboardBody,
      );
      expect(
        body.widgets.find((widget) => widget.type === "metric").properties
          .region,
      ).toBe(REGION);
      expect(commandsOfType(client, GetDashboardCommand)).toHaveLength(1);
      expect(state.dashboardCreated).toBe(true);
    });

    it("skips the whole step when no metric was found", async () => {
      const client = makeClient();
      const state = makeState({ client, metric: undefined });

      await handle(sdkDashboard, state);

      expect(client.send).not.toHaveBeenCalled();
      expect(state.dashboardCreated).toBe(false);
    });

    it("reports dashboard validation messages", async () => {
      const client = makeClient({
        GetMetricStatisticsCommand: { Datapoints: [] },
        PutDashboardCommand: {
          DashboardValidationMessages: [{ Message: "Unknown property." }],
        },
        GetDashboardCommand: { DashboardBody: "{}" },
      });

      await handle(sdkDashboard, makeState({ client, metric }));

      expect(console.log).toHaveBeenCalledWith(
        "\tDashboard validation message: Unknown property.",
      );
    });
  });

  describe("step 7: mute the alarm", () => {
    it("targets only this scenario's alarm", async () => {
      // With MuteTargets omitted the rule would apply to every alarm in the account.
      const client = makeClient({
        GetAlarmMuteRuleCommand: { Status: "Enabled", MuteType: "Scheduled" },
        ListAlarmMuteRulesCommand: { AlarmMuteRuleSummaries: [] },
      });
      const state = makeState({ client });

      await handle(sdkMuteRule, state);

      const input = inputOf(client, PutAlarmMuteRuleCommand);
      expect(input.MuteTargets).toEqual({ AlarmNames: [state.alarmName] });
      // A five-field cron expression, not the six Amazon EventBridge uses, and an
      // ISO 8601 duration.
      expect(input.Rule.Schedule.Expression).toBe("cron(0 2 * * SUN)");
      expect(input.Rule.Schedule.Duration).toBe("PT2H");
      expect(inputOf(client, ListAlarmMuteRulesCommand).AlarmName).toBe(
        state.alarmName,
      );
    });

    it("matches the rule by ARN suffix, because summaries carry no name", async () => {
      const state = makeState();
      const arn = `arn:aws:cloudwatch:${REGION}:123456789012:alarm-mute-rule/${state.muteRuleName}`;
      const client = makeClient({
        GetAlarmMuteRuleCommand: { Status: "Enabled" },
        ListAlarmMuteRulesCommand: {
          AlarmMuteRuleSummaries: [
            { AlarmMuteRuleArn: "arn:aws:cloudwatch:us-west-2:1:rule/other" },
            { AlarmMuteRuleArn: arn, Status: "Enabled" },
          ],
        },
      });
      state.client = client;

      await handle(sdkMuteRule, state);

      expect(console.log).toHaveBeenCalledWith(
        `\t  matched by ARN: ${arn} (Enabled)`,
      );
    });
  });

  describe("step 8: clean up", () => {
    it("deletes what the run created", async () => {
      const client = makeClient();
      const state = makeState({
        client,
        startedEnrichment: true,
        dashboardCreated: true,
      });

      await handle(sdkCleanUp, state);

      expect(inputOf(client, DeleteAlarmMuteRuleCommand)).toEqual({
        AlarmMuteRuleName: state.muteRuleName,
      });
      expect(inputOf(client, DeleteAlarmsCommand)).toEqual({
        AlarmNames: [state.alarmName],
      });
      expect(inputOf(client, DeleteDashboardsCommand)).toEqual({
        DashboardNames: [state.dashboardName],
      });
      expect(commandsOfType(client, StopOTelEnrichmentCommand)).toHaveLength(1);
    });

    it("leaves enrichment it did not start", async () => {
      const client = makeClient();
      const state = makeState({ client, startedEnrichment: false });

      await handle(sdkCleanUp, state);

      expect(commandsOfType(client, StopOTelEnrichmentCommand)).toHaveLength(0);
    });

    it("skips the dashboard it did not create", async () => {
      const client = makeClient();
      const state = makeState({ client, dashboardCreated: false });

      await handle(sdkCleanUp, state);

      expect(commandsOfType(client, DeleteDashboardsCommand)).toHaveLength(0);
    });

    it("is skipped entirely when the user declines", async () => {
      const client = makeClient();
      const state = makeState({
        client,
        deleteResources: false,
        startedEnrichment: true,
        dashboardCreated: true,
      });

      await handle(sdkCleanUp, state);

      expect(client.send).not.toHaveBeenCalled();
    });

    it("attempts every deletion even when each one fails", async () => {
      const client = makeClient({
        DeleteAlarmMuteRuleCommand: new Error("mute rule gone"),
        DeleteAlarmsCommand: new Error("alarm gone"),
        DeleteDashboardsCommand: new Error("dashboard gone"),
        StopOTelEnrichmentCommand: new Error("enrichment stuck"),
      });
      const state = makeState({
        client,
        startedEnrichment: true,
        dashboardCreated: true,
      });

      await handle(sdkCleanUp, state);

      expect(console.log).toHaveBeenCalledWith(
        "\tCould not delete the mute rule: mute rule gone",
      );
      expect(console.log).toHaveBeenCalledWith(
        "\tCould not delete the alarm: alarm gone",
      );
      expect(console.log).toHaveBeenCalledWith(
        "\tCould not delete the dashboard: dashboard gone",
      );
      expect(console.log).toHaveBeenCalledWith(
        "\tCould not stop OTel enrichment: enrichment stuck",
      );
    });
  });

  describe("scenario assembly", () => {
    it("runs the eight steps in order", () => {
      const names = myScenario.stepsOrScenarios
        .map((step) => step.name)
        .filter((name) => name.startsWith("sdk"));

      expect(names).toEqual([
        "sdkListMetrics",
        "sdkStartEnrichment",
        "sdkCreateAlarm",
        "sdkContributors",
        "sdkDashboard",
        "sdkMuteRule",
        "sdkCleanUp",
      ]);
    });

    it("suffixes resource names so repeated runs do not collide", () => {
      for (const key of ["alarmName", "dashboardName", "muteRuleName"]) {
        expect(myScenario.state[key]).toMatch(/-\d{4}$/);
      }
    });
  });
});
