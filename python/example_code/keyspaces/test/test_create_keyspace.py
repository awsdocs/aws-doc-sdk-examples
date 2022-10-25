# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.ks_exists = False
        self.ks_name = 'test-ks'
        self.ks_arn = 'arn:aws:cassandra:test-region:111122223333:/keyspace/test-ks'
        self.keyspaces = [{
            'keyspaceName': f'ks-{ind}', 'resourceArn': self.ks_arn}
            for ind in range(1, 4)]
        answers = [self.ks_name]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            if self.ks_exists:
                runner.add(stubber.stub_get_keyspace, self.ks_name, self.ks_arn)
            else:
                runner.add(
                    stubber.stub_get_keyspace, self.ks_name, self.ks_arn,
                    error_code='ResourceNotFoundException')
                runner.add(stubber.stub_create_keyspace, self.ks_name, self.ks_arn)
            runner.add(stubber.stub_list_keyspaces, self.keyspaces)


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


@pytest.mark.parametrize('ks_exists', [True, False])
def test_create_keyspace(mock_mgr, capsys, ks_exists):
    mock_mgr.ks_exists = ks_exists
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.create_keyspace()

    capt = capsys.readouterr()
    assert mock_mgr.ks_name in capt.out
    for ks in mock_mgr.keyspaces:
        assert ks['keyspaceName'] in capt.out


@pytest.mark.parametrize('error, stop_on_index', [
    ('TESTERROR-stub_get_keyspace', 0),
    ('TESTERROR-stub_create_keyspace', 1),
    ('TESTERROR-stub_list_keyspaces', 2),
])
def test_create_keyspace_error(
        mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.create_keyspace()
    assert exc_info.value.response['Error']['Code'] == error

    assert error in caplog.text
