# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for the Amazon Inspector wrapper and Hello example.

These tests run against live AWS resources. They make read-only calls, plus a
create/delete of a single findings filter that is always cleaned up. They do
not enable or disable Inspector.

WARNING: Running these tests may incur AWS charges.
"""

import time

import boto3
import pytest

from inspector2_hello import hello_inspector
from inspector_wrapper import Inspector2Wrapper


@pytest.fixture
def wrapper():
    return Inspector2Wrapper.from_client()


@pytest.mark.integ
def test_hello_inspector(capsys):
    hello_inspector(boto3.client("inspector2"))
    assert "Hello Amazon Inspector!" in capsys.readouterr().out


@pytest.mark.integ
def test_get_account_status(wrapper):
    accounts = wrapper.get_account_status()
    assert isinstance(accounts, list)
    assert accounts and "state" in accounts[0]


@pytest.mark.integ
def test_list_coverage_and_statistics(wrapper):
    assert isinstance(wrapper.list_coverage(max_results=5), list)
    stats = wrapper.list_coverage_statistics()
    assert "totalCounts" in stats
    grouped = wrapper.list_coverage_statistics(group_by="RESOURCE_TYPE")
    assert isinstance(grouped["countsByGroup"], list)


@pytest.mark.integ
def test_list_findings_and_details(wrapper):
    findings = wrapper.list_findings(max_results=10)
    assert isinstance(findings, list)
    assert len(findings) <= 10
    if findings:
        details = wrapper.batch_get_finding_details([findings[0]["findingArn"]])
        assert isinstance(details, list)


@pytest.mark.integ
def test_list_usage_totals(wrapper):
    assert isinstance(wrapper.list_usage_totals(max_results=10), list)


@pytest.mark.integ
def test_filter_lifecycle(wrapper):
    filter_name = f"integ-suppress-low-{int(time.time())}"
    filter_arn = wrapper.create_low_severity_filter(
        filter_name, "Integration test filter"
    )
    assert filter_arn
    try:
        arns = [f["arn"] for f in wrapper.list_filters(max_results=10)]
        assert isinstance(arns, list)
    finally:
        wrapper.delete_filter(filter_arn)
