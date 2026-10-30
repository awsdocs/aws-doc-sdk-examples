# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests that verify the wrapper's list methods return at most max_results
items and stop paging, even when the service reports more pages.
"""

import boto3
import pytest

from inspector_wrapper import Inspector2Wrapper

ACCOUNT_ID = "123456789012"


@pytest.fixture
def wrapper_and_stubber(make_stubber):
    client = boto3.client("inspector2", region_name="us-east-1")
    return Inspector2Wrapper(client), make_stubber(client)


def test_list_coverage_stops_at_max_results(wrapper_and_stubber):
    wrapper, stubber = wrapper_and_stubber
    resources = [
        {
            "resourceType": "AWS_EC2_INSTANCE",
            "resourceId": f"i-{index:017d}",
            "accountId": ACCOUNT_ID,
            "scanType": "PACKAGE",
        }
        for index in range(5)
    ]
    # A nextToken is returned, but no second page is stubbed. If the wrapper
    # requested another page, the stubber would raise an error.
    stubber.stub_list_coverage(resources, max_results=5, next_token="more")

    assert len(wrapper.list_coverage(max_results=5)) == 5


def test_list_filters_stops_at_max_results(wrapper_and_stubber):
    wrapper, stubber = wrapper_and_stubber
    filters = [
        {
            "arn": f"arn:aws:inspector2:us-east-1:{ACCOUNT_ID}:filter/f{i}",
            "name": f"f{i}",
        }
        for i in range(10)
    ]
    stubber.stub_list_filters(filters, max_results=10, next_token="more")

    assert len(wrapper.list_filters(max_results=10)) == 10


def test_list_usage_totals_stops_at_max_results(wrapper_and_stubber):
    wrapper, stubber = wrapper_and_stubber
    totals = [
        {
            "accountId": f"{100000000000 + i}",
            "usage": [{"type": "ECR_RESCAN", "total": 1.0}],
        }
        for i in range(10)
    ]
    stubber.stub_list_usage_totals(totals, max_results=10, next_token="more")

    assert len(wrapper.list_usage_totals(max_results=10)) == 10
