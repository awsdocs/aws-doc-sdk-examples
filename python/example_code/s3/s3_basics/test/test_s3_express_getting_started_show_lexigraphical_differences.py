# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Tests for s3_express_getting_started.py.
"""

import pytest

import boto3
from botocore.exceptions import ClientError
import os
import sys

script_dir = os.path.dirname(os.path.abspath(__file__))

# Append directory for s3_express_getting_started.py
sys.path.append(os.path.join(script_dir, ".."))
import s3_express_getting_started


@pytest.mark.integ
@pytest.mark.parametrize(
    "error_code, stop_on_index",
    [
        ("TESTERROR-stub_put_object_other_object", 0),
        ("TESTERROR-stub_put_object_other_object", 1),
        ("TESTERROR-stub_put_object_alt_object", 2),
        ("TESTERROR-stub_put_object_alt_object", 3),
        ("TESTERROR-stub_put_object_other_alt_object", 4),
        ("TESTERROR-stub_put_object_other_alt_object", 5),
        ("TESTERROR-stub_list_objects_directory", 6),
        ("TESTERROR-stub_list_objects_regular", 7),
    ],
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
    other_object = f"other/{object_name}"
    alt_object = f"alt/{object_name}"
    other_alt_object = f"other/alt/{object_name}"

    object_keys = [object_name, other_object, alt_object, other_alt_object]

    inputs = []

    monkeypatch.setattr("builtins.input", lambda x: inputs.pop(0))

    s3_express_getting_started.use_press_enter_to_continue = False

    with stub_runner(error_code, stop_on_index) as runner:
        runner.add(s3_stubber.stub_put_object, regular_bucket_name, other_object, "")
        runner.add(s3_stubber.stub_put_object, directory_bucket_name, other_object, "")
        runner.add(s3_stubber.stub_put_object, regular_bucket_name, alt_object, "")
        runner.add(s3_stubber.stub_put_object, directory_bucket_name, alt_object, "")
        runner.add(
            s3_stubber.stub_put_object, regular_bucket_name, other_alt_object, ""
        )
        runner.add(
            s3_stubber.stub_put_object, directory_bucket_name, other_alt_object, ""
        )

        runner.add(s3_stubber.stub_list_objects_v2, directory_bucket_name, object_keys)
        runner.add(s3_stubber.stub_list_objects_v2, regular_bucket_name, object_keys)

    scenario = s3_express_getting_started.S3ExpressScenario(
        cloud_formation_resource, ec2_client, iam_client
    )
    scenario.s3_express_client = s3_client
    scenario.s3_regular_client = s3_client
    scenario.regular_bucket_name = regular_bucket_name
    scenario.directory_bucket_name = directory_bucket_name

    if error_code is None:
        scenario.show_lexigraphical_differences(object_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario.show_lexigraphical_differences(object_name)
        assert exc_info.value.response["Error"]["Code"] == error_code
