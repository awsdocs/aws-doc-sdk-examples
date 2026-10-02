# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run Amazon Athena unit tests.
"""

import os
import sys

# Make the parent example directory importable so tests can import the wrapper
# and Hello modules regardless of the working directory the tests run from.
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

# This is needed so Python can find test_tools on the path.
sys.path.append("../..")
from test_tools.fixtures.common import *
