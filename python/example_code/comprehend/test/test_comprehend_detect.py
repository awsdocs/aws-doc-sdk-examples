# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for comprehend_detect.py
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from comprehend_detect import ComprehendDetect


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_detect_languages(make_stubber, error_code):
    comprehend_client = boto3.client('comprehend')
    comprehend_stubber = make_stubber(comprehend_client)
    comp_detect = ComprehendDetect(comprehend_client)
    text = 'test-text'
    languages = [
        {'LanguageCode': f'test-{index}', 'Score': index} for index in range(5)]

    comprehend_stubber.stub_detect_dominant_language(
        text, languages, error_code=error_code)

    if error_code is None:
        got_languages = comp_detect.detect_languages(text)
        assert got_languages == languages
    else:
        with pytest.raises(ClientError) as exc_info:
            comp_detect.detect_languages(text)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_detect_entities(make_stubber, error_code):
    comprehend_client = boto3.client('comprehend')
    comprehend_stubber = make_stubber(comprehend_client)
    comp_detect = ComprehendDetect(comprehend_client)
    text = 'test-text'
    language = 'fr'
    entities = [{
        'Score': index,
        'Type': 'TEST',
        'Text': f'test-{index}',
        'BeginOffset': index,
        'EndOffset': index*2
    } for index in range(5)]

    comprehend_stubber.stub_detect_entities(
        text, language, entities, error_code=error_code)

    if error_code is None:
        got_entities = comp_detect.detect_entities(text, language)
        assert got_entities == entities
    else:
        with pytest.raises(ClientError) as exc_info:
            comp_detect.detect_entities(text, language)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_detect_key_phrases(make_stubber, error_code):
    comprehend_client = boto3.client('comprehend')
    comprehend_stubber = make_stubber(comprehend_client)
    comp_detect = ComprehendDetect(comprehend_client)
    text = 'test-text'
    language = 'fr'
    phrases = [{
        'Score': index,
        'Text': f'test-{index}',
        'BeginOffset': index,
        'EndOffset': index * 2
    } for index in range(5)]

    comprehend_stubber.stub_detect_key_phrases(
        text, language, phrases, error_code=error_code)

    if error_code is None:
        got_phrases = comp_detect.detect_key_phrases(text, language)
        assert got_phrases == phrases
    else:
        with pytest.raises(ClientError) as exc_info:
            comp_detect.detect_key_phrases(text, language)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_detect_pii(make_stubber, error_code):
    comprehend_client = boto3.client('comprehend')
    comprehend_stubber = make_stubber(comprehend_client)
    comp_detect = ComprehendDetect(comprehend_client)
    text = 'test-text'
    language = 'fr'
    entities = [{
        'Score': index,
        'Type': 'TEST',
        'BeginOffset': index,
        'EndOffset': index*2
    } for index in range(5)]

    comprehend_stubber.stub_detect_pii_entities(
        text, language, entities, error_code=error_code)

    if error_code is None:
        got_entities = comp_detect.detect_pii(text, language)
        assert got_entities == entities
    else:
        with pytest.raises(ClientError) as exc_info:
            comp_detect.detect_pii(text, language)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_detect_sentiment(make_stubber, error_code):
    comprehend_client = boto3.client('comprehend')
    comprehend_stubber = make_stubber(comprehend_client)
    comp_detect = ComprehendDetect(comprehend_client)
    text = 'test-text'
    language = 'fr'
    sentiment = 'FANTASTIC'
    sentiment_scores = {'Positive': 100}
    response = {'Sentiment': sentiment, 'SentimentScore': sentiment_scores}

    comprehend_stubber.stub_detect_sentiment(
        text, language, sentiment, sentiment_scores, error_code=error_code)

    if error_code is None:
        got_response = comp_detect.detect_sentiment(text, language)
        assert got_response == response
    else:
        with pytest.raises(ClientError) as exc_info:
            comp_detect.detect_sentiment(text, language)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_detect_syntax(make_stubber, error_code):
    comprehend_client = boto3.client('comprehend')
    comprehend_stubber = make_stubber(comprehend_client)
    comp_detect = ComprehendDetect(comprehend_client)
    text = 'test-text'
    language = 'fr'
    tokens = [{
        'TokenId': index,
        'Text': f'test-{index}',
        'BeginOffset': index,
        'EndOffset': index * 2,
        'PartOfSpeech': {'Tag': 'TEST', 'Score': index}
    } for index in range(5)]

    comprehend_stubber.stub_detect_syntax(
        text, language, tokens, error_code=error_code)

    if error_code is None:
        got_tokens = comp_detect.detect_syntax(text, language)
        assert got_tokens == tokens
    else:
        with pytest.raises(ClientError) as exc_info:
            comp_detect.detect_syntax(text, language)
        assert exc_info.value.response['Error']['Code'] == error_code
