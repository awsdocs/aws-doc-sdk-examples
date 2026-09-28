# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Tests for the Amazon Pinpoint wrapper and scenario.

Uses botocore.stub.Stubber exclusively — no real AWS calls, no mocking of
the main service client, fully offline.

Run with:  pytest test_pinpoint_wrapper.py -v
"""

import json

import boto3
import pytest
from botocore.stub import Stubber

from pinpoint_wrapper import PinpointWrapper


# ---------------------------------------------------------------------------
# Fixtures
# ---------------------------------------------------------------------------

@pytest.fixture
def pinpoint_client():
    """Creates a Boto3 Pinpoint client."""
    return boto3.client("pinpoint", region_name="us-east-1")


@pytest.fixture
def pinpoint_stubber(pinpoint_client):
    """Returns a Stubber attached to the Pinpoint client."""
    with Stubber(pinpoint_client) as stubber:
        yield stubber
        stubber.assert_no_pending_responses()


@pytest.fixture
def wrapper(pinpoint_client):
    """Returns a PinpointWrapper backed by the stubbed client."""
    return PinpointWrapper(pinpoint_client)


# ---------------------------------------------------------------------------
# Test: get_apps (GetApps)
# ---------------------------------------------------------------------------

class TestGetApps:
    def test_get_apps_returns_list(self, pinpoint_stubber, wrapper):
        pinpoint_stubber.add_response(
            "get_apps",
            {
                "ApplicationsResponse": {
                    "Item": [
                        {"Id": "app-111", "Name": "TestApp1", "Arn": "arn:aws:mobiletargeting:us-east-1:123456789012:apps/app-111"},
                        {"Id": "app-222", "Name": "TestApp2", "Arn": "arn:aws:mobiletargeting:us-east-1:123456789012:apps/app-222"},
                    ]
                }
            },
            {"PageSize": "25"},
        )
        apps = wrapper.get_apps()
        assert len(apps) == 2
        assert apps[0]["Name"] == "TestApp1"

    def test_get_apps_empty(self, pinpoint_stubber, wrapper):
        pinpoint_stubber.add_response(
            "get_apps",
            {"ApplicationsResponse": {"Item": []}},
            {"PageSize": "5"},
        )
        apps = wrapper.get_apps(page_size=5)
        assert apps == []

    def test_get_apps_bad_request(self, pinpoint_stubber, wrapper):
        pinpoint_stubber.add_client_error(
            "get_apps",
            service_error_code="BadRequestException",
            service_message="Invalid request.",
        )
        with pytest.raises(Exception) as exc_info:
            wrapper.get_apps()
        assert "BadRequestException" in str(exc_info.value) or "An error occurred" in str(exc_info.value)


# ---------------------------------------------------------------------------
# Test: create_app (CreateApp)
# ---------------------------------------------------------------------------

class TestCreateApp:
    def test_create_app_success(self, pinpoint_stubber, wrapper):
        pinpoint_stubber.add_response(
            "create_app",
            {
                "ApplicationResponse": {
                    "Id": "app-new-123",
                    "Name": "MyNewApp",
                    "Arn": "arn:aws:mobiletargeting:us-east-1:123456789012:apps/app-new-123",
                }
            },
            {"CreateApplicationRequest": {"Name": "MyNewApp"}},
        )
        result = wrapper.create_app("MyNewApp")
        assert result["Id"] == "app-new-123"
        assert result["Name"] == "MyNewApp"

    def test_create_app_bad_request(self, pinpoint_stubber, wrapper):
        pinpoint_stubber.add_client_error(
            "create_app",
            service_error_code="BadRequestException",
            service_message="Invalid app name.",
        )
        with pytest.raises(Exception):
            wrapper.create_app("BadApp!")


# ---------------------------------------------------------------------------
# Test: update_email_channel (UpdateEmailChannel)
# ---------------------------------------------------------------------------

class TestUpdateEmailChannel:
    def test_update_email_channel_success(self, pinpoint_stubber, wrapper):
        app_id = "app-123"
        from_addr = "user@example.com"
        identity_arn = "arn:aws:ses:us-east-1:123456789012:identity/user@example.com"
        pinpoint_stubber.add_response(
            "update_email_channel",
            {
                "EmailChannelResponse": {
                    "ApplicationId": app_id,
                    "FromAddress": from_addr,
                    "Identity": identity_arn,
                    "Enabled": True,
                    "Platform": "EMAIL",
                }
            },
            {
                "ApplicationId": app_id,
                "EmailChannelRequest": {
                    "FromAddress": from_addr,
                    "Identity": identity_arn,
                    "Enabled": True,
                },
            },
        )
        result = wrapper.update_email_channel(app_id, from_addr, identity_arn)
        assert result["Enabled"] is True
        assert result["FromAddress"] == from_addr

    def test_update_email_channel_bad_request(self, pinpoint_stubber, wrapper):
        pinpoint_stubber.add_client_error(
            "update_email_channel",
            service_error_code="BadRequestException",
            service_message="Unverified identity.",
        )
        with pytest.raises(Exception):
            wrapper.update_email_channel("app-123", "bad@example.com", "arn:bad")


# ---------------------------------------------------------------------------
# Test: create_segment (CreateSegment)
# ---------------------------------------------------------------------------

class TestCreateSegment:
    def test_create_segment_success(self, pinpoint_stubber, wrapper):
        app_id = "app-123"
        pinpoint_stubber.add_response(
            "create_segment",
            {
                "SegmentResponse": {
                    "Id": "seg-456",
                    "Name": "EmailSubscribers",
                    "ApplicationId": app_id,
                    "SegmentType": "DIMENSIONAL",
                    "Arn": "arn:aws:mobiletargeting:us-east-1:123456789012:apps/app-123/segments/seg-456",
                }
            },
            {
                "ApplicationId": app_id,
                "WriteSegmentRequest": {
                    "Name": "EmailSubscribers",
                    "Dimensions": {
                        "Demographic": {
                            "Channel": {
                                "DimensionType": "INCLUSIVE",
                                "Values": ["EMAIL"],
                            }
                        }
                    },
                },
            },
        )
        result = wrapper.create_segment(app_id, "EmailSubscribers")
        assert result["Id"] == "seg-456"
        assert result["SegmentType"] == "DIMENSIONAL"

    def test_create_segment_bad_request(self, pinpoint_stubber, wrapper):
        pinpoint_stubber.add_client_error(
            "create_segment",
            service_error_code="BadRequestException",
            service_message="Invalid segment definition.",
        )
        with pytest.raises(Exception):
            wrapper.create_segment("app-123", "BadSegment")


# ---------------------------------------------------------------------------
# Test: create_campaign (CreateCampaign)
# ---------------------------------------------------------------------------

class TestCreateCampaign:
    def test_create_campaign_success(self, pinpoint_stubber, wrapper):
        app_id = "app-123"
        seg_id = "seg-456"
        pinpoint_stubber.add_response(
            "create_campaign",
            {
                "CampaignResponse": {
                    "Id": "camp-789",
                    "Name": "WelcomeCampaign",
                    "ApplicationId": app_id,
                    "SegmentId": seg_id,
                    "State": {"CampaignStatus": "SCHEDULED"},
                    "Arn": "arn:aws:mobiletargeting:us-east-1:123456789012:apps/app-123/campaigns/camp-789",
                }
            },
            {
                "ApplicationId": app_id,
                "WriteCampaignRequest": {
                    "Name": "WelcomeCampaign",
                    "SegmentId": seg_id,
                    "MessageConfiguration": {
                        "EmailMessage": {
                            "Title": "Welcome!",
                            "Body": "Plain text body.",
                            "HtmlBody": "<h1>Welcome!</h1>",
                            "FromAddress": "user@example.com",
                        }
                    },
                    "Schedule": {"StartTime": "IMMEDIATE"},
                },
            },
        )
        result = wrapper.create_campaign(
            app_id,
            "WelcomeCampaign",
            seg_id,
            "user@example.com",
            "Welcome!",
            "<h1>Welcome!</h1>",
            "Plain text body.",
        )
        assert result["Id"] == "camp-789"
        assert result["State"]["CampaignStatus"] == "SCHEDULED"


# ---------------------------------------------------------------------------
# Test: send_messages (SendMessages) — no snippet tags but code still works
# ---------------------------------------------------------------------------

class TestSendMessages:
    def test_send_messages_success(self, pinpoint_stubber, wrapper):
        app_id = "app-123"
        from_addr = "user@example.com"
        to_addr = "recipient@example.com"
        pinpoint_stubber.add_response(
            "send_messages",
            {
                "MessageResponse": {
                    "ApplicationId": app_id,
                    "Result": {
                        to_addr: {
                            "DeliveryStatus": "SUCCESSFUL",
                            "StatusCode": 200,
                            "MessageId": "msg-abc-123",
                        }
                    },
                }
            },
            {
                "ApplicationId": app_id,
                "MessageRequest": {
                    "Addresses": {
                        to_addr: {"ChannelType": "EMAIL"}
                    },
                    "MessageConfiguration": {
                        "EmailMessage": {
                            "FromAddress": from_addr,
                            "SimpleEmail": {
                                "Subject": {"Charset": "UTF-8", "Data": "Test Subject"},
                                "HtmlPart": {"Charset": "UTF-8", "Data": "<p>Hello</p>"},
                                "TextPart": {"Charset": "UTF-8", "Data": "Hello"},
                            },
                        }
                    },
                },
            },
        )
        result = wrapper.send_messages(
            app_id, from_addr, [to_addr], "Test Subject", "<p>Hello</p>", "Hello"
        )
        assert to_addr in result
        assert result[to_addr]["DeliveryStatus"] == "SUCCESSFUL"

    def test_send_messages_bad_request(self, pinpoint_stubber, wrapper):
        pinpoint_stubber.add_client_error(
            "send_messages",
            service_error_code="BadRequestException",
            service_message="Unverified sender.",
        )
        with pytest.raises(Exception):
            wrapper.send_messages(
                "app-123", "bad@example.com", ["r@example.com"],
                "Subject", "<p>Body</p>", "Body"
            )


# ---------------------------------------------------------------------------
# Test: get_campaign (GetCampaign)
# ---------------------------------------------------------------------------

class TestGetCampaign:
    def test_get_campaign_success(self, pinpoint_stubber, wrapper):
        app_id = "app-123"
        camp_id = "camp-789"
        pinpoint_stubber.add_response(
            "get_campaign",
            {
                "CampaignResponse": {
                    "Id": camp_id,
                    "Name": "WelcomeCampaign",
                    "ApplicationId": app_id,
                    "SegmentId": "seg-456",
                    "State": {"CampaignStatus": "COMPLETED"},
                    "Arn": "arn:aws:mobiletargeting:us-east-1:123456789012:apps/app-123/campaigns/camp-789",
                }
            },
            {"ApplicationId": app_id, "CampaignId": camp_id},
        )
        result = wrapper.get_campaign(app_id, camp_id)
        assert result["Name"] == "WelcomeCampaign"
        assert result["State"]["CampaignStatus"] == "COMPLETED"

    def test_get_campaign_not_found(self, pinpoint_stubber, wrapper):
        pinpoint_stubber.add_client_error(
            "get_campaign",
            service_error_code="NotFoundException",
            service_message="Campaign not found.",
        )
        with pytest.raises(Exception):
            wrapper.get_campaign("app-123", "camp-missing")


# ---------------------------------------------------------------------------
# Test: get_campaign_activities (GetCampaignActivities)
# ---------------------------------------------------------------------------

class TestGetCampaignActivities:
    def test_get_campaign_activities_success(self, pinpoint_stubber, wrapper):
        app_id = "app-123"
        camp_id = "camp-789"
        pinpoint_stubber.add_response(
            "get_campaign_activities",
            {
                "ActivitiesResponse": {
                    "Item": [
                        {
                            "Id": "act-001",
                            "CampaignId": camp_id,
                            "State": "COMPLETED",
                            "SuccessfulEndpointCount": 1,
                            "TotalEndpointCount": 1,
                        }
                    ]
                }
            },
            {"ApplicationId": app_id, "CampaignId": camp_id},
        )
        result = wrapper.get_campaign_activities(app_id, camp_id)
        assert len(result) == 1
        assert result[0]["State"] == "COMPLETED"

    def test_get_campaign_activities_not_found(self, pinpoint_stubber, wrapper):
        pinpoint_stubber.add_client_error(
            "get_campaign_activities",
            service_error_code="NotFoundException",
            service_message="Campaign not found.",
        )
        with pytest.raises(Exception):
            wrapper.get_campaign_activities("app-123", "camp-missing")


# ---------------------------------------------------------------------------
# Test: delete_campaign (DeleteCampaign)
# ---------------------------------------------------------------------------

class TestDeleteCampaign:
    def test_delete_campaign_success(self, pinpoint_stubber, wrapper):
        app_id = "app-123"
        camp_id = "camp-789"
        pinpoint_stubber.add_response(
            "delete_campaign",
            {
                "CampaignResponse": {
                    "Id": camp_id,
                    "Name": "WelcomeCampaign",
                    "ApplicationId": app_id,
                    "Arn": "arn:aws:mobiletargeting:us-east-1:123456789012:apps/app-123/campaigns/camp-789",
                }
            },
            {"ApplicationId": app_id, "CampaignId": camp_id},
        )
        wrapper.delete_campaign(app_id, camp_id)  # should not raise

    def test_delete_campaign_not_found(self, pinpoint_stubber, wrapper):
        pinpoint_stubber.add_client_error(
            "delete_campaign",
            service_error_code="NotFoundException",
            service_message="Campaign already deleted.",
        )
        with pytest.raises(Exception):
            wrapper.delete_campaign("app-123", "camp-gone")


# ---------------------------------------------------------------------------
# Test: delete_segment (DeleteSegment)
# ---------------------------------------------------------------------------

class TestDeleteSegment:
    def test_delete_segment_success(self, pinpoint_stubber, wrapper):
        app_id = "app-123"
        seg_id = "seg-456"
        pinpoint_stubber.add_response(
            "delete_segment",
            {
                "SegmentResponse": {
                    "Id": seg_id,
                    "Name": "EmailSubscribers",
                    "ApplicationId": app_id,
                    "SegmentType": "DIMENSIONAL",
                    "Arn": "arn:aws:mobiletargeting:us-east-1:123456789012:apps/app-123/segments/seg-456",
                }
            },
            {"ApplicationId": app_id, "SegmentId": seg_id},
        )
        wrapper.delete_segment(app_id, seg_id)  # should not raise

    def test_delete_segment_not_found(self, pinpoint_stubber, wrapper):
        pinpoint_stubber.add_client_error(
            "delete_segment",
            service_error_code="NotFoundException",
            service_message="Segment already deleted.",
        )
        with pytest.raises(Exception):
            wrapper.delete_segment("app-123", "seg-gone")


# ---------------------------------------------------------------------------
# Test: delete_app (DeleteApp)
# ---------------------------------------------------------------------------

class TestDeleteApp:
    def test_delete_app_success(self, pinpoint_stubber, wrapper):
        app_id = "app-123"
        pinpoint_stubber.add_response(
            "delete_app",
            {
                "ApplicationResponse": {
                    "Id": app_id,
                    "Name": "MyApp",
                    "Arn": "arn:aws:mobiletargeting:us-east-1:123456789012:apps/app-123",
                }
            },
            {"ApplicationId": app_id},
        )
        wrapper.delete_app(app_id)  # should not raise

    def test_delete_app_not_found(self, pinpoint_stubber, wrapper):
        pinpoint_stubber.add_client_error(
            "delete_app",
            service_error_code="NotFoundException",
            service_message="Application already deleted.",
        )
        with pytest.raises(Exception):
            wrapper.delete_app("app-gone")
