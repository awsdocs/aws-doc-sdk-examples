# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, instance_data, input_mocker):
        self.instance_data = instance_data
        self.instance_id = 'test-instance'
        self.db_name = 'test-db'
        self.db_engine = 'test-engine'
        self.db_engine_versions = [{
            'Engine': self.db_engine, 'EngineVersion': f'test-version-{i}'} for i in range(1, 4)]
        self.group = {'DBParameterGroupFamily': 'test-family', 'DBParameterGroupName': 'test-group'}
        self.instance_opts = [{'DBInstanceClass': 'test-class-micro'}]
        self.scenario_args = [self.instance_id, self.db_name, self.db_engine, self.group]
        self.scenario_out = {'DBInstanceIdentifier': self.instance_id, 'DBInstanceStatus': 'available'}
        self.admin = 'test-admin'
        self.password = 'test-password'
        self.engine_choice = 1
        self.instance_choice = 1
        answers = [self.admin, self.password, self.engine_choice, self.instance_choice]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_describe_db_instances, self.instance_id, error_code='DBInstanceNotFound')
            runner.add(
                stubber.stub_describe_db_engine_versions, self.db_engine, self.db_engine_versions,
                param_family=self.group['DBParameterGroupFamily'])
            runner.add(
                stubber.stub_describe_orderable_db_instance_options, self.db_engine,
                self.db_engine_versions[self.engine_choice-1]['EngineVersion'], self.instance_opts)
            runner.add(
                stubber.stub_create_db_instance, self.instance_id, self.db_engine,
                self.instance_opts[self.instance_choice-1]['DBInstanceClass'],
                db_name=self.db_name, param_group_name=self.group['DBParameterGroupName'],
                db_engine_version=self.db_engine_versions[self.engine_choice-1]['EngineVersion'],
                storage_type='standard', allocated_storage=5, admin_name=self.admin, admin_password=self.password)
            runner.add(stubber.stub_describe_db_instances, self.instance_id)


@pytest.fixture
def mock_mgr(stub_runner, instance_data, input_mocker):
    return MockManager(stub_runner, instance_data, input_mocker)


def test_create_instance_exist(mock_mgr, capsys):
    mock_mgr.instance_data.stubber.stub_describe_db_instances(mock_mgr.instance_id)

    got_output = mock_mgr.instance_data.scenario.create_instance(*mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert got_output == mock_mgr.scenario_out
    assert f"'DBInstanceIdentifier': '{mock_mgr.scenario_out['DBInstanceIdentifier']}'" in capt.out


def test_create_instance(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.instance_data.stubber)

    got_output = mock_mgr.instance_data.scenario.create_instance(*mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert got_output == mock_mgr.scenario_out
    assert f"'DBInstanceIdentifier': '{mock_mgr.scenario_out['DBInstanceIdentifier']}'" in capt.out


@pytest.mark.parametrize('error, stop_on_index', [
    ('TESTERROR-stub_describe_db_instances', 0),
    ('TESTERROR-stub_describe_db_engine_versions', 1),
    ('TESTERROR-stub_describe_orderable_db_instance_options', 2),
    ('TESTERROR-stub_create_db_instance', 3),
    ('TESTERROR-stub_describe_db_instances', 4),
])
def test_create_instance_error(
        mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.instance_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.instance_data.scenario.create_instance(*mock_mgr.scenario_args)
    assert exc_info.value.response['Error']['Code'] == error

    assert error in caplog.text
