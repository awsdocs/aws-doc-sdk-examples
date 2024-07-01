# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for maintenance_window.py functions.
"""

import logging
import queue
from logging.handlers import QueueHandler

import boto3
import pytest
from botocore.exceptions import ClientError

from ops_item import OpsItemWrapper

log_queue = queue.LifoQueue()

queue_handler = QueueHandler(log_queue)
document_logger = logging.getLogger("ops_item")
document_logger.addHandler(queue_handler)


@pytest.mark.parametrize(
    "error_code", [None, "OpsItemLimitExceededException", "TestException"]
)
def test_create_ops_item(make_stubber, error_code):
    ssm_client = boto3.client("ssm")
    ssm_stubber = make_stubber(ssm_client)
    ops_item_wrapper = OpsItemWrapper(ssm_client)
    title = "Test OpsItem"
    description = "This is a test OpsItem"
    source = "test-source"
    severity = "2"
    category = "Availability"

    ops_item_id = "oi-0123456789abcdef"

    ssm_stubber.stub_create_ops_item(
        title,
        source,
        category,
        severity,
        description,
        ops_item_id,
        error_code=error_code,
    )

    if error_code is None:
        ops_item_wrapper.create(title, source, category, severity, description)
        assert ops_item_wrapper.id == ops_item_id
    elif error_code == "OpsItemLimitExceededException":
        with pytest.raises(ClientError):
            ops_item_wrapper.create(title, source, category, severity, description)
        assert (
            log_queue.qsize() > 0
            and "exceeded your open OpsItem" in log_queue.get().getMessage()
        )

    else:
        with pytest.raises(ClientError) as exc_info:
            ops_item_wrapper.create(title, source, category, severity, description)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_ops_item(make_stubber, error_code):
    ssm_client = boto3.client("ssm")
    ssm_stubber = make_stubber(ssm_client)
    ops_item_wrapper = OpsItemWrapper(ssm_client)
    ops_item_id = "oi-0123456789abcdef0"
    ops_item_wrapper.id = ops_item_id

    ssm_stubber.stub_delete_ops_item(ops_item_id, error_code=error_code)

    if error_code is None:
        ops_item_wrapper.delete()
        assert ops_item_wrapper.id is None
    else:
        with pytest.raises(ClientError) as exc_info:
            ops_item_wrapper.delete()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_describe_ops_item(make_stubber, error_code):
    ssm_client = boto3.client("ssm")
    ssm_stubber = make_stubber(ssm_client)
    ops_item_wrapper = OpsItemWrapper(ssm_client)
    ops_item_id = "oi-0123456789abcdef0"
    ops_item_wrapper.id = ops_item_id

    filters = [{"Key": "OpsItemId", "Operator": "Equal", "Values": [ops_item_id]}]

    ssm_stubber.stub_describe_ops_items(filters=filters, error_code=error_code)

    if error_code is None:
        ops_item_wrapper.describe()
    else:
        with pytest.raises(ClientError) as exc_info:
            ops_item_wrapper.describe()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_update_ops_item(make_stubber, error_code):
    ssm_client = boto3.client("ssm")
    ssm_stubber = make_stubber(ssm_client)
    ops_item_wrapper = OpsItemWrapper(ssm_client)
    ops_item_id = "oi-0123456789abcdef0"
    ops_item_wrapper.id = ops_item_id
    new_title = "Updated OpsItem"
    new_description = "This is an updated OpsItem."
    status = "Completed"

    ssm_stubber.stub_update_ops_item(
        ops_item_id,
        title=new_title,
        description=new_description,
        status=status,
        error_code=error_code,
    )

    if error_code is None:
        ops_item_wrapper.update(
            title=new_title, description=new_description, status=status
        )
    else:
        with pytest.raises(ClientError) as exc_info:
            ops_item_wrapper.update(
                title=new_title, description=new_description, status=status
            )
        assert exc_info.value.response["Error"]["Code"] == error_code
