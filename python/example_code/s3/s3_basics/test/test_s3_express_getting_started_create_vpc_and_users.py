# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Tests for s3_express_getting_started.py.
"""

import pytest

import boto3
from botocore.exceptions import ClientError
from botocore import waiter
import os
import sys
import uuid

script_dir = os.path.dirname(os.path.abspath(__file__))

# Append directory for s3_express_getting_started.py
sys.path.append(os.path.join(script_dir, ".."))
import s3_express_getting_started

number_of_uploads = 10

stop_on_index = [
    ("TESTERROR-stub_create_vpc", 0),
    ("TESTERROR-stub_describe_route_tables", 1),
    ("TESTERROR-stub_create_vpc_endpoint", 2),
    ("TESTERROR-stub_create_stack", 3),
    ("TESTERROR-stub_describe_stacks", 4),
]


@pytest.mark.integ
@pytest.mark.parametrize(
    "error_code, stop_on_index",
    stop_on_index,
)
def test_s3_express_scenario_create_vpc_and_users(
    make_stubber, stub_runner, error_code, stop_on_index, monkeypatch
):
    region = "us-east-1"
    cloud_formation_resource = boto3.resource("cloudformation")
    cloud_formation_stubber = make_stubber(cloud_formation_resource.meta.client)

    ec2_client = boto3.client("ec2", region_name=region)
    ec2_stubber = make_stubber(ec2_client)

    iam_client = boto3.client("iam")

    vpc_id = "XXXXXXXXXXXXXXXXXXXXX"
    route_table_id = "XXXXXXXXXXXXXXXXXXXXX"

    filter = [{"Name": "vpc-id", "Values": [vpc_id]}]

    service_name = f"com.amazonaws.{region}.s3express"
    my_uuid = "0000"

    stack_name = f"cfn-stack-s3-express-basics--{my_uuid}"
    template = s3_express_getting_started.S3ExpressScenario.get_template_as_string()
    stack_id = f"arn:aws:cloudformation:{region}:000000000000:stack/{stack_name}"

    express_user_name = "s3-express-user"
    regular_user_name = "regular-user"

    outputs = [
        {"OutputKey": "RegularUser", "OutputValue": regular_user_name},
        {"OutputKey": "ExpressUser", "OutputValue": express_user_name},
    ]

    inputs = [
        "y",
    ]
    monkeypatch.setattr("builtins.input", lambda x: inputs.pop(0))

    s3_express_getting_started.use_press_enter_to_continue = False

    with stub_runner(error_code, stop_on_index) as runner:
        runner.add(ec2_stubber.stub_create_vpc, "10.0.0.0/16", vpc_id)
        runner.add(
            ec2_stubber.stub_describe_route_tables, filter, vpc_id, route_table_id
        )
        runner.add(
            ec2_stubber.stub_create_vpc_endpoint, vpc_id, route_table_id, service_name
        )
        runner.add(
            cloud_formation_stubber.stub_create_stack,
            stack_name,
            template,
            ["CAPABILITY_NAMED_IAM"],
            stack_id,
        )
        runner.add(
            cloud_formation_stubber.stub_describe_stacks,
            stack_name,
            "CREATE_COMPLETE",
            outputs,
        )

    def mock_wait(self, **kwargs):
        return

    # Mock the waiters.
    monkeypatch.setattr(waiter.Waiter, "wait", mock_wait)

    scenario = s3_express_getting_started.S3ExpressScenario(
        cloud_formation_resource, ec2_client, iam_client
    )

    monkeypatch.setattr(uuid, "uuid4", lambda: my_uuid)

    if error_code is None:
        scenario.create_vpc_and_users()
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario.create_vpc_and_users()
        assert exc_info.value.response["Error"]["Code"] == error_code
