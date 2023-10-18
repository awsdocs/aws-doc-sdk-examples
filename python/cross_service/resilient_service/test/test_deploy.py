# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import logging
import time
from unittest.mock import MagicMock
from botocore.exceptions import ClientError, WaiterError
from botocore.stub import ANY
import pytest
import requests

from auto_scaler import AutoScalerError
from load_balancer import LoadBalancerError
from recommendation_service import RecommendationServiceError


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.scenario_data.profile_arn = (
            "arn:aws:iam:us-west-2:123456789012:instance-profile/test-profile"
        )
        self.scenario_data.ami_id = "test-ami-id"
        self.scenario_data.zones = ["test-zone-1", "test-zone-2"]
        self.scenario_data.vpc_id = "test-vpc"
        self.scenario_data.subnet_ids = ["subnet-test-id"]
        self.scenario_data.lb_endpoint = "test-endpoint"
        self.scenario_data.sg_id = "test-sg-id"
        self.scenario_data.ip_address = "test-address"
        self.scenario_args = []
        self.scenario_out = {}
        answers = ["", "", "y", ""]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(
                self.scenario_data.ddb.stubber.stub_create_table,
                self.scenario_data.table_name,
                [
                    {"name": "MediaType", "type": "S", "key_type": "HASH"},
                    {"name": "ItemId", "type": "N", "key_type": "RANGE"},
                ],
                {"read": 5, "write": 5},
            )
            runner.add(
                self.scenario_data.ddb.stubber.stub_describe_table,
                self.scenario_data.table_name,
            )
            runner.add(self.scenario_data.ddb.stubber.stub_batch_write_item, ANY)
            runner.add(
                self.scenario_data.iam.stubber.stub_create_policy,
                f"{self.scenario_data.resource_prefix}-pol",
                self.scenario_data.policy_arn,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_create_role,
                self.scenario_data.role_name,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_attach_role_policy,
                self.scenario_data.role_name,
                self.scenario_data.policy_arn,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_create_instance_profile,
                self.scenario_data.profile_name,
                self.scenario_data.profile_arn,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_get_instance_profile,
                self.scenario_data.profile_name,
                self.scenario_data.profile_arn,
            )
            runner.add(
                self.scenario_data.iam.stubber.stub_add_role_to_instance_profile,
                self.scenario_data.profile_name,
                self.scenario_data.role_name,
            )
            runner.add(
                self.scenario_data.ssm.stubber.stub_get_parameter,
                self.scenario_data.ami_param,
                self.scenario_data.ami_id,
            )
            runner.add(
                self.scenario_data.ec2.stubber.stub_create_launch_template,
                self.scenario_data.lt_name,
                self.scenario_data.inst_type,
                self.scenario_data.ami_id,
                inst_profile=self.scenario_data.profile_name,
                user_data=ANY,
            )
            runner.add(
                self.scenario_data.ec2.stubber.stub_describe_availability_zones,
                self.scenario_data.zones,
            )
            runner.add(
                self.scenario_data.auto_scaling.stubber.stub_create_auto_scaling_group,
                self.scenario_data.asg_name,
                self.scenario_data.zones,
                self.scenario_data.lt_name,
                3,
                3,
            )
            runner.add(
                self.scenario_data.ec2.stubber.stub_describe_vpcs,
                {self.scenario_data.vpc_id: True},
                vpc_filters=[{"Name": "is-default", "Values": ["true"]}],
            )
            runner.add(
                self.scenario_data.ec2.stubber.stub_describe_subnets,
                self.scenario_data.vpc_id,
                self.scenario_data.zones,
                self.scenario_data.subnet_ids,
            )
            runner.add(
                self.scenario_data.elb.stubber.stub_create_target_group,
                self.scenario_data.tg_name,
                "HTTP",
                80,
                self.scenario_data.vpc_id,
                {
                    "path": "/healthcheck",
                    "interval": 10,
                    "timeout": 5,
                    "thresh_healthy": 2,
                    "thresh_unhealthy": 2,
                },
                self.scenario_data.tg_arn,
            )
            runner.add(
                self.scenario_data.elb.stubber.stub_create_load_balancer,
                self.scenario_data.lb_name,
                self.scenario_data.subnet_ids,
                "HTTP",
                80,
                self.scenario_data.lb_arn,
                self.scenario_data.lb_endpoint,
            )
            runner.add(
                self.scenario_data.elb.stubber.stub_describe_load_balancers,
                [self.scenario_data.lb_name],
            )
            runner.add(
                self.scenario_data.elb.stubber.stub_create_listener,
                self.scenario_data.lb_arn,
                "HTTP",
                80,
                self.scenario_data.tg_arn,
            )
            runner.add(
                self.scenario_data.auto_scaling.stubber.stub_attach_load_balancer_target_groups,
                self.scenario_data.asg_name,
                [self.scenario_data.tg_arn],
            )
            runner.add(
                self.scenario_data.ec2.stubber.stub_describe_security_groups,
                [
                    {
                        "id": self.scenario_data.sg_id,
                        "group_name": "default",
                        "ip_permissions": [
                            {
                                "FromPort": 80,
                                "IpRanges": [{"CidrIp": "test"}],
                                "PrefixListIds": [],
                            }
                        ],
                    }
                ],
                self.scenario_data.vpc_id,
            )
            runner.add(
                self.scenario_data.ec2.stubber.stub_authorize_security_group_ingress,
                self.scenario_data.sg_id,
                cidr_ip=f"{self.scenario_data.ip_address}/32",
                port=80,
                ip_protocol="tcp",
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_deploy(mock_mgr, caplog, monkeypatch):
    caplog.set_level(logging.INFO)
    monkeypatch.setattr(time, "sleep", lambda x: None)
    monkeypatch.setattr(
        requests,
        "get",
        lambda x: MagicMock(status_code=404, text=mock_mgr.scenario_data.ip_address),
    )
    mock_mgr.setup_stubs(None, None)

    mock_mgr.scenario_data.scenario.deploy()

    assert len(caplog.records) > 0
    attrs = [
        attr
        for attr in dir(mock_mgr.scenario_data)
        if not callable(getattr(mock_mgr.scenario_data, attr))
        and not attr.startswith("__")
        and attr
        not in [
            "ami_param",
            "lb_arn",
            "lb_endpoint",
            "profile_arn",
            "tg_arn",
            "vpc_id",
            "bad_policy_arn",
            "bad_prof_name",
            "bad_role_name",
        ]
    ]
    for attr in attrs:
        val = getattr(mock_mgr.scenario_data, attr)
        if isinstance(val, str):
            assert any(
                val in rec for rec in caplog.messages
            ), f"'{val}' not in log messages!"


@pytest.mark.parametrize(
    "error, stub_name, stop_on_index",
    [
        (RecommendationServiceError, "stub_create_table", 0),
        (WaiterError, "stub_describe_table", 1),
        (RecommendationServiceError, "stub_batch_write_item", 2),
        (AutoScalerError, "stub_create_policy", 3),
        (AutoScalerError, "stub_create_role", 4),
        (AutoScalerError, "stub_attach_role_policy", 5),
        (AutoScalerError, "stub_create_instance_profile", 6),
        (WaiterError, "stub_get_instance_profile", 7),
        (AutoScalerError, "stub_add_role_to_instance_profile", 8),
        (AutoScalerError, "stub_get_parameter", 9),
        (AutoScalerError, "stub_create_launch_template", 10),
        (AutoScalerError, "stub_describe_availability_zones", 11),
        (AutoScalerError, "stub_create_auto_scaling_group", 12),
        (AutoScalerError, "stub_describe_vpcs", 13),
        (AutoScalerError, "stub_describe_subnets", 14),
        (LoadBalancerError, "stub_create_target_group", 15),
        (LoadBalancerError, "stub_create_load_balancer", 16),
        (WaiterError, "stub_describe_load_balancers", 17),
        (LoadBalancerError, "stub_create_listener", 18),
        (AutoScalerError, "stub_attach_load_balancer_target_groups", 19),
        (AutoScalerError, "stub_describe_security_groups", 20),
        (AutoScalerError, "stub_authorize_security_group_ingress", 21),
    ],
)
def test_deploy_error(mock_mgr, capsys, monkeypatch, error, stub_name, stop_on_index):
    monkeypatch.setattr(time, "sleep", lambda x: None)
    monkeypatch.setattr(
        requests,
        "get",
        lambda x: MagicMock(status_code=404, text=mock_mgr.scenario_data.ip_address),
    )
    mock_mgr.setup_stubs(error, stop_on_index)

    with pytest.raises(error):
        mock_mgr.scenario_data.scenario.deploy()
