# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for inspector2_hello.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from inspector2_hello import hello_inspector


@pytest.mark.parametrize(
    "members, error_code, stop_on_action",
    [
        ([{"accountId": "111122223333", "relationshipStatus": "ENABLED"}], None, None),
        ([], None, None),
        ([], "TESTERROR-stub_list_members", "stub_list_members"),
    ],
)
def test_hello_inspector(
    make_stubber, stub_runner, capsys, caplog, members, error_code, stop_on_action
):
    inspector2_client = boto3.client("inspector2", region_name="us-east-1")
    inspector2_stubber = make_stubber(inspector2_client)

    with stub_runner(error_code, stop_on_action) as runner:
        runner.add(inspector2_stubber.stub_list_members, members)

    if error_code is None:
        hello_inspector(inspector2_client)
        capt = capsys.readouterr()
        if members:
            assert "Found 1 member account(s):" in capt.out
            assert "Account ID: 111122223333, Status: ENABLED" in capt.out
        else:
            assert "No member accounts found" in capt.out
    else:
        with pytest.raises(ClientError) as exc_info:
            hello_inspector(inspector2_client)
        assert exc_info.value.response["Error"]["Code"] == error_code
        assert error_code in caplog.text
