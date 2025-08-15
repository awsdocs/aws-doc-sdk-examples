# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""Shared test fixtures for S3 batch operations tests."""

import boto3
import pytest
from moto import mock_s3, mock_s3control, mock_sts

from test_s3_batch_stubber import S3BatchStubber
from s3_batch_wrapper import S3BatchWrapper
from cloudformation_helper import CloudFormationHelper


class ScenarioData:
    """Holds data for scenario tests."""
    
    def __init__(self, wrapper, cfn_helper, stubber):
        self.wrapper = wrapper
        self.cfn_helper = cfn_helper
        self.stubber = stubber


@pytest.fixture
def scenario_data(make_stubber):
    """Create scenario data with stubbed clients."""
    s3_client = boto3.client("s3", region_name="us-east-1")
    s3control_client = boto3.client("s3control", region_name="us-east-1")
    sts_client = boto3.client("sts", region_name="us-east-1")
    cfn_client = boto3.client("cloudformation", region_name="us-east-1")
    
    wrapper = S3BatchWrapper(s3_client, s3control_client, sts_client)
    cfn_helper = CloudFormationHelper(cfn_client)
    stubber = make_stubber(S3BatchStubber, s3_client, s3control_client, sts_client)
    
    return ScenarioData(wrapper, cfn_helper, stubber)