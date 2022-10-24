# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from unittest.mock import MagicMock
from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker, mock_address):
        self.scenario_data = scenario_data
        self.mock_address = mock_address
        self.scenario_data.scenario.inst_wrapper.instance = MagicMock(id='test-inst')
        self.scenario_data.scenario.key_wrapper.key_file_path = 'test-key-file'
        answers = ['']
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_allocate_elastic_ip, self.mock_address)
            runner.add(stubber.stub_describe_addresses, [self.mock_address])
            runner.add(
                stubber.stub_associate_elastic_ip, self.mock_address,
                self.scenario_data.scenario.inst_wrapper.instance.id)
            runner.add(stubber.stub_describe_addresses, [self.mock_address])


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker, mock_address):
    return MockManager(stub_runner, scenario_data, input_mocker, mock_address)


def test_associate_elastic_ip(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.associate_elastic_ip()

    capt = capsys.readouterr()
    assert f'ec2-user@{mock_mgr.mock_address.public_ip}' in capt.out
    assert mock_mgr.scenario_data.scenario.key_wrapper.key_file_path in capt.out


@pytest.mark.parametrize('error, stop_on_index', [
    ('TESTERROR-stub_allocate_elastic_ip', 0),
    ('TESTERROR-stub_associate_elastic_ip', 2),
])
def test_associate_elastic_ip_error(
        mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.associate_elastic_ip()
    assert exc_info.value.response['Error']['Code'] == error

    assert error in caplog.text
