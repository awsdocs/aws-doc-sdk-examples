# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Pinpoint to
send SMS messages using a message template.
"""

# snippet-start:[pinpoint.python.pinpoint_send_templated_sms_message.complete]
import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


def send_templated_sms_message(
        pinpoint_client,
        project_id,
        destination_number,
        message_type,
        origination_number,
        template_name,
        template_version):
    """
    Sends an SMS message to a specific phone number using a pre-defined template.

    :param pinpoint_client: A Boto3 Pinpoint client.
    :param project_id: An Amazon Pinpoint project (application) ID.
    :param destination_number: The phone number to send the message to.
    :param message_type: The type of SMS message (promotional or transactional).
    :param origination_number: The phone number that the message is sent from.
    :param template_name: The name of the SMS template to use when sending the message.
    :param template_version: The version number of the message template.

    :return The ID of the message.
    """
    try:
        response = pinpoint_client.send_messages(
            ApplicationId=project_id,
            MessageRequest={
                'Addresses': {
                    destination_number: {
                        'ChannelType': 'SMS'
                    }
                },
                'MessageConfiguration': {
                    'SMSMessage': {
                        'MessageType': message_type,
                        'OriginationNumber': origination_number
                    }
                },
                'TemplateConfiguration': {
                    'SMSTemplate': {
                        'Name': template_name,
                        'Version': template_version
                    }
                }
            }
        )

    except ClientError:
        logger.exception("Couldn't send message.")
        raise
    else:
        return response['MessageResponse']['Result'][destination_number]['MessageId']


def main():
    region = "us-east-1"
    origination_number = "+18555550001"
    destination_number = "+14255550142"
    project_id = "7353f53e6885409fa32d07cedexample"
    message_type = "TRANSACTIONAL"
    template_name = "My_SMS_Template"
    template_version = "1"
    message_id = send_templated_sms_message(
        boto3.client('pinpoint', region_name=region), project_id,
        destination_number, message_type, origination_number, template_name,
        template_version)
    print(f"Message sent! Message ID: {message_id}.")


if __name__ == '__main__':
    main()
# snippet-end:[pinpoint.python.pinpoint_send_templated_sms_message.complete]
