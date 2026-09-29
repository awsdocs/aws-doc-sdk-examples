# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for pinpoint_wrapper.py.

These tests use the shared PinpointStubber (from test_tools) via the
make_stubber fixture, following the standard pattern used across the Python
code examples. Each test is parametrized on error_code so both the success
path and the error path are exercised.
"""

import boto3
import pytest
from botocore.exceptions import ClientError

from pinpoint_wrapper import PinpointWrapper


@pytest.mark.parametrize("error_code", [None, "BadRequestException"])
def test_get_apps(make_stubber, error_code):
    pinpoint_client = boto3.client("pinpoint", region_name="us-east-1")
    pinpoint_stubber = make_stubber(pinpoint_client)
    wrapper = PinpointWrapper(pinpoint_client)
    apps = [
        {
            "Id": "app-123",
            "Name": "MyApp",
            "Arn": "arn:aws:mobiletargeting:us-west-2:111122223333:apps/app-123",
        }
    ]

    if error_code is None:
        pinpoint_stubber.stub_get_apps(apps)
        result = wrapper.get_apps()
        assert result == apps
    else:
        pinpoint_stubber.stub_get_apps_error(error_code)
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_apps()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "BadRequestException"])
def test_create_app(make_stubber, error_code):
    pinpoint_client = boto3.client("pinpoint", region_name="us-east-1")
    pinpoint_stubber = make_stubber(pinpoint_client)
    wrapper = PinpointWrapper(pinpoint_client)
    app_name = "MyNewApp"

    if error_code is None:
        pinpoint_stubber.stub_create_app(app_name)
        result = wrapper.create_app(app_name)
        assert result["Name"] == app_name
    else:
        pinpoint_stubber.stub_create_app_error(app_name, error_code)
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_app(app_name)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "BadRequestException"])
def test_update_email_channel(make_stubber, error_code):
    pinpoint_client = boto3.client("pinpoint", region_name="us-east-1")
    pinpoint_stubber = make_stubber(pinpoint_client)
    wrapper = PinpointWrapper(pinpoint_client)
    app_id = "app-123"
    from_addr = "user@example.com"
    identity_arn = "arn:aws:ses:us-east-1:123456789012:identity/user@example.com"

    pinpoint_stubber.stub_update_email_channel(
        app_id, from_addr, identity_arn, error_code=error_code
    )

    if error_code is None:
        result = wrapper.update_email_channel(app_id, from_addr, identity_arn)
        assert result["Enabled"] is True
        assert result["FromAddress"] == from_addr
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.update_email_channel(app_id, from_addr, identity_arn)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "BadRequestException"])
def test_create_segment(make_stubber, error_code):
    pinpoint_client = boto3.client("pinpoint", region_name="us-east-1")
    pinpoint_stubber = make_stubber(pinpoint_client)
    wrapper = PinpointWrapper(pinpoint_client)
    app_id = "app-123"
    segment_name = "EmailSubscribers"
    segment_id = "seg-456"

    pinpoint_stubber.stub_create_segment(
        app_id, segment_name, segment_id, error_code=error_code
    )

    if error_code is None:
        result = wrapper.create_segment(app_id, segment_name)
        assert result["Id"] == segment_id
        assert result["SegmentType"] == "DIMENSIONAL"
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_segment(app_id, segment_name)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "BadRequestException"])
def test_create_campaign(make_stubber, error_code):
    pinpoint_client = boto3.client("pinpoint", region_name="us-east-1")
    pinpoint_stubber = make_stubber(pinpoint_client)
    wrapper = PinpointWrapper(pinpoint_client)
    app_id = "app-123"
    segment_id = "seg-456"
    campaign_id = "camp-789"
    campaign_name = "WelcomeCampaign"
    from_address = "user@example.com"
    subject = "Welcome!"
    html_body = "<h1>Welcome!</h1>"
    text_body = "Plain text body."

    pinpoint_stubber.stub_create_campaign(
        app_id,
        campaign_name,
        segment_id,
        from_address,
        subject,
        html_body,
        text_body,
        campaign_id,
        error_code=error_code,
    )

    if error_code is None:
        result = wrapper.create_campaign(
            app_id,
            campaign_name,
            segment_id,
            from_address,
            subject,
            html_body,
            text_body,
        )
        assert result["Id"] == campaign_id
        assert result["State"]["CampaignStatus"] == "SCHEDULED"
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_campaign(
                app_id,
                campaign_name,
                segment_id,
                from_address,
                subject,
                html_body,
                text_body,
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "NotFoundException"])
def test_get_campaign(make_stubber, error_code):
    pinpoint_client = boto3.client("pinpoint", region_name="us-east-1")
    pinpoint_stubber = make_stubber(pinpoint_client)
    wrapper = PinpointWrapper(pinpoint_client)
    app_id = "app-123"
    campaign_id = "camp-789"

    pinpoint_stubber.stub_get_campaign(app_id, campaign_id, error_code=error_code)

    if error_code is None:
        result = wrapper.get_campaign(app_id, campaign_id)
        assert result["Name"] == "WelcomeCampaign"
        assert result["State"]["CampaignStatus"] == "COMPLETED"
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_campaign(app_id, campaign_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "NotFoundException"])
def test_get_campaign_activities(make_stubber, error_code):
    pinpoint_client = boto3.client("pinpoint", region_name="us-east-1")
    pinpoint_stubber = make_stubber(pinpoint_client)
    wrapper = PinpointWrapper(pinpoint_client)
    app_id = "app-123"
    campaign_id = "camp-789"
    activities = [
        {
            "Id": "act-001",
            "ApplicationId": app_id,
            "CampaignId": campaign_id,
            "State": "COMPLETED",
            "SuccessfulEndpointCount": 1,
            "TotalEndpointCount": 1,
        }
    ]

    pinpoint_stubber.stub_get_campaign_activities(
        app_id, campaign_id, activities, error_code=error_code
    )

    if error_code is None:
        result = wrapper.get_campaign_activities(app_id, campaign_id)
        assert len(result) == 1
        assert result[0]["State"] == "COMPLETED"
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_campaign_activities(app_id, campaign_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "NotFoundException"])
def test_delete_campaign(make_stubber, error_code):
    pinpoint_client = boto3.client("pinpoint", region_name="us-east-1")
    pinpoint_stubber = make_stubber(pinpoint_client)
    wrapper = PinpointWrapper(pinpoint_client)
    app_id = "app-123"
    campaign_id = "camp-789"

    pinpoint_stubber.stub_delete_campaign(app_id, campaign_id, error_code=error_code)

    if error_code is None:
        wrapper.delete_campaign(app_id, campaign_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_campaign(app_id, campaign_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "NotFoundException"])
def test_delete_segment(make_stubber, error_code):
    pinpoint_client = boto3.client("pinpoint", region_name="us-east-1")
    pinpoint_stubber = make_stubber(pinpoint_client)
    wrapper = PinpointWrapper(pinpoint_client)
    app_id = "app-123"
    segment_id = "seg-456"

    pinpoint_stubber.stub_delete_segment(app_id, segment_id, error_code=error_code)

    if error_code is None:
        wrapper.delete_segment(app_id, segment_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_segment(app_id, segment_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "NotFoundException"])
def test_delete_app(make_stubber, error_code):
    pinpoint_client = boto3.client("pinpoint", region_name="us-east-1")
    pinpoint_stubber = make_stubber(pinpoint_client)
    wrapper = PinpointWrapper(pinpoint_client)
    app = {
        "Id": "app-123",
        "Name": "MyApp",
        "Arn": "arn:aws:mobiletargeting:us-west-2:111122223333:apps/app-123",
    }

    if error_code is None:
        pinpoint_stubber.stub_delete_app(app)
        wrapper.delete_app(app["Id"])
    else:
        pinpoint_stubber.stub_delete_app_error(app, error_code)
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_app(app["Id"])
        assert exc_info.value.response["Error"]["Code"] == error_code
