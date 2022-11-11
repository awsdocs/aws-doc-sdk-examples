# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon RDS Data Service unit tests.
"""

import datetime
from test_tools.example_stubber import ExampleStubber

VALUE_KEYS = {
    bytes: 'blobValue',
    bool: 'booleanValue',
    float: 'doubleValue',
    None: 'isNull',
    int: 'longValue',
    str: 'stringValue',
    datetime.date: 'stringValue'
}


class RdsDataStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon RDS Data Service unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 RDS Data Service client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_execute_statement(
            self, resource_arn, secret_arn, database, sql, sql_params=None,
            transaction_id=None, records=None, generated_fields=None, error_code=None, error_message=''):
        expected_params = {
            'database': database, 'resourceArn': resource_arn, 'secretArn': secret_arn,
            'sql': sql}
        if sql_params is not None:
            expected_params['parameters'] = sql_params
        if transaction_id is not None:
            expected_params['transactionId'] = transaction_id
        response = {}
        if records is not None:
            response['records'] = [[
                {VALUE_KEYS[type(val)]: val} for val in record
            ] for record in records]
        if generated_fields is not None:
            response['generatedFields'] = [
                {VALUE_KEYS[type(field)]: field} for field in generated_fields]
        self._stub_bifurcator(
            'execute_statement', expected_params, response, error_code=error_code, error_message=error_message)

    def stub_batch_execute_statement(
            self, resource_arn, secret_arn, database, sql, sql_param_sets=None,
            generated_field_sets=None, error_code=None):
        expected_params = {
            'database': database, 'resourceArn': resource_arn, 'secretArn': secret_arn,
            'sql': sql}
        if sql_param_sets is not None:
            expected_params['parameterSets'] = sql_param_sets
        response = {}
        if generated_field_sets is not None:
            response['updateResults'] = [{
                'generatedFields': [
                    {VALUE_KEYS[type(field)]: field} for field in fields]}
                for fields in generated_field_sets]
        self._stub_bifurcator(
            'batch_execute_statement', expected_params, response, error_code=error_code)

    def stub_begin_transaction(
            self, resource_arn, secret_arn, database, transaction_id, error_code=None):
        expected_params = {
            'resourceArn': resource_arn, 'secretArn': secret_arn, 'database': database}
        response = {'transactionId': transaction_id}
        self._stub_bifurcator(
            'begin_transaction', expected_params, response, error_code=error_code)

    def stub_commit_transaction(
            self, resource_arn, secret_arn, transaction_id, error_code=None):
        expected_params = {
            'resourceArn': resource_arn, 'secretArn': secret_arn,
            'transactionId': transaction_id}
        response = {'transactionStatus': 'TEST_STATUS'}
        self._stub_bifurcator(
            'commit_transaction', expected_params, response, error_code=error_code)

    def stub_rollack_transaction(
            self, resource_arn, secret_arn, transaction_id, error_code=None):
        expected_params = {
            'resourceArn': resource_arn, 'secretArn': secret_arn,
            'transactionId': transaction_id}
        response = {'transactionStatus': 'TEST_STATUS'}
        self._stub_bifurcator(
            'rollback_transaction', expected_params, response, error_code=error_code)
