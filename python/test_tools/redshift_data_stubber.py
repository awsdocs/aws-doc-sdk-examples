# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Redshift DAta unit tests.
"""

from datetime import datetime
import json
from test_tools.example_stubber import ExampleStubber


class RedshiftDataStubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon Redshift Data unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and raise errors if the parameters are not as expected.
    """

    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Step Functions client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_list_databases(
        self, cluster_identifier, database_name, database_user, error_code=None
    ):
        expected_params = {
            "ClusterIdentifier": cluster_identifier,
            "Database": database_name,
            "DbUser": database_user,
        }
        response = {"Databases": ["dev"]}
        self._stub_bifurcator(
            "list_databases", expected_params, response, error_code=error_code
        )

    def stub_execute_statement(
        self, cluster_identifier, database_name, user_name, sql, error_code=None
    ):
        expected_params = {
            "ClusterIdentifier": cluster_identifier,
            "Database": database_name,
            "DbUser": user_name,
            "Sql": sql,
        }
        response = {"Id": "id"}
        self._stub_bifurcator(
            "execute_statement", expected_params, response, error_code=error_code
        )

    def stub_describe_statement(self, statement_id, error_code=None):
        expected_params = {"Id": statement_id}
        response = {"Id": "id", "Status": "SUCCEEDED"}
        self._stub_bifurcator(
            "describe_statement", expected_params, response, error_code=error_code
        )

    def stub_get_statement_result(self, id, error_code=None):
        expected_params = {"Id": id}
        response = {
            "ColumnMetadata": [],
            "Records": [[{"stringValue": "value1"}], [{"stringValue": "value2"}]],
        }

        self._stub_bifurcator(
            "get_statement_result", expected_params, response, error_code=error_code
        )
