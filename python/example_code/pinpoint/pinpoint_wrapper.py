# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon Pinpoint wrapper class that encapsulates Amazon Pinpoint operations.
"""

import logging
from typing import Any, Dict, List, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.pinpoint.PinpointWrapper.class]
# snippet-start:[python.example_code.pinpoint.PinpointWrapper.decl]
class PinpointWrapper:
    """Encapsulates Amazon Pinpoint operations."""

    def __init__(self, pinpoint_client: boto3.client):
        """
        Initializes the PinpointWrapper with a Boto3 Pinpoint client.

        :param pinpoint_client: A Boto3 Amazon Pinpoint client.
        """
        self.pinpoint_client = pinpoint_client

    @classmethod
    def from_client(cls) -> "PinpointWrapper":
        """Creates a PinpointWrapper from a new Boto3 Pinpoint client."""
        pinpoint_client = boto3.client("pinpoint")
        return cls(pinpoint_client)

    # snippet-end:[python.example_code.pinpoint.PinpointWrapper.decl]

    # snippet-start:[python.example_code.pinpoint.GetApps]
    def get_apps(self, page_size: int = 25) -> List[Dict[str, Any]]:
        """
        Retrieves information about all applications (projects) associated
        with the Amazon Pinpoint account.

        :param page_size: The maximum number of applications to return per page.
        :return: A list of application dictionaries.
        """
        try:
            response = self.pinpoint_client.get_apps(PageSize=str(page_size))
            apps = response.get("ApplicationsResponse", dict()).get("Item", list())
            logger.info("Retrieved %s application(s).", len(apps))
            return apps
        except ClientError as err:
            if err.response["Error"]["Code"] == "BadRequestException":
                logger.error(
                    "Bad request retrieving applications: %s",
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.pinpoint.GetApps]

    # snippet-start:[python.example_code.pinpoint.CreateApp]
    def create_app(self, app_name: str) -> Dict[str, Any]:
        """
        Creates a new Amazon Pinpoint application (project).

        :param app_name: The display name of the application.
        :return: The application response dictionary containing Id, Name, and Arn.
        """
        try:
            response = self.pinpoint_client.create_app(
                CreateApplicationRequest={"Name": app_name}
            )
            app_response = response.get("ApplicationResponse", dict())
            logger.info(
                "Created application '%s' with ID '%s'.",
                app_response.get("Name"),
                app_response.get("Id"),
            )
            return app_response
        except ClientError as err:
            if err.response["Error"]["Code"] == "BadRequestException":
                logger.error(
                    "Bad request creating application '%s': %s",
                    app_name,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.pinpoint.CreateApp]

    # snippet-start:[python.example_code.pinpoint.UpdateEmailChannel]
    def update_email_channel(
        self,
        application_id: str,
        from_address: str,
        identity_arn: str,
        enabled: bool = True,
    ) -> Dict[str, Any]:
        """
        Enables and configures the email channel for an application.

        :param application_id: The unique ID of the Amazon Pinpoint application.
        :param from_address: The verified email address to send from.
        :param identity_arn: The ARN of the verified SES email identity.
        :param enabled: Whether to enable the email channel. Default is True.
        :return: The email channel response dictionary.
        """
        try:
            response = self.pinpoint_client.update_email_channel(
                ApplicationId=application_id,
                EmailChannelRequest={
                    "FromAddress": from_address,
                    "Identity": identity_arn,
                    "Enabled": enabled,
                },
            )
            channel_response = response.get("EmailChannelResponse", dict())
            logger.info(
                "Email channel updated for application '%s'. Enabled: %s",
                application_id,
                channel_response.get("Enabled"),
            )
            return channel_response
        except ClientError as err:
            if err.response["Error"]["Code"] == "BadRequestException":
                logger.error(
                    "Bad request updating email channel for application '%s': %s",
                    application_id,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.pinpoint.UpdateEmailChannel]

    # snippet-start:[python.example_code.pinpoint.CreateSegment]
    def create_segment(
        self, application_id: str, segment_name: str
    ) -> Dict[str, Any]:
        """
        Creates a new segment targeting email subscribers for an application.

        :param application_id: The unique ID of the Amazon Pinpoint application.
        :param segment_name: The display name of the segment.
        :return: The segment response dictionary containing Id, Name, and SegmentType.
        """
        try:
            response = self.pinpoint_client.create_segment(
                ApplicationId=application_id,
                WriteSegmentRequest={
                    "Name": segment_name,
                    "Dimensions": {
                        "Demographic": {
                            "Channel": {
                                "DimensionType": "INCLUSIVE",
                                "Values": ["EMAIL"],
                            }
                        }
                    },
                },
            )
            segment_response = response.get("SegmentResponse", dict())
            logger.info(
                "Created segment '%s' with ID '%s'.",
                segment_response.get("Name"),
                segment_response.get("Id"),
            )
            return segment_response
        except ClientError as err:
            if err.response["Error"]["Code"] == "BadRequestException":
                logger.error(
                    "Bad request creating segment '%s': %s",
                    segment_name,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.pinpoint.CreateSegment]

    # snippet-start:[python.example_code.pinpoint.CreateCampaign]
    def create_campaign(
        self,
        application_id: str,
        campaign_name: str,
        segment_id: str,
        from_address: str,
        subject: str,
        html_body: str,
        text_body: str,
    ) -> Dict[str, Any]:
        """
        Creates an email campaign targeting a segment for immediate delivery.

        :param application_id: The unique ID of the Amazon Pinpoint application.
        :param campaign_name: The display name for the campaign.
        :param segment_id: The ID of the segment to target.
        :param from_address: The verified sender email address.
        :param subject: The email subject line.
        :param html_body: The HTML body of the email.
        :param text_body: The plain-text body of the email.
        :return: The campaign response dictionary containing Id, Name, and State.
        """
        try:
            response = self.pinpoint_client.create_campaign(
                ApplicationId=application_id,
                WriteCampaignRequest={
                    "Name": campaign_name,
                    "SegmentId": segment_id,
                    "MessageConfiguration": {
                        "EmailMessage": {
                            "Title": subject,
                            "Body": text_body,
                            "HtmlBody": html_body,
                            "FromAddress": from_address,
                        }
                    },
                    "Schedule": {"StartTime": "IMMEDIATE"},
                },
            )
            campaign_response = response.get("CampaignResponse", dict())
            logger.info(
                "Created campaign '%s' with ID '%s'. State: %s",
                campaign_response.get("Name"),
                campaign_response.get("Id"),
                campaign_response.get("State", dict()).get("CampaignStatus"),
            )
            return campaign_response
        except ClientError as err:
            if err.response["Error"]["Code"] == "BadRequestException":
                logger.error(
                    "Bad request creating campaign '%s': %s",
                    campaign_name,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.pinpoint.CreateCampaign]

    def send_messages(
        self,
        application_id: str,
        from_address: str,
        to_addresses: List[str],
        subject: str,
        html_body: str,
        text_body: str,
    ) -> Dict[str, Any]:
        """
        Sends a direct transactional email message to specific addresses.

        NOTE: This operation (SendMessages) is already covered upstream and is
        intentionally NOT wrapped in snippet tags. The code call is kept because
        the scenario workflow requires it.

        :param application_id: The unique ID of the Amazon Pinpoint application.
        :param from_address: The verified sender email address.
        :param to_addresses: A list of recipient email addresses.
        :param subject: The email subject line.
        :param html_body: The HTML body of the email.
        :param text_body: The plain-text body of the email.
        :return: The message response result dictionary.
        """
        addresses = dict()
        for addr in to_addresses:
            addresses[addr] = {"ChannelType": "EMAIL"}
        try:
            response = self.pinpoint_client.send_messages(
                ApplicationId=application_id,
                MessageRequest={
                    "Addresses": addresses,
                    "MessageConfiguration": {
                        "EmailMessage": {
                            "FromAddress": from_address,
                            "SimpleEmail": {
                                "Subject": {
                                    "Charset": "UTF-8",
                                    "Data": subject,
                                },
                                "HtmlPart": {
                                    "Charset": "UTF-8",
                                    "Data": html_body,
                                },
                                "TextPart": {
                                    "Charset": "UTF-8",
                                    "Data": text_body,
                                },
                            },
                        }
                    },
                },
            )
            result = response.get("MessageResponse", dict()).get("Result", dict())
            for addr, status in result.items():
                logger.info(
                    "Message to %s: %s (status code %s)",
                    addr,
                    status.get("DeliveryStatus"),
                    status.get("StatusCode"),
                )
            return result
        except ClientError as err:
            if err.response["Error"]["Code"] == "BadRequestException":
                logger.error(
                    "Bad request sending messages: %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-start:[python.example_code.pinpoint.GetCampaign]
    def get_campaign(
        self, application_id: str, campaign_id: str
    ) -> Dict[str, Any]:
        """
        Retrieves information about the status, configuration, and other
        settings for a campaign.

        :param application_id: The unique ID of the Amazon Pinpoint application.
        :param campaign_id: The unique ID of the campaign.
        :return: The campaign response dictionary.
        """
        try:
            response = self.pinpoint_client.get_campaign(
                ApplicationId=application_id,
                CampaignId=campaign_id,
            )
            campaign_response = response.get("CampaignResponse", dict())
            logger.info(
                "Retrieved campaign '%s'. State: %s",
                campaign_response.get("Name"),
                campaign_response.get("State", dict()).get("CampaignStatus"),
            )
            return campaign_response
        except ClientError as err:
            if err.response["Error"]["Code"] == "NotFoundException":
                logger.error(
                    "Campaign '%s' not found: %s",
                    campaign_id,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.pinpoint.GetCampaign]

    # snippet-start:[python.example_code.pinpoint.GetCampaignActivities]
    def get_campaign_activities(
        self, application_id: str, campaign_id: str
    ) -> List[Dict[str, Any]]:
        """
        Retrieves information about all the activities for a campaign.

        :param application_id: The unique ID of the Amazon Pinpoint application.
        :param campaign_id: The unique ID of the campaign.
        :return: A list of campaign activity dictionaries.
        """
        try:
            response = self.pinpoint_client.get_campaign_activities(
                ApplicationId=application_id,
                CampaignId=campaign_id,
            )
            activities = response.get("ActivitiesResponse", dict()).get(
                "Item", list()
            )
            logger.info(
                "Retrieved %s activity(ies) for campaign '%s'.",
                len(activities),
                campaign_id,
            )
            return activities
        except ClientError as err:
            if err.response["Error"]["Code"] == "NotFoundException":
                logger.error(
                    "Campaign '%s' not found when retrieving activities: %s",
                    campaign_id,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.pinpoint.GetCampaignActivities]

    # snippet-start:[python.example_code.pinpoint.DeleteCampaign]
    def delete_campaign(
        self, application_id: str, campaign_id: str
    ) -> None:
        """
        Deletes a campaign from an application.

        :param application_id: The unique ID of the Amazon Pinpoint application.
        :param campaign_id: The unique ID of the campaign to delete.
        """
        try:
            self.pinpoint_client.delete_campaign(
                ApplicationId=application_id,
                CampaignId=campaign_id,
            )
            logger.info(
                "Deleted campaign '%s' from application '%s'.",
                campaign_id,
                application_id,
            )
        except ClientError as err:
            if err.response["Error"]["Code"] == "NotFoundException":
                logger.error(
                    "Campaign '%s' not found (may already be deleted): %s",
                    campaign_id,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.pinpoint.DeleteCampaign]

    # snippet-start:[python.example_code.pinpoint.DeleteSegment]
    def delete_segment(
        self, application_id: str, segment_id: str
    ) -> None:
        """
        Deletes a segment from an application.

        :param application_id: The unique ID of the Amazon Pinpoint application.
        :param segment_id: The unique ID of the segment to delete.
        """
        try:
            self.pinpoint_client.delete_segment(
                ApplicationId=application_id,
                SegmentId=segment_id,
            )
            logger.info(
                "Deleted segment '%s' from application '%s'.",
                segment_id,
                application_id,
            )
        except ClientError as err:
            if err.response["Error"]["Code"] == "NotFoundException":
                logger.error(
                    "Segment '%s' not found (may already be deleted): %s",
                    segment_id,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.pinpoint.DeleteSegment]

    # snippet-start:[python.example_code.pinpoint.DeleteApp]
    def delete_app(self, application_id: str) -> None:
        """
        Deletes an Amazon Pinpoint application (project).

        :param application_id: The unique ID of the application to delete.
        """
        try:
            self.pinpoint_client.delete_app(ApplicationId=application_id)
            logger.info("Deleted application '%s'.", application_id)
        except ClientError as err:
            if err.response["Error"]["Code"] == "NotFoundException":
                logger.error(
                    "Application '%s' not found (may already be deleted): %s",
                    application_id,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.pinpoint.DeleteApp]


# snippet-end:[python.example_code.pinpoint.PinpointWrapper.class]
