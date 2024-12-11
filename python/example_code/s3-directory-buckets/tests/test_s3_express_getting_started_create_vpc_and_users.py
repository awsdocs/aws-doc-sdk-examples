# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Tests for s3_express_getting_started.py.
"""

import pytest

from botocore.exceptions import ClientError
from botocore import waiter
import os
import sys
import uuid

script_dir = os.path.dirname(os.path.abspath(__file__))

# Append directory for s3_express_getting_started.py
sys.path.append(os.path.join(script_dir, ".."))
import s3_express_getting_started


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.vpc_id = "XXXXXXXXXXXXXXXXXXXXX"
        self.route_table_id = "XXXXXXXXXXXXXXXXXXXXX"

        self.filters = [{"Name": "vpc-id", "Values": [self.vpc_id]}]

        self.service_name = f"com.amazonaws.{scenario_data.region}.s3express"
        self.my_uuid = "0000"

        self.stack_name = f"cfn-stack-s3-express-basics--{self.my_uuid}"
        self.template = (
            s3_express_getting_started.S3ExpressScenario.get_template_as_string()
        )
        self.stack_id = f"arn:aws:cloudformation:{scenario_data.region}:000000000000:stack/{self.stack_name}"

        self.express_user_name = "s3-express-user"
        self.regular_user_name = "regular-user"

        self.outputs = [
            {"OutputKey": "RegularUser", "OutputValue": self.regular_user_name},
            {"OutputKey": "ExpressUser", "OutputValue": self.express_user_name},
        ]

        answers = [
            "y",
        ]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(
        self, error, stop_on, ec2_stubber, cloud_formation_stubber, monkeypatch
    ):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(ec2_stubber.stub_create_vpc, "10.0.0.0/16", self.vpc_id)
            runner.add(
                ec2_stubber.stub_describe_route_tables,
                self.filters,
                self.vpc_id,
                self.route_table_id,
            )
            runner.add(
                ec2_stubber.stub_create_vpc_endpoint,
                self.vpc_id,
                self.route_table_id,
                self.service_name,
            )
            runner.add(
                cloud_formation_stubber.stub_create_stack,
                self.stack_name,
                self.template,
                ["CAPABILITY_NAMED_IAM"],
                self.stack_id,
            )
            runner.add(
                cloud_formation_stubber.stub_describe_stacks,
                self.stack_name,
                "CREATE_COMPLETE",
                self.outputs,
            )

        monkeypatch.setattr(uuid, "uuid4", lambda: self.my_uuid)

        # Mock the waiters.
        monkeypatch.setattr(waiter.Waiter, "wait", lambda arg, **kwargs: None)


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


@pytest.mark.integ
def test_scenario_create_vpc_and_users(mock_mgr, capsys, monkeypatch):
    mock_mgr.setup_stubs(
        None,
        None,
        mock_mgr.scenario_data.ec2_stubber,
        mock_mgr.scenario_data.cloud_formation_stubber,
        monkeypatch,
    )

    mock_mgr.scenario_data.scenario.create_vpc_and_users()


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_create_vpc", 0),
        ("TESTERROR-stub_describe_route_tables", 1),
        ("TESTERROR-stub_create_vpc_endpoint", 2),
        ("TESTERROR-stub_create_stack", 3),
        ("TESTERROR-stub_describe_stacks", 4),
    ],
)
@pytest.mark.integ
def test_scenario_create_vpc_and_users_error(
    mock_mgr, caplog, error, stop_on_index, monkeypatch
):
    mock_mgr.setup_stubs(
        error,
        stop_on_index,
        mock_mgr.scenario_data.ec2_stubber,
        mock_mgr.scenario_data.cloud_formation_stubber,
        monkeypatch,
    )

    with pytest.raises(ClientError):
        mock_mgr.scenario_data.scenario.create_vpc_and_users()
