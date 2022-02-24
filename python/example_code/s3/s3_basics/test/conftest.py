# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run Amazon S3 tests.
"""

import sys
import pytest
# This is needed so Python can find test_tools on the path.
sys.path.append('../../..')
from test_tools.fixtures.common import *


@pytest.fixture(name='stub_and_patch')
def _stub_and_patch(make_stubber, monkeypatch):
    """Make an S3Stubber and patch the get function to use the same resource."""
    def _do_it(wrapper, get_func, region_name=None):
        s3 = getattr(wrapper, get_func)(region_name)
        monkeypatch.setattr(wrapper, get_func, lambda rgn=None: s3)
        return make_stubber(s3.meta.client)
    return _do_it
