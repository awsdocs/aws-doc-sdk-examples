# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from unittest.mock import MagicMock
from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.image_names = ['amzn2-image-1', 'amzn2-image-2']
        self.images = [MagicMock(
            id=f'test-id-{ind}', description=f'desc-{ind}', architecture='test-arch') for ind in range(1, 3)]
        self.inst_types = [f'test-type-{ind}' for ind in range(1, 3)]
        self.scenario_data.scenario.key_wrapper.key_pair = MagicMock()
        self.scenario_data.scenario.key_wrapper.key_pair.name = 'test-key'
        self.scenario_data.scenario.sg_wrapper.security_group = MagicMock(id='test-sg')
        self.instance = MagicMock(
            id='test-instance', image_id=self.images[0].id, instance_type=self.inst_types[0],
            key_name=self.scenario_data.scenario.key_wrapper.key_pair.name,
            vpc_id='test-vpc', public_ip_address='1.2.3.4', state={'Name': 'running'})
        answers = [1, 1, '']
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(
                self.scenario_data.ssm_stubber.stub_get_parameters_by_path, self.image_names,
                [i.id for i in self.images])
            runner.add(stubber.stub_describe_images, self.images)
            runner.add(stubber.stub_describe_instance_types, self.inst_types)
            runner.add(
                stubber.stub_create_instances, self.images[0].id, self.inst_types[0],
                self.scenario_data.scenario.key_wrapper.key_pair.name, 1, self.instance.id,
                [self.scenario_data.scenario.sg_wrapper.security_group.id])
            runner.add(stubber.stub_describe_instances, [self.instance])
            runner.add(stubber.stub_describe_instances, [self.instance])


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_create_instance(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.create_instance()

    capt = capsys.readouterr()
    assert mock_mgr.images[0].description in capt.out
    assert mock_mgr.inst_types[0] in capt.out
    assert mock_mgr.instance.id in capt.out


@pytest.mark.parametrize('error, stop_on_index', [
    ('TESTERROR-stub_describe_images', 1),
    ('TESTERROR-stub_describe_instance_types', 2),
    ('TESTERROR-stub_create_instances', 3),
    ('TESTERROR-stub_describe_instances', 5),
])
def test_create_instance_error(
        mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.create_instance()
    assert exc_info.value.response['Error']['Code'] == error

    assert error in caplog.text
