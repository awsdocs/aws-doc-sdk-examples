# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run AWS IoT Greengrass V2 unit tests.
"""

import os
import sys
import uuid

import boto3
import pytest

script_dir = os.path.dirname(os.path.abspath(__file__))

# Add relative paths so the wrapper and scenario modules can be imported.
sys.path.append(os.path.dirname(script_dir))
sys.path.append(os.path.join(os.path.dirname(script_dir), "scenarios"))

# This is needed so Python can find test_tools on the path. This import brings in
# the shared fixtures (including make_stubber) and registers the `integ` marker.
sys.path.append(os.path.join(script_dir, "../../.."))
from test_tools.fixtures.common import *

from greengrassv2_wrapper import GreengrassV2Wrapper


# Service-specific fixtures used by the integration tests.
@pytest.fixture(scope="module")
def greengrassv2_client():
    """Creates a shared Greengrass V2 client for integration tests."""
    return boto3.client("greengrassv2")


@pytest.fixture(scope="module")
def iot_client():
    """Creates a shared IoT client for thing group management."""
    return boto3.client("iot")


@pytest.fixture(scope="module")
def wrapper(greengrassv2_client):
    """Creates a wrapper instance for integration tests."""
    return GreengrassV2Wrapper(greengrassv2_client)


@pytest.fixture(scope="module")
def unique_suffix():
    """Generates a unique suffix used across a test module."""
    return str(uuid.uuid4())[:8]
