# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Polly unit tests.
"""

from test_tools.example_stubber import ExampleStubber


class PollyStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon Polly unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Amazon Polly client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_describe_voices(self, voices, error_code=None):
        expected_params = {}
        response = {'Voices': [{'Name': voice} for voice in voices]}
        self._stub_bifurcator(
            'describe_voices', expected_params, response, error_code=error_code)

    def stub_synthesize_speech(
            self, text, engine, voice, audio_format, lang_code, output_stream,
            mark_types=None, error_code=None):
        expected_params = {
            'Engine': engine,
            'LanguageCode': lang_code,
            'OutputFormat': audio_format,
            'Text': text,
            'VoiceId': voice}
        if mark_types is not None:
            expected_params['SpeechMarkTypes'] = mark_types
        response = {'AudioStream': output_stream}
        self._stub_bifurcator(
            'synthesize_speech', expected_params, response, error_code=error_code)

    def stub_start_speech_synthesis_task(
            self, text, engine, voice, audio_format, lang_code, bucket, key, task_id,
            mark_types=None, error_code=None):
        expected_params = {
            'Engine': engine,
            'LanguageCode': lang_code,
            'OutputFormat': audio_format,
            'OutputS3BucketName': bucket,
            'Text': text,
            'VoiceId': voice}
        if mark_types is not None:
            expected_params['SpeechMarkTypes'] = mark_types
        response = {'SynthesisTask': {
            'TaskId': task_id, 'OutputUri': f'{bucket}/{key}'}}
        self._stub_bifurcator(
            'start_speech_synthesis_task', expected_params, response,
            error_code=error_code)

    def stub_get_speech_synthesis_task(
            self, task_id, bucket, key, status, error_code=None):
        expected_params = {'TaskId': task_id}
        response = {'SynthesisTask': {
            'TaskId': task_id, 'OutputUri': f'{bucket}/{key}', 'TaskStatus': status}}
        self._stub_bifurcator(
            'get_speech_synthesis_task', expected_params, response,
            error_code=error_code)

    def stub_put_lexicon(self, name, content, error_code=None):
        expected_params = {'Name': name, 'Content': content}
        response = {}
        self._stub_bifurcator(
            'put_lexicon', expected_params, response, error_code=error_code)

    def stub_get_lexicon(self, name, content, error_code=None):
        expected_params = {'Name': name}
        response = {'Lexicon': {'Name': name, 'Content': content}}
        self._stub_bifurcator(
            'get_lexicon', expected_params, response, error_code=error_code)

    def stub_list_lexicons(self, lexicons, error_code=None):
        expected_params = {}
        response = {'Lexicons': [{'Name': lex} for lex in lexicons]}
        self._stub_bifurcator(
            'list_lexicons', expected_params, response, error_code=error_code)
