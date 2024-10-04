# SPDX-License-Identifier: Apache-2.0

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.key_description = "Created by the AWS KMS API"
        self.window = 7
        self.key_id = "12345678-1234-1234-1234-1234567890ab"
        self.plain_text = b"Hello, AWS KMS!"
        self.ciphertext_blob = b"0123456789abcdef"
        self.alias_name = "alias/MyAlias"
        self.account_id = "123456789012"
        self.operations = [
            "Encrypt",
            "Decrypt",
            "DescribeKey",
        ]
        self.grant = {"GrantToken": "test-grant-token", "GrantId": "test-grant-id"}
        answers = [
            "",
            "",
            "",
            "",
            "",
            "",
            self.alias_name,
            "",
            "",
            "",
            self.account_id,
            "",
            "",
            "",
            "y",
        ]
        input_mocker.mock_answers(answers)
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_create_key, self.key_description, self.key_id)
            runner.add(stubber.stub_describe_key, self.key_id, "ENABLED")
            runner.add(
                stubber.stub_encrypt, self.key_id, self.plain_text, self.ciphertext_blob
            )
            runner.add(
                stubber.stub_create_alias,
                self.alias_name,
                self.key_id,
            )
            runner.add(
                stubber.stub_list_aliases,
                10,
                [self.alias_name] * 10,
            )
            runner.add(stubber.stub_enable_key_rotation, self.key_id)
            runner.add(
                stubber.stub_create_grant,
                self.key_id,
                self.account_id,
                self.operations,
                self.grant,
            )
            runner.add(
                stubber.stub_list_grants,
                self.key_id,
                ["test-grant"] * 5,
            )
            runner.add(stubber.stub_delete_alias, self.alias_name)
            runner.add(stubber.stub_schedule_key_deletion, self.key_id, self.window)
            # if self.ks_exists:
            #     runner.add(stubber.stub_get_keyspace, self.ks_name, self.ks_arn)
            # else:
            #     runner.add(
            #         stubber.stub_get_keyspace,
            #         self.ks_name,
            #         self.ks_arn,
            #         error_code="ResourceNotFoundException",
            #     )
            #     runner.add(stubber.stub_create_keyspace, self.ks_name, self.ks_arn)
            #     runner.add(stubber.stub_get_keyspace, self.ks_name, self.ks_arn)
            # runner.add(stubber.stub_list_keyspaces, self.keyspaces)


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_kms_scenario(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.kms_scenario()

    # capt = capsys.readouterr()
    # assert mock_mgr.ks_name in capt.out
    # for ks in mock_mgr.keyspaces:
    #     assert ks["keyspaceName"] in capt.out


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_create_key", 0),
        ("TESTERROR-stub_describe_key", 1),
        ("TESTERROR-stub_stub_encrypt", 2),
        ("TESTERROR-stub_create_alias", 3),
        ("TESTERROR-stub_list_aliases", 4),
        ("TESTERROR-stub_enable_key_rotation", 5),
        ("TESTERROR-stub_create_grant", 6),
        ("TESTERROR-stub_list_grants", 7),
        ("TESTERROR-stub_delete_alias", 8),
        ("TESTERROR-stub_schedule_key_deletion", 9),
    ],
)
def test_kms_scenario_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.kms_scenario()
    # assert exc_info.value.response["Error"]["Code"] == error
    #
    # assert error in caplog.text
