# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from io import BytesIO
import urllib.request
from botocore.exceptions import ClientError
from botocore.stub import ANY
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.group = {
            'id': 'test-group-id', 'group_name': 'test_group_name', 'ip_permissions': []}
        answers = [self.group['group_name'], '']
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_create_security_group, self.group['group_name'], self.group['id'])
            runner.add(stubber.stub_describe_security_groups, [self.group])
            runner.add(stubber.stub_authorize_security_group_ingress, self.group['id'], ANY)
            runner.add(stubber.stub_describe_security_groups, [self.group])


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


@pytest.fixture(autouse=True)
def patch_urlopen(monkeypatch):
    monkeypatch.setattr(urllib.request, 'urlopen', lambda x: BytesIO(b'1.2.3.4'))


def test_create_security_group(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.create_security_group()

    capt = capsys.readouterr()
    assert mock_mgr.group['group_name'] in capt.out
    assert mock_mgr.group['id'] in capt.out


@pytest.mark.parametrize('error, stop_on_index', [
    ('TESTERROR-stub_create_security_group', 0),
    ('TESTERROR-stub_authorize_security_group_ingress', 2),
    ('TESTERROR-stub_describe_security_groups', 3),
])
def test_create_security_group_error(
        mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.create_security_group()
    assert exc_info.value.response['Error']['Code'] == error

    assert error in caplog.text
