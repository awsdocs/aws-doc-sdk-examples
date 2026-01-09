# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Configuration file for pytest for Topics and Queues cross-service scenario tests.
"""

import sys
import os
import boto3
import pytest

script_dir = os.path.dirname(os.path.abspath(__file__))

# Add relative path to include scenario modules
sys.path.append(script_dir)
sys.path.append(os.path.dirname(script_dir))

from topics_and_queues_scenario import TopicsAndQueuesScenario
from sns_wrapper import SnsWrapper
from sqs_wrapper import SqsWrapper

# Add relative path to include demo_tools and test_tools
sys.path.append(os.path.join(script_dir, "../.."))

from test_tools.fixtures.common import *


class ScenarioData:
    def __init__(
        self,
        sns_client,
        sqs_client,
        sns_stubber,
        sqs_stubber,
    ):
        self.sns_client = sns_client
        self.sqs_client = sqs_client
        self.sns_stubber = sns_stubber
        self.sqs_stubber = sqs_stubber
        
        # Create wrappers and scenario
        self.sns_wrapper = SnsWrapper(sns_client)
        self.sqs_wrapper = SqsWrapper(sqs_client)
        self.scenario = TopicsAndQueuesScenario(self.sns_wrapper, self.sqs_wrapper)


@pytest.fixture
def scenario_data(make_stubber):
    sns_client = boto3.client('sns')
    sqs_client = boto3.client('sqs')

    sns_stubber = make_stubber(sns_client)
    sqs_stubber = make_stubber(sqs_client)

    return ScenarioData(
        sns_client,
        sqs_client,
        sns_stubber,
        sqs_stubber,
    )
