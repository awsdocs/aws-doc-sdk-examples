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
import uuid

script_dir = os.path.dirname(os.path.abspath(__file__))

# Append directory for s3_express_getting_started.py
sys.path.append(os.path.join(script_dir, ".."))
import s3_express_getting_started

number_of_uploads = 10

stop_on_index = [
    ("TESTERROR-stub_create_access_key_regular", 0),
    ("TESTERROR-stub_create_access_key_directory", 1),
    ("TESTERROR-stub_describe_availability_zones", 2),
    ("TESTERROR-stub_create_bucket_directory", 3),
    ("TESTERROR-stub_create_bucket_regular", 4),
]


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
    ec2_stubber = make_stubber(ec2_client)

    iam_client = boto3.client("iam")
    iam_stubber = make_stubber(iam_client)

    s3_client = boto3.client("s3")
    s3_stubber = make_stubber(s3_client)

    my_uuid = "0000"

    express_user_name = "s3-express-user"
    regular_user_name = "regular-user"

    availability_zone_filter = [{"Name": "region-name", "Values": [region]}]
    availability_zone_ids = ["use1-az2"]

    bucket_name_prefix = "amzn-s3-demo-bucket"
    directory_bucket_name = (
        f"{bucket_name_prefix}-{my_uuid}--{availability_zone_ids[0]}--x-s3"
    )
    regular_bucket_name = f"{bucket_name_prefix}-regular-{my_uuid}"

    directory_bucket_configuration = {
        "Bucket": {"Type": "Directory", "DataRedundancy": "SingleAvailabilityZone"},
        "Location": {"Name": availability_zone_ids[0], "Type": "AvailabilityZone"},
    }

    inputs = [
        bucket_name_prefix,
        "1",
    ]
    monkeypatch.setattr("builtins.input", lambda x: inputs.pop(0))

    s3_express_getting_started.use_press_enter_to_continue = False

    with stub_runner(error_code, stop_on_index) as runner:
        runner.add(iam_stubber.stub_create_access_key, regular_user_name)
        runner.add(iam_stubber.stub_create_access_key, express_user_name)
        runner.add(
            ec2_stubber.stub_describe_availability_zones,
            availability_zone_ids,
            availability_zone_filter,
            availability_zone_ids,
        )
        runner.add(
            s3_stubber.stub_create_bucket,
            directory_bucket_name,
            bucket_configuration=directory_bucket_configuration,
        )
        runner.add(s3_stubber.stub_create_bucket, regular_bucket_name)

    scenario = s3_express_getting_started.S3ExpressScenario(
        cloud_formation_resource, ec2_client, iam_client
    )

    def mock_create_s3_client(self, **kwargs):
        return s3_client

    monkeypatch.setattr(
        scenario, "create_s3__client_with_access_key_credentials", mock_create_s3_client
    )

    monkeypatch.setattr(uuid, "uuid4", lambda: my_uuid)

    if error_code is None:
        scenario.setup_clients_and_buckets(express_user_name, regular_user_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario.setup_clients_and_buckets(express_user_name, regular_user_name)
        assert exc_info.value.response["Error"]["Code"] == error_code
