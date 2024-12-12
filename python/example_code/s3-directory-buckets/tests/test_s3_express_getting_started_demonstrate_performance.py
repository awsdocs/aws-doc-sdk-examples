# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Tests for s3_express_getting_started.py.
"""

import pytest


from botocore.exceptions import ClientError


number_of_uploads = 10


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.my_uuid = "0000"

        self.availability_zone_ids = ["use1-az2"]

        self.bucket_name_prefix = "amzn-s3-demo-bucket"
        self.directory_bucket_name = f"{self.bucket_name_prefix}-{self.my_uuid}--{self.availability_zone_ids[0]}--x-s3"
        self.regular_bucket_name = f"{self.bucket_name_prefix}-regular-{self.my_uuid}"

        self.object_name = "basic-text-object"

        answers = [
            "y",
            number_of_uploads,
        ]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

        scenario_data.scenario.s3_express_client = scenario_data.s3_client
        scenario_data.scenario.s3_regular_client = scenario_data.s3_client
        scenario_data.scenario.regular_bucket_name = self.regular_bucket_name
        scenario_data.scenario.directory_bucket_name = self.directory_bucket_name

    def setup_stubs(self, error, stop_on, s3_stubber):
        with self.stub_runner(error, stop_on) as runner:
            for _ in range(number_of_uploads):
                runner.add(
                    s3_stubber.stub_get_object,
                    self.directory_bucket_name,
                    self.object_name,
                )

            for _ in range(number_of_uploads):
                runner.add(
                    s3_stubber.stub_get_object,
                    self.regular_bucket_name,
                    self.object_name,
                )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


@pytest.mark.integ
def test_scenario_demonstrate_performance(mock_mgr, capsys, monkeypatch):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.s3_stubber)

    mock_mgr.scenario_data.scenario.demonstrate_performance(mock_mgr.object_name)


parameter_values = []
for i in range(len(parameter_values), len(parameter_values) + number_of_uploads):
    parameter_values.append((f"TESTERROR-stub_get_object_directory", i))

for i in range(len(parameter_values), len(parameter_values) + number_of_uploads):
    parameter_values.append((f"TESTERROR-stub_get_object_regular", i))


@pytest.mark.parametrize(
    "error, stop_on_index",
    parameter_values,
)
@pytest.mark.integ
def test_scenario_demonstrate_performance_error(
    mock_mgr, caplog, error, stop_on_index, monkeypatch
):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.s3_stubber)

    with pytest.raises(ClientError):
        mock_mgr.scenario_data.scenario.demonstrate_performance(mock_mgr.object_name)
