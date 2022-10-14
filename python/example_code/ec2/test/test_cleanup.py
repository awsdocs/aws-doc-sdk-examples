# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from unittest.mock import MagicMock
from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker, mock_address):
        self.mock_address = mock_address
        self.instance_id = 'test-instance'
        self.sg_id = 'test-group'
        self.key_name = 'test-key'
        scenario_data.scenario.eip_wrapper.elastic_ip = scenario_data.resource.VpcAddress(
            self.mock_address.allocation_id)
        scenario_data.scenario.inst_wrapper.instance = scenario_data.resource.Instance(self.instance_id)
        scenario_data.scenario.sg_wrapper.security_group = scenario_data.resource.SecurityGroup(self.sg_id)
        scenario_data.scenario.key_wrapper.key_pair = scenario_data.resource.KeyPair(self.key_name)
        self.scenario_data = scenario_data
        answers = ['y']
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_describe_addresses, [self.mock_address])
            runner.add(stubber.stub_disassociate_elastic_ip, self.mock_address.association_id)
            runner.add(stubber.stub_release_elastic_ip, self.mock_address.allocation_id)
            runner.add(stubber.stub_terminate_instances, [self.instance_id])
            runner.add(
                stubber.stub_describe_instances, [MagicMock(id=self.instance_id, state={'Name': 'terminated'})])
            runner.add(stubber.stub_delete_security_group, self.sg_id)
            runner.add(stubber.stub_delete_key_pair, self.key_name)


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker, mock_address):
    return MockManager(stub_runner, scenario_data, input_mocker, mock_address)


def test_cleanup(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.cleanup()

    capt = capsys.readouterr()
    assert mock_mgr.mock_address.allocation_id in capt.out
    assert mock_mgr.instance_id in capt.out
    assert mock_mgr.sg_id in capt.out
    assert mock_mgr.key_name in capt.out


@pytest.mark.parametrize('error, stop_on_index', [
    ('TESTERROR-stub_YOUR_STUB', 0),

    ('TESTERROR-stub_describe_addresses', 0),
    ('TESTERROR-stub_disassociate_elastic_ip', 1),
    ('TESTERROR-stub_release_elastic_ip', 2),
    ('TESTERROR-stub_terminate_instances', 3),
    ('TESTERROR-stub_delete_security_group', 5),
    ('TESTERROR-stub_delete_key_pair', 6),
])
def test_cleanup_error(
        mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.cleanup()
    assert exc_info.value.response['Error']['Code'] == error

    assert error in caplog.text
