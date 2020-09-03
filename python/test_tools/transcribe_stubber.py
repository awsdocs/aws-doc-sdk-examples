# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Transcribe unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto3 client.
"""

from test_tools.example_stubber import ExampleStubber


class TranscribeStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon Transcribe unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Transcribe client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    @staticmethod
    def _make_job(job):
        api_job = {
            'TranscriptionJobName': job['name'],
            'Media': {'MediaFileUri': job['media_uri']},
            'MediaFormat': job['media_format'],
            'LanguageCode': job['language_code']}
        if 'vocabulary_name' in job:
            api_job['Settings'] = {'VocabularyName': job['vocabulary_name']}
        if 'status' in job:
            api_job['TranscriptionJobStatus'] = job['status']
        if 'file_uri' in job:
            api_job['Transcript'] = {'TranscriptFileUri': job['file_uri']}
        return api_job

    @staticmethod
    def _make_vocabulary(vocabulary):
        api_vocab = {
            'VocabularyName': vocabulary['name'],
            'LanguageCode': vocabulary['language_code']}
        if 'phrases' in vocabulary:
            api_vocab['Phrases'] = vocabulary['phrases']
        elif 'table_uri' in vocabulary:
            api_vocab['VocabularyFileUri'] = vocabulary['table_uri']
        return api_vocab

    def stub_start_transcription_job(self, job, error_code=None):
        expected_params = self._make_job(job)
        response = {'TranscriptionJob': {'TranscriptionJobName': job['name']}}
        self._stub_bifurcator(
            'start_transcription_job', expected_params, response, error_code=error_code)

    def stub_list_transcription_jobs(
            self, job_filter, jobs, response_slice, next_token=None,error_code=None):
        expected_params = {'JobNameContains': job_filter}
        if next_token is not None:
            expected_params['NextToken'] = next_token
        response = {'TranscriptionJobSummaries': [{
            'TranscriptionJobName': job['name']
        } for job in jobs[response_slice[0]:response_slice[1]]]}
        if response_slice[1] < len(jobs):
            response['NextToken'] = 'test-token'
        self._stub_bifurcator(
            'list_transcription_jobs', expected_params, response, error_code=error_code)

    def stub_get_transcription_job(self, job, error_code=None):
        expected_params = {'TranscriptionJobName': job['name']}
        response = {'TranscriptionJob': self._make_job(job)}
        self._stub_bifurcator(
            'get_transcription_job', expected_params, response, error_code=error_code)

    def stub_delete_transcription_job(self, job_name, error_code=None):
        expected_params = {'TranscriptionJobName': job_name}
        self._stub_bifurcator(
            'delete_transcription_job', expected_params, error_code=error_code)

    def stub_create_vocabulary(self, vocabulary, error_code=None):
        expected_params = self._make_vocabulary(vocabulary)
        response = {'VocabularyName': vocabulary['name']}
        self._stub_bifurcator(
            'create_vocabulary', expected_params, response, error_code=error_code)

    def stub_list_vocabularies(
            self, vocab_filter, vocabularies, vocab_slice, next_token=None,
            error_code=None):
        expected_params = {'NameContains': vocab_filter}
        if next_token is not None:
            expected_params['NextToken'] = next_token
        response = {
            'Vocabularies': [
                self._make_vocabulary(vocab)
                for vocab in vocabularies[vocab_slice[0]:vocab_slice[1]]]}
        if vocab_slice[1] < len(vocabularies):
            response['NextToken'] = 'test-token'
        self._stub_bifurcator(
            'list_vocabularies', expected_params, response, error_code=error_code)

    def stub_get_vocabulary(self, vocabulary, error_code=None):
        expected_params = {'VocabularyName': vocabulary['name']}
        response = self._make_vocabulary(vocabulary)
        self._stub_bifurcator(
            'get_vocabulary', expected_params, response, error_code=error_code)

    def stub_update_vocabulary(self, vocabulary, error_code=None):
        expected_params = self._make_vocabulary(vocabulary)
        response = {'VocabularyName': vocabulary['name']}
        self._stub_bifurcator(
            'update_vocabulary', expected_params, response, error_code=error_code)

    def stub_delete_vocabulary(self, vocab_name, error_code=None):
        expected_params = {'VocabularyName': vocab_name}
        self._stub_bifurcator(
            'delete_vocabulary', expected_params, error_code=error_code)
