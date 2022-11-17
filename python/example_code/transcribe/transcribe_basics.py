# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Amazon Transcribe API to
transcribe an audio file to a text file. Also shows how to define a custom vocabulary
to improve the accuracy of the transcription.

This example uses a public domain audio file downloaded from Wikipedia and converted
from .ogg to .mp3 format. The file contains a reading of the poem Jabberwocky by
Lewis Carroll. The original audio source file can be found here:
    https://en.wikisource.org/wiki/File:Jabberwocky.ogg
"""

import logging
import sys
import time
import boto3
from botocore.exceptions import ClientError
import requests

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append('../..')
from demo_tools.custom_waiter import CustomWaiter, WaitState

logger = logging.getLogger(__name__)


class TranscribeCompleteWaiter(CustomWaiter):
    """
    Waits for the transcription to complete.
    """
    def __init__(self, client):
        super().__init__(
            'TranscribeComplete', 'GetTranscriptionJob',
            'TranscriptionJob.TranscriptionJobStatus',
            {'COMPLETED': WaitState.SUCCESS, 'FAILED': WaitState.FAILURE},
            client)

    def wait(self, job_name):
        self._wait(TranscriptionJobName=job_name)


class VocabularyReadyWaiter(CustomWaiter):
    """
    Waits for the custom vocabulary to be ready for use.
    """
    def __init__(self, client):
        super().__init__(
            'VocabularyReady', 'GetVocabulary', 'VocabularyState',
            {'READY': WaitState.SUCCESS}, client)

    def wait(self, vocabulary_name):
        self._wait(VocabularyName=vocabulary_name)


# snippet-start:[python.example_code.transcribe.StartTranscriptionJob]
def start_job(
        job_name, media_uri, media_format, language_code, transcribe_client,
        vocabulary_name=None):
    """
    Starts a transcription job. This function returns as soon as the job is started.
    To get the current status of the job, call get_transcription_job. The job is
    successfully completed when the job status is 'COMPLETED'.

    :param job_name: The name of the transcription job. This must be unique for
                     your AWS account.
    :param media_uri: The URI where the audio file is stored. This is typically
                      in an Amazon S3 bucket.
    :param media_format: The format of the audio file. For example, mp3 or wav.
    :param language_code: The language code of the audio file.
                          For example, en-US or ja-JP
    :param transcribe_client: The Boto3 Transcribe client.
    :param vocabulary_name: The name of a custom vocabulary to use when transcribing
                            the audio file.
    :return: Data about the job.
    """
    try:
        job_args = {
            'TranscriptionJobName': job_name,
            'Media': {'MediaFileUri': media_uri},
            'MediaFormat': media_format,
            'LanguageCode': language_code}
        if vocabulary_name is not None:
            job_args['Settings'] = {'VocabularyName': vocabulary_name}
        response = transcribe_client.start_transcription_job(**job_args)
        job = response['TranscriptionJob']
        logger.info("Started transcription job %s.", job_name)
    except ClientError:
        logger.exception("Couldn't start transcription job %s.", job_name)
        raise
    else:
        return job
# snippet-end:[python.example_code.transcribe.StartTranscriptionJob]


# snippet-start:[python.example_code.transcribe.ListTranscriptionJobs]
def list_jobs(job_filter, transcribe_client):
    """
    Lists summaries of the transcription jobs for the current AWS account.

    :param job_filter: The list of returned jobs must contain this string in their
                       names.
    :param transcribe_client: The Boto3 Transcribe client.
    :return: The list of retrieved transcription job summaries.
    """
    try:
        response = transcribe_client.list_transcription_jobs(
            JobNameContains=job_filter)
        jobs = response['TranscriptionJobSummaries']
        next_token = response.get('NextToken')
        while next_token is not None:
            response = transcribe_client.list_transcription_jobs(
                JobNameContains=job_filter, NextToken=next_token)
            jobs += response['TranscriptionJobSummaries']
            next_token = response.get('NextToken')
        logger.info("Got %s jobs with filter %s.", len(jobs), job_filter)
    except ClientError:
        logger.exception("Couldn't get jobs with filter %s.", job_filter)
        raise
    else:
        return jobs
# snippet-end:[python.example_code.transcribe.ListTranscriptionJobs]


# snippet-start:[python.example_code.transcribe.GetTranscriptionJob]
def get_job(job_name, transcribe_client):
    """
    Gets details about a transcription job.

    :param job_name: The name of the job to retrieve.
    :param transcribe_client: The Boto3 Transcribe client.
    :return: The retrieved transcription job.
    """
    try:
        response = transcribe_client.get_transcription_job(
            TranscriptionJobName=job_name)
        job = response['TranscriptionJob']
        logger.info("Got job %s.", job['TranscriptionJobName'])
    except ClientError:
        logger.exception("Couldn't get job %s.", job_name)
        raise
    else:
        return job
# snippet-end:[python.example_code.transcribe.GetTranscriptionJob]


# snippet-start:[python.example_code.transcribe.DeleteTranscriptionJob]
def delete_job(job_name, transcribe_client):
    """
    Deletes a transcription job. This also deletes the transcript associated with
    the job.

    :param job_name: The name of the job to delete.
    :param transcribe_client: The Boto3 Transcribe client.
    """
    try:
        transcribe_client.delete_transcription_job(
            TranscriptionJobName=job_name)
        logger.info("Deleted job %s.", job_name)
    except ClientError:
        logger.exception("Couldn't delete job %s.", job_name)
        raise
# snippet-end:[python.example_code.transcribe.DeleteTranscriptionJob]


# snippet-start:[python.example_code.transcribe.CreateVocabulary]
def create_vocabulary(
        vocabulary_name, language_code, transcribe_client,
        phrases=None, table_uri=None):
    """
    Creates a custom vocabulary that can be used to improve the accuracy of
    transcription jobs. This function returns as soon as the vocabulary processing
    is started. Call get_vocabulary to get the current status of the vocabulary.
    The vocabulary is ready to use when its status is 'READY'.

    :param vocabulary_name: The name of the custom vocabulary.
    :param language_code: The language code of the vocabulary.
                          For example, en-US or nl-NL.
    :param transcribe_client: The Boto3 Transcribe client.
    :param phrases: A list of comma-separated phrases to include in the vocabulary.
    :param table_uri: A table of phrases and pronunciation hints to include in the
                      vocabulary.
    :return: Information about the newly created vocabulary.
    """
    try:
        vocab_args = {'VocabularyName': vocabulary_name, 'LanguageCode': language_code}
        if phrases is not None:
            vocab_args['Phrases'] = phrases
        elif table_uri is not None:
            vocab_args['VocabularyFileUri'] = table_uri
        response = transcribe_client.create_vocabulary(**vocab_args)
        logger.info("Created custom vocabulary %s.", response['VocabularyName'])
    except ClientError:
        logger.exception("Couldn't create custom vocabulary %s.", vocabulary_name)
        raise
    else:
        return response
# snippet-end:[python.example_code.transcribe.CreateVocabulary]


# snippet-start:[python.example_code.transcribe.ListVocabularies]
def list_vocabularies(vocabulary_filter, transcribe_client):
    """
    Lists the custom vocabularies created for this AWS account.

    :param vocabulary_filter: The returned vocabularies must contain this string in
                              their names.
    :param transcribe_client: The Boto3 Transcribe client.
    :return: The list of retrieved vocabularies.
    """
    try:
        response = transcribe_client.list_vocabularies(
            NameContains=vocabulary_filter)
        vocabs = response['Vocabularies']
        next_token = response.get('NextToken')
        while next_token is not None:
            response = transcribe_client.list_vocabularies(
                NameContains=vocabulary_filter, NextToken=next_token)
            vocabs += response['Vocabularies']
            next_token = response.get('NextToken')
        logger.info(
            "Got %s vocabularies with filter %s.", len(vocabs), vocabulary_filter)
    except ClientError:
        logger.exception(
            "Couldn't list vocabularies with filter %s.", vocabulary_filter)
        raise
    else:
        return vocabs
# snippet-end:[python.example_code.transcribe.ListVocabularies]


# snippet-start:[python.example_code.transcribe.GetVocabulary]
def get_vocabulary(vocabulary_name, transcribe_client):
    """
    Gets information about a custom vocabulary.

    :param vocabulary_name: The name of the vocabulary to retrieve.
    :param transcribe_client: The Boto3 Transcribe client.
    :return: Information about the vocabulary.
    """
    try:
        response = transcribe_client.get_vocabulary(VocabularyName=vocabulary_name)
        logger.info("Got vocabulary %s.", response['VocabularyName'])
    except ClientError:
        logger.exception("Couldn't get vocabulary %s.", vocabulary_name)
        raise
    else:
        return response
# snippet-end:[python.example_code.transcribe.GetVocabulary]


# snippet-start:[python.example_code.transcribe.UpdateVocabulary]
def update_vocabulary(
        vocabulary_name, language_code, transcribe_client, phrases=None,
        table_uri=None):
    """
    Updates an existing custom vocabulary. The entire vocabulary is replaced with
    the contents of the update.

    :param vocabulary_name: The name of the vocabulary to update.
    :param language_code: The language code of the vocabulary.
    :param transcribe_client: The Boto3 Transcribe client.
    :param phrases: A list of comma-separated phrases to include in the vocabulary.
    :param table_uri: A table of phrases and pronunciation hints to include in the
                      vocabulary.
    """
    try:
        vocab_args = {'VocabularyName': vocabulary_name, 'LanguageCode': language_code}
        if phrases is not None:
            vocab_args['Phrases'] = phrases
        elif table_uri is not None:
            vocab_args['VocabularyFileUri'] = table_uri
        response = transcribe_client.update_vocabulary(**vocab_args)
        logger.info(
            "Updated custom vocabulary %s.", response['VocabularyName'])
    except ClientError:
        logger.exception("Couldn't update custom vocabulary %s.", vocabulary_name)
        raise
# snippet-end:[python.example_code.transcribe.UpdateVocabulary]


# snippet-start:[python.example_code.transcribe.DeleteVocabulary]
def delete_vocabulary(vocabulary_name, transcribe_client):
    """
    Deletes a custom vocabulary.

    :param vocabulary_name: The name of the vocabulary to delete.
    :param transcribe_client: The Boto3 Transcribe client.
    """
    try:
        transcribe_client.delete_vocabulary(VocabularyName=vocabulary_name)
        logger.info("Deleted vocabulary %s.", vocabulary_name)
    except ClientError:
        logger.exception("Couldn't delete vocabulary %s.", vocabulary_name)
        raise
# snippet-end:[python.example_code.transcribe.DeleteVocabulary]


# snippet-start:[python.example_code.transcribe.Scenario_CustomVocabulary]
def usage_demo():
    """Shows how to use the Amazon Transcribe service."""
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    s3_resource = boto3.resource('s3')
    transcribe_client = boto3.client('transcribe')

    print('-'*88)
    print("Welcome to the Amazon Transcribe demo!")
    print('-'*88)

    bucket_name = f'jabber-bucket-{time.time_ns()}'
    print(f"Creating bucket {bucket_name}.")
    bucket = s3_resource.create_bucket(
        Bucket=bucket_name,
        CreateBucketConfiguration={
            'LocationConstraint': transcribe_client.meta.region_name})
    media_file_name = '.media/Jabberwocky.mp3'
    media_object_key = 'Jabberwocky.mp3'
    print(f"Uploading media file {media_file_name}.")
    bucket.upload_file(media_file_name, media_object_key)
    media_uri = f's3://{bucket.name}/{media_object_key}'

    job_name_simple = f'Jabber-{time.time_ns()}'
    print(f"Starting transcription job {job_name_simple}.")
    start_job(
        job_name_simple, f's3://{bucket_name}/{media_object_key}', 'mp3', 'en-US',
        transcribe_client)
    transcribe_waiter = TranscribeCompleteWaiter(transcribe_client)
    transcribe_waiter.wait(job_name_simple)
    job_simple = get_job(job_name_simple, transcribe_client)
    transcript_simple = requests.get(
        job_simple['Transcript']['TranscriptFileUri']).json()
    print(f"Transcript for job {transcript_simple['jobName']}:")
    print(transcript_simple['results']['transcripts'][0]['transcript'])

    print('-'*88)
    print("Creating a custom vocabulary that lists the nonsense words to try to "
          "improve the transcription.")
    vocabulary_name = f'Jabber-vocabulary-{time.time_ns()}'
    create_vocabulary(
        vocabulary_name, 'en-US', transcribe_client,
        phrases=[
            'brillig', 'slithy', 'borogoves', 'mome', 'raths', 'Jub-Jub', 'frumious',
            'manxome', 'Tumtum', 'uffish', 'whiffling', 'tulgey', 'thou', 'frabjous',
            'callooh', 'callay', 'chortled'],
        )
    vocabulary_ready_waiter = VocabularyReadyWaiter(transcribe_client)
    vocabulary_ready_waiter.wait(vocabulary_name)

    job_name_vocabulary_list = f'Jabber-vocabulary-list-{time.time_ns()}'
    print(f"Starting transcription job {job_name_vocabulary_list}.")
    start_job(
        job_name_vocabulary_list, media_uri, 'mp3', 'en-US', transcribe_client,
        vocabulary_name)
    transcribe_waiter.wait(job_name_vocabulary_list)
    job_vocabulary_list = get_job(job_name_vocabulary_list, transcribe_client)
    transcript_vocabulary_list = requests.get(
        job_vocabulary_list['Transcript']['TranscriptFileUri']).json()
    print(f"Transcript for job {transcript_vocabulary_list['jobName']}:")
    print(transcript_vocabulary_list['results']['transcripts'][0]['transcript'])

    print('-'*88)
    print("Updating the custom vocabulary with table data that provides additional "
          "pronunciation hints.")
    table_vocab_file = 'jabber-vocabulary-table.txt'
    bucket.upload_file(table_vocab_file, table_vocab_file)
    update_vocabulary(
        vocabulary_name, 'en-US', transcribe_client,
        table_uri=f's3://{bucket.name}/{table_vocab_file}')
    vocabulary_ready_waiter.wait(vocabulary_name)

    job_name_vocab_table = f'Jabber-vocab-table-{time.time_ns()}'
    print(f"Starting transcription job {job_name_vocab_table}.")
    start_job(
        job_name_vocab_table, media_uri, 'mp3', 'en-US', transcribe_client,
        vocabulary_name=vocabulary_name)
    transcribe_waiter.wait(job_name_vocab_table)
    job_vocab_table = get_job(job_name_vocab_table, transcribe_client)
    transcript_vocab_table = requests.get(
        job_vocab_table['Transcript']['TranscriptFileUri']).json()
    print(f"Transcript for job {transcript_vocab_table['jobName']}:")
    print(transcript_vocab_table['results']['transcripts'][0]['transcript'])

    print('-'*88)
    print("Getting data for jobs and vocabularies.")
    jabber_jobs = list_jobs('Jabber', transcribe_client)
    print(f"Found {len(jabber_jobs)} jobs:")
    for job_sum in jabber_jobs:
        job = get_job(job_sum['TranscriptionJobName'], transcribe_client)
        print(f"\t{job['TranscriptionJobName']}, {job['Media']['MediaFileUri']}, "
              f"{job['Settings'].get('VocabularyName')}")

    jabber_vocabs = list_vocabularies('Jabber', transcribe_client)
    print(f"Found {len(jabber_vocabs)} vocabularies:")
    for vocab_sum in jabber_vocabs:
        vocab = get_vocabulary(vocab_sum['VocabularyName'], transcribe_client)
        vocab_content = requests.get(vocab['DownloadUri']).text
        print(f"\t{vocab['VocabularyName']} contents:")
        print(vocab_content)

    print('-'*88)
    print("Deleting demo jobs.")
    for job_name in [job_name_simple, job_name_vocabulary_list, job_name_vocab_table]:
        delete_job(job_name, transcribe_client)
    print("Deleting demo vocabulary.")
    delete_vocabulary(vocabulary_name, transcribe_client)
    print("Deleting demo bucket.")
    bucket.objects.delete()
    bucket.delete()
    print("Thanks for watching!")
# snippet-end:[python.example_code.transcribe.Scenario_CustomVocabulary]


if __name__ == '__main__':
    usage_demo()
