# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run Amazon Bedrock tests.
"""

import sys

# This is needed so Python can find test_tools on the path.
sys.path.append("../..")
from test_tools.fixtures.common import *


class FakeData:
    AGENT_ID = "FAKE_AGENT_ID"
    AGENT_NAME = "FakeAgentName"
    ALIAS_ID = "FAKE_AGENT_ALIAS_ID"
    ALIAS_NAME = "FakeAliasName"
    ACTION_GROUP_ID = "FAKE_ACTION_GROUP_ID"
    ACTION_GROUP_NAME = "FakeActionGroupName"
    API_SCHEMA = "openapi: 3.0.0"
    ARN = "arn:aws:service:region:123456789012/fake-resource"
    DESCRIPTION = "A fake description."
    FOUNDATION_MODEL_ID = "fake.foundation-model-id"
    INSTRUCTION = "A fake instruction with a minimum of 40 characters"
    TIMESTAMP = "1970-01-01T00:00:00Z"
    VERSION = "1.234.5"
