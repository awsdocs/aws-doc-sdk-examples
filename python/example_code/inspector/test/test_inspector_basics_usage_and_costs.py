# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.account_id = "123456789012"
        self.totals = [
            {
                "accountId": self.account_id,
                "usage": [
                    {
                        "type": "EC2_INSTANCE_HOURS",
                        "total": 120.0,
                        "estimatedMonthlyCost": 1.5,
                        "currency": "USD",
                    }
                ],
            }
        ]
        self.counts_by_group = [{"groupKey": "AWS_EC2_INSTANCE", "count": 3}]
        input_mocker.mock_answers([])
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_list_usage_totals, self.totals)
            runner.add(
                stubber.stub_list_coverage_statistics,
                3,
                self.counts_by_group,
                group_by="RESOURCE_TYPE",
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_check_usage_and_costs(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.check_usage_and_costs()

    capt = capsys.readouterr()
    assert "EC2_INSTANCE_HOURS: 120.0 (est. 1.5 USD)" in capt.out
    assert "AWS_EC2_INSTANCE: 3" in capt.out


def test_check_usage_and_costs_validation_error(mock_mgr, capsys, caplog):
    """Spec: ListUsageTotals ValidationException prints a validation error."""
    mock_mgr.setup_stubs(
        "ValidationException", "stub_list_usage_totals", mock_mgr.scenario_data.stubber
    )
    # The scenario keeps going after a ValidationException, so stub the rest.
    mock_mgr.scenario_data.stubber.stub_list_coverage_statistics(
        3, mock_mgr.counts_by_group, group_by="RESOURCE_TYPE"
    )

    mock_mgr.scenario_data.scenario.check_usage_and_costs()

    assert "Validation error listing usage totals" in caplog.text
    assert "No usage totals found." in capsys.readouterr().out


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_list_usage_totals", 0),
        ("TESTERROR-stub_list_coverage_statistics", 1),
    ],
)
def test_check_usage_and_costs_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.check_usage_and_costs()
    assert exc_info.value.response["Error"]["Code"] == error

    assert error in caplog.text
