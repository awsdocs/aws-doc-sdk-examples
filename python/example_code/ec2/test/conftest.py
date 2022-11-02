# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test fixtures used to run unit tests.
"""

import sys
from unittest.mock import MagicMock
import boto3
import pytest

from elastic_ip import ElasticIpWrapper
from instance import InstanceWrapper
from key_pair import KeyPairWrapper
from security_group import SecurityGroupWrapper
import scenario_get_started_instances

# This is needed so Python can find test_tools on the path.
sys.path.append('../..')
from test_tools.fixtures.common import *


class ScenarioData:
    def __init__(self, resource, stubber, ssm_client, ssm_stubber):
        self.resource = resource
        self.stubber = stubber
        self.ssm_client = ssm_client
        self.ssm_stubber = ssm_stubber
        self.scenario = scenario_get_started_instances.Ec2InstanceScenario(
            InstanceWrapper(self.resource), KeyPairWrapper(self.resource, MockDir()),
            SecurityGroupWrapper(self.resource), ElasticIpWrapper(self.resource),
            ssm_client)


class MockDir:
    def __init__(self):
        self.name = ''


@pytest.fixture
def mock_address():
    return MagicMock(
            allocation_id='mock-allocation-id',
            public_ip='1.2.3.4',
            domain='vpc',
            association_id='mock-association-id',
            instance_id='mock-instance-id',
            network_interface_id='mock-network-interface-id')


@pytest.fixture
def scenario_data(make_stubber):
    resource = boto3.resource('ec2')
    stubber = make_stubber(resource.meta.client)
    ssm_client = boto3.client('ssm')
    ssm_stubber = make_stubber(ssm_client)
    return ScenarioData(resource, stubber, ssm_client, ssm_stubber)
