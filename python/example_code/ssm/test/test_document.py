# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for document.py functions.
"""

import boto3
import pytest
from botocore.exceptions import ClientError

from document import DocumentWrapper
from logging.handlers import QueueHandler
import logging
import queue

log_queue = queue.LifoQueue()

queue_handler = QueueHandler(log_queue)
document_logger = logging.getLogger("document")
document_logger.addHandler(queue_handler)


@pytest.mark.parametrize("error_code", [None, "TestException", "DocumentAlreadyExists"])
def test_create(make_stubber, error_code):
    ssm_client = boto3.client("ssm")
    ssm_stubber = make_stubber(ssm_client)
    document_wrapper = DocumentWrapper(ssm_client)
    name = "python-test"
    content = """
{
    "schemaVersion": "2.2",
    "description": "Run a simple shell command",
    "mainSteps": [
        {
            "action": "aws:runShellScript",
            "name": "runEchoCommand",
            "inputs": {
              "runCommand": [
                "echo 'Hello, world!'"
              ]
            }
        }
    ]
}
            """

    ssm_stubber.stub_create_document(
        content,
        name,
        error_code=error_code,
    )

    if error_code is None:
        document_wrapper.create(
            content,
            name,
        )
        assert document_wrapper.name == name
    elif error_code == "DocumentAlreadyExists":
        document_wrapper.create(
            content,
            name,
        )
        assert document_wrapper.name == name
        assert (
            log_queue.qsize() > 0 and "already exists" in log_queue.get().getMessage()
        )
    else:
        with pytest.raises(ClientError) as exc_info:
            document_wrapper.create(
                content,
                name,
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "InvalidDocument", "TestException"])
def test_delete(make_stubber, error_code):
    ssm_client = boto3.client("ssm")
    ssm_stubber = make_stubber(ssm_client)
    document_wrapper = DocumentWrapper(ssm_client)
    name = "python-test"
    document_wrapper.name = name

    ssm_stubber.stub_delete_document(
        name,
        error_code=error_code,
    )

    if error_code is None:
        document_wrapper.delete()
        assert document_wrapper.name is None
    elif error_code == "InvalidDocument":
        with pytest.raises(ssm_client.exceptions.InvalidDocument) as exc_info:
            document_wrapper.delete()
        assert exc_info.value.response["Error"]["Code"] == error_code
    else:
        with pytest.raises(ClientError) as exc_info:
            document_wrapper.delete()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "InvalidDocument", "TestException"])
def test_describe_document(make_stubber, error_code):
    ssm_client = boto3.client("ssm")
    ssm_stubber = make_stubber(ssm_client)
    document_wrapper = DocumentWrapper(ssm_client)
    name = "python-test"
    document_wrapper.name = name

    ssm_stubber.stub_describe_document(
        name,
        error_code=error_code,
    )

    if error_code is None:
        status = document_wrapper.describe()
        assert status == "Active"
    elif error_code == "InvalidDocument":
        with pytest.raises(ssm_client.exceptions.InvalidDocument) as exc_info:
            document_wrapper.describe()
        assert exc_info.value.response["Error"]["Code"] == error_code
    else:
        with pytest.raises(ClientError) as exc_info:
            document_wrapper.describe()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "InvalidInstanceId", "TestException"])
def test_list_commands(make_stubber, error_code):
    ssm_client = boto3.client("ssm")
    ssm_stubber = make_stubber(ssm_client)
    document_wrapper = DocumentWrapper(ssm_client)
    instance_id = "XXXXXXXXXXXX"

    ssm_stubber.stub_list_commands(
        instance_id=instance_id,
        error_code=error_code,
    )

    if error_code is None:
        document_wrapper.list_commands(instance_id)

    elif error_code == "InvalidInstanceId":
        with pytest.raises(ssm_client.exceptions.InvalidInstanceId) as exc_info:
            document_wrapper.list_commands(instance_id)

        assert exc_info.value.response["Error"]["Code"] == error_code
    else:
        with pytest.raises(ClientError) as exc_info:
            document_wrapper.list_commands(instance_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "InvalidDocument", "TestException"])
def test_send_command(make_stubber, error_code):
    ssm_client = boto3.client("ssm")
    ssm_stubber = make_stubber(ssm_client)
    document_wrapper = DocumentWrapper(ssm_client)
    name = "python-test"
    document_wrapper.name = name
    instance_ids = ["i-0123456789abcdef"]
    command_id = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"

    ssm_stubber.stub_send_command(
        instance_ids,
        document_name=name,
        command_id=command_id,
        error_code=error_code,
    )

    if error_code is None:
        response = document_wrapper.send_command(instance_ids)
        assert response == command_id
    elif error_code == "InvalidDocument":
        with pytest.raises(ssm_client.exceptions.InvalidDocument) as exc_info:
            document_wrapper.send_command(instance_ids)
        assert exc_info.value.response["Error"]["Code"] == error_code
    else:
        with pytest.raises(ClientError) as exc_info:
            document_wrapper.send_command(instance_ids)
        assert exc_info.value.response["Error"]["Code"] == error_code
