# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the OpenTelemetry features of
Amazon CloudWatch: turning on OTel enrichment so that CloudWatch vended metrics are
queryable with PromQL, alarming on a PromQL query, inspecting the individual series
(contributors) that put a PromQL alarm into ALARM, and muting alarm actions on a
schedule.

Note that OTLP metric ingestion is not an AWS SDK operation. To send OpenTelemetry
metrics to CloudWatch you point an OpenTelemetry collector or the AWS Distro for
OpenTelemetry (ADOT) SDK at the CloudWatch OTLP metrics endpoint,
https://monitoring.<region>.amazonaws.com/v1/metrics. See otlp_collector_config.yaml
in this directory for a working collector configuration. The SDK operations shown here
cover everything you do *after* those metrics land in CloudWatch.
"""

# snippet-start:[python.example_code.cloudwatch.otel.imports]
import logging
import time

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)

# snippet-end:[python.example_code.cloudwatch.otel.imports]


# snippet-start:[python.example_code.cloudwatch.CloudWatchOTelWrapper]
class CloudWatchOTelWrapper:
    """Encapsulates the OpenTelemetry-oriented Amazon CloudWatch operations."""

    def __init__(self, cloudwatch_client):
        """
        :param cloudwatch_client: A Boto3 CloudWatch client. The OpenTelemetry
                                  operations are only available on the client
                                  interface, not on the higher-level
                                  ``boto3.resource("cloudwatch")`` interface.
        """
        self.cloudwatch_client = cloudwatch_client

    @classmethod
    def from_client(cls):
        """
        Creates a wrapper backed by a default CloudWatch client.

        :return: A CloudWatchOTelWrapper.
        """
        return cls(boto3.client("cloudwatch"))

    # snippet-end:[python.example_code.cloudwatch.CloudWatchOTelWrapper]

    # snippet-start:[python.example_code.cloudwatch.StartOTelEnrichment]
    def start_otel_enrichment(self):
        """
        Turns on OTel enrichment for the account. Once enrichment is running,
        CloudWatch vended metrics that carry a resource identifier dimension, such as
        the EC2 CPUUtilization metric with its InstanceId dimension, are decorated with
        resource ARN and resource tag labels and become queryable with PromQL.

        Resource tags on telemetry must already be enabled for the account before you
        call this operation.
        """
        try:
            # Boto3 splits the OTel prefix when it converts the StartOTelEnrichment
            # operation name to snake case, so the method is start_o_tel_enrichment.
            self.cloudwatch_client.start_o_tel_enrichment()
            logger.info("Started OTel enrichment for this account.")
        except ClientError:
            logger.exception("Couldn't start OTel enrichment.")
            raise

    # snippet-end:[python.example_code.cloudwatch.StartOTelEnrichment]

    # snippet-start:[python.example_code.cloudwatch.GetOTelEnrichment]
    def get_otel_enrichment_status(self):
        """
        Gets the current OTel enrichment status for the account.

        :return: The status, either 'Running' or 'Stopped'.
        """
        try:
            response = self.cloudwatch_client.get_o_tel_enrichment()
        except ClientError:
            logger.exception("Couldn't get the OTel enrichment status.")
            raise
        else:
            status = response["Status"]
            logger.info("OTel enrichment status is %s.", status)
            return status

    # snippet-end:[python.example_code.cloudwatch.GetOTelEnrichment]

    # snippet-start:[python.example_code.cloudwatch.StopOTelEnrichment]
    def stop_otel_enrichment(self):
        """
        Turns off OTel enrichment for the account. Existing PromQL alarms are not
        deleted, but vended metrics stop being enriched with resource ARN and tag
        labels, so queries that select on those labels stop matching.
        """
        try:
            self.cloudwatch_client.stop_o_tel_enrichment()
            logger.info("Stopped OTel enrichment for this account.")
        except ClientError:
            logger.exception("Couldn't stop OTel enrichment.")
            raise

    # snippet-end:[python.example_code.cloudwatch.StopOTelEnrichment]

    # snippet-start:[python.example_code.cloudwatch.PutMetricAlarm_PromQL]
    def create_promql_alarm(
        self,
        alarm_name,
        query,
        evaluation_interval,
        pending_period=300,
        recovery_period=120,
        description=None,
        alarm_actions=None,
    ):
        """
        Creates an alarm that evaluates a PromQL query.

        A PromQL alarm differs from a classic metric alarm in a few ways. The query can
        match many series at once, and each matching series is tracked separately as a
        *contributor*. Instead of counting breaching periods, you specify durations: a
        contributor moves to ALARM after it breaches continuously for the pending
        period, and back to OK after it stops breaching for the recovery period. A
        PromQL alarm starts in the OK state rather than INSUFFICIENT_DATA.

        The PromQL evaluation parameters live in the EvaluationCriteria union, which is
        mutually exclusive with the classic MetricName and Metrics parameters. When you
        use EvaluationCriteria you must also set EvaluationInterval, and you must not
        set Period, Statistic, Threshold, ComparisonOperator, EvaluationPeriods,
        DatapointsToAlarm, or TreatMissingData.

        :param alarm_name: The name of the alarm. Must be unique within the Region.
        :param query: The PromQL query to evaluate, such as
                      'avg(cpu_utilization_percent) > 80'. The comparison belongs in
                      the query itself; there is no separate threshold parameter.
        :param evaluation_interval: How often, in seconds, to run the query. Valid
                                    values are 10, 20, 30, and any multiple of 60, up
                                    to 3600.
        :param pending_period: How long, in seconds, a contributor must breach
                               continuously before it moves to ALARM.
        :param recovery_period: How long, in seconds, a contributor must stop breaching
                                before it moves back to OK.
        :param description: The description of the alarm.
        :param alarm_actions: A list of ARNs to notify when the alarm fires, such as an
                              Amazon SNS topic.
        """
        promql_criteria = {
            "Query": query,
            "PendingPeriod": pending_period,
            "RecoveryPeriod": recovery_period,
        }
        kwargs = {
            "AlarmName": alarm_name,
            "EvaluationCriteria": {"PromQLCriteria": promql_criteria},
            "EvaluationInterval": evaluation_interval,
        }
        if description is not None:
            kwargs["AlarmDescription"] = description
        if alarm_actions is not None:
            kwargs["AlarmActions"] = alarm_actions

        try:
            self.cloudwatch_client.put_metric_alarm(**kwargs)
            logger.info("Created PromQL alarm %s for query %s.", alarm_name, query)
        except ClientError:
            logger.exception("Couldn't create PromQL alarm %s.", alarm_name)
            raise

    # snippet-end:[python.example_code.cloudwatch.PutMetricAlarm_PromQL]

    # snippet-start:[python.example_code.cloudwatch.DescribeAlarmContributors]
    def describe_alarm_contributors(self, alarm_name):
        """
        Gets the contributors for a PromQL alarm. Each contributor is one series that
        the alarm's query matched, identified by its label set. This is how you find out
        *which* hosts, services, or pods are breaching, rather than only that something
        is.

        :param alarm_name: The name of the PromQL alarm.
        :return: The list of contributors. Each contributor has a ContributorId, a
                 ContributorAttributes map of the labels that identify the series, a
                 StateReason, and the time it last changed state.
        """
        contributors = []
        try:
            next_token = None
            while True:
                kwargs = {"AlarmName": alarm_name}
                if next_token is not None:
                    kwargs["NextToken"] = next_token
                response = self.cloudwatch_client.describe_alarm_contributors(**kwargs)
                contributors.extend(response["AlarmContributors"])
                next_token = response.get("NextToken")
                if not next_token:
                    break
        except ClientError:
            logger.exception("Couldn't get contributors for alarm %s.", alarm_name)
            raise
        else:
            logger.info(
                "Got %s contributors for alarm %s.", len(contributors), alarm_name
            )
            return contributors

    # snippet-end:[python.example_code.cloudwatch.DescribeAlarmContributors]

    # snippet-start:[python.example_code.cloudwatch.PutAlarmMuteRule]
    def put_alarm_mute_rule(
        self,
        name,
        expression,
        duration,
        alarm_names=None,
        timezone=None,
        description=None,
    ):
        """
        Creates or updates an alarm mute rule. While a mute rule is active the targeted
        alarms keep evaluating and keep transitioning between states, but their
        configured actions do not fire. This is the supported way to suppress
        notifications during a known maintenance window instead of disabling alarm
        actions and hoping someone remembers to turn them back on.

        :param name: The name of the mute rule.
        :param expression: When the rule activates. Use a cron expression for a
                           recurring window, such as 'cron(0 2 ? * SUN *)', or an at
                           expression for a one-time window, such as
                           'at(2026-09-05T02:00:00)'.
        :param duration: How long the mute window lasts once it activates, such as
                         '2h' or '30m'.
        :param alarm_names: The names of up to 100 alarms to mute. If omitted, the rule
                            applies to all alarms in the account.
        :param timezone: The time zone the expression is evaluated in, such as
                         'America/Los_Angeles'.
        :param description: The description of the mute rule.
        """
        schedule = {"Expression": expression, "Duration": duration}
        if timezone is not None:
            schedule["Timezone"] = timezone

        kwargs = {"Name": name, "Rule": {"Schedule": schedule}}
        if alarm_names is not None:
            kwargs["MuteTargets"] = {"AlarmNames": alarm_names}
        if description is not None:
            kwargs["Description"] = description

        try:
            self.cloudwatch_client.put_alarm_mute_rule(**kwargs)
            logger.info("Put alarm mute rule %s.", name)
        except ClientError:
            logger.exception("Couldn't put alarm mute rule %s.", name)
            raise

    # snippet-end:[python.example_code.cloudwatch.PutAlarmMuteRule]

    # snippet-start:[python.example_code.cloudwatch.GetAlarmMuteRule]
    def get_alarm_mute_rule(self, name):
        """
        Gets the full configuration of an alarm mute rule, including its schedule, the
        alarms it targets, and whether it is currently SCHEDULED, ACTIVE, or EXPIRED.

        :param name: The name of the mute rule.
        :return: The mute rule.
        """
        try:
            response = self.cloudwatch_client.get_alarm_mute_rule(
                AlarmMuteRuleName=name
            )
        except ClientError:
            logger.exception("Couldn't get alarm mute rule %s.", name)
            raise
        else:
            logger.info("Got alarm mute rule %s.", name)
            return response

    # snippet-end:[python.example_code.cloudwatch.GetAlarmMuteRule]

    # snippet-start:[python.example_code.cloudwatch.ListAlarmMuteRules]
    def list_alarm_mute_rules(self, alarm_name=None, statuses=None):
        """
        Lists alarm mute rules in the account.

        :param alarm_name: When specified, only rules that target this alarm are
                           returned.
        :param statuses: When specified, only rules in these statuses are returned.
                         Valid values are 'SCHEDULED', 'ACTIVE', and 'EXPIRED'.
        :return: The list of mute rule summaries.
        """
        summaries = []
        try:
            next_token = None
            while True:
                kwargs = {}
                if alarm_name is not None:
                    kwargs["AlarmName"] = alarm_name
                if statuses is not None:
                    kwargs["Statuses"] = statuses
                if next_token is not None:
                    kwargs["NextToken"] = next_token
                response = self.cloudwatch_client.list_alarm_mute_rules(**kwargs)
                summaries.extend(response.get("AlarmMuteRuleSummaries", []))
                next_token = response.get("NextToken")
                if not next_token:
                    break
        except ClientError:
            logger.exception("Couldn't list alarm mute rules.")
            raise
        else:
            logger.info("Got %s alarm mute rules.", len(summaries))
            return summaries

    # snippet-end:[python.example_code.cloudwatch.ListAlarmMuteRules]

    # snippet-start:[python.example_code.cloudwatch.DeleteAlarmMuteRule]
    def delete_alarm_mute_rule(self, name):
        """
        Deletes an alarm mute rule.

        :param name: The name of the mute rule.
        """
        try:
            self.cloudwatch_client.delete_alarm_mute_rule(AlarmMuteRuleName=name)
            logger.info("Deleted alarm mute rule %s.", name)
        except ClientError:
            logger.exception("Couldn't delete alarm mute rule %s.", name)
            raise

    # snippet-end:[python.example_code.cloudwatch.DeleteAlarmMuteRule]

    # snippet-start:[python.example_code.cloudwatch.otel.DeleteAlarms]
    def delete_alarms(self, alarm_names):
        """
        Deletes the specified alarms.

        :param alarm_names: The names of the alarms to delete.
        """
        try:
            self.cloudwatch_client.delete_alarms(AlarmNames=alarm_names)
            logger.info("Deleted alarms %s.", ", ".join(alarm_names))
        except ClientError:
            logger.exception("Couldn't delete alarms %s.", ", ".join(alarm_names))
            raise

    # snippet-end:[python.example_code.cloudwatch.otel.DeleteAlarms]


# snippet-start:[python.example_code.cloudwatch.Scenario_OTelMetrics]
def usage_demo():
    """
    Walks through the OpenTelemetry metrics workflow in CloudWatch: turn on
    enrichment, alarm on a PromQL query, inspect the contributors that matched, mute
    the alarm for a maintenance window, then clean up.

    This scenario assumes OpenTelemetry metrics are already flowing into the account,
    either from an OpenTelemetry collector, the CloudWatch agent, or the ADOT SDK.
    """
    print("-" * 88)
    print("Welcome to the Amazon CloudWatch OpenTelemetry metrics demo!")
    print("-" * 88)

    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    cw = CloudWatchOTelWrapper.from_client()

    alarm_name = "doc-example-promql-high-cpu"
    mute_rule_name = "doc-example-maintenance-window"

    print("Checking whether OTel enrichment is on for this account.")
    status = cw.get_otel_enrichment_status()
    started_enrichment_here = False
    if status == "Stopped":
        print("Enrichment is stopped. Starting it so vended metrics accept PromQL.")
        cw.start_otel_enrichment()
        started_enrichment_here = True
    else:
        print("Enrichment is already running. Leaving it alone.")

    query = 'avg by (host_name) (cpu_utilization_percent{service_name="checkout"}) > 80'
    print(f"\nCreating a PromQL alarm on: {query}")
    cw.create_promql_alarm(
        alarm_name,
        query,
        evaluation_interval=30,
        pending_period=300,
        recovery_period=120,
        description="Average CPU over 80% per host for the checkout service.",
    )
    print(
        "The alarm evaluates every 30 seconds. A host moves to ALARM after breaching "
        "for 300 seconds straight, and back to OK after 120 seconds clean."
    )

    print("\nWaiting a moment for the first evaluation, then listing contributors.")
    time.sleep(30)
    contributors = cw.describe_alarm_contributors(alarm_name)
    if not contributors:
        print(
            "No contributors yet. The query matched no series, which usually means "
            "no OTel metrics with these labels have arrived. Send some OTel metrics "
            "through the OTLP endpoint and run this again."
        )
    for contributor in contributors:
        labels = ", ".join(
            f"{key}={value}"
            for key, value in sorted(contributor["ContributorAttributes"].items())
        )
        print(f"  {contributor['ContributorId']}: {labels}")
        print(f"    reason: {contributor['StateReason']}")

    print(f"\nMuting {alarm_name} for a weekly two-hour maintenance window.")
    cw.put_alarm_mute_rule(
        mute_rule_name,
        expression="cron(0 2 ? * SUN *)",
        duration="2h",
        alarm_names=[alarm_name],
        timezone="America/Los_Angeles",
        description="Suppress checkout CPU pages during Sunday patching.",
    )
    rule = cw.get_alarm_mute_rule(mute_rule_name)
    print(f"Mute rule status is {rule.get('Status')}.")
    print(
        "While the window is active the alarm keeps evaluating and still changes "
        "state; only its actions are suppressed."
    )

    print(f"\nMute rules targeting {alarm_name}:")
    for summary in cw.list_alarm_mute_rules(alarm_name=alarm_name):
        print(f"  {summary.get('AlarmMuteRuleArn')} ({summary.get('Status')})")

    print("\nCleaning up.")
    cw.delete_alarm_mute_rule(mute_rule_name)
    cw.delete_alarms([alarm_name])
    if started_enrichment_here:
        print("Stopping OTel enrichment, since this demo started it.")
        cw.stop_otel_enrichment()

    print("\nThanks for watching!")
    print("-" * 88)


# snippet-end:[python.example_code.cloudwatch.Scenario_OTelMetrics]


if __name__ == "__main__":
    usage_demo()
