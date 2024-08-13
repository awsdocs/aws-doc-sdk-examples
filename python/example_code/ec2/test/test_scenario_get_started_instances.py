# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import os
import sys

import boto3

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))
from elastic_ip import ElasticIpWrapper
from instance import EC2InstanceWrapper
from key_pair import KeyPairWrapper
from scenario_get_started_instances import EC2InstanceScenario
from security_group import SecurityGroupWrapper


def test_scenario_happy_path(capsys):
    # Instantiate the wrappers
    inst_wrapper = EC2InstanceWrapper.from_client()
    key_wrapper = KeyPairWrapper.from_client()
    sg_wrapper = SecurityGroupWrapper.from_client()
    eip_wrapper = ElasticIpWrapper.from_client()

    # Create the scenario object
    scenario = EC2InstanceScenario(
        inst_wrapper=inst_wrapper,
        key_wrapper=key_wrapper,
        sg_wrapper=sg_wrapper,
        eip_wrapper=eip_wrapper,
        ssm_client=boto3.client("ssm"),
    )

    # Run the scenario, exit 1 with error
    scenario.run_scenario()

    capt = capsys.readouterr()
    assert "Thanks for watching!" in capt.out
