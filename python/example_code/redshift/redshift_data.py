# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to access Amazon Redshift data.
"""

import boto3
import logging
from botocore.exceptions import ClientError


# snippet-start:[python.example_code.redshift_data.RedshiftDataWrapper]
class RedshiftDataWrapper:
    """Encapsulates Amazon Redshift data."""

    def __init__(self, client):
        """
        :param client: A Boto3 RedshiftDataWrapper client.
        """
        self.client = client

    # snippet-end:[python.example_code.redshift_data.RedshiftDataWrapper]

    # snippet-start:[python.example_code.redshift_data.ListDatabases]
    def list_databases(self, cluster_identifier, database_name, database_user):
        """
        Lists databases in a cluster.

        :param cluster_identifier: The cluster identifier.
        :param database_name: The database name.
        :param database_user: The database user.
        :return: The list of databases.
        """
        try:
            paginator = self.client.get_paginator("list_databases")
            databases = []
            for page in paginator.paginate(
                ClusterIdentifier=cluster_identifier,
                Database=database_name,
                DbUser=database_user,
            ):
                databases.extend(page["Databases"])

            return databases
        except ClientError as err:
            logging.error(
                "Couldn't list databases. Here's why: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.redshift_data.ListDatabases]

    # snippet-start:[python.example_code.redshift_data.ExecuteStatement]
    def execute_statement(
        self, cluster_identifier, database_name, user_name, sql, parameter_list=None
    ):
        """
        Executes a SQL statement.

        :param cluster_identifier: The cluster identifier.
        :param database_name: The database name.
        :param user_name: The user's name.
        :param sql: The SQL statement.
        :param parameter_list: The optional SQL statement parameters.
        :return: The SQL statement result.
        """

        try:
            kwargs = {
                "ClusterIdentifier": cluster_identifier,
                "Database": database_name,
                "DbUser": user_name,
                "Sql": sql,
            }
            if parameter_list:
                kwargs["Parameters"] = parameter_list
            response = self.client.execute_statement(**kwargs)
            return response
        except ClientError as err:
            logging.error(
                "Couldn't execute statement. Here's why: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.redshift_data.ExecuteStatement]

    # snippet-start:[python.example_code.redshift_data.DescribeStatement]
    def describe_statement(self, statement_id):
        """
        Describes a SQL statement.

        :param statement_id: The SQL statement identifier.
        :return: The SQL statement result.
        """
        try:
            response = self.client.describe_statement(Id=statement_id)
            return response
        except ClientError as err:
            logging.error(
                "Couldn't describe statement. Here's why: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.redshift_data.DescribeStatement]

    # snippet-start:[python.example_code.redshift_data.GetStatementResult]
    def get_statement_result(self, statement_id):
        """
        Gets the result of a SQL statement.

        :param statement_id: The SQL statement identifier.
        :return: The SQL statement result.
        """
        try:
            result = {
                "Records": [],
            }
            paginator = self.client.get_paginator("get_statement_result")
            for page in paginator.paginate(Id=statement_id):
                if "ColumnMetadata" not in result:
                    result["ColumnMetadata"] = page["ColumnMetadata"]
                result["Records"].extend(page["Records"])
            return result
        except ClientError as err:
            logging.error(
                "Couldn't get statement result. Here's why: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.redshift_data.GetStatementResult]


if __name__ == "__main__":
    # Demonstrates how to initiate the wrapper object and use it.
    # snippet-start:[python.example_code.redshift_data.RedshiftDataWrapper.instantiation]
    client = boto3.client("redshift-data")
    redshift_data_wrapper = RedshiftDataWrapper(client)
    # snippet-end:[python.example_code.redshift_data.RedshiftDataWrapper.instantiation]

    redshift_data_wrapper.list_databases(
        "redshift-cluster-movies", "dev", "AwsUser1000"
    )
