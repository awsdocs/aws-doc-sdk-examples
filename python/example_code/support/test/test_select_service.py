# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.support_services = [
            {
                "name": f"Service-{i}",
                "code": f"service-code-{i}",
                "categories": [{"name": f"Category-{j}"} for j in range(4)],
            }
            for i in range(1)
        ]
        self.scenario_args = []
        self.category_args = [self.support_services[0]]
        answers = ["1", "1"]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_describe_services, "en", self.support_services)


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_display_and_select_service(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.display_and_select_service(*mock_mgr.scenario_args)

    capt = capsys.readouterr()
    for service in mock_mgr.support_services:
        assert service["name"] in capt.out


def test_display_and_select_category(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.display_and_select_service(*mock_mgr.scenario_args)
    mock_mgr.scenario_data.scenario.display_and_select_category(*mock_mgr.category_args)

    capt = capsys.readouterr()
    for category in mock_mgr.support_services[0]["categories"]:
        assert category["name"] in capt.out


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_describe_services", 0),
    ],
)
def test_services_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.display_and_select_service(
            *mock_mgr.scenario_args
        )
    assert exc_info.value.response["Error"]["Code"] == error

    assert error in caplog.text
