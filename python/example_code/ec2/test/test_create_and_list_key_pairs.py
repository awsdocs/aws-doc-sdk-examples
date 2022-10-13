# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from unittest.mock import patch, mock_open
from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.key_name = 'test-key-name'
        self.keys = [{
            'KeyName': f'key-{ind}', 'KeyType': f'type-{ind}', 'KeyFingerprint': f'fingerprint-{ind}'
        } for ind in range(1, 4)]
        answers = [self.key_name, 'y']
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_create_key_pair, self.key_name, None)
            runner.add(stubber.stub_describe_key_pairs, self.keys)


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_create_and_list_key_pairs(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    with patch('builtins.open', mock_open()) as mock_file:
        mock_mgr.scenario_data.scenario.create_and_list_key_pairs()
        mock_file.assert_called_with(f'{mock_mgr.key_name}.pem', 'w')

    capt = capsys.readouterr()
    assert mock_mgr.key_name in capt.out
    for key in mock_mgr.keys:
        assert key['KeyName'] in capt.out
        assert key['KeyFingerprint'] in capt.out


@pytest.mark.parametrize('error, stop_on_index', [
    ('TESTERROR-stub_create_key_pair', 0),
    ('TESTERROR-stub_describe_key_pairs', 1),
])
def test_create_and_list_key_pairs_error(
        mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with patch('builtins.open', mock_open()) as mock_file:
        with pytest.raises(ClientError) as exc_info:
            mock_mgr.scenario_data.scenario.create_and_list_key_pairs()
        assert exc_info.value.response['Error']['Code'] == error

    assert error in caplog.text
