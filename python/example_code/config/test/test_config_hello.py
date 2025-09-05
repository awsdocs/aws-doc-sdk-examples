# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for config_hello.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

import sys
import os
sys.path.append(os.path.join(os.path.dirname(__file__), '..'))
from config_hello import hello_config


@pytest.mark.parametrize("error_code", [None, "AccessDeniedException", "TestException"])
def test_hello_config(make_stubber, capsys, error_code):
    config_client = boto3.client("config")
    config_stubber = make_stubber(config_client)
    
    recorders = [
        {
            "name": "test-recorder-1",
            "recordingGroup": {"allSupported": True}
        },
        {
            "name": "test-recorder-2", 
            "recordingGroup": {"allSupported": False}
        }
    ]

    config_stubber.stub_describe_configuration_recorders(None, recorders, error_code=error_code)

    if error_code is None:
        hello_config(config_client)
        captured = capsys.readouterr()
        assert "Hello, AWS Config!" in captured.out
        assert "Found 2 configuration recorder(s)" in captured.out
        assert "test-recorder-1" in captured.out
        assert "test-recorder-2" in captured.out
    elif error_code == "AccessDeniedException":
        hello_config(config_client)
        captured = capsys.readouterr()
        assert "You don't have permission to access AWS Config" in captured.out
    else:
        with pytest.raises(ClientError) as exc_info:
            hello_config(config_client)
        assert exc_info.value.response["Error"]["Code"] == error_code


def test_hello_config_no_recorders(make_stubber, capsys):
    config_client = boto3.client("config")
    config_stubber = make_stubber(config_client)
    
    config_stubber.stub_describe_configuration_recorders(None, [], error_code=None)

    hello_config(config_client)
    captured = capsys.readouterr()
    assert "Hello, AWS Config!" in captured.out
    assert "No configuration recorders found" in captured.out