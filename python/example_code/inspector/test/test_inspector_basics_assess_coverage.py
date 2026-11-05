# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.account_id = "123456789012"
        self.covered_resources = [
            {
                "resourceType": "AWS_EC2_INSTANCE",
                "resourceId": f"i-{index:017d}",
                "accountId": self.account_id,
                "scanType": "PACKAGE",
                "scanStatus": {
                    "statusCode": "ACTIVE" if index % 2 else "INACTIVE",
                    "reason": "SUCCESSFUL",
                },
            }
            for index in range(1, 5)
        ]
        self.total_counts = len(self.covered_resources)
        input_mocker.mock_answers([])
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_list_coverage, self.covered_resources)
            runner.add(stubber.stub_list_coverage_statistics, self.total_counts, [])


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


@pytest.mark.parametrize("has_resources", [True, False])
def test_assess_coverage(mock_mgr, capsys, has_resources):
    if not has_resources:
        mock_mgr.covered_resources = []
        mock_mgr.total_counts = 0
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.assess_coverage()

    capt = capsys.readouterr()
    assert f"Total covered resources: {mock_mgr.total_counts}" in capt.out
    if has_resources:
        assert "AWS_EC2_INSTANCE: 4 resource(s)" in capt.out
        assert "ACTIVE: 2 resource(s)" in capt.out
        assert "INACTIVE: 2 resource(s)" in capt.out
    else:
        assert "No coverage information found." in capt.out


@pytest.mark.parametrize("stop_on_index", [0, 1])
def test_assess_coverage_validation_error(mock_mgr, capsys, caplog, stop_on_index):
    """Spec: ListCoverage and ListCoverageStatistics ValidationExceptions are printed."""
    mock_mgr.setup_stubs(
        "ValidationException", stop_on_index, mock_mgr.scenario_data.stubber
    )
    # The scenario keeps going after a ValidationException, so stub the rest.
    if stop_on_index == 0:
        mock_mgr.scenario_data.stubber.stub_list_coverage_statistics(0, [])

    mock_mgr.scenario_data.scenario.assess_coverage()

    assert "ValidationException" in caplog.text
    assert "Total covered resources" in capsys.readouterr().out


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_list_coverage", 0),
        ("TESTERROR-stub_list_coverage_statistics", 1),
    ],
)
def test_assess_coverage_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.assess_coverage()
    assert exc_info.value.response["Error"]["Code"] == error

    assert error in caplog.text
