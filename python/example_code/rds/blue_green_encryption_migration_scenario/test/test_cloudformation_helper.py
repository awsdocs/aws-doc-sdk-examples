# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for cloudformation_helper.py.
"""

import os

import boto3
import pytest
from botocore.exceptions import ClientError, WaiterError

from cloudformation_helper import DEFAULT_TEMPLATE_PATH, CloudFormationHelper

STACK_NAME = "RdsBlueGreenKmsKeysStack"
STACK_ID = (
    "arn:aws:cloudformation:us-east-1:123456789012:stack/"
    f"{STACK_NAME}/11111111-2222-3333-4444-555555555555"
)


class HelperData:
    def __init__(self, cf_client, stubber):
        self.cf_client = cf_client
        self.stubber = stubber
        self.helper = CloudFormationHelper(cf_client)


@pytest.fixture
def helper_data(make_stubber):
    cf_client = boto3.client("cloudformation", region_name="us-east-1")
    stubber = make_stubber(cf_client)
    return HelperData(cf_client, stubber)


def _template_body(helper):
    with open(helper.template_path, encoding="utf-8") as template_file:
        return template_file.read()


def test_stack_exists_true(helper_data):
    helper_data.stubber.stub_describe_stacks(STACK_NAME, "CREATE_COMPLETE")

    assert helper_data.helper.stack_exists(STACK_NAME) is True


def test_stack_exists_false(helper_data):
    helper_data.stubber.stub_describe_stacks(
        STACK_NAME, "CREATE_COMPLETE", error_code="ValidationError"
    )

    assert helper_data.helper.stack_exists(STACK_NAME) is False


def test_stack_exists_error(helper_data):
    helper_data.stubber.stub_describe_stacks(
        STACK_NAME, "CREATE_COMPLETE", error_code="TESTERROR"
    )

    with pytest.raises(ClientError) as exc_info:
        helper_data.helper.stack_exists(STACK_NAME)
    assert exc_info.value.response["Error"]["Code"] == "TESTERROR"


def test_deploy_stack_creates_stack(helper_data):
    stubber = helper_data.stubber
    stubber.stub_describe_stacks(
        STACK_NAME, "CREATE_COMPLETE", error_code="ValidationError"
    )
    stubber.stub_create_stack(
        STACK_NAME, _template_body(helper_data.helper), ["CAPABILITY_IAM"], STACK_ID
    )
    stubber.stub_describe_stacks(STACK_NAME, "CREATE_COMPLETE")

    assert helper_data.helper.deploy_stack(STACK_NAME) is True


def test_deploy_stack_already_exists(helper_data):
    helper_data.stubber.stub_describe_stacks(STACK_NAME, "CREATE_COMPLETE")

    assert helper_data.helper.deploy_stack(STACK_NAME) is False


def test_deploy_stack_create_error(helper_data):
    stubber = helper_data.stubber
    stubber.stub_describe_stacks(
        STACK_NAME, "CREATE_COMPLETE", error_code="ValidationError"
    )
    stubber.stub_create_stack(
        STACK_NAME,
        _template_body(helper_data.helper),
        ["CAPABILITY_IAM"],
        STACK_ID,
        error_code="TESTERROR",
    )

    with pytest.raises(ClientError) as exc_info:
        helper_data.helper.deploy_stack(STACK_NAME)
    assert exc_info.value.response["Error"]["Code"] == "TESTERROR"


def test_deploy_stack_waiter_failure(helper_data):
    stubber = helper_data.stubber
    stubber.stub_describe_stacks(
        STACK_NAME, "CREATE_COMPLETE", error_code="ValidationError"
    )
    stubber.stub_create_stack(
        STACK_NAME, _template_body(helper_data.helper), ["CAPABILITY_IAM"], STACK_ID
    )
    stubber.stub_describe_stacks(STACK_NAME, "CREATE_FAILED")

    with pytest.raises(WaiterError):
        helper_data.helper.deploy_stack(STACK_NAME)


def test_get_stack_outputs(helper_data):
    outputs = [
        {"OutputKey": "OriginalKmsKeyArn", "OutputValue": "arn:original"},
        {"OutputKey": "TargetKmsKeyArn", "OutputValue": "arn:target"},
    ]
    helper_data.stubber.stub_describe_stacks(
        STACK_NAME, "CREATE_COMPLETE", outputs=outputs
    )

    assert helper_data.helper.get_stack_outputs(STACK_NAME) == {
        "OriginalKmsKeyArn": "arn:original",
        "TargetKmsKeyArn": "arn:target",
    }


def test_get_stack_outputs_no_outputs(helper_data):
    helper_data.stubber.stub_describe_stacks(STACK_NAME, "CREATE_COMPLETE")

    assert helper_data.helper.get_stack_outputs(STACK_NAME) == {}


def test_get_stack_outputs_error(helper_data):
    helper_data.stubber.stub_describe_stacks(
        STACK_NAME, "CREATE_COMPLETE", error_code="TESTERROR"
    )

    with pytest.raises(ClientError) as exc_info:
        helper_data.helper.get_stack_outputs(STACK_NAME)
    assert exc_info.value.response["Error"]["Code"] == "TESTERROR"


def test_destroy_stack(helper_data):
    helper_data.stubber.stub_delete_stack(STACK_NAME)
    helper_data.stubber.stub_describe_stacks(STACK_NAME, "DELETE_COMPLETE")

    helper_data.helper.destroy_stack(STACK_NAME)


def test_destroy_stack_error(helper_data):
    helper_data.stubber.stub_delete_stack(STACK_NAME, error_code="TESTERROR")

    with pytest.raises(ClientError) as exc_info:
        helper_data.helper.destroy_stack(STACK_NAME)
    assert exc_info.value.response["Error"]["Code"] == "TESTERROR"


def test_destroy_stack_waiter_failure(helper_data):
    helper_data.stubber.stub_delete_stack(STACK_NAME)
    helper_data.stubber.stub_describe_stacks(STACK_NAME, "DELETE_FAILED")

    with pytest.raises(WaiterError):
        helper_data.helper.destroy_stack(STACK_NAME)


def test_default_template_path():
    """The template path is resolved from the module file, not the cwd."""
    assert os.path.isabs(DEFAULT_TEMPLATE_PATH)
    assert os.path.isfile(DEFAULT_TEMPLATE_PATH)
    with open(DEFAULT_TEMPLATE_PATH, encoding="utf-8") as template_file:
        template = template_file.read()
    assert "OriginalKmsKeyArn" in template
    assert "TargetKmsKeyArn" in template
    assert template.count("AWS::KMS::Key") == 2
    assert "PendingWindowInDays: 7" in template
    assert "BootstrapVersion" not in template
    assert "CDKMetadata" not in template
