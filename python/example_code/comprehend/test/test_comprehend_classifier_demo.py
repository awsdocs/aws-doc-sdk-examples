# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for comprehend_classifier_demo.py
"""

from unittest.mock import MagicMock
from botocore.exceptions import ClientError
import pytest
import requests

from comprehend_classifier_demo import ClassifierDemo


@pytest.mark.parametrize('status_code,training', [
    (200, True), (200, False), (403, True)])
def test_get_training_issues(monkeypatch, status_code, training):
    labels = {'test1', 'test2', 'test3'}
    classifier_demo = ClassifierDemo(None)
    mock_response = MagicMock(
        status_code=status_code, json=lambda: {'items': [{
            'title': 'title, the',
            'body': 'first line\r\nsecond line',
            'labels': [{'name': f'test-{index}'}]
        } for index in range(0, 3)]})

    monkeypatch.setattr(requests, 'get', lambda url: mock_response)

    if training:
        got_issues = classifier_demo.get_training_issues(labels)
    else:
        got_issues = classifier_demo.get_input_issues(labels)
    if status_code == 200:
        assert len(got_issues) == 9
        assert all([',' not in issue['title'] for issue in got_issues])
        assert all(['\r' not in issue['body'] for issue in got_issues])
        assert all(['\n' not in issue['body'] for issue in got_issues])
        if training:
            assert all([issue['labels'] - labels == set() for issue in got_issues])
        else:
            assert all([issue['labels']] for issue in got_issues)
    else:
        assert len(got_issues) == 0


@pytest.mark.parametrize('training,error_code', [
    (True, None),
    (False, 'TestException')])
def test_upload_issue_data(make_stubber, training, error_code):
    def verify_issue_bytes(issue_bytes, obj_key):
        if error_code is not None:
            raise ClientError({'Error': {'Code': error_code}}, 'test-op')

        if training:
            assert issue_bytes.read() == (
                b'label1|label2,test1 body1\nlabel3,test2 body2')
        else:
            assert issue_bytes.read() == b'test1 body1\ntest2 body2'
        assert obj_key == 'training/issues.txt' if training else 'input/issues.txt'

    demo_resources = MagicMock(bucket=MagicMock(upload_fileobj=verify_issue_bytes))
    classifier_demo = ClassifierDemo(demo_resources)
    if training:
        issues = [
            {'title': 'test1', 'body': 'body1', 'labels': ['label1', 'label2']},
            {'title': 'test2', 'body': 'body2', 'labels': ['label3']}
        ]
    else:
        issues = [
            {'title': 'test1', 'body': 'body1'},
            {'title': 'test2', 'body': 'body2'}
        ]

    if error_code is None:
        classifier_demo.upload_issue_data(issues, training)
    else:
        with pytest.raises(ClientError) as exc_info:
            classifier_demo.upload_issue_data(issues, training)
        assert exc_info.value.response['Error']['Code'] == error_code


def test_reconcile_job_output():
    input_issues = [
        {'labels': [f'in-label-{index}']}
        for index in range(3)]
    output_dict = {
        'file1': {'data': [
            {
                'File': 'test-file',
                'Line': index,
                'Labels': [{'Name': f'label-{index}', 'Score': 0.5}],
            } for index in range(3)]}
    }

    got_reconciled = ClassifierDemo.reconcile_job_output(input_issues, output_dict)
    assert len(got_reconciled) == 3
