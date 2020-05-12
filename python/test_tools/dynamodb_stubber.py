# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon DynamoDB unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

from botocore.stub import ANY
from test_tools.example_stubber import ExampleStubber


class DynamoStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon DynamoDB unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto 3 DynamoDB client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    @staticmethod
    def _encode_type(att_type):
        """Transform a Python type to DynamoDB encoding."""
        if att_type is str:
            return 'S'
        elif att_type is float:
            return 'N'
        elif att_type is bytes:
            return 'B'
        else:
            raise TypeError

    @staticmethod
    def _add_table_schema(table_desc, table_name, schema):
        """Build a table schema from its parts."""
        table_desc['TableName'] = table_name
        table_desc['AttributeDefinitions'] = [{
                'AttributeName': item['name'],
                'AttributeType': DynamoStubber._encode_type(item['type'])
            } for item in schema]
        table_desc['KeySchema'] = [{
                'AttributeName': item['name'],
                'KeyType': item['key_type']
            } for item in schema]

    def stub_create_table(self, table_name, schema, throughput):
        table_input = {
            'ProvisionedThroughput': {
                'ReadCapacityUnits': throughput['read'],
                'WriteCapacityUnits': throughput['write']
            }
        }
        self._add_table_schema(table_input, table_name, schema)

        table_output = {
            'TableStatus': 'CREATING'
        }
        self._add_table_schema(table_output, table_name, schema)

        self.add_response(
            'create_table',
            expected_params=table_input,
            service_response={
                'TableDescription': table_output
            }
        )

    def stub_create_table_error(self, error_code):
        self.add_client_error(
            'create_table',
            expected_params={
                'TableName': ANY, 'AttributeDefinitions': ANY, 'KeySchema': ANY,
                'ProvisionedThroughput': ANY
            },
            service_error_code=error_code
        )

    def stub_describe_table(self, table_name, status='ACTIVE'):
        self.add_response(
            'describe_table',
            expected_params={'TableName': table_name},
            service_response={'Table': {'TableStatus': status}}
        )

    def stub_delete_table(self, table_name):
        self.add_response(
            'delete_table',
            expected_params={'TableName': table_name},
            service_response={}
        )

    def stub_put_item(self, table_name, item):
        self.add_response(
            'put_item',
            expected_params={'TableName': table_name, 'Item': item},
            service_response={}
        )

    def stub_put_item_error(self, error_code):
        self.add_client_error(
            'put_item',
            expected_params={'TableName': ANY, 'Item': ANY},
            service_error_code=error_code
        )

    def stub_scan(self, table_name, select, output_items):
        response_items = []
        for output_item in output_items:
            response_items.append({
                key: {self._encode_type(type(value)): str(value)}
                for key, value in output_item.items()})

        self.add_response(
            'scan',
            expected_params={
                'TableName': table_name,
                'Select': select
            },
            service_response={'Items': response_items}
        )

    def stub_scan_error(self, error_code):
        self.add_client_error(
            'scan',
            expected_params={'TableName': ANY, 'Select': ANY},
            service_error_code=error_code
        )
