# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Textract unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto3 client.
"""

from test_tools.example_stubber import ExampleStubber


class TextractStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon Textract unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Textract client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_detect_document_text(self, doc_bytes, blocks, error_code=None):
        expected_params = {'Document': {'Bytes': doc_bytes}}
        response = {'Blocks': blocks}
        self._stub_bifurcator(
            'detect_document_text', expected_params, response, error_code=error_code)

    def stub_analyze_document(self, doc_bytes, feature_types, blocks, error_code=None):
        expected_params = {
            'Document': {'Bytes': doc_bytes}, 'FeatureTypes': feature_types}
        response = {'Blocks': blocks}
        self._stub_bifurcator(
            'analyze_document', expected_params, response, error_code=error_code)

    def stub_start_document_text_detection(
            self, bucket_name, obj_name, job_id, topic_arn=None, role_arn=None,
            error_code=None):
        expected_params = {
            'DocumentLocation': {'S3Object': {'Bucket': bucket_name, 'Name': obj_name}}}
        if topic_arn is not None and role_arn is not None:
            expected_params['NotificationChannel'] = {
                'SNSTopicArn': topic_arn, 'RoleArn': role_arn}
        response = {'JobId': job_id}
        self._stub_bifurcator(
            'start_document_text_detection', expected_params, response,
            error_code=error_code)

    def stub_get_document_text_detection(self, job_id, status, error_code=None):
        expected_params = {'JobId': job_id}
        response = {'JobStatus': status}
        self._stub_bifurcator(
            'get_document_text_detection', expected_params, response,
            error_code=error_code)

    def stub_start_document_analysis(
            self, bucket_name, obj_name, feature_types, job_id, topic_arn=None,
            role_arn=None, error_code=None):
        expected_params = {
            'DocumentLocation': {'S3Object': {'Bucket': bucket_name, 'Name': obj_name}},
            'FeatureTypes': feature_types}
        if topic_arn is not None and role_arn is not None:
            expected_params['NotificationChannel'] = {
                'SNSTopicArn': topic_arn, 'RoleArn': role_arn}
        response = {'JobId': job_id}
        self._stub_bifurcator(
            'start_document_analysis', expected_params, response, error_code=error_code)

    def stub_get_document_analysis(self, job_id, status, error_code=None):
        expected_params = {'JobId': job_id}
        response = {'JobStatus': status}
        self._stub_bifurcator(
            'get_document_analysis', expected_params, response, error_code=error_code)
