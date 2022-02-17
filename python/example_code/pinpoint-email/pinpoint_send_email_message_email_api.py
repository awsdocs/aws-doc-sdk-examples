# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Pinpoint Email to
send email messages.
"""

# snippet-start:[pinpoint.python.pinpoint_send_email_message_email_api.complete]

import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


def send_email_message(
        pinpoint_email_client, sender, to_addresses, cc_addresses, char_set,
        subject, html_message, text_message):
    """
    Sends an email message that contains both plain text and HTML versions.

    :param pinpoint_email_client: A Boto3 Pinpoint Email client.
    :param sender: The "From" address. This address must be verified in
                   Amazon Pinpoint in the region you're using to send email.
    :param to_addresses: The addresses on the "To" line. If your Amazon Pinpoint account
                        is in the sandbox, these addresses must also be verified.
    :param cc_addresses: The CC addresses. If your account is in the sandbox, these
                         addresses must also be verified.
    :param char_set: The character encoding that you want to use for the subject line
                     and message body of the email.
    :param subject: The subject line of the email.
    :param html_message: The body of the email for recipients whose email clients can
                         display HTML content.
    :param text_message: The body of the email for recipients whose email clients don't
                         support HTML content.
    :return: The ID of the message.
    """
    try:
        response = pinpoint_email_client.send_email(
            FromEmailAddress=sender,
            Destination={'ToAddresses': to_addresses, 'CcAddresses': cc_addresses},
            Content={
                # If you want to include attachments, send a Raw message instead.
                'Simple': {
                    'Subject': {'Charset': char_set, 'Data': subject},
                    'Body': {
                        'Html': {'Charset': char_set, 'Data': html_message},
                        'Text': {'Charset': char_set, 'Data': text_message}}}})
    except ClientError:
        logger.exception("The message wasn't sent!")
        raise
    else:
        return response['MessageId']


def main():
    sender = "sender@example.com"
    to_addresses = ["recipient@example.com"]
    cc_addresses = ["cc_recipient1@example.com", "cc_recipient2@example.com"]
    subject = "Amazon Pinpoint Test (SDK for Python (Boto3))"
    text_message = """Amazon Pinpoint Test (SDK for Python (Boto3))
    -------------------------------------
    This email was sent with Amazon Pinpoint using the AWS SDK for Python (Boto3).
    For more information, see https://aws.amazon.com/sdk-for-python/
                """
    html_message = """<html>
    <head></head>
    <body>
      <h1>Amazon Pinpoint Test (SDK for Python (Boto3))</h1>
      <p>This email was sent with
        <a href='https://aws.amazon.com/pinpoint/'>Amazon Pinpoint</a> using the
        <a href='https://aws.amazon.com/sdk-for-python/'>
          AWS SDK for Python (Boto3)</a>.</p>
    </body>
    </html>
                """
    char_set = "UTF-8"

    print(f"Sending email.")
    message_id = send_email_message(
        boto3.client('pinpoint-email'), sender, to_addresses, cc_addresses, char_set,
        subject, html_message, text_message)
    print(f"Email sent!\nMessage ID: {message_id}.")


if __name__ == '__main__':
    main()
# snippet-end:[pinpoint.python.pinpoint_send_email_message_email_api.complete]
