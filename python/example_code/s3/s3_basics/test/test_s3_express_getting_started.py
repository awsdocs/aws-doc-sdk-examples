# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for demo_bucket_basics.py.
"""

import pytest

import boto3
from botocore.exceptions import ClientError
from botocore import waiter
import os
import sys

script_dir = os.path.dirname(os.path.abspath(__file__))

# Append directory for s3_express_getting_started.py
sys.path.append(os.path.join(script_dir, ".."))
import s3_express_getting_started


@pytest.mark.parametrize(
    "keep, error_code, stop_on_method",
    [
        (True, None, None),
        (False, None, None),
        (False, "TestException", "stub_list_buckets"),
        (False, "TestException", "stub_create_bucket"),
        (False, "TestException", "stub_delete_bucket"),
    ],
)
def test_create_and_delete_my_bucket(
    make_stubber, stub_runner, keep, error_code, stop_on_method, monkeypatch
):
    cloud_formation_client = boto3.client("cloudformation")
    cloud_formation_stubber = make_stubber(cloud_formation_client)

    ec2_client = boto3.client("ec2")
    ec2_stubber = make_stubber(ec2_client)

    iam_client = boto3.client("iam")
    iam_stubber = make_stubber(iam_client)

    s3_client = boto3.client("s3")
    s3_stubber = make_stubber(s3_client.meta.client)

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(s3_stubber.stub_list_buckets, [])
        runner.add(s3_stubber.stub_create_bucket, bucket_name, region)
        runner.add(s3_stubber.stub_head_bucket, bucket_name)
        runner.add(s3_stubber.stub_list_buckets, [s3_client.Bucket(bucket_name)])
        if not keep:
            runner.add(s3_stubber.stub_delete_bucket, bucket_name)
            runner.add(s3_stubber.stub_head_bucket, bucket_name, 404)
            runner.add(s3_stubber.stub_list_buckets, [])

    def mock_wait(self, **kwargs):
        return

    # Mock the waiters.
    monkeypatch.setattr(waiter.Waiter, "wait", mock_wait)

    scenario = s3_express_getting_started.S3ExpressScenario(cloud_formation_client, ec2_client,
                                                            iam_client)

    def mock_create_s3_client(self, **kwargs):
        return s3_client

    monkeypatch.setattr(scenario, "create_s3__client_with_access_key_credentials", mock_create_s3_client)

    if error_code is None:
        scenario.s3_express_scenario()
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario.s3_express_scenario()
        assert exc_info.value.response["Error"]["Code"] == error_code
