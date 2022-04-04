# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for report.py
"""

from unittest.mock import MagicMock
import boto3
import pytest

from report import Report, reqparse, render_template


@pytest.mark.parametrize('error_code, stop_on_method', [
    (None, None),
    ('TestException', 'stub_list_objects'),
    ('TestException', 'stub_detect_labels'),
])
def test_get_report(make_stubber, stub_runner, error_code, stop_on_method):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    bucket = s3_resource.Bucket('test-bucket')
    report = Report(bucket, rekognition_client, None)
    photos = [f'photo-{index}' for index in range(3)]
    labels = {}
    for index, photo in enumerate(photos):
        label = MagicMock(confidence=index, instances=[], parents=[])
        label.name = f'label-{index}'
        labels[photo] = [label]

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(s3_stubber.stub_list_objects, bucket.name, photos)
        for photo, label in zip(photos, labels):
            runner.add(
                rekognition_stubber.stub_detect_labels,
                {'S3Object': {'Bucket': bucket.name, 'Name': photo}}, None,
                labels[photo], raise_and_continue=True)

    got_report, result = report.get()
    if error_code is None:
        assert got_report[1:] == [
            ','.join((photo, label[0].name, str(label[0].confidence)))
            for photo, label in labels.items()]
        assert result == 200
    elif stop_on_method == 'stub_list_objects':
        assert result == 400


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_post_report(make_stubber, monkeypatch, error_code):
    ses_client = boto3.client('ses')
    ses_stubber = make_stubber(ses_client)
    report = Report(None, None, ses_client)

    post_args = {
        'sender': 'test-sender', 'recipient': 'test-recipient',
        'subject': 'test-subject', 'message': 'test-message',
        'analysis_labels': [
            f'label{index}-1,label{index}-2,label{index}-3' for index in range(3)]}
    text_body = f"{post_args['message']}\n\n" + '\n'.join(post_args['analysis_labels'])
    mock_parser = MagicMock(
        name='mock_parser', return_value=MagicMock(parse_args=MagicMock(
            return_value=post_args)))
    monkeypatch.setattr(reqparse, 'RequestParser', mock_parser)

    html_body = 'test-html'
    monkeypatch.setattr('report.render_template', MagicMock(return_value=html_body))

    ses_stubber.stub_send_email(
        post_args['sender'], {'ToAddresses': [post_args['recipient']]},
        post_args['subject'], text_body, html_body, 'test-id', error_code=error_code)

    _, result = report.post()
    if error_code is None:
        assert result == 200
    else:
        assert result == 400
