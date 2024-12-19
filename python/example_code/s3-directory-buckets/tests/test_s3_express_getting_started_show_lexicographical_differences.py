# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Tests for s3_express_getting_started.py.
"""

import pytest

from botocore.exceptions import ClientError


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.my_uuid = "0000"

        self.availability_zone_ids = ["use1-az2"]

        self.bucket_name_prefix = "amzn-s3-demo-bucket"
        self.directory_bucket_name = f"{self.bucket_name_prefix}-{self.my_uuid}--{self.availability_zone_ids[0]}--x-s3"
        self.regular_bucket_name = f"{self.bucket_name_prefix}-regular-{self.my_uuid}"

        self.object_name = "basic-text-object"
        self.other_object = f"other/{self.object_name}"
        self.alt_object = f"alt/{self.object_name}"
        self.other_alt_object = f"other/alt/{self.object_name}"

        self.object_keys = [
            self.object_name,
            self.other_object,
            self.alt_object,
            self.other_alt_object,
        ]

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
                self.other_object,
                "",
            )
            runner.add(
                s3_stubber.stub_put_object,
                self.directory_bucket_name,
                self.other_object,
                "",
            )
            runner.add(
                s3_stubber.stub_put_object,
                self.regular_bucket_name,
                self.alt_object,
                "",
            )
            runner.add(
                s3_stubber.stub_put_object,
                self.directory_bucket_name,
                self.alt_object,
                "",
            )
            runner.add(
                s3_stubber.stub_put_object,
                self.regular_bucket_name,
                self.other_alt_object,
                "",
            )
            runner.add(
                s3_stubber.stub_put_object,
                self.directory_bucket_name,
                self.other_alt_object,
                "",
            )

            runner.add(
                s3_stubber.stub_list_objects_v2,
                self.directory_bucket_name,
                self.object_keys,
            )
            runner.add(
                s3_stubber.stub_list_objects_v2,
                self.regular_bucket_name,
                self.object_keys,
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


@pytest.mark.integ
def test_scenario_show_lexicographical_differences(mock_mgr, capsys, monkeypatch):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.s3_stubber)

    mock_mgr.scenario_data.scenario.show_lexicographical_differences(
        mock_mgr.object_name
    )


@pytest.mark.parametrize(
    "error, stop_on_index",
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
@pytest.mark.integ
def test_scenario_show_lexicographical_differences_error(
    mock_mgr, caplog, error, stop_on_index, monkeypatch
):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.s3_stubber)

    with pytest.raises(ClientError):
        mock_mgr.scenario_data.scenario.show_lexicographical_differences(
            mock_mgr.object_name
        )
