# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, cluster_data, input_mocker):
        self.cluster_data = cluster_data
        self.db_engine = 'test-engine'
        self.group_name = 'test-group'
        self.group = {'DBClusterParameterGroupName': self.group_name}
        self.engine_versions = [
            {'DBParameterGroupFamily': 'test-group-1'},
            {'DBParameterGroupFamily': 'test-group-1'},

        ]
        self.scenario_args = [self.db_engine, self.group_name]
        self.scenario_out = self.group
        version_choice = 1
        input_mocker.mock_answers([version_choice])
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(
                stubber.stub_describe_db_cluster_parameter_groups, self.group_name, [],
                error_code='DBParameterGroupNotFound')
            runner.add(stubber.stub_describe_db_engine_versions, self.db_engine, self.engine_versions)
            runner.add(stubber.stub_create_db_cluster_parameter_group, self.group_name, 'test-group-1')
            runner.add(stubber.stub_describe_db_cluster_parameter_groups, self.group_name, [self.group])


@pytest.fixture
def mock_mgr(stub_runner, cluster_data, input_mocker):
    return MockManager(stub_runner, cluster_data, input_mocker)


def test_create_parameter_group_exists(mock_mgr, capsys):
    mock_mgr.cluster_data.stubber.stub_describe_db_cluster_parameter_groups(
        mock_mgr.group_name, [mock_mgr.group])

    got_group = mock_mgr.cluster_data.scenario.create_parameter_group(*mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert got_group == mock_mgr.scenario_out
    assert f"'DBClusterParameterGroupName': '{mock_mgr.scenario_out['DBClusterParameterGroupName']}'" in capt.out


def test_create_parameter_group_not_exist(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.cluster_data.stubber)

    got_group = mock_mgr.cluster_data.scenario.create_parameter_group(*mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert got_group == mock_mgr.scenario_out
    assert f"'DBClusterParameterGroupName': '{mock_mgr.scenario_out['DBClusterParameterGroupName']}'" in capt.out


@pytest.mark.parametrize('error, stop_on_index', [
    ('TESTERROR-describe_db_cluster_parameter_groups', 0),
    ('TESTERROR-describe_db_engine_versions', 1),
    ('TESTERROR-create_db_cluster_parameter_group', 2),
    ('TESTERROR-describe_db_cluster_parameter_groups', 3),
])
def test_create_parameter_group_not_exist_error(
        mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.cluster_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.cluster_data.scenario.create_parameter_group(*mock_mgr.scenario_args)
    assert exc_info.value.response['Error']['Code'] == error

    assert error in caplog.text
