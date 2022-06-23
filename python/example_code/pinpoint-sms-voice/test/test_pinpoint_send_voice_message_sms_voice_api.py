# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for pinpoint_send_voice_message_sms_voice_api.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

import pinpoint_send_voice_message_sms_voice_api as api


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_send_voice_message(make_stubber, error_code):
    sms_voice_client = boto3.client('pinpoint-sms-voice')
    sms_voice_stubber = make_stubber(sms_voice_client)
    test_orig_num = 'origination'
    test_caller_id = 'callerid'
    test_dest_num = 'destination'
    test_lang = 'test-lang'
    test_voice_id = 'test-voice'
    test_message = 'Test message!'
    test_id = 'test-id'

    sms_voice_stubber.stub_send_voice_message(
        test_orig_num, test_caller_id, test_dest_num, test_lang, test_voice_id,
        test_message, test_id, error_code=error_code)

    if error_code is None:
        got_id = api.send_voice_message(
            sms_voice_client, test_orig_num, test_caller_id, test_dest_num, test_lang,
            test_voice_id, test_message)
        assert got_id == test_id
    else:
        with pytest.raises(ClientError) as exc_info:
            api.send_voice_message(
                sms_voice_client, test_orig_num, test_caller_id, test_dest_num, test_lang,
                test_voice_id, test_message)
        assert exc_info.value.response['Error']['Code'] == error_code
