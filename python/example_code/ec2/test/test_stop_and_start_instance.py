# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from unittest.mock import MagicMock
from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.instance_id = 'test-instance'
        self.scenario_data.scenario.inst_wrapper.instance = (
            self.scenario_data.resource.Instance(self.instance_id))
        self.instance = MagicMock(
            id='test-instance', image_id='test-image', instance_type='test-type',
            key_name='test-key', vpc_id='test-vpc', public_ip_address='1.2.3.4', state={'Name': 'running'})

        self.scenario_data.scenario.eip_wrapper.elastic_ip = MagicMock(public_ip='1.2.3.4')
        answers = ['']
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_stop_instances, [self.instance_id])
            runner.add(stubber.stub_describe_instances, [
                MagicMock(id=self.instance_id, state={'Name': 'stopped'})])
            runner.add(stubber.stub_start_instances, [self.instance_id])
            runner.add(stubber.stub_describe_instances, [
                MagicMock(id=self.instance_id, state={'Name': 'running'})])
            runner.add(stubber.stub_describe_instances, [
                MagicMock(id=self.instance_id, state={'Name': 'running'})])


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_stop_and_start_instance(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.stop_and_start_instance()

    capt = capsys.readouterr()
    assert mock_mgr.instance_id in capt.out
    assert mock_mgr.scenario_data.scenario.eip_wrapper.elastic_ip.public_ip in capt.out


@pytest.mark.parametrize('error, stop_on_index', [
    ('TESTERROR-stub_stop_instances', 0),
    ('TESTERROR-stub_start_instances', 2),
    ('TESTERROR-stub_describe_instances', 4),
])
def test_stop_and_start_instance_error(
        mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.stop_and_start_instance()
    assert exc_info.value.response['Error']['Code'] == error

    assert error in caplog.text
