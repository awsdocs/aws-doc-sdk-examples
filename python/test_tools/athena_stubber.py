# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Athena unit tests.
"""

from botocore.stub import ANY
from test_tools.example_stubber import ExampleStubber


class AthenaStubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon Athena unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests. If the parameters are not as expected, the functions
    raise errors.
    """

    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Amazon Athena client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_work_group(
        self, name, output_location, description=ANY, error_code=None
    ):
        expected_params = {
            "Name": name,
            "Configuration": {
                "ResultConfiguration": {"OutputLocation": output_location},
                "EnforceWorkGroupConfiguration": True,
                "PublishCloudWatchMetricsEnabled": True,
            },
            "Description": description,
        }
        response = {}
        self._stub_bifurcator(
            "create_work_group", expected_params, response, error_code=error_code
        )

    def stub_get_work_group(self, name, work_group, error_code=None):
        expected_params = {"WorkGroup": name}
        response = {"WorkGroup": work_group}
        self._stub_bifurcator(
            "get_work_group", expected_params, response, error_code=error_code
        )

    def stub_start_query_execution(
        self,
        query_string,
        work_group,
        query_execution_id,
        database=None,
        error_code=None,
    ):
        expected_params = {"QueryString": query_string, "WorkGroup": work_group}
        if database is not None:
            expected_params["QueryExecutionContext"] = {"Database": database}
        response = {"QueryExecutionId": query_execution_id}
        self._stub_bifurcator(
            "start_query_execution", expected_params, response, error_code=error_code
        )

    def stub_get_query_execution(self, query_execution_id, state, error_code=None):
        expected_params = {"QueryExecutionId": query_execution_id}
        response = {
            "QueryExecution": {
                "QueryExecutionId": query_execution_id,
                "Status": {"State": state},
            }
        }
        self._stub_bifurcator(
            "get_query_execution", expected_params, response, error_code=error_code
        )

    def stub_get_query_results(
        self, query_execution_id, columns, rows, error_code=None
    ):
        expected_params = {"QueryExecutionId": query_execution_id}
        column_info = [{"Name": col, "Type": "varchar"} for col in columns]
        result_rows = [
            {"Data": [{"VarCharValue": value} for value in row]} for row in rows
        ]
        response = {
            "ResultSet": {
                "Rows": result_rows,
                "ResultSetMetadata": {"ColumnInfo": column_info},
            }
        }
        self._stub_bifurcator(
            "get_query_results", expected_params, response, error_code=error_code
        )

    def stub_list_query_executions(self, work_group, execution_ids, error_code=None):
        expected_params = {"WorkGroup": work_group}
        response = {"QueryExecutionIds": execution_ids}
        self._stub_bifurcator(
            "list_query_executions", expected_params, response, error_code=error_code
        )

    def stub_create_named_query(
        self,
        name,
        description,
        database,
        query_string,
        work_group,
        named_query_id,
        error_code=None,
    ):
        expected_params = {
            "Name": name,
            "Description": description,
            "Database": database,
            "QueryString": query_string,
            "WorkGroup": work_group,
        }
        response = {"NamedQueryId": named_query_id}
        self._stub_bifurcator(
            "create_named_query", expected_params, response, error_code=error_code
        )

    def stub_get_named_query(self, named_query_id, named_query, error_code=None):
        expected_params = {"NamedQueryId": named_query_id}
        response = {"NamedQuery": named_query}
        self._stub_bifurcator(
            "get_named_query", expected_params, response, error_code=error_code
        )

    def stub_list_named_queries(self, work_group, named_query_ids, error_code=None):
        expected_params = {"WorkGroup": work_group}
        response = {"NamedQueryIds": named_query_ids}
        self._stub_bifurcator(
            "list_named_queries", expected_params, response, error_code=error_code
        )

    def stub_delete_named_query(self, named_query_id, error_code=None):
        expected_params = {"NamedQueryId": named_query_id}
        response = {}
        self._stub_bifurcator(
            "delete_named_query", expected_params, response, error_code=error_code
        )

    def stub_delete_work_group(self, name, recursive=True, error_code=None):
        expected_params = {"WorkGroup": name, "RecursiveDeleteOption": recursive}
        response = {}
        self._stub_bifurcator(
            "delete_work_group", expected_params, response, error_code=error_code
        )

    def stub_list_work_groups(self, work_groups, error_code=None):
        expected_params = {}
        response = {"WorkGroups": work_groups}
        self._stub_bifurcator(
            "list_work_groups", expected_params, response, error_code=error_code
        )
