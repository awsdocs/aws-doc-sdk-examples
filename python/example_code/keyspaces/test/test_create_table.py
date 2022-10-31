# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.table_exists = False
        self.scenario_data.scenario.ks_wrapper.ks_name = 'test-ks'
        self.table_name = 'test-table'
        self.table_arn = 'arn:aws:cassandra:test-region:111122223333:/keyspace/test-ks/test-table'
        self.table_status = 'ACTIVE'
        self.table_schema = {
            'allColumns': [{'name': 'test-column', 'type': 'text'}], 'partitionKeys': [{'name': 'test-column'}]}
        self.tables = [{
            'keyspaceName': self.scenario_data.scenario.ks_wrapper.ks_name,
            'tableName': f'table-{ind}', 'resourceArn': self.table_arn} for ind in range(1, 4)]
        answers = [self.table_name]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        ks_name = self.scenario_data.scenario.ks_wrapper.ks_name
        with self.stub_runner(error, stop_on) as runner:
            if self.table_exists:
                runner.add(
                    stubber.stub_get_table, ks_name, self.table_name, self.table_status, self.table_arn,
                    schema=self.table_schema)
            else:
                runner.add(
                    stubber.stub_get_table, ks_name, self.table_name, self.table_status, self.table_arn,
                    error_code='ResourceNotFoundException')
                runner.add(stubber.stub_create_table, ks_name, self.table_name, {'status': 'ENABLED'}, self.table_arn)
                runner.add(
                    stubber.stub_get_table, ks_name, self.table_name, self.table_status, self.table_arn,
                    schema=self.table_schema)
            runner.add(stubber.stub_list_tables, ks_name, self.tables)


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


@pytest.mark.parametrize('table_exists', [True, False])
def test_create_table(mock_mgr, capsys, table_exists):
    mock_mgr.table_exists = table_exists
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.create_table()

    capt = capsys.readouterr()
    assert mock_mgr.table_name in capt.out
    for table in mock_mgr.tables:
        assert table['tableName'] in capt.out


@pytest.mark.parametrize('error, stop_on_index', [
    ('TESTERROR-stub_get_table', 0),
    ('TESTERROR-stub_create_table', 1),
    ('TESTERROR-stub_get_table', 2),
    ('TESTERROR-stub_list_tables', 3),
])
def test_create_table_error(
        mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.create_table()
    assert exc_info.value.response['Error']['Code'] == error

    assert error in caplog.text
