# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for Redshift Data functions.
"""

import boto3
import pytest
from botocore.exceptions import ClientError

from redshift_data import RedshiftDataWrapper


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_databases(make_stubber, error_code):
    redshift_data_client = boto3.client("redshift-data")
    redshift_data_stubber = make_stubber(redshift_data_client)
    redshift_data_wrapper = RedshiftDataWrapper(redshift_data_client)

    cluster_identifier = "test-cluster"
    data_base_name = "test-database"
    database_user = "XXXXXXXXX"

    redshift_data_stubber.stub_list_databases(
        cluster_identifier, data_base_name, database_user, error_code=error_code
    )

    if error_code is None:
        got_databases = redshift_data_wrapper.list_databases(
            cluster_identifier, data_base_name, database_user
        )
        assert got_databases[0] == "dev"
    else:
        with pytest.raises(ClientError) as exc_info:
            redshift_data_wrapper.list_databases(
                cluster_identifier, data_base_name, database_user
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_execute_statement(make_stubber, error_code):
    redshift_data_client = boto3.client("redshift-data")
    redshift_data_stubber = make_stubber(redshift_data_client)
    redshift_data_wrapper = RedshiftDataWrapper(redshift_data_client)

    cluster_identifier = "test-cluster"
    database_name = "test-database"
    user_name = "XXXXXXXXX"
    sql = "SELECT * FROM test_table"

    redshift_data_stubber.stub_execute_statement(
        cluster_identifier, database_name, user_name, sql, error_code=error_code
    )

    if error_code is None:
        got_result = redshift_data_wrapper.execute_statement(
            cluster_identifier, database_name, user_name, sql
        )
        assert got_result["Id"] == "id"
    else:
        with pytest.raises(ClientError) as exc_info:
            redshift_data_wrapper.execute_statement(
                cluster_identifier, database_name, user_name, sql
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_describe_statement(make_stubber, error_code):
    redshift_data_client = boto3.client("redshift-data")
    redshift_data_stubber = make_stubber(redshift_data_client)
    redshift_data_wrapper = RedshiftDataWrapper(redshift_data_client)

    statement_id = "id"

    redshift_data_stubber.stub_describe_statement(statement_id, error_code=error_code)

    if error_code is None:
        got_result = redshift_data_wrapper.describe_statement(statement_id)
        assert got_result["Id"] == "id"
    else:
        with pytest.raises(ClientError) as exc_info:
            redshift_data_wrapper.describe_statement(statement_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_statement_result(make_stubber, error_code):
    redshift_data_client = boto3.client("redshift-data")
    redshift_data_stubber = make_stubber(redshift_data_client)
    redshift_data_wrapper = RedshiftDataWrapper(redshift_data_client)

    statement_id = "id"

    redshift_data_stubber.stub_get_statement_result(statement_id, error_code=error_code)

    if error_code is None:
        got_result = redshift_data_wrapper.get_statement_result(statement_id)
        assert len(got_result["Records"]) == 2
    else:
        with pytest.raises(ClientError) as exc_info:
            redshift_data_wrapper.get_statement_result(statement_id)
        assert exc_info.value.response["Error"]["Code"] == error_code
