# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, cluster_data, input_mocker):
        self.cluster_data = cluster_data
        self.db_inst = {'DBInstanceIdentifier': 'test-instance'}
        self.cluster = {'DBClusterIdentifier': 'test-cluster'}
        self.group = {'DBClusterParameterGroupName': 'test-group'}
        self.scenario_args = [self.db_inst, self.cluster, self.group]
        self.scenario_out = None
        answers = ['y']
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_delete_db_instance, self.db_inst['DBInstanceIdentifier'])
            runner.add(stubber.stub_delete_db_cluster, self.cluster['DBClusterIdentifier'])
            runner.add(
                stubber.stub_describe_db_instances, self.db_inst['DBInstanceIdentifier'],
                error_code='DBInstanceNotFound')
            runner.add(
                stubber.stub_describe_db_clusters, self.cluster['DBClusterIdentifier'],
                error_code='DBClusterNotFoundFault')
            runner.add(stubber.stub_delete_db_cluster_parameter_group, self.group['DBClusterParameterGroupName'])


@pytest.fixture
def mock_mgr(stub_runner, cluster_data, input_mocker):
    return MockManager(stub_runner, cluster_data, input_mocker)


def test_cleanup(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.cluster_data.stubber)

    mock_mgr.cluster_data.scenario.cleanup(*mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert mock_mgr.db_inst['DBInstanceIdentifier'] in capt.out
    assert mock_mgr.cluster['DBClusterIdentifier'] in capt.out
    assert mock_mgr.group['DBClusterParameterGroupName'] in capt.out


@pytest.mark.parametrize('error, stop_on_index', [
    ('TESTERROR-stub_delete_db_instance', 0),
    ('TESTERROR-stub_delete_db_cluster', 1),
    ('TESTERROR-stub_describe_db_instances', 2),
    ('TESTERROR-stub_describe_db_clusters', 3),
])
def test_cleanup_error(
        mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.cluster_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.cluster_data.scenario.cleanup(*mock_mgr.scenario_args)
    assert exc_info.value.response['Error']['Code'] == error

    assert error in caplog.text
