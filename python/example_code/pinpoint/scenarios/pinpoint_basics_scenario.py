# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon Pinpoint Basics Scenario

Demonstrates a complete end-to-end email marketing campaign lifecycle using
Amazon Pinpoint. Steps:

1.  Deploy a CloudFormation stack to create a verified SES email identity.
2.  CreateApp — Create a Pinpoint project.
3.  UpdateEmailChannel — Enable and configure the email channel.
4.  CreateSegment — Define the target audience.
5.  CreateCampaign — Create an email campaign targeting the segment.
6.  SendMessages — Send a direct transactional email (already-covered action;
    no snippet tags).
7.  GetCampaign — Check campaign status.
8.  GetCampaignActivities — Inspect campaign execution results.
9.  DeleteCampaign — Clean up the campaign.
10. DeleteSegment — Clean up the segment.
11. DeleteApp — Clean up the application.
12. Delete the CloudFormation stack.
"""

import json
import logging
import time
from typing import Any, Optional

import boto3
from botocore.exceptions import ClientError

from pinpoint_wrapper import PinpointWrapper

logger = logging.getLogger(__name__)

STACK_NAME = "pinpoint-basics-ses-identity"


# snippet-start:[python.example_code.pinpoint.PinpointScenario]
class PinpointScenario:
    """Runs the Amazon Pinpoint Basics interactive scenario."""

    def __init__(
        self,
        pinpoint_wrapper: PinpointWrapper,
        cf_client: Any,
        sts_client: Any,
    ):
        """
        :param pinpoint_wrapper: An instance of PinpointWrapper.
        :param cf_client: A Boto3 CloudFormation client.
        :param sts_client: A Boto3 STS client (used to derive the account ID).
        """
        self.pinpoint_wrapper = pinpoint_wrapper
        self.cf_client = cf_client
        self.sts_client = sts_client
        self.app_id: Optional[str] = None
        self.segment_id: Optional[str] = None
        self.campaign_id: Optional[str] = None
        self.email_address: Optional[str] = None
        self.stack_deployed = False

    # ------------------------------------------------------------------
    # Setup: deploy the SES identity via CloudFormation
    # ------------------------------------------------------------------
    def setup(self, email_address: str) -> None:
        """
        Deploys a CloudFormation stack that creates a verified SES email
        identity, then waits for the user to confirm the verification link.

        :param email_address: The email address to verify.
        """
        self.email_address = email_address
        template_body = json.dumps(
            {
                "AWSTemplateFormatVersion": "2010-09-09",
                "Description": "SES email identity for Pinpoint Basics scenario.",
                "Resources": {
                    "SESEmailIdentity": {
                        "Type": "AWS::SES::EmailIdentity",
                        "Properties": {"EmailIdentity": email_address},
                    }
                },
            }
        )
        print(
            "\n"
            + "-" * 80
            + "\nSetting up resources for the Amazon Pinpoint Basics scenario..."
            + "\n"
            + "-" * 80
        )
        print(
            f"\nDeploying CloudFormation stack '{STACK_NAME}' to create "
            f"SES email identity for {email_address}..."
        )
        try:
            self.cf_client.create_stack(
                StackName=STACK_NAME,
                TemplateBody=template_body,
            )
            self.stack_deployed = True
            waiter = self.cf_client.get_waiter("stack_create_complete")
            print("Waiting for stack creation to complete...")
            waiter.wait(
                StackName=STACK_NAME,
                WaiterConfig={"Delay": 10, "MaxAttempts": 60},
            )
            print("Stack creation complete.\n")
        except ClientError:
            logger.exception("Failed to deploy CloudFormation stack.")
            raise

        print(
            f"IMPORTANT: A verification email has been sent to {email_address}.\n"
            "Please check your inbox, click the verification link, and then "
            "press Enter to continue..."
        )
        input()

    # ------------------------------------------------------------------
    # Scenario steps
    # ------------------------------------------------------------------
    def run(self) -> None:
        """Runs all scenario steps in sequence."""
        print("-" * 80)

        # Step 1 — CreateApp
        print("\nStep 1: Creating a new Amazon Pinpoint application...")
        app = self.pinpoint_wrapper.create_app("PinpointBasicsExample")
        self.app_id = app.get("Id")
        print(
            f"  Name: {app.get('Name')}\n"
            f"  ID:   {self.app_id}\n"
            f"  ARN:  {app.get('Arn')}\n"
        )

        # Step 2 — UpdateEmailChannel
        print("Step 2: Configuring the email channel...")
        account_id = self.sts_client.get_caller_identity()["Account"]
        region = self.pinpoint_wrapper.pinpoint_client.meta.region_name
        identity_arn = (
            f"arn:aws:ses:{region}:{account_id}:identity/{self.email_address}"
        )
        channel = self.pinpoint_wrapper.update_email_channel(
            application_id=self.app_id,
            from_address=self.email_address,
            identity_arn=identity_arn,
        )
        print(
            f"  From Address: {channel.get('FromAddress')}\n"
            f"  Identity:     {channel.get('Identity')}\n"
            f"  Enabled:      {channel.get('Enabled')}\n"
        )

        # Step 3 — CreateSegment
        print("Step 3: Creating a segment targeting email subscribers...")
        segment = self.pinpoint_wrapper.create_segment(
            application_id=self.app_id, segment_name="EmailSubscribers"
        )
        self.segment_id = segment.get("Id")
        print(
            f"  Name: {segment.get('Name')}\n"
            f"  ID:   {self.segment_id}\n"
            f"  Type: {segment.get('SegmentType')}\n"
        )

        # Step 4 — CreateCampaign
        print("Step 4: Creating an email campaign targeting the segment...")
        campaign = self.pinpoint_wrapper.create_campaign(
            application_id=self.app_id,
            campaign_name="WelcomeEmailCampaign",
            segment_id=self.segment_id,
            from_address=self.email_address,
            subject="Welcome to Our Service!",
            html_body=(
                "<html><body><h1>Welcome!</h1>"
                "<p>Thank you for subscribing to our service.</p>"
                "</body></html>"
            ),
            text_body="Welcome! Thank you for subscribing to our service.",
        )
        self.campaign_id = campaign.get("Id")
        print(
            f"  Name:  {campaign.get('Name')}\n"
            f"  ID:    {self.campaign_id}\n"
            f"  State: {campaign.get('State', dict()).get('CampaignStatus')}\n"
        )

        # Step 5 — SendMessages (no snippet tags — already covered upstream)
        print("Step 5: Sending a direct transactional email via SendMessages API...")
        result = self.pinpoint_wrapper.send_messages(
            application_id=self.app_id,
            from_address=self.email_address,
            to_addresses=[self.email_address],
            subject="Direct Message from Pinpoint",
            html_body=(
                "<h1>Hello!</h1>"
                "<p>This is a transactional email sent via the SendMessages API.</p>"
            ),
            text_body=(
                "Hello! This is a transactional email sent via the " "SendMessages API."
            ),
        )
        print("Message delivery results:")
        for addr, status in result.items():
            print(
                f"  {addr}:\n"
                f"    Delivery Status: {status.get('DeliveryStatus')}\n"
                f"    Status Code:     {status.get('StatusCode')}\n"
                f"    Message ID:      {status.get('MessageId')}\n"
            )

        # Brief pause so the campaign has time to execute.
        print("Waiting a moment for the campaign to execute...")
        time.sleep(5)

        # Step 6 — GetCampaign
        print("Step 6: Retrieving campaign details...")
        campaign_detail = self.pinpoint_wrapper.get_campaign(
            application_id=self.app_id, campaign_id=self.campaign_id
        )
        print(
            f"  Name:       {campaign_detail.get('Name')}\n"
            f"  ID:         {campaign_detail.get('Id')}\n"
            f"  Segment ID: {campaign_detail.get('SegmentId')}\n"
            f"  State:      {campaign_detail.get('State', dict()).get('CampaignStatus')}\n"
            f"  Created:    {campaign_detail.get('CreationDate')}\n"
        )

        # Step 7 — GetCampaignActivities
        print("Step 7: Checking campaign activities...")
        activities = self.pinpoint_wrapper.get_campaign_activities(
            application_id=self.app_id, campaign_id=self.campaign_id
        )
        if activities:
            for i, activity in enumerate(activities, 1):
                print(
                    f"  Activity {i}:\n"
                    f"    ID:               {activity.get('Id')}\n"
                    f"    Campaign ID:      {activity.get('CampaignId')}\n"
                    f"    State:            {activity.get('State')}\n"
                    f"    Start Time:       {activity.get('Start')}\n"
                    f"    End Time:         {activity.get('End')}\n"
                    f"    Successful Count: {activity.get('SuccessfulEndpointCount')}\n"
                    f"    Total Endpoints:  {activity.get('TotalEndpointCount')}\n"
                )
        else:
            print("  No activities found.\n")

    # ------------------------------------------------------------------
    # Cleanup
    # ------------------------------------------------------------------
    def cleanup(self) -> None:
        """Deletes all resources created by the scenario."""
        print("-" * 80 + "\nCleaning up resources...\n" + "-" * 80)
        if self.campaign_id and self.app_id:
            try:
                print(
                    f"Step 8: Deleting campaign '{self.campaign_id}'...",
                    end=" ",
                )
                self.pinpoint_wrapper.delete_campaign(self.app_id, self.campaign_id)
                print("Done.")
            except ClientError:
                logger.info("Campaign may already be deleted.")

        if self.segment_id and self.app_id:
            try:
                print(
                    f"Step 9: Deleting segment '{self.segment_id}'...",
                    end=" ",
                )
                self.pinpoint_wrapper.delete_segment(self.app_id, self.segment_id)
                print("Done.")
            except ClientError:
                logger.info("Segment may already be deleted.")

        if self.app_id:
            try:
                print(
                    f"Step 10: Deleting application '{self.app_id}'...",
                    end=" ",
                )
                self.pinpoint_wrapper.delete_app(self.app_id)
                print("Done.")
            except ClientError:
                logger.info("Application may already be deleted.")

        if self.stack_deployed:
            try:
                print(
                    f"Deleting CloudFormation stack '{STACK_NAME}'...",
                    end=" ",
                )
                self.cf_client.delete_stack(StackName=STACK_NAME)
                waiter = self.cf_client.get_waiter("stack_delete_complete")
                waiter.wait(
                    StackName=STACK_NAME,
                    WaiterConfig={"Delay": 10, "MaxAttempts": 60},
                )
                print("Done.")
            except ClientError:
                logger.info("CloudFormation stack may already be deleted.")

        print("-" * 80 + "\nAll resources cleaned up successfully.\n" + "-" * 80)


# snippet-end:[python.example_code.pinpoint.PinpointScenario]


def main() -> None:
    """Entry point for running the Pinpoint Basics scenario."""
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    pinpoint_client = boto3.client("pinpoint")
    cf_client = boto3.client("cloudformation")
    sts_client = boto3.client("sts")
    wrapper = PinpointWrapper(pinpoint_client)
    scenario = PinpointScenario(wrapper, cf_client, sts_client)

    print("=" * 80)
    print("Welcome to the Amazon Pinpoint Basics Scenario!")
    print("=" * 80)
    email = input(
        "\nEnter the email address to use for sending "
        "(must be accessible for verification):\n> "
    ).strip()

    try:
        scenario.setup(email)
        scenario.run()
    finally:
        scenario.cleanup()


if __name__ == "__main__":
    main()
