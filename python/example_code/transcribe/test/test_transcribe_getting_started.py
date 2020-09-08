# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for getting_started.py functions.
"""

import time
import boto3

import getting_started


def test_start_job(make_stubber, monkeypatch):
    transcribe_client = boto3.client('transcribe')
    transcribe_stubber = make_stubber(transcribe_client)
    job = {
        'name': 'Example-job',
        'media_uri': 's3://test-transcribe/answer2.wav',
        'media_format': 'wav',
        'language_code': 'en-US'
    }

    monkeypatch.setattr(time, 'sleep', lambda x: None)

    transcribe_stubber.stub_start_transcription_job(job)
    job['status'] = 'INPROGRESS'
    transcribe_stubber.stub_get_transcription_job(job)
    job['status'] = 'COMPLETED'
    job['file_uri'] = 'https://test-uri'
    transcribe_stubber.stub_get_transcription_job(job)

    getting_started.transcribe_file(
        job['name'], job['media_uri'], transcribe_client)
