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


class FakeFlowData:   
    FLOW_NAME = "Fake_flow"
    FLOW_DESCRIPTION = "Playlist creator flow"
    FLOW_ID = "XXXXXXXXXX"
    FLOW_VERSION = "DRAFT"
    ROLE_ARN = f"arn:aws:iam::123456789012:role/BedrockFlowRole-{FLOW_NAME}"
    FLOW_ARN = f"arn:aws:bedrock:us-east-1:123456789012:flow/{FLOW_ID}"
    FLOW_DEFINITION = {}
    CREATED_AT = "2025-03-29T21:34:43.048609+00:00"
    UPDATED_AT = "2025-03-30T21:34:43.048609+00:00"
   
    VERSION_NAME = "Fake_flow_alias"
    VERSION_DESCRIPTION = "Playlist creator flow version"
    VERSION_ID = "XXXXXXXXXX"
    FLOW_VERSION = "1"
    VERSION_ARN = f"arn:aws:bedrock:us-east-1:123456789012:flow/{FLOW_ID}/version/{VERSION_ID}"

    ALIAS_NAME = "Fake_flow_alias"
    ALIAS_DESCRIPTION = "Playlist creator flow alias"
    FLOW_ID = "XXXXXXXXXX"
    ALIAS_ID = "XXXXXXXXXX"
    ALIAS_ARN = f"arn:aws:bedrock:us-east-1:123456789012:flow/{FLOW_ID}/alias/{ALIAS_ID}"
    ROUTING_CONFIG = [
        {
            "flowVersion": FLOW_VERSION
        }
    ]
    SESSION_ID = "XXXXXXXXXX"