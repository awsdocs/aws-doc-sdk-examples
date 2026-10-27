# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run Amazon SES v2 search-and-filter
unit and integration tests.
"""

import os
import sys

import boto3
import pytest

# Make test_tools importable (python/ root) and the wrapper importable (the
# scenario directory one level up from this test directory).
sys.path.append("../../../..")
sys.path.insert(0, os.path.join(os.path.dirname(__file__), ".."))

from test_tools.fixtures.common import *

from sesv2_wrapper import SearchFilterWrapper


class ScenarioData:
    """Encapsulates data for SES v2 search-and-filter scenario tests."""

    def __init__(self, sesv2_client, sesv2_stubber):
        self.sesv2_client = sesv2_client
        self.sesv2_stubber = sesv2_stubber
        self.wrapper = SearchFilterWrapper(sesv2_client)


@pytest.fixture
def scenario_data(make_stubber):
    """Creates a ScenarioData object for SES v2 search-and-filter tests."""
    sesv2_client = boto3.client("sesv2", region_name="us-east-1")
    sesv2_stubber = make_stubber(sesv2_client)
    return ScenarioData(sesv2_client, sesv2_stubber)
