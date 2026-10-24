# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run unit tests.
"""

import sys
import os

# This is needed so Python can find test_tools on the path.
sys.path.append("../..")
from test_tools.fixtures.common import *

# Add the parent directory so tests can import kinesis_wrapper.
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
