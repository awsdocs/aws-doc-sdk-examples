# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, cluster_data, input_mocker):
        self.cluster_data = cluster_data
        self.group_name = 'test-group'
        param_values = [str(ind*10) for ind in range(1, 4)]
        self.parameters = [{
            'ParameterName': f'auto_increment_{ind}', 'ParameterValue': param_values[ind-1],
            'IsModifiable': True, 'DataType': 'integer', 'Description': 'Test description',
            'AllowedValues': f'{ind}-{ind}00'
        } for ind in range(1, 4)]
        self.scenario_args = [self.group_name]
        self.scenario_out = None
        input_mocker.mock_answers(param_values)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_describe_db_cluster_parameters, self.group_name, self.parameters)
            runner.add(stubber.stub_modify_db_cluster_parameter_group, self.group_name, self.parameters)
            runner.add(stubber.stub_describe_db_cluster_parameters, self.group_name, self.parameters, source='user')


@pytest.fixture
def mock_mgr(stub_runner, cluster_data, input_mocker):
    return MockManager(stub_runner, cluster_data, input_mocker)


def test_set_user_parameters(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.cluster_data.stubber)

    mock_mgr.cluster_data.scenario.set_user_parameters(*mock_mgr.scenario_args)

    capt = capsys.readouterr()
    for param in mock_mgr.parameters:
        assert f"'ParameterName': '{param['ParameterName']}'" in capt.out


@pytest.mark.parametrize('error, stop_on_index', [
    ('TESTERROR-stub_describe_db_cluster_parameters', 0),
    ('TESTERROR-stub_modify_db_cluster_parameter_group', 1),
    ('TESTERROR-stub_describe_db_cluster_parameters', 2),
])
def test_set_user_parameters_error(
        mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.cluster_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.cluster_data.scenario.set_user_parameters(*mock_mgr.scenario_args)
        assert exc_info.value.response['Error']['Code'] == error

    assert error in caplog.text
