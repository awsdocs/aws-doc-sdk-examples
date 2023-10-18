# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from datetime import datetime
import time
from unittest.mock import MagicMock
from botocore.exceptions import ClientError, WaiterError
from botocore.stub import ANY
import pytest
import requests

from auto_scaler import AutoScalerError
from load_balancer import LoadBalancerError
from parameters import ParameterHelper, ParameterHelperError


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.scenario_data.endpoint = "test-endpoint"
        self.scenario_data.bad_profile_name = (
            f"{self.scenario_data.resource_prefix}-bc-prof"
        )
        self.scenario_data.bad_profile_arn = (
            "arn:aws:iam:us-west-2:123456789012:instance-profile/test-bad-profile"
        )
        self.scenario_data.association_id = "test-association-id"
        self.scenario_args = []
        self.scenario_out = {}
        answers = ["1", "2", "3", "3", "3", "3", "3", "3", "3"]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(
                self.scenario_data.ssm.stubber.stub_put_parameter,
                ParameterHelper.table,
                self.scenario_data.table_name,
            )
            runner.add(
                self.scenario_data.ssm.stubber.stub_put_parameter,
                ParameterHelper.failure_response,
                "none",
            )
            runner.add(
                self.scenario_data.ssm.stubber.stub_put_parameter,
                ParameterHelper.health_check,
                "shallow",
            )
            runner.add(
                self.scenario_data.elb.stubber.stub_describe_load_balancers,
                [self.scenario_data.lb_name],
                [self.scenario_data.endpoint],
            )
            runner.add(
                self.scenario_data.elb.stubber.stub_describe_target_groups,
                [self.scenario_data.tg_name],
                [self.scenario_data.tg_arn],
            )
            runner.add(
                self.scenario_data.elb.stubber.stub_describe_target_health,
                self.scenario_data.tg_arn,
                [
                    {
                        "id": "test-id",
                        "port": 80,
                        "state": "unhealthy",
                        "reason": "test reason",
                        "desc": "test desc",
                    }
                ],
            )
            runner.add(
                self.scenario_data.ssm.stubber.stub_put_parameter,
                ParameterHelper.table,
                "this-is-not-a-table",
            )
            runner.add(
                self.scenario_data.ssm.stubber.stub_put_parameter,
                ParameterHelper.failure_response,
                "static",
            )
            runner.add(
                self.scenario_data.ssm.stubber.stub_put_parameter,
                ParameterHelper.table,
                self.scenario_data.table_name,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_create_policy,
                f"{self.scenario_data.resource_prefix}-bc-pol",
                self.scenario_data.bad_policy_arn,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_create_role,
                self.scenario_data.bad_role_name,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_attach_role_policy,
                self.scenario_data.bad_role_name,
                self.scenario_data.bad_policy_arn,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_attach_role_policy,
                self.scenario_data.bad_role_name,
                ANY,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_create_instance_profile,
                self.scenario_data.bad_profile_name,
                self.scenario_data.bad_profile_arn,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_get_instance_profile,
                self.scenario_data.bad_profile_name,
                self.scenario_data.bad_profile_arn,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_add_role_to_instance_profile,
                self.scenario_data.bad_profile_name,
                self.scenario_data.bad_role_name,
            )
            runner.add(
                self.scenario_data.auto_scaling.stubber.stub_describe_auto_scaling_groups,
                [self.scenario_data.asg_name],
                [
                    {
                        "AutoScalingGroupName": self.scenario_data.asg_name,
                        "MinSize": 3,
                        "MaxSize": 3,
                        "DesiredCapacity": 0,
                        "DefaultCooldown": 0,
                        "AvailabilityZones": ["test-zone"],
                        "HealthCheckType": "EC2",
                        "CreatedTime": datetime.now(),
                        "Instances": [self.scenario_data.instance],
                    }
                ],
            )
            runner.add(
                self.scenario_data.ec2.stubber.stub_describe_iam_instance_profile_associations,
                self.scenario_data.instance["InstanceId"],
                self.scenario_data.association_id,
            )
            runner.add(
                self.scenario_data.ec2.stubber.stub_replace_iam_instance_profile_association,
                self.scenario_data.bad_profile_name,
                self.scenario_data.association_id,
            )
            runner.add(
                self.scenario_data.ec2.stubber.stub_reboot_instances,
                [self.scenario_data.instance["InstanceId"]],
            )
            runner.add(
                self.scenario_data.ssm.stubber.stub_describe_instance_information,
                [self.scenario_data.instance["InstanceId"]],
            )
            runner.add(
                self.scenario_data.ssm.stubber.stub_send_command,
                [self.scenario_data.instance["InstanceId"]],
                commands=ANY,
                timeout=None,
            )
            runner.add(
                self.scenario_data.ssm.stubber.stub_put_parameter,
                ParameterHelper.health_check,
                "deep",
            )
            runner.add(
                self.scenario_data.auto_scaling.stubber.stub_terminate_instance_in_auto_scaling_group,
                self.scenario_data.instance["InstanceId"],
                False,
                None,
            )
            runner.add(
                self.scenario_data.ssm.stubber.stub_put_parameter,
                ParameterHelper.table,
                "this-is-not-a-table",
            )
            runner.add(
                self.scenario_data.ssm.stubber.stub_put_parameter,
                ParameterHelper.table,
                self.scenario_data.table_name,
            )
            runner.add(
                self.scenario_data.ssm.stubber.stub_put_parameter,
                ParameterHelper.failure_response,
                "none",
            )
            runner.add(
                self.scenario_data.ssm.stubber.stub_put_parameter,
                ParameterHelper.health_check,
                "shallow",
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_demo(mock_mgr, monkeypatch):
    monkeypatch.setattr(time, "sleep", lambda x: None)
    monkeypatch.setattr(
        requests, "get", lambda x: MagicMock(status_code=200, text="test text")
    )
    mock_mgr.setup_stubs(None, None)

    mock_mgr.scenario_data.scenario.demo()


@pytest.mark.parametrize(
    "error, stub_name, stop_on_index",
    [
        (ParameterHelperError, "stub_put_parameter", 0),
        (ParameterHelperError, "stub_put_parameter", 1),
        (ParameterHelperError, "stub_put_parameter", 2),
        (LoadBalancerError, "stub_describe_load_balancers", 3),
        (LoadBalancerError, "stub_describe_target_groups", 4),
        (LoadBalancerError, "stub_describe_target_health", 5),
        (ParameterHelperError, "stub_put_parameter", 6),
        (ParameterHelperError, "stub_put_parameter", 7),
        (ParameterHelperError, "stub_put_parameter", 8),
        (AutoScalerError, "stub_create_policy", 9),
        (AutoScalerError, "stub_create_role", 10),
        (AutoScalerError, "stub_attach_role_policy", 11),
        (AutoScalerError, "stub_attach_role_policy", 12),
        (AutoScalerError, "stub_create_instance_profile", 13),
        (WaiterError, "stub_get_instance_profile", 14),
        (AutoScalerError, "stub_add_role_to_instance_profile", 15),
        (AutoScalerError, "stub_describe_auto_scaling_groups", 16),
        (AutoScalerError, "stub_describe_iam_instance_profile_associations", 17),
        (AutoScalerError, "stub_replace_iam_instance_profile_association", 18),
        (AutoScalerError, "stub_reboot_instances", 19),
        (AutoScalerError, "stub_describe_instance_information", 20),
        (AutoScalerError, "stub_send_command", 21),
        (ParameterHelperError, "stub_put_parameter", 22),
        (AutoScalerError, "stub_terminate_instance_in_auto_scaling_group", 23),
        (ParameterHelperError, "stub_put_parameter", 24),
        (ParameterHelperError, "stub_put_parameter", 25),
        (ParameterHelperError, "stub_put_parameter", 26),
        (ParameterHelperError, "stub_put_parameter", 27),
    ],
)
def test_demo_error(mock_mgr, caplog, error, stub_name, stop_on_index, monkeypatch):
    monkeypatch.setattr(time, "sleep", lambda x: None)
    monkeypatch.setattr(
        requests, "get", lambda x: MagicMock(status_code=200, text="test text")
    )
    mock_mgr.setup_stubs(error, stop_on_index)

    with pytest.raises(error):
        mock_mgr.scenario_data.scenario.demo()
