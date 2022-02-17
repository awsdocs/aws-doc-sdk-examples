# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Unit tests for ses_email.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from ses_email import SesDestination, SesMailSender


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_send_email(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_mail_sender = SesMailSender(ses_client)
    source = 'test@example.com'
    destination = SesDestination(['test-dest-1@example.com', 'test-dest-2@example.com'])
    subject = 'Testing!'
    text = 'Texting!'
    html = 'Htmling!'
    reply_tos = ['test-reply@example.com']
    message_id = 'message-id'

    ses_stubber.stub_send_email(
        source, destination.to_service_format(), subject, text, html, message_id,
        reply_tos=reply_tos, error_code=error_code)

    if error_code is None:
        got_id = ses_mail_sender.send_email(
            source, destination, subject, text, html, reply_tos)
        assert got_id == message_id
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_mail_sender.send_email(
                source, destination, subject, text, html, reply_tos)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize("error_code", [None, 'TestException'])
def test_send_templated_email(make_stubber, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    ses_mail_sender = SesMailSender(ses_client)
    source = 'test@example.com'
    destination = SesDestination(['test-dest-1@example.com', 'test-dest-2@example.com'])
    reply_tos = ['test-reply@example.com']
    template_name = 'test-template'
    template_data = {'thing': 'oboe'}
    message_id = 'message-id'

    ses_stubber.stub_send_templated_email(
        source, destination.to_service_format(), template_name, template_data,
        message_id, reply_tos=reply_tos, error_code=error_code)

    if error_code is None:
        got_id = ses_mail_sender.send_templated_email(
            source, destination, template_name, template_data, reply_tos)
        assert got_id == message_id
    else:
        with pytest.raises(ClientError) as exc_info:
            ses_mail_sender.send_templated_email(
                source, destination, template_name, template_data, reply_tos)
        assert exc_info.value.response['Error']['Code'] == error_code
