# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Tests for s3_express_getting_started.py.
"""


import pytest

from botocore import waiter


class MockManager:
    def __init__(self, stub_runner, scenario_data):
        self.scenario_data = scenario_data
        self.my_uuid = "0000"
        self.bucket_name_prefix = "amzn-s3-demo-bucket"
        self.directory_bucket_name = (
            f"{self.bucket_name_prefix}-{self.my_uuid}--use1-az2--x-s3"
        )
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

        self.vpc_id = "XXXXXXXXXXXXXXXXXXXXX"
        self.vpc_endpoint_id = f"vpce-{self.vpc_id}"

        self.stack_name = f"cfn-stack-s3-express-basics--{self.my_uuid}"
        self.stack = scenario_data.cloud_formation_resource.Stack(self.stack_name)
        self.stub_runner = stub_runner

    def setup_stubs(
        self,
        error,
        stop_on,
        ec2_stubber,
        cloud_formation_stubber,
        s3_stubber,
        monkeypatch,
    ):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(
                s3_stubber.stub_list_objects_v2,
                self.directory_bucket_name,
                self.object_keys,
            )
            runner.add(
                s3_stubber.stub_delete_objects,
                self.directory_bucket_name,
                self.object_keys,
            )
            runner.add(s3_stubber.stub_delete_bucket, self.directory_bucket_name)
            runner.add(
                s3_stubber.stub_list_objects_v2,
                self.regular_bucket_name,
                self.object_keys,
            )
            runner.add(
                s3_stubber.stub_delete_objects,
                self.regular_bucket_name,
                self.object_keys,
            )
            runner.add(s3_stubber.stub_delete_bucket, self.regular_bucket_name)
            runner.add(cloud_formation_stubber.stub_delete_stack, self.stack_name)
            runner.add(ec2_stubber.stub_delete_vpc_endpoints, [self.vpc_endpoint_id])
            runner.add(ec2_stubber.stub_delete_vpc, self.vpc_id)

        # Mock the waiters.
        monkeypatch.setattr(waiter.Waiter, "wait", lambda arg, **kwargs: None)

        self.scenario_data.scenario.s3_express_client = self.scenario_data.s3_client
        self.scenario_data.scenario.s3_regular_client = self.scenario_data.s3_client
        self.scenario_data.scenario.directory_bucket_name = self.directory_bucket_name
        # The cleanup function keeps executing after an exception is raised.
        # This allows the app to clean up all possible resources.
        # Because of this, exception testing fails because actions are called after the exception is raised.
        # To avoid false negatives when testing exceptions, certain fields are set only when they should be tested.

        if stop_on is None or stop_on > 2:
            self.scenario_data.scenario.regular_bucket_name = self.regular_bucket_name

        if stop_on is None or stop_on > 5:
            self.scenario_data.scenario.stack = self.stack

        if stop_on is None or stop_on > 6:
            self.scenario_data.scenario.vpc_endpoint_id = self.vpc_endpoint_id

        if stop_on is None or stop_on > 7:
            self.scenario_data.scenario.vpc_id = self.vpc_id


@pytest.fixture
def mock_mgr(stub_runner, scenario_data):
    return MockManager(stub_runner, scenario_data)


@pytest.mark.integ
def test_scenario_cleanup(mock_mgr, capsys, monkeypatch):
    mock_mgr.setup_stubs(
        None,
        None,
        mock_mgr.scenario_data.ec2_stubber,
        mock_mgr.scenario_data.cloud_formation_stubber,
        mock_mgr.scenario_data.s3_stubber,
        monkeypatch,
    )

    mock_mgr.scenario_data.scenario.cleanup()


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_list_objects_directory", 0),
        ("TESTERROR-stub_delete_objects_directory", 1),
        ("TESTERROR-stub_delete_bucket_directory", 2),
        ("TESTERROR-stub_list_objects_regular", 3),
        ("TESTERROR-stub_delete_objects_regular", 4),
        ("TESTERROR-stub_delete_bucket_regular", 5),
        ("TESTERROR-stub_delete_stack", 6),
        ("TESTERROR-stub_delete_vpc_endpoints", 7),
        ("TESTERROR-stub_delete_vpc", 8),
    ],
)
@pytest.mark.integ
def test_scenario_cleanup_error(mock_mgr, caplog, error, stop_on_index, monkeypatch):
    mock_mgr.setup_stubs(
        error,
        stop_on_index,
        mock_mgr.scenario_data.ec2_stubber,
        mock_mgr.scenario_data.cloud_formation_stubber,
        mock_mgr.scenario_data.s3_stubber,
        monkeypatch,
    )

    mock_mgr.scenario_data.scenario.cleanup()
