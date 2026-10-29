# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Pytest configuration for Blue/Green Encryption Migration tests.
Makes the parent (example) folder importable so tests can import the wrapper
and scenario modules.
"""

import sys
import os

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))


def pytest_configure(config):
    config.addinivalue_line(
        "markers",
        "integ: integration test that requires and uses AWS resources. "
        "Use of this tag is likely to incur charges on your account.",
    )
