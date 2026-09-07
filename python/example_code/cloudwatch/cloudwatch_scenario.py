# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon CloudWatch, framed around
the OpenTelemetry (OTel) metrics experience that CloudWatch now supports natively.

CloudWatch ingests OpenTelemetry metrics natively, and this scenario walks through what
you do with them: turning on enrichment so CloudWatch can correlate incoming OTLP
metrics with the resources that produced them, alarming on those metrics with a PromQL
query, and finding out which individual series drove the alarm.

A PromQL alarm works differently from a classic metric alarm. Rather than watching one
metric and counting breaching periods, it evaluates a query that can match many series
at once, and tracks each matching series separately as a contributor.

Note that sending OTLP metrics to CloudWatch is not an AWS SDK operation. Metrics arrive
over the OTLP protocol through the CloudWatch agent, an OpenTelemetry Collector, or an
ADOT SDK. Everything this scenario does is configuration and querying around that
ingestion path.

This scenario performs the following tasks:

1. List metrics and namespaces from Amazon CloudWatch.
2. Start OpenTelemetry enrichment for the account.
3. Explain how OTLP metrics reach CloudWatch.
4. Create an alarm that evaluates a PromQL query.
5. Inspect the contributors to the PromQL alarm.
6. Get metric statistics and chart the metric on a dashboard.
7. Mute the alarm for a maintenance window.
8. Clean up the Amazon CloudWatch resources.
"""

# snippet-start:[python.example_code.cloudwatch.Scenario_Basics]
from collections import Counter
from datetime import datetime, timedelta, timezone
import json
import logging
import random
import sys
import boto3
from botocore.exceptions import ClientError

from cloudwatch_basics import CloudWatchWrapper
from cloudwatch_otel import CloudWatchOTelWrapper

logger = logging.getLogger(__name__)

DEFAULT_QUERY = "avg by (host) (system_cpu_utilization) > 80"

# Valid evaluation intervals are 10, 20, 30, or any multiple of 60 up to 3600 seconds.
EVALUATION_INTERVAL = 60
PENDING_PERIOD = 300
RECOVERY_PERIOD = 120

DASHES = "-" * 80


class CloudWatchScenario:
    """Runs an interactive scenario that shows how to use Amazon CloudWatch."""

    def __init__(self, cloudwatch_wrapper, otel_wrapper):
        """
        :param cloudwatch_wrapper: An object that wraps CloudWatch metric, statistic,
                                   and dashboard actions.
        :param otel_wrapper: An object that wraps CloudWatch OTel enrichment, PromQL
                             alarm, and alarm mute rule actions.
        """
        self.cloudwatch_wrapper = cloudwatch_wrapper
        self.otel_wrapper = otel_wrapper

        # Suffix the resource names so repeated runs do not collide.
        suffix = random.randint(1000, 9999)
        self.alarm_name = f"doc-example-promql-alarm-{suffix}"
        self.dashboard_name = f"doc-example-dashboard-{suffix}"
        self.mute_rule_name = f"doc-example-mute-rule-{suffix}"

        # Tracks whether this run turned enrichment on, so that cleanup only turns off
        # enrichment that this run started.
        self.started_enrichment = False
        self.dashboard_created = False

    def run_scenario(self):
        """Runs the eight steps of the scenario in order."""
        print(DASHES)
        print("Welcome to the Amazon CloudWatch Basics scenario.")
        print(
            "\nCloudWatch now ingests OpenTelemetry metrics natively. This scenario walks\n"
            "through that experience: it turns on OTel enrichment so CloudWatch can\n"
            "correlate incoming OTLP metrics with the resources that produced them, alarms\n"
            "on those metrics with a PromQL query, and shows you which individual series\n"
            "drove the alarm.\n"
            "\nA PromQL alarm works differently from a classic metric alarm. Rather than\n"
            "watching one metric and counting breaching periods, it evaluates a query that\n"
            "can match many series at once, and tracks each one separately as a contributor."
        )
        print(DASHES)

        namespaces = self.list_metrics_and_namespaces()
        self.start_otel_enrichment()
        self.explain_otlp_ingestion()
        self.create_promql_alarm()
        self.inspect_alarm_contributors()
        self.get_statistics_and_chart_metric(namespaces)
        self.mute_alarm_for_maintenance()

    def list_metrics_and_namespaces(self):
        """
        Lists the metrics and namespaces already present in the account, to orient the
        reader before any configuration happens.

        :return: A Counter of namespace to metric count, most common first.
        """
        print("1. List metrics and namespaces")
        print(
            "\nBefore configuring anything, let's see what CloudWatch is already\n"
            "collecting in this account by calling ListMetrics.\n"
        )

        namespaces = Counter()
        metric_count = 0
        for metric in self.cloudwatch_wrapper.list_all_metrics():
            namespaces[metric.namespace] += 1
            metric_count += 1
            # This account may have a very large number of metrics, so stop once we
            # have enough to give the reader a sense of what is there.
            if metric_count >= 500:
                break

        print(f"\tFound {metric_count} metrics across {len(namespaces)} namespaces:")
        for namespace, count in namespaces.most_common(10):
            print(f"\t  {namespace} ({count} metrics)")

        if not namespaces:
            print(
                "\tNo metrics found in this account. The statistics and dashboard steps\n"
                "\tlater on need an existing metric, so they will be skipped."
            )

        print(DASHES)
        return namespaces

    def start_otel_enrichment(self):
        """
        Starts OTel enrichment, but only if it is not already running. Enrichment is
        what makes CloudWatch attach AWS resource context to incoming OTLP metrics.
        """
        print("2. Start OpenTelemetry enrichment")
        print(
            "\nEnrichment is what lets CloudWatch attach AWS resource context to the OTLP\n"
            "metrics you send it. Without it, your metrics arrive as opaque series with no\n"
            "connection to the resources that emitted them.\n"
            "\nWe check the current state first, and only start enrichment if it isn't\n"
            "already on.\n"
        )

        status = self.otel_wrapper.get_otel_enrichment_status()
        print(f"\tEnrichment status: {status}")

        if status != "Running":
            self.otel_wrapper.start_otel_enrichment()
            self.started_enrichment = True
            status = self.otel_wrapper.get_otel_enrichment_status()
            print(f"\tEnrichment status: {status}")
            print(
                "\n\tNote: this run started enrichment, so the cleanup step will stop it\n"
                "\tagain."
            )
        else:
            print(
                "\n\tEnrichment was already running, so we will leave it alone. The cleanup\n"
                "\tstep will not stop it, because other workloads in this account may\n"
                "\tdepend on it."
            )

        print(DASHES)

    @staticmethod
    def explain_otlp_ingestion():
        """
        Explains that OTLP metric ingestion is not an AWS SDK operation. This step makes
        no service call; naming the gap explicitly is the point.
        """
        print("3. Send OTLP metrics to CloudWatch")
        print(
            "\nThis step is not an AWS SDK operation, and that's worth being explicit\n"
            "about. Metrics reach CloudWatch over the OTLP protocol, through the CloudWatch\n"
            "agent, an OpenTelemetry Collector, or an ADOT SDK. There is no PutOTelMetrics\n"
            "API to call.\n"
            "\nPoint your collector at the CloudWatch metrics endpoint, which follows the\n"
            "pattern\n"
            "\thttps://monitoring.<region>.amazonaws.com/v1/metrics\n"
            "\nThe endpoint is HTTP/1.1 only and does not support gRPC, so use an otlphttp\n"
            "exporter rather than otlp. The metrics endpoint signs as 'monitoring'.\n"
            "\nSee otlp_collector_config.yaml in this folder for a working collector\n"
            "configuration."
        )
        print(DASHES)

    def create_promql_alarm(self):
        """Creates an alarm whose evaluation is a PromQL query."""
        print("4. Create a PromQL alarm")
        print(
            "\nNow we alarm on those metrics. The comparison goes inside the query itself:\n"
            "a PromQL alarm has no separate threshold, comparison operator, statistic, or\n"
            "period.\n"
        )

        query = (
            input(
                f"Enter a PromQL query, or press ENTER for [{DEFAULT_QUERY}]: "
            ).strip()
            or DEFAULT_QUERY
        )

        self.otel_wrapper.create_promql_alarm(
            self.alarm_name,
            query,
            EVALUATION_INTERVAL,
            pending_period=PENDING_PERIOD,
            recovery_period=RECOVERY_PERIOD,
            description="A PromQL alarm created by the Boto3 Basics scenario.",
        )

        print(f"\tCreated alarm {self.alarm_name}:")
        print(f"\t  query:              {query}")
        print(f"\t  evaluationInterval: {EVALUATION_INTERVAL} seconds")
        print(f"\t  pendingPeriod:      {PENDING_PERIOD} seconds")
        print(f"\t  recoveryPeriod:     {RECOVERY_PERIOD} seconds")
        print(
            "\n\tA PromQL alarm starts in the OK state rather than INSUFFICIENT_DATA, which\n"
            "\tis another way it differs from a classic alarm."
        )
        print(DASHES)

    def inspect_alarm_contributors(self):
        """
        Shows which individual series the alarm's query matched. This is the step with
        no classic-alarm equivalent.
        """
        print("5. Inspect the alarm's contributors")
        print(
            "\nEach contributor is one series the query matched, identified by its label\n"
            "set. This is how you find out which host is unhealthy rather than only that\n"
            "something is. Classic alarms have no equivalent.\n"
        )

        contributors = self.otel_wrapper.describe_alarm_contributors(self.alarm_name)

        if not contributors:
            print(
                "\tNo contributors yet. The query matched no series, which usually means no\n"
                "\tOTel metrics with these labels have arrived. Once your collector is\n"
                "\tsending data, each matching series appears here with its labels and the\n"
                "\treason it breached."
            )
        else:
            print(f"\tFound {len(contributors)} contributors:")
            for contributor in contributors:
                labels = ", ".join(
                    f"{key}={value}"
                    for key, value in sorted(
                        contributor.get("ContributorAttributes", {}).items()
                    )
                )
                print(f"\t  {contributor['ContributorId']}: {labels}")
                print(f"\t    reason: {contributor.get('StateReason')}")

        print(DASHES)

    def get_statistics_and_chart_metric(self, namespaces):
        """
        Gets statistics for an existing metric and charts it on a dashboard, so the
        reader can see what the alarm is evaluating.

        :param namespaces: The Counter of namespaces discovered in step 1.
        """
        print("6. Get statistics and chart the metric on a dashboard")
        print(
            "\nStatistics and dashboards are how you see what the alarm is evaluating.\n"
        )

        if not namespaces:
            print("\tSkipping statistics and dashboard because no metrics exist yet.")
            print(DASHES)
            return

        namespace = namespaces.most_common(1)[0][0]
        metric = next(
            (
                candidate
                for candidate in self.cloudwatch_wrapper.list_all_metrics()
                if candidate.namespace == namespace
            ),
            None,
        )

        if metric is None:
            print(f"\tNo metrics found in namespace {namespace}, skipping.")
            print(DASHES)
            return

        try:
            stats = self.cloudwatch_wrapper.get_metric_statistics(
                metric.namespace,
                metric.name,
                datetime.now(timezone.utc) - timedelta(days=1),
                datetime.now(timezone.utc),
                3600,
                ["Average", "Maximum"],
            )
            datapoints = stats["Datapoints"]
            print(
                f"\tStatistics for {metric.namespace} {metric.name} over the last day:"
            )
            print(f"\t  Datapoints: {len(datapoints)}")
            for datapoint in datapoints[:3]:
                print(
                    f"\t  {datapoint['Timestamp']} average {datapoint.get('Average')}, "
                    f"maximum {datapoint.get('Maximum')}"
                )
        except ClientError as error:
            print(f"\tCould not get statistics: {error}")

        try:
            body = self.build_dashboard_body(metric)
            messages = self.cloudwatch_wrapper.put_dashboard(self.dashboard_name, body)
            self.dashboard_created = True
            for message in messages:
                print(f"\tDashboard validation message: {message.get('Message')}")
            print(f"\tCreated dashboard {self.dashboard_name}.")

            stored = self.cloudwatch_wrapper.get_dashboard(self.dashboard_name)
            print(
                f"\tRead the dashboard back, {len(stored)} characters of widget JSON."
            )
        except ClientError as error:
            print(f"\tCould not create the dashboard: {error}")

        print(DASHES)

    @staticmethod
    def build_dashboard_body(metric):
        """
        Builds a single-widget dashboard body that charts the given metric.

        :param metric: A Boto3 CloudWatch Metric resource.
        :return: The dashboard body, as a JSON string.
        """
        metric_spec = [metric.namespace, metric.name]
        for dimension in metric.dimensions or []:
            metric_spec.extend([dimension["Name"], dimension["Value"]])

        return json.dumps(
            {
                "widgets": [
                    {
                        "type": "text",
                        "x": 0,
                        "y": 0,
                        "width": 24,
                        "height": 2,
                        "properties": {
                            "markdown": "This dashboard was created programmatically "
                            "by an AWS SDK code example."
                        },
                    },
                    {
                        "type": "metric",
                        "x": 0,
                        "y": 2,
                        "width": 12,
                        "height": 6,
                        "properties": {
                            "metrics": [metric_spec],
                            "view": "timeSeries",
                            "stat": "Average",
                            "period": 300,
                            "title": metric.name,
                        },
                    },
                ]
            }
        )

    def mute_alarm_for_maintenance(self):
        """
        Creates a mute rule so the alarm's actions are suppressed during a maintenance
        window, then reads it back and finds it in the account's rules.
        """
        print("7. Mute the alarm for a maintenance window")
        print(
            "\nWhile a mute rule is active the targeted alarms keep evaluating and keep\n"
            "changing state, but their actions do not fire. This is the supported way to\n"
            "suppress notifications during planned maintenance, instead of disabling alarm\n"
            "actions and hoping someone remembers to turn them back on.\n"
        )

        # The expression is a five-field cron expression,
        # cron(Minutes Hours Day-of-month Month Day-of-week). Note that this is five
        # fields, not the six that Amazon EventBridge uses. For a one-time window, use
        # at(yyyy-MM-ddThh:mm), with no seconds. The duration is an ISO 8601 duration
        # from PT1M to P15D, so PT2H rather than 2h.
        expression = "cron(0 2 * * SUN)"
        duration = "PT2H"
        tz = "America/Los_Angeles"

        self.otel_wrapper.put_alarm_mute_rule(
            self.mute_rule_name,
            expression,
            duration,
            alarm_names=[self.alarm_name],
            timezone=tz,
            description="A mute rule created by the Boto3 Basics scenario.",
        )

        print(f"\tCreated mute rule {self.mute_rule_name}:")
        print(f"\t  schedule: {expression} for {duration}")
        print(f"\t  timezone: {tz}")
        print(f"\t  targets:  {self.alarm_name}")
        print(
            "\n\tNote the two formats here. The expression is a five-field cron expression,\n"
            "\tfive rather than the six Amazon EventBridge uses. The duration is an ISO 8601\n"
            "\tduration, so 'PT2H' and not '2h'.\n"
            "\n\tAlso note that MuteTargets is set explicitly. If you leave it out, the rule\n"
            "\tapplies to every alarm in the account."
        )

        mute_rule = self.otel_wrapper.get_alarm_mute_rule(self.mute_rule_name)
        print(
            f"\tRead the rule back: status {mute_rule.get('Status')}, "
            f"mute type {mute_rule.get('MuteType')}."
        )

        summaries = self.otel_wrapper.list_alarm_mute_rules(alarm_name=self.alarm_name)
        print(f"\tFound {len(summaries)} mute rules targeting this alarm.")
        # Mute rule summaries carry no name field, only an ARN, so match on the ARN
        # suffix.
        for summary in summaries:
            arn = summary.get("AlarmMuteRuleArn", "")
            if arn.endswith(f"/{self.mute_rule_name}") or arn.endswith(
                f":{self.mute_rule_name}"
            ):
                print(f"\t  matched by ARN: {arn} ({summary.get('Status')})")
                break

        print(DASHES)

    def clean_up(self):
        """
        Deletes the resources the scenario created. Each deletion is attempted
        independently so that one failure does not leave the remaining resources behind.
        """
        print("8. Clean up")
        answer = input("Delete the resources this scenario created? (y/n) ")
        if answer.strip().lower() != "y":
            print(
                "\tSkipping cleanup. Note that the alarm, dashboard, and mute rule are\n"
                "\tstill in your account, and enrichment may still be running."
            )
            print(DASHES)
            return

        try:
            self.otel_wrapper.delete_alarm_mute_rule(self.mute_rule_name)
            print(f"\tDeleted mute rule {self.mute_rule_name}.")
        except ClientError as error:
            print(f"\tCould not delete the mute rule: {error}")

        try:
            self.otel_wrapper.delete_alarms([self.alarm_name])
            print(f"\tDeleted alarm {self.alarm_name}.")
        except ClientError as error:
            print(f"\tCould not delete the alarm: {error}")

        if self.dashboard_created:
            try:
                self.cloudwatch_wrapper.delete_dashboards([self.dashboard_name])
                print(f"\tDeleted dashboard {self.dashboard_name}.")
            except ClientError as error:
                print(f"\tCould not delete the dashboard: {error}")

        if self.started_enrichment:
            try:
                self.otel_wrapper.stop_otel_enrichment()
                print("\tStopped OTel enrichment, because this run started it.")
            except ClientError as error:
                print(f"\tCould not stop OTel enrichment: {error}")
        else:
            print(
                "\tLeft OTel enrichment running, because it was already on before this run."
            )

        print(DASHES)


def main():
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    scenario = CloudWatchScenario(
        CloudWatchWrapper(boto3.resource("cloudwatch")),
        CloudWatchOTelWrapper(boto3.client("cloudwatch")),
    )
    try:
        scenario.run_scenario()
    except Exception:  # pylint: disable=broad-except
        logging.exception("Something went wrong with the scenario.")
    finally:
        scenario.clean_up()

    print("This concludes the Amazon CloudWatch Basics scenario.")


if __name__ == "__main__":
    sys.exit(main())


# snippet-end:[python.example_code.cloudwatch.Scenario_Basics]
