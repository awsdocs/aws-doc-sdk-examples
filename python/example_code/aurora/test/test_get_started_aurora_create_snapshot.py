# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, cluster_data, input_mocker):
        self.cluster_data = cluster_data
        self.cluster_name = 'test-cluster'
        self.scenario_args = [self.cluster_name]
        self.snapshot_id = f'{self.cluster_name}-test-guid'
        self.scenario_out = None
        answers = ['y']
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_create_db_cluster_snapshot, self.snapshot_id, self.cluster_name)
            runner.add(stubber.stub_describe_db_cluster_snapshots, self.snapshot_id)


@pytest.fixture
def mock_mgr(stub_runner, cluster_data, input_mocker):
    return MockManager(stub_runner, cluster_data, input_mocker)


def test_create_snapshot(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.cluster_data.stubber)

    mock_mgr.cluster_data.scenario.create_snapshot(*mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert f"'DBClusterSnapshotIdentifier': '{mock_mgr.snapshot_id}'" in capt.out


@pytest.mark.parametrize('error, stop_on_index', [
    ('TESTERROR-stub_create_db_cluster_snapshot', 0),
    ('TESTERROR-stub_describe_db_cluster_snapshots', 1),
])
def test_create_snapshot_error(
        mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.cluster_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.cluster_data.scenario.create_snapshot(*mock_mgr.scenario_args)
    assert exc_info.value.response['Error']['Code'] == error

    assert error in caplog.text
