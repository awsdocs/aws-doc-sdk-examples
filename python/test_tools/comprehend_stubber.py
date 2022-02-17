# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Comprehend unit tests.
"""

import datetime
from test_tools.example_stubber import ExampleStubber


class ComprehendStubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon Comprehend unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and raise errors if the parameters are not as expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Comprehend client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_detect_dominant_language(self, text, languages, error_code=None):
        expected_params = {'Text': text}
        response = {'Languages': languages}
        self._stub_bifurcator(
            'detect_dominant_language', expected_params, response,
            error_code=error_code)

    def stub_detect_entities(self, text, language, entities, error_code=None):
        expected_params = {'Text': text, 'LanguageCode': language}
        response = {'Entities': entities}
        self._stub_bifurcator(
            'detect_entities', expected_params, response, error_code=error_code)

    def stub_detect_key_phrases(self, text, language, phrases, error_code=None):
        expected_params = {'Text': text, 'LanguageCode': language}
        response = {'KeyPhrases': phrases}
        self._stub_bifurcator(
            'detect_key_phrases', expected_params, response, error_code=error_code)

    def stub_detect_pii_entities(self, text, language, entities, error_code=None):
        expected_params = {'Text': text, 'LanguageCode': language}
        response = {'Entities': entities}
        self._stub_bifurcator(
            'detect_pii_entities', expected_params, response, error_code=error_code)

    def stub_detect_sentiment(
            self, text, language, sentiment, sentiment_scores, error_code=None):
        expected_params = {'Text': text, 'LanguageCode': language}
        response = {'Sentiment': sentiment, 'SentimentScore': sentiment_scores}
        self._stub_bifurcator(
            'detect_sentiment', expected_params, response, error_code=error_code)

    def stub_detect_syntax(self, text, language, tokens, error_code=None):
        expected_params = {'Text': text, 'LanguageCode': language}
        response = {'SyntaxTokens': tokens}
        self._stub_bifurcator(
            'detect_syntax', expected_params, response, error_code=error_code)

    def stub_create_document_classifier(
            self, name, lang_code, bucket_name, training_key, data_access_role_arn,
            mode, classifier_arn, error_code=None):
        expected_params = {
            'DocumentClassifierName': name,
            'LanguageCode': lang_code,
            'InputDataConfig': {'S3Uri': f's3://{bucket_name}/{training_key}'},
            'DataAccessRoleArn': data_access_role_arn,
            'Mode': mode}
        response = {'DocumentClassifierArn': classifier_arn}
        self._stub_bifurcator(
            'create_document_classifier', expected_params, response,
            error_code=error_code)

    def stub_describe_document_classifier(
            self, classifier_arn, status, error_code=None):
        expected_params = {'DocumentClassifierArn': classifier_arn}
        response = {'DocumentClassifierProperties': {
            'DocumentClassifierArn': classifier_arn,
            'Status': status}}
        self._stub_bifurcator(
            'describe_document_classifier', expected_params, response,
            error_code=error_code)

    def stub_list_document_classifiers(self, arns, statuses, error_code=None):
        expected_params = {}
        response = {'DocumentClassifierPropertiesList': [
            {'DocumentClassifierArn': arn, 'Status': status}
            for arn, status in zip(arns, statuses)]}
        self._stub_bifurcator(
            'list_document_classifiers', expected_params, response,
            error_code=error_code)

    def stub_delete_document_classifier(self, classifier_arn, error_code=None):
        expected_params = {'DocumentClassifierArn': classifier_arn}
        response = {}
        self._stub_bifurcator(
            'delete_document_classifier', expected_params, response,
            error_code=error_code)

    def stub_start_document_classification_job(
            self, classifier_arn, job_name, input_bucket, input_key, input_format,
            output_bucket, output_key, data_role_arn, job_status, error_code=None):
        expected_params = {
            'DocumentClassifierArn': classifier_arn,
            'JobName': job_name,
            'InputDataConfig': {
                                  'S3Uri': f's3://{input_bucket}/{input_key}',
                                  'InputFormat': input_format},
            'OutputDataConfig': {'S3Uri': f's3://{output_bucket}/{output_key}'},
            'DataAccessRoleArn': data_role_arn}
        response = {'JobStatus': job_status}
        self._stub_bifurcator(
            'start_document_classification_job', expected_params, response,
            error_code=error_code)

    def stub_describe_document_classification_job(
            self, job_id, job_name, job_status, error_code=None):
        expected_params = {'JobId': job_id}
        response = {'DocumentClassificationJobProperties': {
            'JobId': job_id, 'JobName': job_name, 'JobStatus': job_status}}
        self._stub_bifurcator(
            'describe_document_classification_job', expected_params, response,
            error_code=error_code)

    def stub_list_document_classification_jobs(self, jobs, error_code=None):
        expected_params = {}
        response = {'DocumentClassificationJobPropertiesList': [
            {'JobId': job} for job in jobs]}
        self._stub_bifurcator(
            'list_document_classification_jobs', expected_params, response,
            error_code=error_code)

    def stub_start_topics_detection_job(
            self, job_name, input_bucket, input_key, input_format, output_bucket,
            output_key, data_access_role_arn, job_id, job_status, error_code=None):
        expected_params = {
            'JobName': job_name,
            'DataAccessRoleArn': data_access_role_arn,
            'InputDataConfig': {
                'S3Uri': f's3://{input_bucket}/{input_key}',
                'InputFormat': input_format},
            'OutputDataConfig': {'S3Uri': f's3://{output_bucket}/{output_key}'}}
        response = {'JobId': job_id, 'JobStatus': job_status}
        self._stub_bifurcator(
            'start_topics_detection_job', expected_params, response,
            error_code=error_code)

    def stub_describe_topics_detection_job(self, job_id, error_code=None):
        expected_params = {'JobId': job_id}
        response = {'TopicsDetectionJobProperties': {'JobId': job_id}}
        self._stub_bifurcator(
            'describe_topics_detection_job', expected_params, response,
            error_code=error_code)

    def stub_list_topics_detection_jobs(self, job_ids, error_code=None):
        expected_params = {}
        response = {'TopicsDetectionJobPropertiesList': [
            {'JobId': job_id} for job_id in job_ids]}
        self._stub_bifurcator(
            'list_topics_detection_jobs', expected_params, response,
            error_code=error_code)
