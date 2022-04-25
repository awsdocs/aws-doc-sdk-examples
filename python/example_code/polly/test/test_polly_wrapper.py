# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for polly_wrapper.py.
"""

import io
import json
import time
import boto3
from botocore.exceptions import ClientError
import pytest

from polly_wrapper import PollyWrapper


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_describe_voices(make_stubber, error_code):
    polly_client = boto3.client('polly')
    polly_stubber = make_stubber(polly_client)
    polly_wrapper = PollyWrapper(polly_client, None)
    voices = [f'voice-{index}' for index in range(3)]

    polly_stubber.stub_describe_voices(voices, error_code=error_code)

    if error_code is None:
        got_voices = polly_wrapper.describe_voices()
        assert [voice['Name'] for voice in got_voices] == voices
    else:
        with pytest.raises(ClientError) as exc_info:
            polly_wrapper.describe_voices()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('include_visemes,error_code', [
    (True, None),
    (False, None),
    (True, 'TestException')])
def test_synthesize(make_stubber, include_visemes, error_code):
    polly_client = boto3.client('polly')
    polly_stubber = make_stubber(polly_client)
    polly_wrapper = PollyWrapper(polly_client, None)
    text = 'test-text'
    engine = 'standard'
    voice = 'Test'
    lang_code = 'en-US'
    audio_stream = 'test-stream'
    visemes = [{'value': 'i', 'time': index} for index in range(3)]
    viseme_stream = io.BytesIO()
    viseme_stream.write('\n'.join([json.dumps(v, separators=(',', ':'))
                                   for v in visemes]).encode())
    viseme_stream.seek(0)

    polly_stubber.stub_synthesize_speech(
        text, engine, voice, 'mp3', lang_code, audio_stream, error_code=error_code)
    if error_code is None and include_visemes:
        polly_stubber.stub_synthesize_speech(
            text, engine, voice, 'json', lang_code, viseme_stream,
            mark_types=['viseme'], error_code=error_code)

    if error_code is None:
        got_audio_stream, got_visemes = polly_wrapper.synthesize(
            text, engine, voice, 'mp3', lang_code, include_visemes)
        assert got_audio_stream == audio_stream
        if include_visemes:
            assert got_visemes == visemes
        else:
            assert got_visemes is None
    else:
        with pytest.raises(ClientError) as exc_info:
            polly_wrapper.synthesize(
                text, engine, voice, 'mp3', lang_code, include_visemes)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('include_visemes,error_code,stop_on_method', [
    (True, None, None),
    (False, None, None),
    (True, 'TestException', 'stub_start_speech_synthesis_task'),
    (True, 'TestException', 'stub_get_speech_synthesis_task')])
def test_do_synthesis_task(
        make_stubber, stub_runner, monkeypatch, include_visemes, error_code,
        stop_on_method):
    polly_client = boto3.client('polly')
    s3_resource = boto3.resource('s3')
    polly_stubber = make_stubber(polly_client)
    s3_stubber = make_stubber(s3_resource.meta.client)
    polly_wrapper = PollyWrapper(polly_client, s3_resource)
    text = 'test-text'
    engine = 'standard'
    voice = 'Test'
    lang_code = 'en-US'
    audio_stream = io.BytesIO(b'test-stream')
    speech_task_id = 'speech'
    visemes = [{'value': 'i', 'time': index} for index in range(3)]
    viseme_stream = io.BytesIO(
        '\n'.join([json.dumps(v, separators=(',', ':')) for v in visemes]).encode())
    viseme_task_id = 'viseme'
    bucket = 'test-bucket'
    key = 'test-key'
    status = 'completed'

    def wait_callback(task_type, task_status):
        assert task_type in ('speech', 'viseme')
        assert task_status == status

    streams = [audio_stream, viseme_stream]
    def mock_download_fileobj(Fileobj, **kwargs):
        stm = streams.pop(0)
        Fileobj.write(stm.read())
        stm.seek(0)

    monkeypatch.setattr(time, 'sleep', lambda x: None)
    monkeypatch.setattr(
        s3_resource.meta.client, 'download_fileobj', mock_download_fileobj)

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            polly_stubber.stub_start_speech_synthesis_task, text, engine, voice,
            'mp3', lang_code, bucket, key, speech_task_id)
        if include_visemes:
            runner.add(
                polly_stubber.stub_start_speech_synthesis_task, text, engine, voice,
                'json', lang_code, bucket, key, viseme_task_id, mark_types=['viseme'])
        runner.add(
            polly_stubber.stub_get_speech_synthesis_task, speech_task_id, bucket, key,
            status)
        runner.add(s3_stubber.stub_delete_object, bucket, key)
        if include_visemes:
            runner.add(
                polly_stubber.stub_get_speech_synthesis_task, viseme_task_id, bucket,
                key, status)
            runner.add(s3_stubber.stub_delete_object, bucket, key)

    if error_code is None:
        got_audio_stream, got_visemes = polly_wrapper.do_synthesis_task(
            text, engine, voice, 'mp3', bucket, 'en-US', include_visemes, wait_callback)
        assert got_audio_stream.read() == audio_stream.read()
        if include_visemes:
            assert got_visemes == visemes
        else:
            assert got_visemes is None
    else:
        with pytest.raises(ClientError) as exc_info:
            polly_wrapper.do_synthesis_task(
                text, engine, voice, 'mp3', bucket, 'en-US', include_visemes,
                wait_callback)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create_lexicon(make_stubber, error_code):
    polly_client = boto3.client('polly')
    polly_stubber = make_stubber(polly_client)
    polly_wrapper = PollyWrapper(polly_client, None)
    name = 'test-name'
    content = 'test-content'

    polly_stubber.stub_put_lexicon(name, content, error_code=error_code)

    if error_code is None:
        polly_wrapper.create_lexicon(name, content)
    else:
        with pytest.raises(ClientError) as exc_info:
            polly_wrapper.create_lexicon(name, content)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_lexicon(make_stubber, error_code):
    polly_client = boto3.client('polly')
    polly_stubber = make_stubber(polly_client)
    polly_wrapper = PollyWrapper(polly_client, None)
    name = 'test-name'
    content = 'test-content'

    polly_stubber.stub_get_lexicon(name, content, error_code=error_code)

    if error_code is None:
        got_lexicon = polly_wrapper.get_lexicon(name)
        assert got_lexicon['Lexicon'] == {'Name': name, 'Content': content}
    else:
        with pytest.raises(ClientError) as exc_info:
            polly_wrapper.get_lexicon(name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list_lexicons(make_stubber, error_code):
    polly_client = boto3.client('polly')
    polly_stubber = make_stubber(polly_client)
    polly_wrapper = PollyWrapper(polly_client, None)
    lexicons = [f'lexicon-{index}' for index in range(3)]

    polly_stubber.stub_list_lexicons(lexicons, error_code=error_code)

    if error_code is None:
        got_lexicons = polly_wrapper.list_lexicons()
        assert [lex['Name'] for lex in got_lexicons] == lexicons
    else:
        with pytest.raises(ClientError) as exc_info:
            polly_wrapper.list_lexicons()
        assert exc_info.value.response['Error']['Code'] == error_code


voice_metadata = [{
     'Gender': 'Female',
     'Id': 'Kimberly',
     'LanguageCode': 'en-US',
     'LanguageName': 'US English',
     'Name': 'Kimberly',
     'SupportedEngines': ['neural', 'standard']},
    {'Gender': 'Male',
     'Id': 'Jan-ID',
     'LanguageCode': 'pl-PL',
     'LanguageName': 'Polish',
     'Name': 'Jan',
     'SupportedEngines': ['standard']},
    {'Gender': 'Female',
     'Id': 'Liv',
     'LanguageCode': 'nb-NO',
     'LanguageName': 'Norwegian',
     'Name': 'Liv',
     'SupportedEngines': ['standard']},
    {'Gender': 'Male',
     'Id': 'Joey',
     'LanguageCode': 'en-US',
     'LanguageName': 'US English',
     'Name': 'Joey',
     'SupportedEngines': ['neural', 'standard']}]


def test_get_voice_engines():
    polly_wrapper = PollyWrapper(None, None)
    polly_wrapper.voice_metadata = voice_metadata
    got_engines = polly_wrapper.get_voice_engines()
    assert got_engines == {'standard', 'neural'}


@pytest.mark.parametrize('engine,langs', [
    ('standard', {'US English': 'en-US', 'Norwegian': 'nb-NO', 'Polish': 'pl-PL'}),
    ('test', {})])
def test_get_languages(engine, langs):
    polly_wrapper = PollyWrapper(None, None)
    polly_wrapper.voice_metadata = voice_metadata
    got_langs = polly_wrapper.get_languages(engine)
    assert got_langs == langs


@pytest.mark.parametrize('engine,lang_code,voices', [
    ('neural', 'en-US', {'Joey': 'Joey', 'Kimberly': 'Kimberly'}),
    ('standard', 'pl-PL', {'Jan': 'Jan-ID'}),
    ('neural', 'ts-TS', {})])
def test_get_voices(engine, lang_code, voices):
    polly_wrapper = PollyWrapper(None, None)
    polly_wrapper.voice_metadata = voice_metadata
    got_voices = polly_wrapper.get_voices(engine, lang_code)
    assert got_voices == voices

