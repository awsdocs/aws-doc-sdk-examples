# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for inspector2_hello.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from inspector2_hello import hello_inspector

ACCOUNT_ID = "123456789012"


@pytest.fixture
def hello_stubber(make_stubber):
    inspector2_client = boto3.client("inspector2", region_name="us-east-1")
    return inspector2_client, make_stubber(inspector2_client)


@pytest.mark.parametrize(
    "members, expected",
    [
        (
            [{"accountId": "111122223333", "relationshipStatus": "ENABLED"}],
            "Account ID: 111122223333, Status: ENABLED",
        ),
        ([], "No member accounts found"),
    ],
)
def test_hello_inspector(hello_stubber, stub_runner, capsys, members, expected):
    inspector2_client, stubber = hello_stubber
    with stub_runner(None, None) as runner:
        runner.add(stubber.stub_batch_get_account_status, ACCOUNT_ID, "ENABLED")
        runner.add(stubber.stub_list_members, members)

    hello_inspector(inspector2_client)

    capt = capsys.readouterr()
    assert f"Account {ACCOUNT_ID} Inspector status: ENABLED" in capt.out
    assert expected in capt.out


def test_hello_inspector_not_delegated_admin(hello_stubber, stub_runner, capsys):
    """AccessDeniedException from ListMembers is reported, not raised."""
    inspector2_client, stubber = hello_stubber
    with stub_runner("AccessDeniedException", "stub_list_members") as runner:
        runner.add(stubber.stub_batch_get_account_status, ACCOUNT_ID, "ENABLED")
        runner.add(stubber.stub_list_members, [])

    hello_inspector(inspector2_client)

    assert "not an Inspector delegated administrator" in capsys.readouterr().out


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_batch_get_account_status", 0),
        # IAM denials on the status check are still raised.
        ("AccessDeniedException", 0),
        ("TESTERROR-stub_list_members", 1),
    ],
)
def test_hello_inspector_error(
    hello_stubber, stub_runner, caplog, error, stop_on_index
):
    inspector2_client, stubber = hello_stubber
    with stub_runner(error, stop_on_index) as runner:
        runner.add(stubber.stub_batch_get_account_status, ACCOUNT_ID, "ENABLED")
        runner.add(stubber.stub_list_members, [])

    with pytest.raises(ClientError) as exc_info:
        hello_inspector(inspector2_client)
    assert exc_info.value.response["Error"]["Code"] == error

    assert error in caplog.text
