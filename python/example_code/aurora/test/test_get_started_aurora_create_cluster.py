# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, cluster_data, input_mocker):
        self.cluster_data = cluster_data
        self.cluster_name = 'test-cluster'
        self.db_name = 'test-db'
        self.engine = 'test-engine'
        self.group = {'DBParameterGroupFamily': 'test-family', 'DBClusterParameterGroupName': 'test-group'}
        self.engine_versions = [{'EngineVersion': f'test-version-{i}'} for i in range(1, 4)]
        self.admin = 'test-admin'
        self.password = 'test-password'
        self.scenario_args = [self.cluster_name, self.engine, self.db_name, self.group]
        self.scenario_out = {'DBClusterIdentifier': self.cluster_name, 'Status': 'available'}
        self.engine_choice = 1
        answers = [self.admin, self.password, self.engine_choice]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_describe_db_clusters, self.cluster_name, error_code='DBClusterNotFoundFault')
            runner.add(
                stubber.stub_describe_db_engine_versions, self.engine, self.engine_versions,
                param_family=self.group['DBParameterGroupFamily'])
            runner.add(
                stubber.stub_create_db_cluster, self.cluster_name, self.db_name, self.admin, self.password,
                engine=self.engine, engine_mode=None, enable_http=None,
                group_name=self.group['DBClusterParameterGroupName'],
                engine_version=self.engine_versions[self.engine_choice-1]['EngineVersion'])
            runner.add(stubber.stub_describe_db_clusters, self.cluster_name)


@pytest.fixture
def mock_mgr(stub_runner, cluster_data, input_mocker):
    return MockManager(stub_runner, cluster_data, input_mocker)


def test_create_cluster_exists(mock_mgr, capsys):
    mock_mgr.cluster_data.stubber.stub_describe_db_clusters(mock_mgr.cluster_name)

    got_output = mock_mgr.cluster_data.scenario.create_cluster(*mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert got_output == mock_mgr.scenario_out
    assert f"'DBClusterIdentifier': '{mock_mgr.scenario_out['DBClusterIdentifier']}'" in capt.out


def test_create_cluster_not_exist(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.cluster_data.stubber)

    got_output = mock_mgr.cluster_data.scenario.create_cluster(*mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert got_output == mock_mgr.scenario_out
    assert f"'DBClusterIdentifier': '{mock_mgr.scenario_out['DBClusterIdentifier']}'" in capt.out


@pytest.mark.parametrize('error, stop_on_index', [
    ('TESTERROR-stub_describe_db_clusters', 0),
    ('TESTERROR-stub_describe_db_engine_versions', 1),
    ('TESTERROR-stub_create_db_cluster', 2),
    ('TESTERROR-stub_describe_db_clusters', 3),
])
def test_create_cluster_error(
        mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.cluster_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.cluster_data.scenario.create_cluster(*mock_mgr.scenario_args)
    assert exc_info.value.response['Error']['Code'] == error

    assert error in caplog.text
