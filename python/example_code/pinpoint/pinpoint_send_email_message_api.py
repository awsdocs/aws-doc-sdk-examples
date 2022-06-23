# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Pinpoint to
send email.
"""

# snippet-start:[pinpoint.python.pinpoint_send_email_message_api.complete]

import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


def send_email_message(
        pinpoint_client, app_id, sender, to_addresses, char_set, subject,
        html_message, text_message):
    """
    Sends an email message with HTML and plain text versions.
    
    :param pinpoint_client: A Boto3 Pinpoint client.
    :param app_id: The Amazon Pinpoint project ID to use when you send this message.
    :param sender: The "From" address. This address must be verified in
                   Amazon Pinpoint in the AWS Region you're using to send email.
    :param to_addresses: The addresses on the "To" line. If your Amazon Pinpoint account
                         is in the sandbox, these addresses must be verified.
    :param char_set: The character encoding to use for the subject line and message
                     body of the email.
    :param subject: The subject line of the email.
    :param html_message: The body of the email for recipients whose email clients can
                         display HTML content.
    :param text_message: The body of the email for recipients whose email clients
                         don't support HTML content.
    :return: A dict of to_addresses and their message IDs.
    """
    try:
        response = pinpoint_client.send_messages(
            ApplicationId=app_id,
            MessageRequest={
                'Addresses': {
                    to_address: {'ChannelType': 'EMAIL'} for to_address in to_addresses
                },
                'MessageConfiguration': {
                    'EmailMessage': {
                        'FromAddress': sender,
                        'SimpleEmail': {
                            'Subject': {'Charset': char_set, 'Data': subject},
                            'HtmlPart': {'Charset': char_set, 'Data': html_message},
                            'TextPart': {'Charset': char_set, 'Data': text_message}}}}})
    except ClientError:
        logger.exception("Couldn't send email.")
        raise
    else:
        return {
            to_address: message['MessageId'] for
            to_address, message in response['MessageResponse']['Result'].items()
        }


def main():
    app_id = "ce796be37f32f178af652b26eexample"
    sender = "sender@example.com"
    to_address = "recipient@example.com"
    char_set = "UTF-8"
    subject = "Amazon Pinpoint Test (SDK for Python (Boto3))"
    text_message = """Amazon Pinpoint Test (SDK for Python)
    -------------------------------------
    This email was sent with Amazon Pinpoint using the AWS SDK for Python (Boto3).
    For more information, see https://aws.amazon.com/sdk-for-python/
                """
    html_message = """<html>
    <head></head>
    <body>
      <h1>Amazon Pinpoint Test (SDK for Python (Boto3)</h1>
      <p>This email was sent with
        <a href='https://aws.amazon.com/pinpoint/'>Amazon Pinpoint</a> using the
        <a href='https://aws.amazon.com/sdk-for-python/'>
          AWS SDK for Python (Boto3)</a>.</p>
    </body>
    </html>
                """

    print("Sending email.")
    message_ids = send_email_message(
        boto3.client('pinpoint'), app_id, sender, [to_address], char_set, subject,
        html_message, text_message)
    print(f"Message sent! Message IDs: {message_ids}")


if __name__ == '__main__':
    main()
# snippet-end:[pinpoint.python.pinpoint_send_email_message_api.complete]
