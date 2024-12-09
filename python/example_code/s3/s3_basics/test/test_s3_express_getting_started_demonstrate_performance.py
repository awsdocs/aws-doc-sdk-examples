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


stop_on_index = []
for i in range(len(stop_on_index), len(stop_on_index) + number_of_uploads):
    stop_on_index.append((f"TESTERROR-stub_get_object_directory", i))

for i in range(len(stop_on_index), len(stop_on_index) + number_of_uploads):
    stop_on_index.append((f"TESTERROR-stub_get_object_regular", i))


@pytest.mark.integ
@pytest.mark.parametrize(
    "error_code, stop_on_index",
    stop_on_index,
)
def test_s3_express_scenario(
    make_stubber, stub_runner, error_code, stop_on_index, monkeypatch
):
    region = "us-east-1"
    cloud_formation_resource = boto3.resource("cloudformation")

    ec2_client = boto3.client("ec2", region_name=region)

    iam_client = boto3.client("iam")

    s3_client = boto3.client("s3")
    s3_stubber = make_stubber(s3_client)

    my_uuid = "0000"

    availability_zone_ids = ["use1-az2"]

    bucket_name_prefix = "amzn-s3-demo-bucket"
    directory_bucket_name = (
        f"{bucket_name_prefix}-{my_uuid}--{availability_zone_ids[0]}--x-s3"
    )
    regular_bucket_name = f"{bucket_name_prefix}-regular-{my_uuid}"

    object_name = "basic-text-object"

    inputs = [
        "y",
        number_of_uploads,
    ]
    monkeypatch.setattr("builtins.input", lambda x: inputs.pop(0))

    s3_express_getting_started.use_press_enter_to_continue = False

    with stub_runner(error_code, stop_on_index) as runner:
        for _ in range(number_of_uploads):
            runner.add(s3_stubber.stub_get_object, directory_bucket_name, object_name)

        for _ in range(number_of_uploads):
            runner.add(s3_stubber.stub_get_object, regular_bucket_name, object_name)

    def mock_wait(self, **kwargs):
        return

    # Mock the waiters.
    monkeypatch.setattr(waiter.Waiter, "wait", mock_wait)

    scenario = s3_express_getting_started.S3ExpressScenario(
        cloud_formation_resource, ec2_client, iam_client
    )

    scenario.s3_express_client = s3_client
    scenario.s3_regular_client = s3_client
    scenario.regular_bucket_name = regular_bucket_name
    scenario.directory_bucket_name = directory_bucket_name

    monkeypatch.setattr(uuid, "uuid4", lambda: my_uuid)

    if error_code is None:
        scenario.demonstrate_performance(object_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario.demonstrate_performance(object_name)
        assert exc_info.value.response["Error"]["Code"] == error_code
