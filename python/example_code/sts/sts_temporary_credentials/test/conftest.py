# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run AWS Security Token Service (AWS STS) tests.
"""

import sys
import pytest
# This is needed so Python can find test_tools on the path.
sys.path.append('../../..')
from test_tools.fixtures.common import *


@pytest.fixture
def unique_names():
    return {
        'user': 'test-user',
        'role': 'test-role',
        'policy': 'test-policy',
        'user-policy': 'test-user-policy',
        'mfa': 'test-mfa'
    }
