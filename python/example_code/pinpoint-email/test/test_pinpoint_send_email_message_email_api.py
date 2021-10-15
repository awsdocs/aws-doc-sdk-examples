# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for pinpoint_send_email_message_email_api.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

import pinpoint_send_email_message_email_api as api


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_send_email_message(make_stubber, error_code):
    pinpoint_email_client = boto3.client('pinpoint-email')
    pinpoint_email_stubber = make_stubber(pinpoint_email_client)
    sender = 'test-sender'
    to_addresses = ['test-to']
    cc_addresses = ['test-cc']
    char_set = 'test-charset'
    subject = 'test-subject'
    html_message = '<p>test html</p>'
    text_message = 'test-message'
    message_id = 'test-id'

    pinpoint_email_stubber.stub_send_email(
        sender, to_addresses, cc_addresses, char_set, subject, html_message,
        text_message, message_id, error_code=error_code)

    if error_code is None:
        got_message_id = api.send_email_message(
            pinpoint_email_client, sender, to_addresses, cc_addresses, char_set,
            subject, html_message, text_message)
        assert got_message_id == message_id
    else:
        with pytest.raises(ClientError) as exc_info:
            api.send_email_message(
                pinpoint_email_client, sender, to_addresses, cc_addresses, char_set,
                subject, html_message, text_message)
        assert exc_info.value.response['Error']['Code'] == error_code
