# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Unit tests for cloudwatch_scenario.py.

The scenario's own logic is what these tests cover: which steps run in which order,
which resources cleanup is allowed to touch, and the shape of the dashboard body it
builds. The service calls themselves are covered by test_cloudwatch_basics.py and
test_cloudwatch_otel.py, so the two wrappers are mocked here.
"""

import json
from unittest.mock import DEFAULT, MagicMock
import pytest
from botocore.exceptions import ClientError

from cloudwatch_scenario import (
    DEFAULT_QUERY,
    EVALUATION_INTERVAL,
    PENDING_PERIOD,
    RECOVERY_PERIOD,
    CloudWatchScenario,
)


def make_metric(namespace, name, dimensions=None):
    """Builds a stand-in for a Boto3 CloudWatch Metric resource."""
    metric = MagicMock(namespace=namespace, dimensions=dimensions)
    # 'name' is a MagicMock constructor keyword, so it has to be set afterward.
    metric.name = name
    return metric


def make_scenario(status="Stopped", metrics=None, region="us-west-2"):
    """
    Builds a scenario with both wrappers mocked.

    :param status: The enrichment status the OTel wrapper reports.
    :param metrics: The metrics the CloudWatch wrapper lists.
    :param region: The region the OTel wrapper's client reports.
    :return: A tuple of the scenario, the CloudWatch wrapper, and the OTel wrapper.
    """
    cloudwatch_wrapper = MagicMock()
    cloudwatch_wrapper.list_all_metrics.return_value = metrics or []
    cloudwatch_wrapper.put_dashboard.return_value = []
    cloudwatch_wrapper.get_dashboard.return_value = "{}"

    otel_wrapper = MagicMock()
    otel_wrapper.get_otel_enrichment_status.return_value = status
    otel_wrapper.cloudwatch_client.meta.region_name = region
    otel_wrapper.describe_alarm_contributors.return_value = []
    otel_wrapper.get_alarm_mute_rule.return_value = {}
    otel_wrapper.list_alarm_mute_rules.return_value = []

    scenario = CloudWatchScenario(cloudwatch_wrapper, otel_wrapper)
    return scenario, cloudwatch_wrapper, otel_wrapper


def client_error(operation):
    """Builds a ClientError for the given operation."""
    return ClientError(
        {"Error": {"Code": "TestException", "Message": "Test error."}}, operation
    )


def test_resource_names_are_suffixed():
    """Repeated runs must not collide on resource names."""
    scenario, _, _ = make_scenario()
    other, _, _ = make_scenario()

    for name in (scenario.alarm_name, scenario.dashboard_name, scenario.mute_rule_name):
        suffix = name.rsplit("-", 1)[1]
        assert suffix.isdigit()
        assert 1000 <= int(suffix) <= 9999

    # Not a strict guarantee for any single pair, but the names must at least be
    # derived from the same suffix within one run.
    assert (
        scenario.alarm_name.rsplit("-", 1)[1]
        == scenario.dashboard_name.rsplit("-", 1)[1]
    )
    assert other.alarm_name.rsplit("-", 1)[1] == other.mute_rule_name.rsplit("-", 1)[1]


def test_start_otel_enrichment_when_stopped():
    """Enrichment that isn't running gets started, and the run records that it did."""
    scenario, _, otel_wrapper = make_scenario(status="Stopped")

    scenario.start_otel_enrichment()

    otel_wrapper.start_otel_enrichment.assert_called_once_with()
    assert scenario.started_enrichment is True


def test_start_otel_enrichment_when_already_running():
    """
    Enrichment that is already running is left alone, because other workloads in the
    account may depend on it.
    """
    scenario, _, otel_wrapper = make_scenario(status="Running")

    scenario.start_otel_enrichment()

    otel_wrapper.start_otel_enrichment.assert_not_called()
    assert scenario.started_enrichment is False


@pytest.mark.parametrize("status", ["Stopped", "Starting", "Stopping", None])
def test_start_otel_enrichment_for_non_running_statuses(status):
    """Any status other than Running is treated as 'not on yet'."""
    scenario, _, otel_wrapper = make_scenario(status=status)

    scenario.start_otel_enrichment()

    otel_wrapper.start_otel_enrichment.assert_called_once_with()
    assert scenario.started_enrichment is True


def test_list_metrics_and_namespaces_counts_by_namespace():
    metrics = [
        make_metric("AWS/EC2", "CPUUtilization"),
        make_metric("AWS/EC2", "NetworkIn"),
        make_metric("AWS/S3", "BucketSizeBytes"),
    ]
    scenario, cloudwatch_wrapper, _ = make_scenario(metrics=metrics)

    namespaces = scenario.list_metrics_and_namespaces()

    cloudwatch_wrapper.list_all_metrics.assert_called_once_with()
    assert namespaces["AWS/EC2"] == 2
    assert namespaces["AWS/S3"] == 1
    assert namespaces.most_common(1)[0] == ("AWS/EC2", 2)


def test_list_metrics_and_namespaces_stops_at_500():
    """An account can hold far more metrics than the scenario needs to enumerate."""
    metrics = [make_metric("AWS/EC2", f"metric-{index}") for index in range(600)]
    scenario, _, _ = make_scenario(metrics=metrics)

    namespaces = scenario.list_metrics_and_namespaces()

    assert sum(namespaces.values()) == 500


def test_list_metrics_and_namespaces_with_no_metrics():
    scenario, _, _ = make_scenario(metrics=[])

    assert scenario.list_metrics_and_namespaces() == {}


def test_create_promql_alarm_uses_default_query(input_mocker):
    """Pressing ENTER at the prompt falls back to the documented default query."""
    scenario, _, otel_wrapper = make_scenario()
    input_mocker.mock_answers([""])

    scenario.create_promql_alarm()

    otel_wrapper.create_promql_alarm.assert_called_once_with(
        scenario.alarm_name,
        DEFAULT_QUERY,
        EVALUATION_INTERVAL,
        pending_period=PENDING_PERIOD,
        recovery_period=RECOVERY_PERIOD,
        description="A PromQL alarm created by the Boto3 Basics scenario.",
    )


def test_create_promql_alarm_uses_entered_query(input_mocker):
    scenario, _, otel_wrapper = make_scenario()
    input_mocker.mock_answers(["  up == 0  "])

    scenario.create_promql_alarm()

    assert otel_wrapper.create_promql_alarm.call_args.args[1] == "up == 0"


def test_inspect_alarm_contributors_with_contributors(capsys):
    scenario, _, otel_wrapper = make_scenario()
    otel_wrapper.describe_alarm_contributors.return_value = [
        {
            "ContributorId": "contributor-1",
            "ContributorAttributes": {"host": "web-1", "az": "us-west-2a"},
            "StateReason": "Threshold crossed.",
        }
    ]

    scenario.inspect_alarm_contributors()

    otel_wrapper.describe_alarm_contributors.assert_called_once_with(
        scenario.alarm_name
    )
    output = capsys.readouterr().out
    assert "Found 1 contributors" in output
    # Labels are sorted so repeated runs print them the same way.
    assert "az=us-west-2a, host=web-1" in output
    assert "Threshold crossed." in output


def test_inspect_alarm_contributors_with_none(capsys):
    """No contributors is the expected state when no OTel metrics have arrived."""
    scenario, _, _ = make_scenario()

    scenario.inspect_alarm_contributors()

    assert "No contributors yet" in capsys.readouterr().out


def test_build_dashboard_body_names_its_region():
    """
    A metric widget must name its region. A dashboard can chart metrics from several
    regions, so the widget cannot inherit one.
    """
    metric = make_metric("AWS/EC2", "CPUUtilization")

    body = json.loads(CloudWatchScenario.build_dashboard_body(metric, "eu-central-1"))

    widget = next(w for w in body["widgets"] if w["type"] == "metric")
    assert widget["properties"]["region"] == "eu-central-1"
    assert widget["properties"]["metrics"] == [["AWS/EC2", "CPUUtilization"]]
    assert widget["properties"]["title"] == "CPUUtilization"


def test_build_dashboard_body_flattens_dimensions():
    """Dimensions go into the metric spec as alternating name and value entries."""
    metric = make_metric(
        "AWS/EC2",
        "CPUUtilization",
        dimensions=[
            {"Name": "InstanceId", "Value": "i-abc123"},
            {"Name": "InstanceType", "Value": "t3.micro"},
        ],
    )

    body = json.loads(CloudWatchScenario.build_dashboard_body(metric, "us-east-1"))

    widget = next(w for w in body["widgets"] if w["type"] == "metric")
    assert widget["properties"]["metrics"] == [
        [
            "AWS/EC2",
            "CPUUtilization",
            "InstanceId",
            "i-abc123",
            "InstanceType",
            "t3.micro",
        ]
    ]


def test_build_dashboard_body_with_no_dimensions():
    """A metric with dimensions of None must not raise."""
    metric = make_metric("AWS/EC2", "CPUUtilization", dimensions=None)

    body = json.loads(CloudWatchScenario.build_dashboard_body(metric, "us-east-1"))

    widget = next(w for w in body["widgets"] if w["type"] == "metric")
    assert widget["properties"]["metrics"] == [["AWS/EC2", "CPUUtilization"]]


def test_get_statistics_and_chart_metric_charts_busiest_namespace():
    metrics = [
        make_metric("AWS/S3", "BucketSizeBytes"),
        make_metric("AWS/EC2", "CPUUtilization"),
        make_metric("AWS/EC2", "NetworkIn"),
    ]
    scenario, cloudwatch_wrapper, _ = make_scenario(metrics=metrics)
    namespaces = scenario.list_metrics_and_namespaces()

    scenario.get_statistics_and_chart_metric(namespaces)

    # AWS/EC2 has the most metrics, so the charted metric comes from there.
    stats_args = cloudwatch_wrapper.get_metric_statistics.call_args.args
    assert stats_args[0] == "AWS/EC2"
    assert stats_args[1] == "CPUUtilization"

    body = json.loads(cloudwatch_wrapper.put_dashboard.call_args.args[1])
    widget = next(w for w in body["widgets"] if w["type"] == "metric")
    assert widget["properties"]["metrics"] == [["AWS/EC2", "CPUUtilization"]]
    assert scenario.dashboard_created is True


def test_get_statistics_and_chart_metric_skips_with_no_namespaces():
    """The statistics and dashboard steps need an existing metric."""
    scenario, cloudwatch_wrapper, _ = make_scenario()

    scenario.get_statistics_and_chart_metric({})

    cloudwatch_wrapper.get_metric_statistics.assert_not_called()
    cloudwatch_wrapper.put_dashboard.assert_not_called()
    assert scenario.dashboard_created is False


def test_get_statistics_and_chart_metric_survives_statistics_failure():
    """A statistics failure must not stop the dashboard from being created."""
    metrics = [make_metric("AWS/EC2", "CPUUtilization")]
    scenario, cloudwatch_wrapper, _ = make_scenario(metrics=metrics)
    cloudwatch_wrapper.get_metric_statistics.side_effect = client_error(
        "GetMetricStatistics"
    )
    namespaces = scenario.list_metrics_and_namespaces()

    scenario.get_statistics_and_chart_metric(namespaces)

    cloudwatch_wrapper.put_dashboard.assert_called_once()
    assert scenario.dashboard_created is True


def test_get_statistics_and_chart_metric_survives_dashboard_failure():
    """
    A dashboard that could not be created must not be recorded as created, or cleanup
    would try to delete a dashboard that isn't there.
    """
    metrics = [make_metric("AWS/EC2", "CPUUtilization")]
    scenario, cloudwatch_wrapper, _ = make_scenario(metrics=metrics)
    cloudwatch_wrapper.put_dashboard.side_effect = client_error("PutDashboard")
    namespaces = scenario.list_metrics_and_namespaces()

    scenario.get_statistics_and_chart_metric(namespaces)

    assert scenario.dashboard_created is False


def test_mute_alarm_for_maintenance_targets_only_this_alarm():
    """
    MuteTargets has to be set explicitly. Leaving it out mutes every alarm in the
    account.
    """
    scenario, _, otel_wrapper = make_scenario()

    scenario.mute_alarm_for_maintenance()

    args, kwargs = otel_wrapper.put_alarm_mute_rule.call_args
    assert args[0] == scenario.mute_rule_name
    assert kwargs["alarm_names"] == [scenario.alarm_name]
    # A five-field cron expression, and an ISO 8601 duration.
    assert args[1] == "cron(0 2 * * SUN)"
    assert args[2] == "PT2H"
    otel_wrapper.get_alarm_mute_rule.assert_called_once_with(scenario.mute_rule_name)
    otel_wrapper.list_alarm_mute_rules.assert_called_once_with(
        alarm_name=scenario.alarm_name
    )


def test_mute_alarm_for_maintenance_matches_summary_by_arn(capsys):
    """Mute rule summaries carry no name field, so the rule is matched by ARN suffix."""
    scenario, _, otel_wrapper = make_scenario()
    arn = f"arn:aws:cloudwatch:us-west-2:123456789012:alarm-mute-rule/{scenario.mute_rule_name}"
    otel_wrapper.list_alarm_mute_rules.return_value = [
        {
            "AlarmMuteRuleArn": "arn:aws:cloudwatch:us-west-2:1:rule/other",
            "Status": "A",
        },
        {"AlarmMuteRuleArn": arn, "Status": "Enabled"},
    ]

    scenario.mute_alarm_for_maintenance()

    output = capsys.readouterr().out
    assert f"matched by ARN: {arn} (Enabled)" in output


def test_clean_up_deletes_what_the_run_created(input_mocker):
    scenario, cloudwatch_wrapper, otel_wrapper = make_scenario()
    scenario.started_enrichment = True
    scenario.dashboard_created = True
    input_mocker.mock_answers(["y"])

    scenario.clean_up()

    otel_wrapper.delete_alarm_mute_rule.assert_called_once_with(scenario.mute_rule_name)
    otel_wrapper.delete_alarms.assert_called_once_with([scenario.alarm_name])
    cloudwatch_wrapper.delete_dashboards.assert_called_once_with(
        [scenario.dashboard_name]
    )
    otel_wrapper.stop_otel_enrichment.assert_called_once_with()


def test_clean_up_leaves_enrichment_it_did_not_start(input_mocker):
    """
    Enrichment that was already on before this run stays on. Other workloads in the
    account may depend on it.
    """
    scenario, _, otel_wrapper = make_scenario(status="Running")
    scenario.start_otel_enrichment()
    input_mocker.mock_answers(["y"])

    scenario.clean_up()

    otel_wrapper.stop_otel_enrichment.assert_not_called()


def test_clean_up_skips_dashboard_it_did_not_create(input_mocker):
    scenario, cloudwatch_wrapper, _ = make_scenario()
    input_mocker.mock_answers(["y"])

    scenario.clean_up()

    cloudwatch_wrapper.delete_dashboards.assert_not_called()


@pytest.mark.parametrize("answer", ["n", "N", "no", "", "  "])
def test_clean_up_declined(input_mocker, answer):
    """Anything other than 'y' leaves the resources in place."""
    scenario, cloudwatch_wrapper, otel_wrapper = make_scenario()
    scenario.started_enrichment = True
    scenario.dashboard_created = True
    input_mocker.mock_answers([answer])

    scenario.clean_up()

    otel_wrapper.delete_alarm_mute_rule.assert_not_called()
    otel_wrapper.delete_alarms.assert_not_called()
    cloudwatch_wrapper.delete_dashboards.assert_not_called()
    otel_wrapper.stop_otel_enrichment.assert_not_called()


@pytest.mark.parametrize("answer", ["y", "Y", " y "])
def test_clean_up_accepted(input_mocker, answer):
    scenario, _, otel_wrapper = make_scenario()
    input_mocker.mock_answers([answer])

    scenario.clean_up()

    otel_wrapper.delete_alarms.assert_called_once_with([scenario.alarm_name])


def test_clean_up_continues_after_a_failure(input_mocker, capsys):
    """
    Each deletion is attempted independently, so one failure does not leave the
    remaining resources behind.
    """
    scenario, cloudwatch_wrapper, otel_wrapper = make_scenario()
    scenario.started_enrichment = True
    scenario.dashboard_created = True
    otel_wrapper.delete_alarm_mute_rule.side_effect = client_error(
        "DeleteAlarmMuteRule"
    )
    otel_wrapper.delete_alarms.side_effect = client_error("DeleteAlarms")
    cloudwatch_wrapper.delete_dashboards.side_effect = client_error("DeleteDashboards")
    otel_wrapper.stop_otel_enrichment.side_effect = client_error("StopOTelEnrichment")
    input_mocker.mock_answers(["y"])

    scenario.clean_up()

    output = capsys.readouterr().out
    assert "Could not delete the mute rule" in output
    assert "Could not delete the alarm" in output
    assert "Could not delete the dashboard" in output
    assert "Could not stop OTel enrichment" in output
    # Every deletion was still attempted.
    otel_wrapper.delete_alarms.assert_called_once()
    cloudwatch_wrapper.delete_dashboards.assert_called_once()
    otel_wrapper.stop_otel_enrichment.assert_called_once()


def test_run_scenario_runs_the_steps_in_order(input_mocker):
    scenario, _, _ = make_scenario(metrics=[make_metric("AWS/EC2", "CPUUtilization")])
    step_names = [
        "list_metrics_and_namespaces",
        "start_otel_enrichment",
        "explain_otlp_ingestion",
        "create_promql_alarm",
        "inspect_alarm_contributors",
        "get_statistics_and_chart_metric",
        "mute_alarm_for_maintenance",
    ]
    calls = []
    mocks = {}
    for step in step_names:
        # Returning DEFAULT keeps each mock's own return_value, so step 6 still
        # receives what step 1 returned.
        def record(*_args, _step=step, **_kwargs):
            calls.append(_step)
            return DEFAULT

        mocks[step] = MagicMock(side_effect=record)
        setattr(scenario, step, mocks[step])
    input_mocker.mock_answers([""])

    scenario.run_scenario()

    assert calls == step_names
    # Step 6 charts a metric from the namespaces step 1 discovered.
    mocks["get_statistics_and_chart_metric"].assert_called_once_with(
        mocks["list_metrics_and_namespaces"].return_value
    )


def test_main_cleans_up_after_a_failure(monkeypatch, input_mocker):
    """
    run_scenario does not call clean_up itself. main does, in a finally block, so a
    failure partway through still releases what was created.
    """
    import cloudwatch_scenario

    scenario = MagicMock()
    scenario.run_scenario.side_effect = RuntimeError("Step 4 blew up.")
    monkeypatch.setattr(
        cloudwatch_scenario, "CloudWatchScenario", MagicMock(return_value=scenario)
    )
    monkeypatch.setattr(cloudwatch_scenario, "boto3", MagicMock())

    cloudwatch_scenario.main()

    scenario.clean_up.assert_called_once_with()


def test_main_cleans_up_on_success(monkeypatch):
    import cloudwatch_scenario

    scenario = MagicMock()
    monkeypatch.setattr(
        cloudwatch_scenario, "CloudWatchScenario", MagicMock(return_value=scenario)
    )
    monkeypatch.setattr(cloudwatch_scenario, "boto3", MagicMock())

    cloudwatch_scenario.main()

    scenario.run_scenario.assert_called_once_with()
    scenario.clean_up.assert_called_once_with()
