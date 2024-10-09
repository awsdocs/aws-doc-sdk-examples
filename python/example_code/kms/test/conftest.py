# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run unit tests.
"""

import sys

import boto3
import pytest

import kms_scenario
from alias_management import AliasManager
from grant_management import GrantManager
from key_encryption import KeyEncrypt
from key_management import KeyManager
from key_policies import KeyPolicy

# This is needed so Python can find test_tools on the path.
sys.path.append("../..")
from test_tools.fixtures.common import *


class ScenarioData:
    def __init__(self, client, stubber):
        self.client = client
        self.stubber = stubber
        self.scenario = kms_scenario.KMSScenario(
            key_manager=KeyManager(self.client),
            grant_manager=GrantManager(self.client),
            alias_manager=AliasManager(self.client),
            key_policy=KeyPolicy(self.client),
            key_encryption=KeyEncrypt(self.client),
        )


@pytest.fixture
def scenario_data(make_stubber):
    client = boto3.client("kms")
    stubber = make_stubber(client)
    return ScenarioData(client, stubber)
