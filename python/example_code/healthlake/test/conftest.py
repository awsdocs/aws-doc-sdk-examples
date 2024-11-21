# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run AWS HealthImaging
tests.
"""

import sys
import os

script_dir = os.path.dirname(os.path.abspath(__file__))

# This is needed so Python can find test_tools on the path.
sys.path.append(os.path.join(script_dir, "../../.."))

from test_tools.fixtures.common import *