# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Tests for s3_express_getting_started.py.
"""

import pytest
from botocore.exceptions import ClientError
import uuid

class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data

        self.my_uuid = "0000"

        self.express_user_name = "s3-express-user"
        self.regular_user_name = "regular-user"

        self.availability_zone_filter = [{"Name": "region-name", "Values": [self.scenario_data.region]}]
        self.availability_zone_ids = ["use1-az2"]

        self.bucket_name_prefix = "amzn-s3-demo-bucket"
        self.directory_bucket_name = (
            f"{self.bucket_name_prefix}-{self.my_uuid}--{self.availability_zone_ids[0]}--x-s3"
        )
        self.regular_bucket_name = f"{self.bucket_name_prefix}-regular-{self.my_uuid}"

        self.directory_bucket_configuration = {
            "Bucket": {"Type": "Directory", "DataRedundancy": "SingleAvailabilityZone"},
            "Location": {"Name": self.availability_zone_ids[0], "Type": "AvailabilityZone"},
        }

        answers = [
                    self.bucket_name_prefix,
        "1",
        ]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, ec2_stubber, iam_stubber, s3_stubber, monkeypatch):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(iam_stubber.stub_create_access_key, self.regular_user_name)
            runner.add(iam_stubber.stub_create_access_key, self.express_user_name)
            runner.add(
                ec2_stubber.stub_describe_availability_zones,
                self.availability_zone_ids,
                self.availability_zone_filter,
                self.availability_zone_ids,
            )
            runner.add(
                s3_stubber.stub_create_bucket,
                self.directory_bucket_name,
                bucket_configuration=self.directory_bucket_configuration,
            )
            runner.add(s3_stubber.stub_create_bucket, self.regular_bucket_name)

        def mock_create_s3_client(arg1):
            return self.scenario_data.s3_client

        monkeypatch.setattr(
            self.scenario_data.scenario, "create_s3__client_with_access_key_credentials", mock_create_s3_client
        )

        monkeypatch.setattr(uuid, "uuid4", lambda: self.my_uuid)

@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


@pytest.mark.integ
def test_scenario_create_one_time_schedule(mock_mgr, capsys, monkeypatch):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.ec2_stubber,
                         mock_mgr.scenario_data.iam_stubber, mock_mgr.scenario_data.s3_stubber, monkeypatch)

    mock_mgr.scenario_data.scenario.setup_clients_and_buckets(mock_mgr.express_user_name, mock_mgr.regular_user_name)


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_create_access_key_regular", 0),
        ("TESTERROR-stub_create_access_key_directory", 1),
        ("TESTERROR-stub_describe_availability_zones", 2),
        ("TESTERROR-stub_create_bucket_directory", 3),
        ("TESTERROR-stub_create_bucket_regular", 4),
    ]
)

@pytest.mark.integ
def test_scenario_create_one_time_schedule_error(mock_mgr, caplog, error, stop_on_index, monkeypatch):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.ec2_stubber,
                         mock_mgr.scenario_data.iam_stubber, mock_mgr.scenario_data.s3_stubber, monkeypatch)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.setup_clients_and_buckets(mock_mgr.express_user_name, mock_mgr.regular_user_name)
