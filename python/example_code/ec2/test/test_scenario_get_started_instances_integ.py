# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import boto3
import pytest

from elastic_ip import ElasticIpWrapper
from instance import InstanceWrapper
from key_pair import KeyPairWrapper
from security_group import SecurityGroupWrapper
from scenario_get_started_instances import Ec2InstanceScenario


@pytest.mark.integ
def test_run_cluster_scenario_integ(input_mocker, capsys):
    scenario = Ec2InstanceScenario(
        InstanceWrapper.from_resource(), KeyPairWrapper.from_resource(), SecurityGroupWrapper.from_resource(),
        ElasticIpWrapper.from_resource(), boto3.client('ssm'))

    input_mocker.mock_answers([
        'doc-example-test-key', 'y',    # create and list key pairs
        'doc-example-test-group', '',   # create security group
        1, 1, '',                       # create instance
        '',                             # stop and start instance
        '',                             # associate elastic ip
        '',                             # stop and start instance
        'y',                            # cleanup
    ])

    scenario.run_scenario()

    capt = capsys.readouterr()
    assert "Thanks for watching!" in capt.out
