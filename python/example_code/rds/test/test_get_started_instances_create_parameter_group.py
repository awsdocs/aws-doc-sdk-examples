# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, instance_data, input_mocker):
        self.instance_data = instance_data
        self.db_engine = 'test-engine'
        self.group_name = 'test-group'
        self.desc = 'Example parameter group.'
        self.group = {'DBParameterGroupName': self.group_name}
        self.engine_versions = [
            {'DBParameterGroupFamily': 'test-group-1'},
            {'DBParameterGroupFamily': 'test-group-1'},

        ]
        self.scenario_args = [self.group_name, self.db_engine]
        self.scenario_out = self.group
        answers = [1]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(
                stubber.stub_describe_db_parameter_groups, self.group_name, error_code='DBParameterGroupNotFound')
            runner.add(stubber.stub_describe_db_engine_versions, self.db_engine, self.engine_versions)
            runner.add(
                stubber.stub_create_db_parameter_group, self.group_name, 'test-group-1', 'Example parameter group.')
            runner.add(stubber.stub_describe_db_parameter_groups, self.group_name)


@pytest.fixture
def mock_mgr(stub_runner, instance_data, input_mocker):
    return MockManager(stub_runner, instance_data, input_mocker)


def test_create_parameter_group_exist(mock_mgr, capsys):
    mock_mgr.instance_data.stubber.stub_describe_db_parameter_groups(mock_mgr.group_name)

    got_output = mock_mgr.instance_data.scenario.create_parameter_group(*mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert got_output == mock_mgr.scenario_out
    assert f"'DBParameterGroupName': '{mock_mgr.scenario_out['DBParameterGroupName']}'" in capt.out


def test_create_parameter_group(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.instance_data.stubber)

    got_output = mock_mgr.instance_data.scenario.create_parameter_group(*mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert got_output == mock_mgr.scenario_out
    assert f"'DBParameterGroupName': '{mock_mgr.scenario_out['DBParameterGroupName']}'" in capt.out


@pytest.mark.parametrize('error, stop_on_index', [
    ('TESTERROR-stub_describe_db_parameter_groups', 0),
    ('TESTERROR-stub_describe_db_engine_versions', 1),
    ('TESTERROR-stub_create_db_parameter_group', 2),
    ('TESTERROR-stub_describe_db_parameter_groups', 3),
])
def test_create_parameter_group_error(
        mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.instance_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.instance_data.scenario.create_parameter_group(*mock_mgr.scenario_args)
    assert exc_info.value.response['Error']['Code'] == error

    assert error in caplog.text
