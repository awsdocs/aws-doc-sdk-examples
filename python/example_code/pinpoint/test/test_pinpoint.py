# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for pinpoint_send_<various>.py.
"""

from unittest import mock
import boto3
from botocore.exceptions import ClientError
import pytest

import pinpoint_send_email_message_api
import pinpoint_send_email_smtp
import pinpoint_send_sms_message_api
import pinpoint_send_templated_email_message
import pinpoint_send_templated_sms_message


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_send_email_message(make_stubber, error_code):
    pinpoint_client = boto3.client('pinpoint')
    pinpoint_stubber = make_stubber(pinpoint_client)
    app_id = 'test-app-id'
    sender = 'test-sender'
    to_address = 'test-to'
    char_set = 'test-charset'
    subject = 'test-subject'
    html_message = '<p>test html</p>'
    text_message = 'test-message'
    message_id = 'test-id'

    pinpoint_stubber.stub_send_email_messages(
        app_id, sender, [to_address], char_set, subject, html_message,
        text_message, [message_id], error_code=error_code)

    if error_code is None:
        got_ids = pinpoint_send_email_message_api.send_email_message(
            pinpoint_client, app_id, sender, [to_address], char_set, subject,
            html_message, text_message)
        assert list(got_ids.values()) == [message_id]
    else:
        with pytest.raises(ClientError) as exc_info:
            pinpoint_send_email_message_api.send_email_message(
                pinpoint_client, app_id, sender, [to_address], char_set, subject,
                html_message, text_message)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_send_templated_email_message(make_stubber, error_code):
    pinpoint_client = boto3.client('pinpoint')
    pinpoint_stubber = make_stubber(pinpoint_client)
    app_id = 'test-app-id'
    sender = 'test-sender'
    to_addresses = ['test-to-1', 'test-to-2']
    template_name = 'test-template'
    template_version = 'test-version'
    message_ids = ['test-id-1', 'test-id-2']

    pinpoint_stubber.stub_send_templated_email_messages(
        app_id, sender, to_addresses, template_name, template_version, message_ids,
        error_code=error_code)

    if error_code is None:
        got_ids = pinpoint_send_templated_email_message.send_templated_email_message(
            pinpoint_client, app_id, sender, to_addresses, template_name,
            template_version)
        assert list(got_ids.values()) == message_ids
    else:
        with pytest.raises(ClientError) as exc_info:
            pinpoint_send_templated_email_message.send_templated_email_message(
                pinpoint_client, app_id, sender, to_addresses, template_name,
                template_version)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_send_sms_message(make_stubber, error_code):
    pinpoint_client = boto3.client('pinpoint')
    pinpoint_stubber = make_stubber(pinpoint_client)
    app_id = 'test-app-id'
    origination_number = 'test-sender'
    destination_number = 'test-dest'
    message = 'test-message'
    message_type = 'TRANSACTIONAL'
    message_id = 'test-id'

    pinpoint_stubber.stub_send_sms_message(
        app_id, origination_number, destination_number, message, message_type,
        message_id, error_code=error_code)

    if error_code is None:
        got_id = pinpoint_send_sms_message_api.send_sms_message(
            pinpoint_client, app_id, origination_number, destination_number, message,
            message_type)
        assert got_id == message_id
    else:
        with pytest.raises(ClientError) as exc_info:
            pinpoint_send_sms_message_api.send_sms_message(
                pinpoint_client, app_id, origination_number, destination_number, message,
                message_type)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_send_templated_sms_message(make_stubber, error_code):
    pinpoint_client = boto3.client('pinpoint')
    pinpoint_stubber = make_stubber(pinpoint_client)
    app_id = 'test-app-id'
    origination_number = 'test-sender'
    destination_number = 'test-dest'
    template_name = 'test-template'
    template_version = 'test-version'
    message_type = 'TRANSACTIONAL'
    message_id = 'test-id'

    pinpoint_stubber.stub_send_templated_sms_message(
        app_id, origination_number, destination_number, message_type,
        template_name, template_version, message_id, error_code=error_code)

    if error_code is None:
        got_id = pinpoint_send_templated_sms_message.send_templated_sms_message(
            pinpoint_client, app_id, destination_number, message_type,
            origination_number, template_name, template_version)
        assert got_id == message_id
    else:
        with pytest.raises(ClientError) as exc_info:
            pinpoint_send_templated_sms_message.send_templated_sms_message(
                pinpoint_client, app_id, destination_number, message_type,
                origination_number, template_name, template_version)
        assert exc_info.value.response['Error']['Code'] == error_code


def test_send_smtp_message():
    smtp_mock = mock.MagicMock()
    smtp_mock.login = mock.MagicMock()
    smtp_mock.sendmail = mock.MagicMock()
    username = 'test-username'
    password = 'test-password'
    sender = 'test-sender'
    to_address = 'test-to'
    cc_address = 'test-cc'
    subject = 'test-subject'
    html_message = 'test-html'
    text_message = 'test-text'
    pinpoint_send_email_smtp.send_smtp_message(
        smtp_mock, username, password, sender, to_address, cc_address, subject,
        html_message, text_message)
    smtp_mock.login.assert_called_with(username, password)
    smtp_mock.sendmail.assert_called_with(sender, to_address, mock.ANY)
