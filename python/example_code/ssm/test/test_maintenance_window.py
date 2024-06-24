# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for maintenance_window.py functions.
"""

import boto3
import pytest
from botocore.exceptions import ClientError

from maintenance_window import MaintenanceWindowWrapper


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create(make_stubber, error_code):
    ssm_client = boto3.client("ssm")
    ssm_stubber = make_stubber(ssm_client)
    maintenance_window_wrapper = MaintenanceWindowWrapper(ssm_client)
    name = "python-test"
    schedule = "cron(0 10 ? * MON-FRI *)"
    duration = 2
    cutoff = 1
    allow_unassociated_targets = True
    window_id = "mw-0123456789abcdef0"

    ssm_stubber.stub_create_maintenance_window(
        name=name,
        window_id=window_id,
        allow_unassociated_targets=allow_unassociated_targets,
        cutoff=cutoff,
        duration=duration,
        schedule=schedule,
        error_code=error_code,
    )

    if error_code is None:
        maintenance_window_wrapper.create(
            name, schedule, duration, cutoff, allow_unassociated_targets
        )
        assert maintenance_window_wrapper.window_id == window_id
        assert maintenance_window_wrapper.name == name
    else:
        with pytest.raises(ClientError) as exc_info:
            maintenance_window_wrapper.create(
                name, schedule, duration, cutoff, allow_unassociated_targets
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_maintenance_window(make_stubber, error_code):
    ssm_client = boto3.client("ssm")
    ssm_stubber = make_stubber(ssm_client)
    maintenance_window_wrapper = MaintenanceWindowWrapper(ssm_client)
    window_id = "mw-0123456789abcdef0"
    maintenance_window_wrapper.window_id = window_id

    ssm_stubber.stub_delete_maintenance_window(
        window_id,
        error_code=error_code,
    )

    if error_code is None:
        maintenance_window_wrapper.delete()
        assert maintenance_window_wrapper.window_id is None
    else:
        with pytest.raises(ClientError) as exc_info:
            maintenance_window_wrapper.delete()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_update_maintenance_window(make_stubber, error_code):
    ssm_client = boto3.client("ssm")
    ssm_stubber = make_stubber(ssm_client)
    maintenance_window_wrapper = MaintenanceWindowWrapper(ssm_client)
    window_id = "mw-0123456789abcdef0"
    maintenance_window_wrapper.window_id = window_id
    new_name = "updated-python-test"
    new_schedule = "cron(0 12 ? * MON-FRI *)"
    enabled = True
    duration = 4
    cutoff = 2
    allow_unassociated_targets = False

    ssm_stubber.stub_update_maintenance_window(
        window_id,
        name=new_name,
        schedule=new_schedule,
        enabled=enabled,
        duration=duration,
        cutoff=cutoff,
        allow_unassociated_targets=allow_unassociated_targets,
        error_code=error_code,
    )

    if error_code is None:
        maintenance_window_wrapper.update(
            name=new_name,
            enabled=enabled,
            schedule=new_schedule,
            duration=duration,
            cutoff=cutoff,
            allow_unassociated_targets=allow_unassociated_targets,
        )
    else:
        with pytest.raises(ClientError) as exc_info:
            maintenance_window_wrapper.update(
                name=new_name,
                enabled=enabled,
                schedule=new_schedule,
                duration=duration,
                cutoff=cutoff,
                allow_unassociated_targets=allow_unassociated_targets,
            )
        assert exc_info.value.response["Error"]["Code"] == error_code
