# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Unit tests for cloudwatch_otel.py
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from cloudwatch_otel import CloudWatchOTelWrapper


def make_wrapper(make_stubber):
    cloudwatch_client = boto3.client("cloudwatch", region_name="us-east-1")
    return CloudWatchOTelWrapper(cloudwatch_client), make_stubber(cloudwatch_client)


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_start_otel_enrichment(make_stubber, error_code):
    cw_wrapper, stubber = make_wrapper(make_stubber)

    stubber.stub_start_otel_enrichment(error_code=error_code)

    if error_code is None:
        cw_wrapper.start_otel_enrichment()
    else:
        with pytest.raises(ClientError) as exc_info:
            cw_wrapper.start_otel_enrichment()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_otel_enrichment_status(make_stubber, error_code):
    cw_wrapper, stubber = make_wrapper(make_stubber)
    status = "Running"

    stubber.stub_get_otel_enrichment(status, error_code=error_code)

    if error_code is None:
        assert cw_wrapper.get_otel_enrichment_status() == status
    else:
        with pytest.raises(ClientError) as exc_info:
            cw_wrapper.get_otel_enrichment_status()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_stop_otel_enrichment(make_stubber, error_code):
    cw_wrapper, stubber = make_wrapper(make_stubber)

    stubber.stub_stop_otel_enrichment(error_code=error_code)

    if error_code is None:
        cw_wrapper.stop_otel_enrichment()
    else:
        with pytest.raises(ClientError) as exc_info:
            cw_wrapper.stop_otel_enrichment()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create_promql_alarm(make_stubber, error_code):
    cw_wrapper, stubber = make_wrapper(make_stubber)
    alarm_name = "test-promql-alarm"
    query = 'avg by (host_name) (cpu_utilization_percent{service_name="checkout"}) > 80'
    evaluation_interval = 30
    pending_period = 300
    recovery_period = 120
    description = "test-description"

    stubber.stub_put_promql_metric_alarm(
        alarm_name,
        query,
        evaluation_interval,
        pending_period,
        recovery_period,
        description=description,
        error_code=error_code,
    )

    if error_code is None:
        cw_wrapper.create_promql_alarm(
            alarm_name,
            query,
            evaluation_interval,
            pending_period=pending_period,
            recovery_period=recovery_period,
            description=description,
        )
    else:
        with pytest.raises(ClientError) as exc_info:
            cw_wrapper.create_promql_alarm(
                alarm_name,
                query,
                evaluation_interval,
                pending_period=pending_period,
                recovery_period=recovery_period,
                description=description,
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_describe_alarm_contributors(make_stubber, error_code):
    cw_wrapper, stubber = make_wrapper(make_stubber)
    alarm_name = "test-promql-alarm"
    contributors = [
        {
            "ContributorId": f"contributor-{index}",
            "ContributorAttributes": {"host_name": f"host-{index}"},
            "StateReason": "Threshold Crossed",
        }
        for index in range(3)
    ]

    stubber.stub_describe_alarm_contributors(
        alarm_name, contributors, error_code=error_code
    )

    if error_code is None:
        assert cw_wrapper.describe_alarm_contributors(alarm_name) == contributors
    else:
        with pytest.raises(ClientError) as exc_info:
            cw_wrapper.describe_alarm_contributors(alarm_name)
        assert exc_info.value.response["Error"]["Code"] == error_code


def test_describe_alarm_contributors_paginates(make_stubber):
    """The wrapper follows NextToken until the service stops returning one."""
    cw_wrapper, stubber = make_wrapper(make_stubber)
    alarm_name = "test-promql-alarm"
    first_page = [
        {
            "ContributorId": "contributor-0",
            "ContributorAttributes": {"host_name": "host-0"},
            "StateReason": "Threshold Crossed",
        }
    ]
    second_page = [
        {
            "ContributorId": "contributor-1",
            "ContributorAttributes": {"host_name": "host-1"},
            "StateReason": "Threshold Crossed",
        }
    ]

    stubber.stub_describe_alarm_contributors(
        alarm_name, first_page, next_token_out="token-1"
    )
    stubber.stub_describe_alarm_contributors(
        alarm_name, second_page, next_token="token-1"
    )

    assert cw_wrapper.describe_alarm_contributors(alarm_name) == (
        first_page + second_page
    )


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_put_alarm_mute_rule(make_stubber, error_code):
    cw_wrapper, stubber = make_wrapper(make_stubber)
    name = "test-mute-rule"
    expression = "cron(0 2 * * SUN)"
    duration = "PT2H"
    alarm_names = ["test-promql-alarm"]
    timezone = "America/Los_Angeles"
    description = "test-description"

    stubber.stub_put_alarm_mute_rule(
        name,
        expression,
        duration,
        alarm_names=alarm_names,
        timezone=timezone,
        description=description,
        error_code=error_code,
    )

    if error_code is None:
        cw_wrapper.put_alarm_mute_rule(
            name,
            expression,
            duration,
            alarm_names=alarm_names,
            timezone=timezone,
            description=description,
        )
    else:
        with pytest.raises(ClientError) as exc_info:
            cw_wrapper.put_alarm_mute_rule(
                name,
                expression,
                duration,
                alarm_names=alarm_names,
                timezone=timezone,
                description=description,
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_alarm_mute_rule(make_stubber, error_code):
    cw_wrapper, stubber = make_wrapper(make_stubber)
    name = "test-mute-rule"
    status = "SCHEDULED"

    stubber.stub_get_alarm_mute_rule(name, status, error_code=error_code)

    if error_code is None:
        assert cw_wrapper.get_alarm_mute_rule(name)["Status"] == status
    else:
        with pytest.raises(ClientError) as exc_info:
            cw_wrapper.get_alarm_mute_rule(name)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_alarm_mute_rules(make_stubber, error_code):
    cw_wrapper, stubber = make_wrapper(make_stubber)
    alarm_name = "test-promql-alarm"
    summaries = [
        {
            "AlarmMuteRuleArn": f"arn:aws:cloudwatch:us-east-1:123456789012:mute-rule/rule-{index}",
            "Status": "SCHEDULED",
        }
        for index in range(2)
    ]

    stubber.stub_list_alarm_mute_rules(
        summaries, alarm_name=alarm_name, error_code=error_code
    )

    if error_code is None:
        assert cw_wrapper.list_alarm_mute_rules(alarm_name=alarm_name) == summaries
    else:
        with pytest.raises(ClientError) as exc_info:
            cw_wrapper.list_alarm_mute_rules(alarm_name=alarm_name)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_alarm_mute_rule(make_stubber, error_code):
    cw_wrapper, stubber = make_wrapper(make_stubber)
    name = "test-mute-rule"

    stubber.stub_delete_alarm_mute_rule(name, error_code=error_code)

    if error_code is None:
        cw_wrapper.delete_alarm_mute_rule(name)
    else:
        with pytest.raises(ClientError) as exc_info:
            cw_wrapper.delete_alarm_mute_rule(name)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_alarms(make_stubber, error_code):
    cw_wrapper, stubber = make_wrapper(make_stubber)
    alarm_names = ["test-promql-alarm"]

    stubber.stub_delete_alarms_by_name(alarm_names, error_code=error_code)

    if error_code is None:
        cw_wrapper.delete_alarms(alarm_names)
    else:
        with pytest.raises(ClientError) as exc_info:
            cw_wrapper.delete_alarms(alarm_names)
        assert exc_info.value.response["Error"]["Code"] == error_code
