# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for report.py
"""

from datetime import datetime
from unittest.mock import MagicMock
import boto3
import pytest

from report import Report, reqparse, render_template


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_post_report(make_stubber, monkeypatch, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    work_items = [{
        'id': index, 'name': f'user-{index}', 'description': f'desc-{index}',
        'guide': f'guide-{index}', 'status': f'status-{index}',
        'created_date': str(datetime.now()), 'state': True
        } for index in range(1, 5)]
    sender = 'sender@example.com'
    storage = MagicMock(get_work_items=lambda x: work_items)
    report = Report(storage, sender, ses_client)
    email = 'recip@example.com'
    status = 'active'

    post_args = {'email': email, 'status': status}
    text_body = "\n".join(str(i) for i in work_items)
    mock_parser = MagicMock(
        name='mock_parser', return_value=MagicMock(parse_args=MagicMock(
            return_value=post_args)))
    monkeypatch.setattr(reqparse, 'RequestParser', mock_parser)

    html_body = 'test-html'
    monkeypatch.setattr('report.render_template', MagicMock(return_value=html_body))

    ses_stubber.stub_send_email(
        sender, {'ToAddresses': [post_args['email']]},
        f"Work items: {status}", text_body, html_body, 'test-id', error_code=error_code)

    _, result = report.post()
    if error_code is None:
        assert result == 200
    else:
        assert result == 400
