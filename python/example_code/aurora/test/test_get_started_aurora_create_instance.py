# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, cluster_data, input_mocker):
        self.cluster_data = cluster_data
        self.cluster_id = 'test-cluster'
        self.instance_id = self.cluster_id
        self.db_engine = 'test-engine'
        self.db_engine_version = 'test-engine-version'
        self.instance_opts = [{'DBInstanceClass': 'test-class'}]
        self.scenario_args = [{
            'DBClusterIdentifier': self.cluster_id, 'Engine': self.db_engine, 'EngineVersion': self.db_engine_version}]
        self.scenario_out = {'DBInstanceIdentifier': self.instance_id, 'DBInstanceStatus': 'available'}
        self.instance_choice = 1
        answers = [self.instance_choice]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_describe_db_instances, self.cluster_id, error_code='DBInstanceNotFound')
            runner.add(
                stubber.stub_describe_orderable_db_instance_options, self.db_engine, self.db_engine_version,
                self.instance_opts)
            runner.add(
                stubber.stub_create_db_instance, self.instance_id, self.db_engine,
                self.instance_opts[self.instance_choice-1]['DBInstanceClass'], cluster_id=self.cluster_id)
            runner.add(stubber.stub_describe_db_instances, self.cluster_id)


@pytest.fixture
def mock_mgr(stub_runner, cluster_data, input_mocker):
    return MockManager(stub_runner, cluster_data, input_mocker)


def test_create_instance_exists(mock_mgr, capsys):
    mock_mgr.cluster_data.stubber.stub_describe_db_instances(mock_mgr.cluster_id)

    got_output = mock_mgr.cluster_data.scenario.create_instance(*mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert got_output == mock_mgr.scenario_out
    assert f"'DBInstanceIdentifier': '{mock_mgr.scenario_out['DBInstanceIdentifier']}'" in capt.out



def test_create_instance(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.cluster_data.stubber)

    got_output = mock_mgr.cluster_data.scenario.create_instance(*mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert got_output == mock_mgr.scenario_out
    assert f"'DBInstanceIdentifier': '{mock_mgr.scenario_out['DBInstanceIdentifier']}'" in capt.out


@pytest.mark.parametrize('error, stop_on_index', [
    ('TESTERROR-stub_describe_db_instances', 0),
    ('TESTERROR-stub_describe_orderable_db_instances', 1),
    ('TESTERROR-stub_create_db_instance', 2),
    ('TESTERROR-stub_describe_db_instances', 3),
])
def test_create_instance_error(
        mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.cluster_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.cluster_data.scenario.create_instance(*mock_mgr.scenario_args)
    assert exc_info.value.response['Error']['Code'] == error

    assert error in caplog.text
