# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for transcribe_basics.py functions.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

import transcribe_basics


def make_test_job(index):
    return {
        'name': f'test-job-{index}',
        'media_uri': 's3://example-bucket/test-media.mp3',
        'media_format': 'mp3',
        'language_code': 'en-US',
        'vocabulary_name': f'test-vocabulary-{index}'
    }


def make_test_vocabulary(index, phrases=False, table_uri=False):
    vocab = {'name': f'test-vocab-{index}', 'language_code': 'en-US'}
    if phrases:
        vocab['phrases'] = ['word', 'other-word', 'yet-another-word']
    if table_uri:
        vocab['table_uri'] = 's3://test-bucket/test-table.txt'
    return vocab


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_start_job(make_stubber, error_code):
    transcribe_client = boto3.client('transcribe')
    transcribe_stubber = make_stubber(transcribe_client)
    job = make_test_job(1)

    transcribe_stubber.stub_start_transcription_job(job, error_code=error_code)

    if error_code is None:
        got_job = transcribe_basics.start_job(
            job['name'], job['media_uri'], job['media_format'],
            job['language_code'], transcribe_client, job['vocabulary_name'])
        assert got_job['TranscriptionJobName'] == job['name']
    else:
        with pytest.raises(ClientError) as exc_info:
            transcribe_basics.start_job(
                job['name'], job['media_uri'], job['media_format'],
                job['language_code'], transcribe_client, job['vocabulary_name'])
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('job_slice,error_code', [
    ((0, 10), None),
    ((0, 5), None),
    ((0, 10), 'TestException')])
def test_list_jobs(make_stubber, job_slice, error_code):
    transcribe_client = boto3.client('transcribe')
    transcribe_stubber = make_stubber(transcribe_client)
    job_filter = 'test-filter'
    jobs = [make_test_job(index) for index in range(0, 10)]

    transcribe_stubber.stub_list_transcription_jobs(
        job_filter, jobs, job_slice, error_code=error_code)
    if job_slice[1] < len(jobs):
        transcribe_stubber.stub_list_transcription_jobs(
            job_filter, jobs, [job_slice[1], len(jobs)], next_token='test-token',
            error_code=error_code)

    if error_code is None:
        got_jobs = transcribe_basics.list_jobs(job_filter, transcribe_client)
        assert [got['TranscriptionJobName'] for got in got_jobs] == \
               [had['name'] for had in jobs]
    else:
        with pytest.raises(ClientError) as exc_info:
            transcribe_basics.list_jobs(job_filter, transcribe_client)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_job(make_stubber, error_code):
    transcribe_client = boto3.client('transcribe')
    transcribe_stubber = make_stubber(transcribe_client)
    job = make_test_job(1)

    transcribe_stubber.stub_get_transcription_job(job, error_code=error_code)

    if error_code is None:
        got_job = transcribe_basics.get_job(job['name'], transcribe_client)
        assert got_job['TranscriptionJobName'] == job['name']
    else:
        with pytest.raises(ClientError) as exc_info:
            transcribe_basics.get_job(job['name'], transcribe_client)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_job(make_stubber, error_code):
    transcribe_client = boto3.client('transcribe')
    transcribe_stubber = make_stubber(transcribe_client)
    job_name = 'test-job'

    transcribe_stubber.stub_delete_transcription_job(job_name, error_code=error_code)

    if error_code is None:
        transcribe_basics.delete_job(job_name, transcribe_client)
    else:
        with pytest.raises(ClientError) as exc_info:
            transcribe_basics.delete_job(job_name, transcribe_client)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('phrases,error_code', [
    (True, None),
    (False, None),
    (True, 'TestException')])
def test_create_vocabulary(make_stubber, phrases, error_code):
    transcribe_client = boto3.client('transcribe')
    transcribe_stubber = make_stubber(transcribe_client)
    vocab = make_test_vocabulary(1, phrases=phrases, table_uri=not phrases)

    transcribe_stubber.stub_create_vocabulary(vocab, error_code=error_code)

    if error_code is None:
        got_vocab = transcribe_basics.create_vocabulary(
            vocab['name'], vocab['language_code'], transcribe_client,
            phrases=vocab.get('phrases'), table_uri=vocab.get('table_uri'))
        assert got_vocab['VocabularyName'] == vocab['name']
    else:
        with pytest.raises(ClientError) as exc_info:
            transcribe_basics.create_vocabulary(
                vocab['name'], vocab['language_code'], transcribe_client,
                phrases=vocab.get('phrases'), table_uri=vocab.get('table_uri'))
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('vocab_slice,error_code', [
    ((0, 10), None),
    ((0, 5), None),
    ((0, 10), 'TestException')])
def test_list_vocabularies(make_stubber, vocab_slice, error_code):
    transcribe_client = boto3.client('transcribe')
    transcribe_stubber = make_stubber(transcribe_client)
    vocab_filter = 'test-filter'
    vocabs = [make_test_vocabulary(index) for index in range(0, 10)]

    transcribe_stubber.stub_list_vocabularies(
        vocab_filter, vocabs, vocab_slice, error_code=error_code)
    if vocab_slice[1] < len(vocabs):
        transcribe_stubber.stub_list_vocabularies(
            vocab_filter, vocabs, [vocab_slice[1], len(vocabs)],
            next_token='test-token', error_code=error_code)

    if error_code is None:
        got_vocabs = transcribe_basics.list_vocabularies(
            vocab_filter, transcribe_client)
        assert [got['VocabularyName'] for got in got_vocabs] == \
               [had['name'] for had in vocabs]
    else:
        with pytest.raises(ClientError) as exc_info:
            transcribe_basics.list_vocabularies(vocab_filter, transcribe_client)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_vocabulary(make_stubber, error_code):
    transcribe_client = boto3.client('transcribe')
    transcribe_stubber = make_stubber(transcribe_client)
    vocab = make_test_vocabulary(1)

    transcribe_stubber.stub_get_vocabulary(vocab, error_code=error_code)

    if error_code is None:
        transcribe_basics.get_vocabulary(vocab['name'], transcribe_client)
    else:
        with pytest.raises(ClientError) as exc_info:
            transcribe_basics.get_vocabulary(vocab['name'], transcribe_client)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('phrases,error_code', [
    (True, None),
    (False, None),
    (True, 'TestException')])
def test_update_vocabulary(make_stubber, phrases, error_code):
    transcribe_client = boto3.client('transcribe')
    transcribe_stubber = make_stubber(transcribe_client)
    vocab = make_test_vocabulary(1, phrases=phrases, table_uri=not phrases)

    transcribe_stubber.stub_update_vocabulary(vocab, error_code=error_code)

    if error_code is None:
        transcribe_basics.update_vocabulary(
            vocab['name'], vocab['language_code'], transcribe_client,
            phrases=vocab.get('phrases'), table_uri=vocab.get('table_uri'))
    else:
        with pytest.raises(ClientError) as exc_info:
            transcribe_basics.update_vocabulary(
                vocab['name'], vocab['language_code'], transcribe_client,
                phrases=vocab.get('phrases'), table_uri=vocab.get('table_uri'))
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_vocabulary(make_stubber, error_code):
    transcribe_client = boto3.client('transcribe')
    transcribe_stubber = make_stubber(transcribe_client)
    vocab_name = 'test-vocab'

    transcribe_stubber.stub_delete_vocabulary(vocab_name, error_code=error_code)

    if error_code is None:
        transcribe_basics.delete_vocabulary(vocab_name, transcribe_client)
    else:
        with pytest.raises(ClientError) as exc_info:
            transcribe_basics.delete_vocabulary(vocab_name, transcribe_client)
        assert exc_info.value.response['Error']['Code'] == error_code
