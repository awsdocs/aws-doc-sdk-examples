# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Keyspaces (for Apache Cassandra) unit tests.
"""

from botocore.stub import ANY
from test_tools.example_stubber import ExampleStubber


class KeyspacesStubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon Keyspaces unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and raise errors if the parameters are not as expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Amazon Keyspaces client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_keyspace(self, ks_name, ks_arn, error_code=None):
        expected_params = {'keyspaceName': ks_name}
        response = {'resourceArn': ks_arn}
        self._stub_bifurcator(
            'create_keyspace', expected_params, response, error_code=error_code)

    def stub_get_keyspace(self, ks_name, ks_arn, error_code=None):
        expected_params = {'keyspaceName': ks_name}
        response = {'keyspaceName': ks_name, 'resourceArn': ks_arn}
        self._stub_bifurcator(
            'get_keyspace', expected_params, response, error_code=error_code)

    def stub_list_keyspaces(self, keyspaces, error_code=None):
        expected_params = {}
        response = {'keyspaces': keyspaces}
        self._stub_bifurcator(
            'list_keyspaces', expected_params, response, error_code=error_code)

    def stub_create_table(self, ks_name, table_name, pit_recovery, table_arn, columns=ANY, keys=ANY, error_code=None):
        expected_params = {
            'keyspaceName': ks_name, 'tableName': table_name,
            'schemaDefinition': {'allColumns': columns, 'partitionKeys': keys},
            'pointInTimeRecovery': pit_recovery}
        response = {'resourceArn': table_arn}
        self._stub_bifurcator(
            'create_table', expected_params, response, error_code=error_code)

    def stub_get_table(self, ks_name, table_name, status, table_arn, schema=None, error_code=None):
        expected_params = {'keyspaceName': ks_name, 'tableName': table_name}
        response = {
            'keyspaceName': ks_name, 'tableName': table_name, 'resourceArn': table_arn, 'status': status}
        if schema is not None:
            response['schemaDefinition'] = schema
        self._stub_bifurcator(
            'get_table', expected_params, response, error_code=error_code)

    def stub_list_tables(self, ks_name, tables, error_code=None):
        expected_params = {'keyspaceName': ks_name}
        response = {'tables': tables}
        self._stub_bifurcator(
            'list_tables', expected_params, response, error_code=error_code)

    def stub_update_table(self, ks_name, table_name, table_arn, columns=ANY, error_code=None):
        expected_params = {
            'keyspaceName': ks_name, 'tableName': table_name, 'addColumns': columns}
        response = {'resourceArn': table_arn}
        self._stub_bifurcator(
            'update_table', expected_params, response, error_code=error_code)

    def stub_restore_table(
            self, source_ks, source_table, target_ks, target_table, table_arn, timestamp=ANY, error_code=None):
        expected_params = {
            'sourceKeyspaceName': source_ks, 'sourceTableName': source_table,
            'targetKeyspaceName': target_ks, 'targetTableName': target_table,
            'restoreTimestamp': timestamp}
        response = {'restoredTableARN': table_arn}
        self._stub_bifurcator(
            'restore_table', expected_params, response, error_code=error_code)

    def stub_delete_table(self, ks_name, table_name, error_code=None):
        expected_params = {'keyspaceName': ks_name, 'tableName': table_name}
        response = {}
        self._stub_bifurcator(
            'delete_table', expected_params, response, error_code=error_code)

    def stub_delete_keyspace(self, ks_name, error_code=None):
        expected_params = {'keyspaceName': ks_name}
        response = {}
        self._stub_bifurcator(
            'delete_keyspace', expected_params, response, error_code=error_code)
