# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Configuration file for pytest for Topics and Queues cross-service scenario tests.
"""

import sys
import os

# This is needed so Python can find test_tools on the path.
sys.path.append("../..")  # noqa

from test_tools.fixtures.common import (
    pytest_configure,
    fixture_make_stubber,
    fixture_make_unique_name,
    fixture_make_bucket,
    StubRunner,
    stub_runner,
    InputMocker,
    input_mocker,
)
