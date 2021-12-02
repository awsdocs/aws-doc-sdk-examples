# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Pinpoint to
send email using a message template.
"""

# snippet-start:[pinpoint.python.pinpoint_send_templated_email_message.complete]
import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


def send_templated_email_message(
        pinpoint_client, project_id, sender, to_addresses, template_name,
        template_version):
    """
    Sends an email message with HTML and plain text versions.

    :param pinpoint_client: A Boto3 Pinpoint client.
    :param project_id: The Amazon Pinpoint project ID to use when you send this message.
    :param sender: The "From" address. This address must be verified in
                   Amazon Pinpoint in the AWS Region you're using to send email.
    :param to_addresses: The addresses on the "To" line. If your Amazon Pinpoint
                         account is in the sandbox, these addresses must be verified.
    :param template_name: The name of the email template to use when sending the message.
    :param template_version: The version number of the message template.

    :return: A dict of to_addresses and their message IDs.
    """
    try:
        response = pinpoint_client.send_messages(
            ApplicationId=project_id,
            MessageRequest={
                'Addresses': {
                    to_address: {
                        'ChannelType': 'EMAIL'
                    } for to_address in to_addresses
                },
                'MessageConfiguration': {
                    'EmailMessage': {'FromAddress': sender}
                },
                'TemplateConfiguration': {
                    'EmailTemplate': {
                        'Name': template_name,
                        'Version': template_version
                    }
                }
            }
        )
    except ClientError:
        logger.exception("Couldn't send email.")
        raise
    else:
        return {
            to_address: message['MessageId'] for
            to_address, message in response['MessageResponse']['Result'].items()
        }


def main():
    project_id = "296b04b342374fceb661bf494example"
    sender = "sender@example.com"
    to_addresses = ["recipient@example.com"]
    template_name = "My_Email_Template"
    template_version = "1"

    print("Sending email.")
    message_ids = send_templated_email_message(
        boto3.client('pinpoint'), project_id, sender, to_addresses, template_name,
        template_version)
    print(f"Message sent! Message IDs: {message_ids}")


if __name__ == '__main__':
    main()
# snippet-end:[pinpoint.python.pinpoint_send_templated_email_message.complete]
