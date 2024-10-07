# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for kms_scenario.py.
"""

import json

import pytest
from botocore.exceptions import ClientError


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
        self.policy = {"Statement": ["test-statement"]}
        self.message = "Here is the message that will be digitally signed"
        self.signature = b"test-signature"
        self.asymmetric_key_id = "11223344-1122-1122-1122-112233445566"
        self.tag_key = "Environment"
        self.tag_value = "Production"
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
            "",
            "",
            "",
            "",
            self.account_id,
            "",
            "",
            "",
            "",
            "",
            "",
            "y",
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
            runner.add(
                stubber.stub_revoke_grant,
                self.key_id,
                self.grant["GrantId"],
            )
            runner.add(
                stubber.stub_decrypt,
                self.key_id,
                self.ciphertext_blob,
                self.plain_text,
            )
            runner.add(stubber.stub_put_key_policy, self.key_id)
            runner.add(
                stubber.stub_get_key_policy,
                self.key_id,
                json.dumps(self.policy),
            )
            runner.add(stubber.stub_asymmetric_create_key, self.asymmetric_key_id)
            runner.add(
                stubber.stub_sign,
                self.asymmetric_key_id,
                self.message.encode(),
                self.signature,
            )
            runner.add(
                stubber.stub_verify,
                self.asymmetric_key_id,
                self.message.encode(),
                self.signature,
            )
            runner.add(
                stubber.stub_tag_resource,
                self.key_id,
                self.tag_key,
                self.tag_value,
            )
            runner.add(stubber.stub_delete_alias, self.alias_name)
            runner.add(stubber.stub_schedule_key_deletion, self.key_id, self.window)
            runner.add(
                stubber.stub_schedule_key_deletion, self.asymmetric_key_id, self.window
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_kms_scenario(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.kms_scenario()


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_create_key", 0),
        ("TESTERROR-stub_describe_key", 1),
        ("TESTERROR-stub_encrypt", 2),
        ("TESTERROR-stub_create_alias", 3),
        ("TESTERROR-stub_list_aliases", 4),
        ("TESTERROR-stub_enable_key_rotation", 5),
        ("TESTERROR-stub_create_grant", 6),
        ("TESTERROR-stub_list_grants", 7),
        ("TESTERROR-stub_revoke_grant", 8),
        ("TESTERROR-stub_decrypt", 9),
        ("TESTERROR-stub_put_key_policy", 10),
        ("TESTERROR-stub_get_key_policy", 11),
        ("TESTERROR-stub_asymmetric_create_key", 12),
        ("TESTERROR-stub_sign", 13),
        ("TESTERROR-stub_verify", 14),
        ("TESTERROR-stub_tag_resource", 15),
        ("TESTERROR-stub_delete_alias", 16),
        ("TESTERROR-stub_schedule_key_deletion", 17),
        ("TESTERROR-stub_schedule_key_deletion", 18),
    ],
)
def test_kms_scenario_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.kms_scenario()
