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
        ("TESTERROR-stub_create_access_key_regular", 5),
        ("TESTERROR-stub_create_access_key_directory", 6),
        ("TESTERROR-stub_describe_availability_zones", 7),
        ("TESTERROR-stub_create_bucket_directory", 8),
        ("TESTERROR-stub_create_bucket_regular", 9),
        ("TESTERROR-stub_put_object", 10),
        ("TESTERROR-stub_create_session", 11),
        ("TESTERROR-stub_copy_object", 12),
    ]

for i in range(len(stop_on_index), len(stop_on_index) + number_of_uploads):
    stop_on_index.append((f"TESTERROR-stub_get_object_directory", i))

for i in range(len(stop_on_index), len(stop_on_index) + number_of_uploads):
    stop_on_index.append((f"TESTERROR-stub_get_object_regular", i))

@pytest.mark.parametrize(
    "error_code, stop_on_index",
    stop_on_index,
)
def test_s3_express_scenario(
    make_stubber, stub_runner, error_code   , stop_on_index, monkeypatch
):
    region = "us-east-1"
    cloud_formation_resource = boto3.resource("cloudformation")
    cloud_formation_stubber = make_stubber(cloud_formation_resource.meta.client)

    ec2_client = boto3.client("ec2", region_name=region)
    ec2_stubber = make_stubber(ec2_client)

    iam_client = boto3.client("iam")
    iam_stubber = make_stubber(iam_client)

    s3_client = boto3.client("s3")
    s3_stubber = make_stubber(s3_client)

    vpc_id = "XXXXXXXXXXXXXXXXXXXXX"
    route_table_id = "XXXXXXXXXXXXXXXXXXXXX"

    filter = [{"Name": "vpc-id", "Values": [vpc_id]}]

    service_name = f"com.amazonaws.{region}.s3express"
    my_uuid = "0000"

    stack_name = f"cfn-stack-s3-express-basics--{my_uuid}"
    template = s3_express_getting_started.S3ExpressScenario.get_template_as_string()
    stack_id  = f"arn:aws:cloudformation:{region}:000000000000:stack/{stack_name}"

    express_user_name = "s3-express-user"
    regular_user_name = "regular-user"

    outputs = [
        {"OutputKey": "RegularUser", "OutputValue": regular_user_name},
        {"OutputKey": "ExpressUser", "OutputValue": express_user_name},
    ]

    availability_zone_filter = [{"Name": "region-name", "Values": [region]}]
    availability_zone_ids = ["use1-az2"]

    bucket_name_prefix = "amzn-s3-demo-bucket"
    directory_bucket_name = f"{bucket_name_prefix}-{my_uuid}--{availability_zone_ids[0]}--x-s3"
    regular_bucket_name = f"{bucket_name_prefix}-regular-{my_uuid}"

    directory_bucket_configuration = { 'Bucket': { 'Type' : 'Directory',
                                      'DataRedundancy' : 'SingleAvailabilityZone'},
                          'Location' : { 'Name' : availability_zone_ids[0],
                                         'Type' :  'AvailabilityZone'}
                          }

    object_name = "basic-text-object"
    inputs = [
        "y",
        bucket_name_prefix,
        "1",
        "y",
        number_of_uploads
    ]
    monkeypatch.setattr("builtins.input", lambda x: inputs.pop(0))

    s3_express_getting_started.use_press_enter_to_continue = False


    with stub_runner(error_code, stop_on_index) as runner:
        runner.add(ec2_stubber.stub_create_vpc, "10.0.0.0/16", vpc_id)
        runner.add(ec2_stubber.stub_describe_route_tables, filter, vpc_id, route_table_id)
        runner.add(ec2_stubber.stub_create_vpc_endpoint, vpc_id, route_table_id, service_name)
        runner.add(cloud_formation_stubber.stub_create_stack, stack_name, template, ["CAPABILITY_NAMED_IAM"], stack_id)
        runner.add(cloud_formation_stubber.stub_describe_stacks, stack_name, "CREATE_COMPLETE", outputs)
        runner.add(iam_stubber.stub_create_access_key, regular_user_name)
        runner.add(iam_stubber.stub_create_access_key, express_user_name)
        runner.add(ec2_stubber.stub_describe_availability_zones, availability_zone_ids, availability_zone_filter, availability_zone_ids)
        runner.add(s3_stubber.stub_create_bucket, directory_bucket_name, bucket_configuration = directory_bucket_configuration)
        runner.add(s3_stubber.stub_create_bucket, regular_bucket_name)
        runner.add (s3_stubber.stub_put_object, regular_bucket_name, object_name, "Look Ma, I'm a bucket!")
        runner.add(s3_stubber.stub_create_session, directory_bucket_name)
        runner.add (s3_stubber.stub_copy_object, regular_bucket_name, object_name, directory_bucket_name, object_name)

        for _ in range(number_of_uploads):
            runner.add (s3_stubber.stub_get_object, directory_bucket_name, object_name)

        for _ in range(number_of_uploads):
            runner.add(s3_stubber.stub_get_object, regular_bucket_name, object_name)

    def mock_wait(self, **kwargs):
        return

    # Mock the waiters.
    monkeypatch.setattr(waiter.Waiter, "wait", mock_wait)

    scenario = s3_express_getting_started.S3ExpressScenario(cloud_formation_resource, ec2_client,
                                                            iam_client)

    def mock_create_s3_client(self, **kwargs):
        return s3_client

    monkeypatch.setattr(scenario, "create_s3__client_with_access_key_credentials", mock_create_s3_client)

    monkeypatch.setattr(uuid, "uuid4", lambda : my_uuid)



    if error_code is None:
        scenario.s3_express_scenario()
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario.s3_express_scenario()
        assert exc_info.value.response["Error"]["Code"] == error_code
