# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the run() and cleanup() flow in pinpoint_basics_scenario.py.

Uses the shared stub_runner fixture to sequence all Amazon Pinpoint calls the
scenario makes, following the standard scenario-test pattern used across the
Python code examples. Both the success path (error=None) and error paths
(stopping on a specific stubbed call) are exercised.
"""

import time

import pytest
from botocore.exceptions import ClientError


class MockManager:
    def __init__(self, stub_runner, scenario_data):
        self.scenario_data = scenario_data
        # stub_create_app returns this hardcoded application id.
        self.app_id = "d41d8cd98f00b204e9800998ecf8427e"
        self.segment_id = "seg-456"
        self.campaign_id = "camp-789"
        self.email = "sender@example.com"
        self.message_id = "msg-abc-123"

        self.app = {
            "Id": self.app_id,
            "Name": "PinpointBasicsExample",
            "Arn": f"arn:aws:mobiletargeting:us-west-2:111122223333:apps/{self.app_id}",
        }
        self.activities = [
            {
                "Id": "act-001",
                "ApplicationId": self.app_id,
                "CampaignId": self.campaign_id,
                "State": "COMPLETED",
                "SuccessfulEndpointCount": 1,
                "TotalEndpointCount": 1,
            }
        ]
        # Identity ARN the scenario derives from region + account id.
        self.identity_arn = f"arn:aws:ses:us-east-1:111122223333:identity/{self.email}"
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, monkeypatch):
        """Sequence the stubs for the scenario's run() + cleanup() flow."""
        sd = self.scenario_data
        sd.scenario.email_address = self.email
        sd.scenario.stack_deployed = True

        # Avoid real waiting during the demo pause and the CFN delete waiter.
        monkeypatch.setattr(time, "sleep", lambda _seconds: None)
        monkeypatch.setattr(sd.cf_client, "get_waiter", lambda _name: _NoopWaiter())

        with self.stub_runner(error, stop_on) as runner:
            # --- run() ---
            runner.add(sd.pinpoint_stubber.stub_create_app, "PinpointBasicsExample")
            runner.add(sd.sts_stubber.stub_get_caller_identity, "111122223333")
            runner.add(
                sd.pinpoint_stubber.stub_update_email_channel,
                self.app_id,
                self.email,
                self.identity_arn,
            )
            runner.add(
                sd.pinpoint_stubber.stub_create_segment,
                self.app_id,
                "EmailSubscribers",
                self.segment_id,
            )
            runner.add(
                sd.pinpoint_stubber.stub_create_campaign,
                self.app_id,
                "WelcomeEmailCampaign",
                self.segment_id,
                self.email,
                "Welcome to Our Service!",
                (
                    "<html><body><h1>Welcome!</h1>"
                    "<p>Thank you for subscribing to our service.</p>"
                    "</body></html>"
                ),
                "Welcome! Thank you for subscribing to our service.",
                self.campaign_id,
            )
            runner.add(
                sd.pinpoint_stubber.stub_send_messages,
                self.app_id,
                self.email,
                [self.email],
                "Direct Message from Pinpoint",
                (
                    "<h1>Hello!</h1>"
                    "<p>This is a transactional email sent via the SendMessages API.</p>"
                ),
                "Hello! This is a transactional email sent via the SendMessages API.",
                [self.message_id],
            )
            runner.add(
                sd.pinpoint_stubber.stub_get_campaign, self.app_id, self.campaign_id
            )
            runner.add(
                sd.pinpoint_stubber.stub_get_campaign_activities,
                self.app_id,
                self.campaign_id,
                self.activities,
            )
            # --- cleanup() ---
            runner.add(
                sd.pinpoint_stubber.stub_delete_campaign,
                self.app_id,
                self.campaign_id,
            )
            runner.add(
                sd.pinpoint_stubber.stub_delete_segment, self.app_id, self.segment_id
            )
            runner.add(sd.pinpoint_stubber.stub_delete_app, self.app)
            runner.add(sd.cf_stubber.stub_delete_stack, "pinpoint-basics-ses-identity")


class _NoopWaiter:
    """Stand-in for a CloudFormation waiter that returns immediately."""

    def wait(self, **kwargs):
        return None


@pytest.fixture
def mock_mgr(stub_runner, scenario_data):
    return MockManager(stub_runner, scenario_data)


def test_run_scenario(mock_mgr, capsys, monkeypatch):
    """The full scenario run() + cleanup() completes when all calls succeed."""
    mock_mgr.setup_stubs(None, None, monkeypatch)

    scenario = mock_mgr.scenario_data.scenario
    scenario.run()
    scenario.cleanup()

    captured = capsys.readouterr()
    assert "All resources cleaned up successfully." in captured.out


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TestException", 0),  # create_app fails
        ("TestException", 3),  # create_segment fails
        ("TestException", 4),  # create_campaign fails
    ],
)
def test_run_scenario_error(mock_mgr, caplog, monkeypatch, error, stop_on_index):
    """An error on any stubbed call propagates out of run()."""
    mock_mgr.setup_stubs(error, stop_on_index, monkeypatch)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.run()
    assert exc_info.value.response["Error"]["Code"] == error
