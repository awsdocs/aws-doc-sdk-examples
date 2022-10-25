# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from unittest.mock import MagicMock
from botocore.exceptions import ClientError
import pytest

import query

class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        ks_wrapper = scenario_data.scenario.ks_wrapper
        ks_wrapper.ks_name = 'test-ks'
        ks_wrapper.table_name = 'test-table'
        self.table_arn = 'arn:aws:cassandra:test-region:111122223333:/keyspace/test-ks/test-table'
        self.table_status = 'ACTIVE'
        self.table_name_restored = f'{ks_wrapper.table_name}_restored'
        answers = ['', 'y']
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        ks_wrapper = self.scenario_data.scenario.ks_wrapper
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_update_table, ks_wrapper.ks_name, ks_wrapper.table_name, self.table_arn)
            runner.add(
                stubber.stub_get_table, ks_wrapper.ks_name, ks_wrapper.table_name, self.table_status, self.table_arn)
            runner.add(
                stubber.stub_restore_table, ks_wrapper.ks_name, ks_wrapper.table_name, ks_wrapper.ks_name,
                self.table_name_restored, self.table_arn)
            runner.add(
                stubber.stub_get_table, ks_wrapper.ks_name, self.table_name_restored, self.table_status,
                self.table_arn)


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, monkeypatch, input_mocker):
    scenario_data.mm_movie = MagicMock(
        title='test-title', year=1984, release_date='1984-10-31', plot='test-plot')

    monkeypatch.setattr(query, 'SSLContext', lambda x: MagicMock())
    monkeypatch.setattr(query, 'SigV4AuthProvider', lambda x: MagicMock())
    monkeypatch.setattr(query, 'ExecutionProfile', lambda **kw: MagicMock())
    session = MagicMock(execute=lambda s, parameters: MagicMock(
        all=lambda: [scenario_data.mm_movie], one=lambda: scenario_data.mm_movie))
    monkeypatch.setattr(
        query, 'Cluster', lambda x, **kw: MagicMock(connect=lambda x: session))

    return MockManager(stub_runner, scenario_data, input_mocker)


def test_update_and_restore_table(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    with query.QueryManager('test-cert-path', MagicMock(), 'test-ks') as qm:
        mock_mgr.scenario_data.scenario.update_and_restore_table(qm)
        capt = capsys.readouterr()
        assert f"Marked {mock_mgr.scenario_data.mm_movie.title}" in capt.out
        assert f"Restored {mock_mgr.scenario_data.scenario.ks_wrapper.table_name}" in capt.out


@pytest.mark.parametrize('error, stop_on_index', [
    ('TESTERROR-stub_update_table, ks_wrapper.ks_name', 0),
    ('TESTERROR-stub_get_table, ks_wrapper.ks_name', 1),
    ('TESTERROR-stub_restore_table', 2),
    ('TESTERROR-stub_get_table', 3),
])
def test_update_and_restore_table_error(
        mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with query.QueryManager('test-cert-path', MagicMock(), 'test-ks') as qm:
        with pytest.raises(ClientError) as exc_info:
            mock_mgr.scenario_data.scenario.update_and_restore_table(qm)
        assert exc_info.value.response['Error']['Code'] == error
        assert error in caplog.text
