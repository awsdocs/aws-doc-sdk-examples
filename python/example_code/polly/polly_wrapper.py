# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Polly to synthesize
speech and manage custom lexicons.
"""

import io
import json
import logging
import time
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.polly.helper.PollyWrapper]
class PollyWrapper:
    """Encapsulates Amazon Polly functions."""
    def __init__(self, polly_client, s3_resource):
        """
        :param polly_client: A Boto3 Amazon Polly client.
        :param s3_resource: A Boto3 Amazon Simple Storage Service (Amazon S3) resource.
        """
        self.polly_client = polly_client
        self.s3_resource = s3_resource
        self.voice_metadata = None
# snippet-end:[python.example_code.polly.helper.PollyWrapper]

# snippet-start:[python.example_code.polly.DescribeVoices]
    def describe_voices(self):
        """
        Gets metadata about available voices.

        :return: The list of voice metadata.
        """
        try:
            response = self.polly_client.describe_voices()
            self.voice_metadata = response['Voices']
            logger.info("Got metadata about %s voices.", len(self.voice_metadata))
        except ClientError:
            logger.exception("Couldn't get voice metadata.")
            raise
        else:
            return self.voice_metadata
# snippet-end:[python.example_code.polly.DescribeVoices]

# snippet-start:[python.example_code.polly.Synthesize]
    def synthesize(
            self, text, engine, voice, audio_format, lang_code=None,
            include_visemes=False):
        """
        Synthesizes speech or speech marks from text, using the specified voice.

        :param text: The text to synthesize.
        :param engine: The kind of engine used. Can be standard or neural.
        :param voice: The ID of the voice to use.
        :param audio_format: The audio format to return for synthesized speech. When
                             speech marks are synthesized, the output format is JSON.
        :param lang_code: The language code of the voice to use. This has an effect
                          only when a bilingual voice is selected.
        :param include_visemes: When True, a second request is made to Amazon Polly
                                to synthesize a list of visemes, using the specified
                                text and voice. A viseme represents the visual position
                                of the face and mouth when saying part of a word.
        :return: The audio stream that contains the synthesized speech and a list
                 of visemes that are associated with the speech audio.
        """
        try:
            kwargs = {
                'Engine': engine,
                'OutputFormat': audio_format,
                'Text': text,
                'VoiceId': voice}
            if lang_code is not None:
                kwargs['LanguageCode'] = lang_code
            response = self.polly_client.synthesize_speech(**kwargs)
            audio_stream = response['AudioStream']
            logger.info("Got audio stream spoken by %s.", voice)
            visemes = None
            if include_visemes:
                kwargs['OutputFormat'] = 'json'
                kwargs['SpeechMarkTypes'] = ['viseme']
                response = self.polly_client.synthesize_speech(**kwargs)
                visemes = [json.loads(v) for v in
                           response['AudioStream'].read().decode().split() if v]
                logger.info("Got %s visemes.", len(visemes))
        except ClientError:
            logger.exception("Couldn't get audio stream.")
            raise
        else:
            return audio_stream, visemes
# snippet-end:[python.example_code.polly.Synthesize]

    def _wait_for_task(self, tries, task_id, task_type, wait_callback, output_bucket):
        """
        Waits for an asynchronous speech synthesis task to complete. This function
        polls Amazon Polly for data about the specified task until a completion
        status is returned or the number of tries is exceeded.

        When the task successfully completes, the task output is retrieved from the
        output Amazon S3 bucket and the output object is deleted.

        :param tries: The number of times to poll for status.
        :param task_id: The ID of the task to wait for.
        :param task_type: The type of task. This is passed to the `wait_callback`
                          function to display status.
        :param wait_callback: A callback function that is called after each poll,
                              to give the caller an opportunity to take action, such
                              as to display status.
        :param output_bucket: The Amazon S3 bucket where task output is located.
        :return: The output from the task in a byte stream.
        """
        task = None
        while tries > 0:
            task = self.get_speech_synthesis_task(task_id)
            task_status = task['TaskStatus']
            logger.info("Task %s status %s.", task_id, task_status)
            if wait_callback is not None:
                wait_callback(task_type, task_status)
            if task_status in ('completed', 'failed'):
                break
            time.sleep(5)
            tries -= 1

        output_stream = io.BytesIO()
        if task is not None:
            output_key = task['OutputUri'].split('/')[-1]
            output_bucket.download_fileobj(output_key, output_stream)
            output_bucket.Object(output_key).delete()
            logger.info("Downloaded output for task %s.", task_id)
            output_stream.seek(0)

        return output_stream

# snippet-start:[python.example_code.polly.StartSpeechSynthesisTask]
    def do_synthesis_task(
            self, text, engine, voice, audio_format, s3_bucket, lang_code=None,
            include_visemes=False, wait_callback=None):
        """
        Start an asynchronous task to synthesize speech or speech marks, wait for
        the task to complete, retrieve the output from Amazon S3, and return the
        data.

        An asynchronous task is required when the text is too long for near-real time
        synthesis.

        :param text: The text to synthesize.
        :param engine: The kind of engine used. Can be standard or neural.
        :param voice: The ID of the voice to use.
        :param audio_format: The audio format to return for synthesized speech. When
                             speech marks are synthesized, the output format is JSON.
        :param s3_bucket: The name of an existing Amazon S3 bucket that you have
                          write access to. Synthesis output is written to this bucket.
        :param lang_code: The language code of the voice to use. This has an effect
                          only when a bilingual voice is selected.
        :param include_visemes: When True, a second request is made to Amazon Polly
                                to synthesize a list of visemes, using the specified
                                text and voice. A viseme represents the visual position
                                of the face and mouth when saying part of a word.
        :param wait_callback: A callback function that is called periodically during
                              task processing, to give the caller an opportunity to
                              take action, such as to display status.
        :return: The audio stream that contains the synthesized speech and a list
                 of visemes that are associated with the speech audio.
        """
        try:
            kwargs = {
                'Engine': engine,
                'OutputFormat': audio_format,
                'OutputS3BucketName': s3_bucket,
                'Text': text,
                'VoiceId': voice}
            if lang_code is not None:
                kwargs['LanguageCode'] = lang_code
            response = self.polly_client.start_speech_synthesis_task(**kwargs)
            speech_task = response['SynthesisTask']
            logger.info("Started speech synthesis task %s.", speech_task['TaskId'])

            viseme_task = None
            if include_visemes:
                kwargs['OutputFormat'] = 'json'
                kwargs['SpeechMarkTypes'] = ['viseme']
                response = self.polly_client.start_speech_synthesis_task(**kwargs)
                viseme_task = response['SynthesisTask']
                logger.info("Started viseme synthesis task %s.", viseme_task['TaskId'])
        except ClientError:
            logger.exception("Couldn't start synthesis task.")
            raise
        else:
            bucket = self.s3_resource.Bucket(s3_bucket)
            audio_stream = self._wait_for_task(
                10, speech_task['TaskId'], 'speech', wait_callback, bucket)

            visemes = None
            if include_visemes:
                viseme_data = self._wait_for_task(
                    10, viseme_task['TaskId'], 'viseme', wait_callback, bucket)
                visemes = [json.loads(v) for v in
                           viseme_data.read().decode().split() if v]

            return audio_stream, visemes
# snippet-end:[python.example_code.polly.StartSpeechSynthesisTask]

# snippet-start:[python.example_code.polly.GetSpeechSynthesisTask]
    def get_speech_synthesis_task(self, task_id):
        """
        Gets metadata about an asynchronous speech synthesis task, such as its status.

        :param task_id: The ID of the task to retrieve.
        :return: Metadata about the task.
        """
        try:
            response = self.polly_client.get_speech_synthesis_task(TaskId=task_id)
            task = response['SynthesisTask']
            logger.info("Got synthesis task. Status is %s.", task['TaskStatus'])
        except ClientError:
            logger.exception("Couldn't get synthesis task %s.", task_id)
            raise
        else:
            return task
# snippet-end:[python.example_code.polly.GetSpeechSynthesisTask]

# snippet-start:[python.example_code.polly.PutLexicon]
    def create_lexicon(self, name, content):
        """
        Creates a lexicon with the specified content. A lexicon contains custom
        pronunciations.

        :param name: The name of the lexicon.
        :param content: The content of the lexicon.
        """
        try:
            self.polly_client.put_lexicon(Name=name, Content=content)
            logger.info("Created lexicon %s.", name)
        except ClientError:
            logger.exception("Couldn't create lexicon %s.")
            raise
# snippet-end:[python.example_code.polly.PutLexicon]

# snippet-start:[python.example_code.polly.GetLexicon]
    def get_lexicon(self, name):
        """
        Gets metadata and contents of an existing lexicon.

        :param name: The name of the lexicon to retrieve.
        :return: The retrieved lexicon.
        """
        try:
            response = self.polly_client.get_lexicon(Name=name)
            logger.info("Got lexicon %s.", name)
        except ClientError:
            logger.exception("Couldn't get lexicon %s.", name)
            raise
        else:
            return response
# snippet-end:[python.example_code.polly.GetLexicon]

# snippet-start:[python.example_code.polly.ListLexicons]
    def list_lexicons(self):
        """
        Lists lexicons in the current account.

        :return: The list of lexicons.
        """
        try:
            response = self.polly_client.list_lexicons()
            lexicons = response['Lexicons']
            logger.info("Got %s lexicons.", len(lexicons))
        except ClientError:
            logger.exception("Couldn't get  %s.", )
            raise
        else:
            return lexicons
# snippet-end:[python.example_code.polly.ListLexicons]

    def get_voice_engines(self):
        """
        Extracts the set of available voice engine types from the full list of
        voice metadata.

        :return: The set of voice engine types.
        """
        if self.voice_metadata is None:
            self.describe_voices()

        engines = set()
        for voice in self.voice_metadata:
            for engine in voice['SupportedEngines']:
                engines.add(engine)
        return engines

    def get_languages(self, engine):
        """
        Extracts the set of available languages for the specified engine from the
        full list of voice metadata.

        :param engine: The engine type to filter on.
        :return: The set of languages available for the specified engine type.
        """
        if self.voice_metadata is None:
            self.describe_voices()

        return {vo['LanguageName']: vo['LanguageCode'] for vo
                in self.voice_metadata
                if engine in vo['SupportedEngines']}

    def get_voices(self, engine, language_code):
        """
        Extracts the set of voices that are available for the specified engine type
        and language from the full list of voice metadata.

        :param engine: The engine type to filter on.
        :param language_code: The language to filter on.
        :return: The set of voices available for the specified engine type and language.
        """
        if self.voice_metadata is None:
            self.describe_voices()

        return {vo['Name']: vo['Id'] for vo in self.voice_metadata
                if engine in vo['SupportedEngines']
                and language_code == vo['LanguageCode']}
