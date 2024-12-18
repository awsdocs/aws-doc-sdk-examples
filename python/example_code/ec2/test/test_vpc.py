# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for cleanup in vpc.py.
"""
import sys

import boto3
import pytest
import os
from botocore.exceptions import ClientError
from botocore import waiter

script_dir = os.path.dirname(os.path.abspath(__file__))


sys.path.append(script_dir)
# Add relative path to include VpcWrapper.
sys.path.append(os.path.dirname(script_dir))
from vpc import VpcWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append(os.path.join(script_dir, "../../.."))

from test_tools.fixtures.common import *


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create(monkeypatch, make_stubber, error_code):
    ec2_client = boto3.client("ec2")
    ec2_stubber = make_stubber(ec2_client)

    # Mock the waiters.
    monkeypatch.setattr(waiter.Waiter, "wait", lambda arg, **kwargs: None)

    wrapper = VpcWrapper(ec2_client)
    vpc_id = "vpc-a01106c2"
    cidr_block = "10.0.0.0/16"
    ec2_stubber.stub_create_vpc(cidr_block, vpc_id, error_code=error_code)
    if error_code is None:
        created_vpc_id = wrapper.create(cidr_block)
        assert created_vpc_id == vpc_id
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create(cidr_block)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create_vpc_endpoint(make_stubber, error_code):
    ec2_client = boto3.client("ec2")
    ec2_stubber = make_stubber(ec2_client)

    wrapper = VpcWrapper(ec2_client)
    vpc_id = "XXXXXXXXXXXX"
    service_name = "com.amazonaws.us-west-2.s3"
    route_table_id = "XXXXXXXXXXXXX"
    vpc_endpoint_id = f"vpce-{vpc_id}"
    ec2_stubber.stub_create_vpc_endpoint(
        vpc_id, route_table_id, service_name, error_code=error_code
    )
    if error_code is None:
        created_endpoint_id = wrapper.create_vpc_endpoint(
            vpc_id, service_name, [route_table_id]
        )
        assert created_endpoint_id["VpcEndpointId"] == vpc_endpoint_id
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_vpc_endpoint(vpc_id, service_name, [route_table_id])
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_describe_route_tables(make_stubber, error_code):
    ec2_client = boto3.client("ec2")
    ec2_stubber = make_stubber(ec2_client)

    wrapper = VpcWrapper(ec2_client)
    vpc_id = "XXXXXXXXXXXX"
    route_table_id = "XXXXXXXXXXXXX"
    filters = [{"Name": "vpc-id", "Values": [vpc_id]}]
    ec2_stubber.stub_describe_route_tables(
        filters, vpc_id, route_table_id, error_code=error_code
    )
    if error_code is None:
        wrapper.describe_route_tables([vpc_id])
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.describe_route_tables([vpc_id])
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_vpc_endpoints(make_stubber, error_code):
    ec2_client = boto3.client("ec2")
    ec2_stubber = make_stubber(ec2_client)

    wrapper = VpcWrapper(ec2_client)
    vpc_endpoint_id = "XXXXXXXXXXXX"
    ec2_stubber.stub_delete_vpc_endpoints([vpc_endpoint_id], error_code=error_code)
    if error_code is None:
        wrapper.delete_vpc_endpoints([vpc_endpoint_id])
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_vpc_endpoints([vpc_endpoint_id])
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete(make_stubber, error_code):
    ec2_client = boto3.client("ec2")
    ec2_stubber = make_stubber(ec2_client)

    wrapper = VpcWrapper(ec2_client)
    vpc_id = "XXXXXXXXXXXX"
    ec2_stubber.stub_delete_vpc(vpc_id, error_code=error_code)
    if error_code is None:
        wrapper.delete(vpc_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete(vpc_id)
        assert exc_info.value.response["Error"]["Code"] == error_code
