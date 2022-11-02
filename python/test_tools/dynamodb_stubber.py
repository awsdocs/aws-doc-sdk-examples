# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon DynamoDB unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto3 client.
"""

from decimal import Decimal
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

    type_encoding = {
        type(''): 'S',
        type(1): 'N',
        type(.1): 'N',
        type(Decimal()): 'N',
        type(b''): 'B',
        type({}): 'M',
        type([]): 'L',
        type(True): 'BOOL',
    }

    @staticmethod
    def _add_table_schema(table_desc, table_name, schema):
        """Build a table schema from its parts."""
        table_desc['TableName'] = table_name
        table_desc['AttributeDefinitions'] = [{
                'AttributeName': item['name'],
                'AttributeType': item['type']
            } for item in schema]
        table_desc['KeySchema'] = [{
                'AttributeName': item['name'],
                'KeyType': item['key_type']
            } for item in schema]

    def _build_out_item(self, in_item):
        out_item = {}
        for key, value in in_item.items():
            if value is not None:
                value_type = self.type_encoding[type(value)]
                if value_type == 'M':
                    out_val = self._build_out_item(value)
                elif value_type == 'L':
                    out_val = [
                        {self.type_encoding[type(list_val)]: list_val}
                        for list_val in value
                    ]
                elif value_type == 'BOOL':
                    out_val = value
                else:
                    out_val = str(value)
                out_item[key] = {value_type: out_val}
        return out_item

    def stub_create_table(self, table_name, schema, throughput, error_code=None):
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
        self._stub_bifurcator(
            'create_table', table_input, {'TableDescription': table_output},
            error_code=error_code)

    def stub_describe_table(
            self, table_name, schema=None, provisioned_throughput=None, status='ACTIVE',
            error_code=None):
        response = {'Table': {'TableStatus': status}}
        if schema is not None:
            self._add_table_schema(response['Table'], table_name, schema)
        if provisioned_throughput is not None:
            response['Table']['ProvisionedThroughput'] = provisioned_throughput
        self._stub_bifurcator(
            'describe_table',
            expected_params={'TableName': table_name},
            response=response,
            error_code=error_code
        )

    def stub_delete_table(self, table_name, error_code=None):
        self._stub_bifurcator(
            'delete_table',
            expected_params={'TableName': table_name},
            error_code=error_code
        )

    def stub_list_tables(self, table_names, error_code=None):
        self._stub_bifurcator(
            'list_tables',
            response={'TableNames': table_names},
            error_code=error_code)

    def stub_put_item(self, table_name, item, http_status=200, error_code=None):
        self._stub_bifurcator(
            'put_item',
            expected_params={'TableName': table_name, 'Item': item},
            response={'ResponseMetadata': {'HTTPStatusCode': http_status}},
            error_code=error_code
        )

    def stub_get_item(self, table_name, key, item, error_code=None):
        expected_params = {
            'TableName': table_name,
            'Key': key
        }
        if item is not None:
            response = {'Item': self._build_out_item(item)}
        else:
            response = {}
        self._stub_bifurcator('get_item', expected_params, response, error_code)

    def stub_update_item(self, table_name, update_key, update, requested_return,
                         expression=None, condition=None, expression_attrs=None,
                         error_code=None):
        if expression:
            update_expr = expression
            attr_vals = expression_attrs
        else:
            exp_key, exp_val = list(update.items())[0]
            update_expr_parts = [
                f"{exp_key}.{key}=:{key[0]}"
                for key, val in exp_val.items()
            ]
            update_expr = f"set {', '.join(update_expr_parts)}"
            attr_vals = {
                f":{key[0]}": val
                for key, val in exp_val.items()
            }
        expected_params = {
            'TableName': table_name,
            'Key': update_key,
            'UpdateExpression': update_expr,
            'ExpressionAttributeValues': attr_vals,
            'ReturnValues': requested_return
        }
        if condition:
            expected_params['ConditionExpression'] = condition
        response = {
            'Attributes': self._build_out_item(update)
        }
        self._stub_bifurcator('update_item', expected_params, response, error_code)

    def stub_update_item_attr_update(
            self, table_name, update_key, attribs, error_code=None):
        expected_params = {
            'TableName': table_name,
            'Key': update_key,
            'AttributeUpdates': {
                key: {'Value': value, 'Action': 'PUT'}
                for key, value in attribs.items()}}
        response = {
            'Attributes': self._build_out_item(attribs)
        }
        self._stub_bifurcator('update_item', expected_params, response, error_code)

    def stub_delete_item(self, table_name, delete_key, condition=None,
                         expression_attrs=None, error_code=None):
        expected_params = {'TableName': table_name, 'Key': delete_key}
        if condition:
            expected_params['ConditionExpression'] = condition
        if expression_attrs:
            expected_params['ExpressionAttributeValues'] = expression_attrs
        self._stub_bifurcator('delete_item', expected_params, error_code=error_code)

    def stub_scan(self, table_name, output_items, select=None, filter_expression=None,
                  projection_expression=None, expression_attrs=None, start_key=None,
                  last_key=None, error_code=None):
        expected_params = {'TableName': table_name}
        if select:
            expected_params['Select'] = select
        if filter_expression:
            expected_params['FilterExpression'] = filter_expression
        if projection_expression:
            expected_params['ProjectionExpression'] = projection_expression
        if expression_attrs:
            expected_params['ExpressionAttributeNames'] = expression_attrs
        if start_key:
            expected_params['ExclusiveStartKey'] = start_key
        response = {'Items': [
            self._build_out_item(output_item) for output_item in output_items
        ]}
        if last_key:
            response['LastEvaluatedKey'] = last_key
        self._stub_bifurcator('scan', expected_params, response, error_code=error_code)

    def stub_query(self, table_name, output_items, key_condition=None,
                   projection=None, expression_attrs=None, expression_attr_vals=None,
                   error_code=None):
        expected_params = {'TableName': table_name}
        if key_condition is not None:
            expected_params['KeyConditionExpression'] = key_condition
        if projection is not None:
            expected_params['ProjectionExpression'] = projection
        if expression_attrs is not None:
            expected_params['ExpressionAttributeNames'] = expression_attrs
        if expression_attr_vals is not None:
            expected_params['ExpressionAttributeValues'] = expression_attr_vals
        response_items = [
            self._build_out_item(output_item) for output_item in output_items
        ]
        self._stub_bifurcator(
            'query', expected_params, {'Items': response_items}, error_code=error_code)

    def stub_batch_write_item(
            self, request_items, unprocessed_items=None, error_code=None):
        expected_params = {'RequestItems': request_items}
        response = {
            'UnprocessedItems': unprocessed_items
            if unprocessed_items is not None else {}}
        self._stub_bifurcator(
            'batch_write_item', expected_params, response, error_code=error_code)

    def stub_batch_get_item(
            self, request_items, response_items=None, unprocessed_keys=None,
            error_code=None):
        expected_params = {'RequestItems': request_items}
        response = {
            'UnprocessedKeys': unprocessed_keys
            if unprocessed_keys is not None else {}}
        if response_items is not None:
            response['Responses'] = response_items
        self._stub_bifurcator(
            'batch_get_item', expected_params, response, error_code=error_code)

    def stub_execute_statement(self, statement, params, items, error_code=None):
        expected_params = {'Statement': statement}
        if params is not None:
            expected_params['Parameters'] = params
        response = {'Items': items}
        self._stub_bifurcator(
            'execute_statement', expected_params, response, error_code=error_code)

    def stub_batch_execute_statement(self, statements, responses, error_code=None):
        expected_params = {'Statements': statements}
        response = {'Responses': responses}
        self._stub_bifurcator(
            'batch_execute_statement', expected_params, response, error_code=error_code)
