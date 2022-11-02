# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        ks_wrapper = scenario_data.scenario.ks_wrapper
        ks_wrapper.ks_name = 'test-ks'
        ks_wrapper.table_name = 'test-table'
        ks_wrapper.table_arn = 'arn:aws:cassandra:test-region:111122223333:/keyspace/test-ks/test-table'
        ks_wrapper.table_status = 'ACTIVE'
        self.scenario_data = scenario_data
        answers = ['y']
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        ks_wrapper = self.scenario_data.scenario.ks_wrapper
        with self.stub_runner(error, stop_on) as runner:
            runner.add(
                stubber.stub_delete_table, ks_wrapper.ks_name, ks_wrapper.table_name)
            runner.add(
                stubber.stub_get_table, ks_wrapper.ks_name, ks_wrapper.table_name,
                ks_wrapper.table_status, ks_wrapper.table_arn)
            runner.add(
                stubber.stub_get_table, ks_wrapper.ks_name, ks_wrapper.table_name,
                ks_wrapper.table_status, ks_wrapper.table_arn, error_code='ResourceNotFoundException')
            runner.add(stubber.stub_delete_keyspace, ks_wrapper.ks_name)


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_cleanup(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.cleanup()

    capt = capsys.readouterr()
    assert "Keyspace deleted" in capt.out


@pytest.mark.parametrize('error, stop_on_index', [
    ('TESTERROR-stub_delete_table', 0),
    ('TESTERROR-stub_get_table', 1),
    ('TESTERROR-stub_delete_keyspace', 3),
])
def test_cleanup_error(
        mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.cleanup()
    assert exc_info.value.response['Error']['Code'] == error

    assert error in caplog.text
