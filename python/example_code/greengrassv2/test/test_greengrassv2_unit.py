# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the AWS IoT Greengrass V2 wrapper (``GreengrassV2Wrapper``).

These tests use the shared ``make_stubber`` fixture and the
``GreengrassV2Stubber`` from ``test_tools`` to intercept Boto3 calls so each
wrapper method is exercised without making real AWS calls. Both success paths
and representative error paths are covered using the ``error_code`` pattern.
"""

import boto3
import pytest
from botocore.exceptions import ClientError

from greengrassv2_wrapper import GreengrassV2Wrapper

COMPONENT_NAME = "com.example.GreengrassBasics"
COMPONENT_ARN = "arn:aws:greengrass:us-east-1:123456789012:components:" + COMPONENT_NAME
COMPONENT_V1_ARN = COMPONENT_ARN + ":versions:1.0.0"
COMPONENT_V2_ARN = COMPONENT_ARN + ":versions:2.0.0"
TARGET_ARN = "arn:aws:iot:us-east-1:123456789012:thinggroup/MyGroup"
DEPLOYMENT_ID = "dep-123"


# ---------------------------------------------------------------------------
# Pure helpers (no client involved)
# ---------------------------------------------------------------------------


def test_component_arn_from_version_arn_strips_version():
    assert (
        GreengrassV2Wrapper.component_arn_from_version_arn(COMPONENT_V1_ARN)
        == COMPONENT_ARN
    )


def test_component_arn_from_version_arn_without_version_is_unchanged():
    assert (
        GreengrassV2Wrapper.component_arn_from_version_arn(COMPONENT_ARN)
        == COMPONENT_ARN
    )


# ---------------------------------------------------------------------------
# list_core_devices
# ---------------------------------------------------------------------------


@pytest.mark.parametrize("error_code", [None, "ValidationException"])
def test_list_core_devices(make_stubber, error_code):
    client = boto3.client("greengrassv2", region_name="us-east-1")
    stubber = make_stubber(client)
    wrapper = GreengrassV2Wrapper(client)
    core_devices = [
        {"coreDeviceThingName": "device-1", "status": "HEALTHY"},
        {"coreDeviceThingName": "device-2", "status": "UNHEALTHY"},
    ]
    stubber.stub_list_core_devices(core_devices, error_code=error_code)

    if error_code is None:
        devices = wrapper.list_core_devices()
        assert len(devices) == 2
    else:
        with pytest.raises(ClientError) as exc:
            wrapper.list_core_devices()
        assert exc.value.response["Error"]["Code"] == error_code


def test_list_core_devices_with_status_filter(make_stubber):
    client = boto3.client("greengrassv2", region_name="us-east-1")
    stubber = make_stubber(client)
    wrapper = GreengrassV2Wrapper(client)
    stubber.stub_list_core_devices(
        [{"coreDeviceThingName": "device-1", "status": "HEALTHY"}], status="HEALTHY"
    )
    devices = wrapper.list_core_devices(status="HEALTHY")
    assert len(devices) == 1


# ---------------------------------------------------------------------------
# create_component_version
# ---------------------------------------------------------------------------


@pytest.mark.parametrize("error_code", [None, "ConflictException"])
def test_create_component_version(make_stubber, error_code):
    client = boto3.client("greengrassv2", region_name="us-east-1")
    stubber = make_stubber(client)
    wrapper = GreengrassV2Wrapper(client)
    recipe = {
        "RecipeFormatVersion": "2020-01-25",
        "ComponentName": COMPONENT_NAME,
        "ComponentVersion": "1.0.0",
        "ComponentPublisher": "AWS Code Examples",
        "Manifests": [{"Platform": {"os": "linux"}}],
    }
    stubber.stub_create_component_version(
        COMPONENT_V1_ARN, COMPONENT_NAME, "1.0.0", error_code=error_code
    )

    if error_code is None:
        response = wrapper.create_component_version(recipe)
        assert response["arn"] == COMPONENT_V1_ARN
        assert response["componentVersion"] == "1.0.0"
    else:
        with pytest.raises(ClientError) as exc:
            wrapper.create_component_version(recipe)
        assert exc.value.response["Error"]["Code"] == error_code


# ---------------------------------------------------------------------------
# list_component_versions
# ---------------------------------------------------------------------------


@pytest.mark.parametrize("error_code", [None, "ResourceNotFoundException"])
def test_list_component_versions(make_stubber, error_code):
    client = boto3.client("greengrassv2", region_name="us-east-1")
    stubber = make_stubber(client)
    wrapper = GreengrassV2Wrapper(client)
    versions = [
        {"componentName": COMPONENT_NAME, "componentVersion": "2.0.0"},
        {"componentName": COMPONENT_NAME, "componentVersion": "1.0.0"},
    ]
    stubber.stub_list_component_versions(COMPONENT_ARN, versions, error_code=error_code)

    if error_code is None:
        result = wrapper.list_component_versions(COMPONENT_ARN)
        assert [v["componentVersion"] for v in result] == ["2.0.0", "1.0.0"]
    else:
        with pytest.raises(ClientError) as exc:
            wrapper.list_component_versions(COMPONENT_ARN)
        assert exc.value.response["Error"]["Code"] == error_code


# ---------------------------------------------------------------------------
# get_component
# ---------------------------------------------------------------------------


def test_get_component(make_stubber):
    client = boto3.client("greengrassv2", region_name="us-east-1")
    stubber = make_stubber(client)
    wrapper = GreengrassV2Wrapper(client)
    recipe_bytes = b'{"ComponentName": "com.example.GreengrassBasics"}'
    stubber.stub_get_component(COMPONENT_V2_ARN, recipe_bytes)
    response = wrapper.get_component(COMPONENT_V2_ARN)
    assert response["recipe"] == recipe_bytes


# ---------------------------------------------------------------------------
# describe_component
# ---------------------------------------------------------------------------


def test_describe_component(make_stubber):
    client = boto3.client("greengrassv2", region_name="us-east-1")
    stubber = make_stubber(client)
    wrapper = GreengrassV2Wrapper(client)
    stubber.stub_describe_component(
        COMPONENT_V2_ARN, COMPONENT_NAME, "2.0.0", component_state="DEPLOYABLE"
    )
    response = wrapper.describe_component(COMPONENT_V2_ARN)
    assert response["status"]["componentState"] == "DEPLOYABLE"


# ---------------------------------------------------------------------------
# wait_for_component_deployable (polling)
# ---------------------------------------------------------------------------


def test_wait_for_component_deployable_success(make_stubber):
    client = boto3.client("greengrassv2", region_name="us-east-1")
    stubber = make_stubber(client)
    wrapper = GreengrassV2Wrapper(client)
    # First poll not-yet-ready, second poll DEPLOYABLE.
    stubber.stub_describe_component(
        COMPONENT_V1_ARN, COMPONENT_NAME, "1.0.0", component_state="REQUESTED"
    )
    stubber.stub_describe_component(
        COMPONENT_V1_ARN, COMPONENT_NAME, "1.0.0", component_state="DEPLOYABLE"
    )
    assert (
        wrapper.wait_for_component_deployable(
            COMPONENT_V1_ARN, max_attempts=3, delay_seconds=0
        )
        is True
    )


def test_wait_for_component_deployable_terminal_state(make_stubber):
    client = boto3.client("greengrassv2", region_name="us-east-1")
    stubber = make_stubber(client)
    wrapper = GreengrassV2Wrapper(client)
    stubber.stub_describe_component(
        COMPONENT_V1_ARN, COMPONENT_NAME, "1.0.0", component_state="FAILED"
    )
    assert (
        wrapper.wait_for_component_deployable(
            COMPONENT_V1_ARN, max_attempts=3, delay_seconds=0
        )
        is False
    )


# ---------------------------------------------------------------------------
# create_deployment
# ---------------------------------------------------------------------------


@pytest.mark.parametrize("error_code", [None, "ValidationException"])
def test_create_deployment(make_stubber, error_code):
    client = boto3.client("greengrassv2", region_name="us-east-1")
    stubber = make_stubber(client)
    wrapper = GreengrassV2Wrapper(client)
    components = {COMPONENT_NAME: {"componentVersion": "2.0.0"}}
    stubber.stub_create_deployment(
        TARGET_ARN, "MyDeployment", components, DEPLOYMENT_ID, error_code=error_code
    )

    if error_code is None:
        response = wrapper.create_deployment(TARGET_ARN, "MyDeployment", components)
        assert response["deploymentId"] == DEPLOYMENT_ID
    else:
        with pytest.raises(ClientError) as exc:
            wrapper.create_deployment(TARGET_ARN, "MyDeployment", components)
        assert exc.value.response["Error"]["Code"] == error_code


# ---------------------------------------------------------------------------
# get_deployment
# ---------------------------------------------------------------------------


def test_get_deployment(make_stubber):
    client = boto3.client("greengrassv2", region_name="us-east-1")
    stubber = make_stubber(client)
    wrapper = GreengrassV2Wrapper(client)
    stubber.stub_get_deployment(DEPLOYMENT_ID, deployment_status="ACTIVE")
    response = wrapper.get_deployment(DEPLOYMENT_ID)
    assert response["deploymentStatus"] == "ACTIVE"


# ---------------------------------------------------------------------------
# list_deployments
# ---------------------------------------------------------------------------


def test_list_deployments(make_stubber):
    client = boto3.client("greengrassv2", region_name="us-east-1")
    stubber = make_stubber(client)
    wrapper = GreengrassV2Wrapper(client)
    deployments = [{"deploymentId": DEPLOYMENT_ID, "deploymentStatus": "ACTIVE"}]
    stubber.stub_list_deployments(
        deployments, target_arn=TARGET_ARN, history_filter="ALL"
    )
    result = wrapper.list_deployments(target_arn=TARGET_ARN, history_filter="ALL")
    assert len(result) == 1
    assert result[0]["deploymentId"] == DEPLOYMENT_ID


# ---------------------------------------------------------------------------
# cancel_deployment
# ---------------------------------------------------------------------------


@pytest.mark.parametrize("error_code", [None, "ConflictException"])
def test_cancel_deployment(make_stubber, error_code):
    client = boto3.client("greengrassv2", region_name="us-east-1")
    stubber = make_stubber(client)
    wrapper = GreengrassV2Wrapper(client)
    stubber.stub_cancel_deployment(DEPLOYMENT_ID, error_code=error_code)

    if error_code is None:
        response = wrapper.cancel_deployment(DEPLOYMENT_ID)
        assert "message" in response
    else:
        with pytest.raises(ClientError) as exc:
            wrapper.cancel_deployment(DEPLOYMENT_ID)
        assert exc.value.response["Error"]["Code"] == error_code


# ---------------------------------------------------------------------------
# delete_component
# ---------------------------------------------------------------------------


@pytest.mark.parametrize("error_code", [None, "ConflictException"])
def test_delete_component(make_stubber, error_code):
    client = boto3.client("greengrassv2", region_name="us-east-1")
    stubber = make_stubber(client)
    wrapper = GreengrassV2Wrapper(client)
    stubber.stub_delete_component(COMPONENT_V1_ARN, error_code=error_code)

    if error_code is None:
        assert wrapper.delete_component(COMPONENT_V1_ARN) is None
    else:
        with pytest.raises(ClientError) as exc:
            wrapper.delete_component(COMPONENT_V1_ARN)
        assert exc.value.response["Error"]["Code"] == error_code
