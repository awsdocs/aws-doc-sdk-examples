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

class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.my_uuid = "0000"

        self.availability_zone_ids = ["use1-az2"]

        self.bucket_name_prefix = "amzn-s3-demo-bucket"
        self.directory_bucket_name = (
            f"{self.bucket_name_prefix}-{self.my_uuid}--{self.availability_zone_ids[0]}--x-s3"
        )
        self.regular_bucket_name = f"{self.bucket_name_prefix}-regular-{self.my_uuid}"

        self.object_name = "basic-text-object"


        answers = []
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

        scenario_data.scenario.s3_express_client = scenario_data.s3_client
        scenario_data.scenario.s3_regular_client = scenario_data.s3_client
        scenario_data.scenario.regular_bucket_name = self.regular_bucket_name
        scenario_data.scenario.directory_bucket_name = self.directory_bucket_name

    def setup_stubs(self, error, stop_on, s3_stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(
                s3_stubber.stub_put_object,
                self.regular_bucket_name,
                self.object_name,
                "Look Ma, I'm a bucket!",
            )
            runner.add(s3_stubber.stub_create_session, self.directory_bucket_name)
            runner.add(
                s3_stubber.stub_copy_object,
                self.regular_bucket_name,
                self.object_name,
                self.directory_bucket_name,
                self.object_name,
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


@pytest.mark.integ
def test_scenario_create_one_time_schedule(mock_mgr, capsys, monkeypatch):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.s3_stubber)

    mock_mgr.scenario_data.scenario.create_session_and_add_objects()


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_put_object", 0),
        ("TESTERROR-stub_create_session", 1),
        ("TESTERROR-stub_copy_object", 2),
    ]
)

@pytest.mark.integ
def test_scenario_create_one_time_schedule_error(mock_mgr, caplog, error, stop_on_index, monkeypatch):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.s3_stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.create_session_and_add_objects()
